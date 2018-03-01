package com.mfg.Service;

import com.google.gson.*;
import com.mfg.Entity.*;
import com.mfg.Model.BasicResponse;
import com.mfg.Repository.*;
import com.mfg.config.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by I309908 on 7/21/2017.
 */
@Service
public class DashboardService {

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private ProjectModuleRepository projectModuleRepository;

    @Autowired
    private JenkinsServerRepository jenkinsServerRepository;

    @Autowired
    private JenkinsJobRepository jenkinsJobRepository;

    @Autowired
    private SonarServerRepository sonarServerRepository;

    @Autowired
    private BCPServerRepository bcpServerRepository;

    @Autowired
    private DashboardViewerRepository dashboardViewerRepository;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private ParsingRuleRepository parsingRuleRepository;

    public JsonObject getDashboardByOwnerId(String ownerId){
        List<Dashboard> dashboardList = dashboardRepository.findByOwnerAndIsVisible(ownerId, true);
        JsonObject responseObj = new JsonObject();
        JsonArray dashboardArray = new JsonArray();
        for (Dashboard dashboard : dashboardList){
            JsonObject dashboardObj = new JsonObject();
            dashboardObj.addProperty("id",dashboard.getId());
            dashboardObj.addProperty("name",dashboard.getName());
            dashboardObj.addProperty("createdBy",dashboard.getCreatedBy());
            dashboardObj.addProperty("owner",dashboard.getOwner());
            dashboardObj.addProperty("active",dashboard.isActive());
            dashboardObj.addProperty("createdTime",dashboard.getCreatedTime().toString());
            dashboardArray.add(dashboardObj);
        }
        responseObj.add("Owned_Dashboards", dashboardArray);
        List<DashboardViewer> viewerList = dashboardViewerRepository.findByViewerId(ownerId);
        JsonArray readOnlyArray = new JsonArray();
        for (DashboardViewer viewer : viewerList){
            Dashboard dashboardReadOnly = dashboardRepository.findByIdAndIsVisible(viewer.getDashboardId(), true);
            JsonObject readOnlyObj = new JsonObject();
            if (dashboardReadOnly != null){
                readOnlyObj.addProperty("id",dashboardReadOnly.getId());
                readOnlyObj.addProperty("name",dashboardReadOnly.getName());
                readOnlyObj.addProperty("createdBy",dashboardReadOnly.getCreatedBy());
                readOnlyObj.addProperty("owner",dashboardReadOnly.getOwner());
                readOnlyArray.add(readOnlyObj);
            }
        }
        responseObj.add("ReadOnly_Dashboards",readOnlyArray);
        return responseObj;
    }

    public JsonObject getActiveDashboardByOwnerId(String ownerId){
        Dashboard dashboard = dashboardRepository.findByOwnerAndIsVisibleAndIsActive(ownerId, true, true);
        JsonObject dashboardObj = new JsonObject();
        if (dashboard != null){
            dashboardObj.addProperty("id",dashboard.getId());
            dashboardObj.addProperty("name",dashboard.getName());
            dashboardObj.addProperty("createdBy",dashboard.getCreatedBy());
            dashboardObj.addProperty("owner",dashboard.getOwner());
            dashboardObj.addProperty("active",dashboard.isActive());
            dashboardObj.addProperty("createdTime",dashboard.getCreatedTime().toString());
        }else {
            dashboardObj.addProperty("message", "No active dashboard found.");
        }
        return dashboardObj;
    }

    public JsonObject getDashboardById(Long dashboardId){
        Dashboard dashboard = dashboardRepository.findByIdAndIsVisible(dashboardId, true);
        JsonObject dashboardObj = new JsonObject();
        dashboardObj.addProperty("id",dashboard.getId());
        dashboardObj.addProperty("name",dashboard.getName());
        dashboardObj.addProperty("createdBy",dashboard.getCreatedBy());
        dashboardObj.addProperty("owner",dashboard.getOwner());
        dashboardObj.addProperty("active",dashboard.isActive());
        dashboardObj.addProperty("createdTime",dashboard.getCreatedTime().toString());
        return dashboardObj;
    }

    public BasicResponse updateDashboardActiveStatus(Long id, String param){
        JsonParser jp = new JsonParser();
        JsonObject jsonObject = jp.parse(param).getAsJsonObject();
        BasicResponse updateResponse = new BasicResponse();
        final String UPDATE_FAILED_MESSAGE = "The dashboard is not active!";
        if (jsonObject.get("active").getAsBoolean()){
            dashboardRepository.setAllToFalse(jsonObject.get("owner").getAsString());
            dashboardRepository.setActiveToTrue(jsonObject.get("id").getAsLong());
            //define job and trigger for new saved dashboard
            schedulerService.unscheduleJob(id);
            schedulerService.scheduleJob(id);
            updateResponse.setStatus(Constants.STATUS_SUCCESSFUL);
        }else{
            updateResponse.setStatus(Constants.STATUS_FAILED);
            updateResponse.setMessage(UPDATE_FAILED_MESSAGE);
        }
        return updateResponse;
    }

    public JsonObject getDashboardConfig(Long dashboardId){
        Dashboard dashboard = dashboardRepository.findByIdAndIsVisible(dashboardId, true);
        JsonObject responseBody = new JsonObject();
        responseBody.addProperty("name", dashboard.getName());
        responseBody.addProperty("id", dashboard.getId());
        responseBody.addProperty("schedulerTime", decodeCronStr(dashboard.getCronStr()));
        responseBody.addProperty("op", 0);
        Gson gson = new Gson();
        JsonParser jp = new JsonParser();
        List<ProjectModule> projectList = new ArrayList<ProjectModule>();
        List<ProjectModule> moduleList = new ArrayList<ProjectModule>();
        List<ProjectModule> sonarTaskList = new ArrayList<ProjectModule>();
        JsonArray projectArray = new JsonArray();
        for (ProjectModule pm : dashboard.getVisibleProjectModuleList()){
            if (pm.getParentId() == null){
                projectList.add(pm);
            }else if(pm.getParentId() != null && !pm.isSonarTask()){
                moduleList.add(pm);
            }else {
                sonarTaskList.add(pm);
            }
        }
        for (ProjectModule project : projectList){
            JsonObject projectObj = wrapProjectModuleObj(project, sonarTaskList, "Project");
            JsonArray moduleArray = new JsonArray();
            for (ProjectModule module : moduleList){
                if (module.getParentId() == project.getId()){
                    JsonObject moduleObj = new JsonObject();
                    moduleObj = wrapProjectModuleObj(module, sonarTaskList, "Module");
                    moduleArray.add(moduleObj);
                }
            }
            projectObj.add("modules", moduleArray);
            projectArray.add(projectObj);
        }
        responseBody.add("modules", projectArray);

        if (dashboard.getJenkinsServerList() != null){
            for (JenkinsServer jenkinsServer : dashboard.getJenkinsServerList()){
                JsonObject jenkinsServerObj = new JsonObject();
                if (jenkinsServer != null){
                    jenkinsServerObj.addProperty("serverId", jenkinsServer.getId());
                    jenkinsServerObj.addProperty("op", 0);
                    jenkinsServerObj.addProperty("url", jenkinsServer.getUrl());
                    jenkinsServerObj.addProperty("userName", jenkinsServer.getUserName());
                    jenkinsServerObj.addProperty("password", jenkinsServer.getPassword());
                    jenkinsServerObj.addProperty("isPipelineJob", jenkinsServer.isPipeLineJob());
                    if (jenkinsServer.isMain()){
                        responseBody.add("jenkinsServer", jenkinsServerObj);
                    }else {
                        responseBody.add("jenkinsServerRef", jenkinsServerObj);
                    }
                }
            }
        }

        JsonObject sonarServerObj = new JsonObject();
        if (dashboard.getSonarServer() != null){
            sonarServerObj.addProperty("serverId", dashboard.getSonarServer().getId());
            sonarServerObj.addProperty("op", 0);
            sonarServerObj.addProperty("url", dashboard.getSonarServer().getUrl());
            sonarServerObj.addProperty("userName", dashboard.getSonarServer().getUserName());
            sonarServerObj.addProperty("password", dashboard.getSonarServer().getPassword());
            responseBody.add("sonarServer", sonarServerObj);
        }

        JsonObject bcpServerObj = new JsonObject();
        if (dashboard.getBcpServer() != null){
            bcpServerObj.addProperty("serverId", dashboard.getBcpServer().getId());
            bcpServerObj.addProperty("op", 0);
            bcpServerObj.addProperty("url", dashboard.getBcpServer().getUrl());
            bcpServerObj.addProperty("component",dashboard.getBcpServer().getComponent());
            JsonArray memberArray = new JsonArray();
            if (dashboard.getBcpServer().getProjectMemberList() != null){
                for (ProjectMember member : dashboard.getBcpServer().getProjectMemberList()){
                    memberArray.add(jp.parse(member.getUserId()));
                }
            }
            bcpServerObj.add("projectMembers", memberArray);
            responseBody.add("bcpServer", bcpServerObj);
        }
        return responseBody;
    }

    public JsonObject wrapProjectModuleObj(ProjectModule pm, List<ProjectModule> sonarTaskList, String type){
        JsonParser jp = new JsonParser();
        Gson gson = new Gson();
        JsonObject projectModuleObj = new JsonObject();

        projectModuleObj.addProperty("name", pm.getName());
        projectModuleObj.addProperty("type", type);
        projectModuleObj.addProperty("moduleId", pm.getId());
        projectModuleObj.addProperty("op", 0);
        projectModuleObj.addProperty("sonarKey", pm.getSonarKey());


        List<JenkinsJob> visibleJenkinsJobList = pm.getVisibleJenkinsJobList();
        for (JenkinsJob jenkinsJob : visibleJenkinsJobList){
            if (jenkinsJob != null){
                JsonObject jenkinsJobObj = new JsonObject();
                jenkinsJobObj.addProperty("name", jenkinsJob.getName());
                jenkinsJobObj.addProperty("jobId", jenkinsJob.getId());
                jenkinsJobObj.addProperty("op", 0);
                jenkinsJobObj.addProperty("isActive", jenkinsJob.isActive());
                jenkinsJobObj.addProperty("module", pm.getName());

                JsonObject rulesObj = new JsonObject();
                for (ParsingRule rule : jenkinsJob.getParsingRuleList()){
                    JsonObject ruleObj = new JsonObject();
//            ruleObj = jp.parse(gson.toJson(rule)).getAsJsonObject();
                    ruleObj.addProperty("ruleId", rule.getId());
                    ruleObj.addProperty("op", 0);
                    ruleObj.addProperty("startIdentifier", rule.getStartIdentifier());
                    ruleObj.addProperty("endIdentifier", rule.getEndIdentifier());
                    ruleObj.addProperty("reportFormat", rule.getReportFormat());
                    rulesObj.add(rule.getType(), ruleObj);

                }
                jenkinsJobObj.add("rules", rulesObj);

                if (jenkinsJob.getJenkinsServer() == null){
                    projectModuleObj.add("jenkinsJob", jenkinsJobObj);
                    projectModuleObj.add("jenkinsJobRef", jenkinsJobObj);
                }else {
                    if (jenkinsJob.getJenkinsServer().isMain()){
                        projectModuleObj.add("jenkinsJob", jenkinsJobObj);
                    }else {
                        projectModuleObj.add("jenkinsJobRef", jenkinsJobObj);
                    }
                }
            }
        }
        JsonArray sonarTaskArray = new JsonArray();
        for (ProjectModule sonarTask : sonarTaskList){
            if (sonarTask.getParentId() == pm.getId()){
                JsonObject sonarTaskObj = new JsonObject();
                sonarTaskObj.addProperty("name", sonarTask.getName());
                sonarTaskObj.addProperty("taskId", sonarTask.getId());
                sonarTaskObj.addProperty("op", 0);
                sonarTaskObj.addProperty("sonarKey", sonarTask.getSonarKey());
                sonarTaskArray.add(sonarTaskObj);
            }
        }
        projectModuleObj.add("sonarTasks", sonarTaskArray);

        return projectModuleObj;
    }

    public BasicResponse createDashboardConfig(String jsonBodyStr){
        BasicResponse response = new BasicResponse();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession(true);//true will create if necessary
        User sessionUser = (User)session.getAttribute(Constants.USER_SESSION_KEY);
        if (sessionUser == null){
            response.setStatus("Failed");
            response.setMessage("No user session found, please login again.");
            return response;
        }
        JsonParser jp = new JsonParser();
        JsonObject paraBodyObj = (JsonObject) jp.parse(jsonBodyStr);
        Dashboard dashboard = new Dashboard();
        String dashboardName = paraBodyObj.get("name").getAsString();
        if (!checkDashboardNameExisted(dashboardName, sessionUser.getSapId(), new Long(0))){
            dashboard.setName(dashboardName);
        }else {
            response.setStatus("Failed");
            response.setMessage("Dashboard name existed.");
            return response;
        }
        String schedulerStr = paraBodyObj.get("schedulerTime").getAsString();
        if (schedulerStr != null){
            dashboard.setCronStr(encodeCronStr(schedulerStr));
        }
        dashboard.setOwner(sessionUser.getSapId());
        dashboard.setCreatedBy(sessionUser.getUserName());
//        dashboard.setOwner("I309908");
//        dashboard.setCreatedBy("Siqi");
        dashboard.setActive(false);
        dashboard = dashboardRepository.save(dashboard);

        //save jenkins servers before saving jenkins job
        JsonObject jenkinsServerObj = paraBodyObj.getAsJsonObject("jenkinsServer");
        wrapAndSaveJenkinsServer(jenkinsServerObj, dashboard, true);

        JsonObject jenkinsServerRefObj = paraBodyObj.getAsJsonObject("jenkinsServerRef");
        wrapAndSaveJenkinsServer(jenkinsServerRefObj, dashboard, false);

        JsonArray projectArray = paraBodyObj.getAsJsonArray("modules");

        if (projectArray != null){
            for (int i = 0;i < projectArray.size();i++){
                JsonObject projectObj = (JsonObject) projectArray.get(i);
                wrapAndSaveProjectModule(projectObj, dashboard);

                JsonObject sonarServerObj = paraBodyObj.getAsJsonObject("sonarServer");
                wrapAndSaveSonarServer(sonarServerObj, dashboard);

                JsonObject bcpServerObj = paraBodyObj.getAsJsonObject("bcpServer");
                wrapAndSaveBCPServer(bcpServerObj, dashboard);

            }
            response.setStatus("Success");
            return response;
        }
        response.setStatus("Failed");
        response.setMessage("No content in parameter!");
        return response;
    }

    public BasicResponse updateDashboardConfig(String jsonBodyStr) throws Exception{
        BasicResponse response = new BasicResponse();
        JsonParser jp = new JsonParser();
        JsonObject paraBodyObj = (JsonObject) jp.parse(jsonBodyStr);
        // field op presents operation type, 0 for no change, 1 for new, 2 for update, 3 for delete
        if (paraBodyObj.get("op").getAsInt() == 0 || paraBodyObj.get("op").getAsInt() == 2){

            Dashboard dashboard = dashboardRepository.findOne(paraBodyObj.get("id").getAsLong());

            if (paraBodyObj.get("op").getAsInt() == 2){
                String dashboardName = paraBodyObj.get("name").getAsString();
                if (!checkDashboardNameExisted(dashboardName, dashboard.getOwner(), dashboard.getId())){
                    dashboard.setName(dashboardName);
                }else {
                    response.setStatus("Failed");
                    response.setMessage("Dashboard name existed.");
                    return response;
                }
//                dashboard.setName(paraBodyObj.get("name").getAsString());
//                dashboard.setOwner("I309908");
//                dashboard.setCreatedBy("Siqi");
                String schedulerStr = paraBodyObj.get("schedulerTime").getAsString();
                if (schedulerStr != null){
                    dashboard.setCronStr(encodeCronStr(schedulerStr));
                }
                dashboard = dashboardRepository.save(dashboard);
                //if this is dashboard is active, update its scheduler to run data synchronization job
                if (dashboard.isActive() && dashboard.isVisible()){
                    schedulerService.updateScheduler(dashboard.getId());
                }
            }

            JsonArray projectArray = paraBodyObj.getAsJsonArray("modules");
            for (int i = 0;i < projectArray.size();i++){
                JsonObject projectObj = (JsonObject) projectArray.get(i);
                wrapAndUpdateProjectModule(projectObj, dashboard);

                JsonObject jenkinsServerObj = paraBodyObj.getAsJsonObject("jenkinsServer");
                wrapAndUpdateJenkinsServer(jenkinsServerObj);

                JsonObject jenkinsServerRefObj = paraBodyObj.getAsJsonObject("jenkinsServerRef");
                wrapAndUpdateJenkinsServer(jenkinsServerRefObj);

                JsonObject sonarServerObj = paraBodyObj.getAsJsonObject("sonarServer");
                wrapAndUpdateSonarServer(sonarServerObj);

                JsonObject bcpServerObj = paraBodyObj.getAsJsonObject("bcpServer");
                wrapAndUpdateBCPServer(bcpServerObj);
            }
            response.setStatus("Success");
            return response;
        }else if (paraBodyObj.get("op").getAsInt() == 3){
            Dashboard dashboard = dashboardRepository.findOne(paraBodyObj.get("id").getAsLong());
            dashboard.setVisible(false);
            dashboardRepository.save(dashboard);
        }
        response.setStatus("Success");
        response.setMessage("There is no change!");
        return response;

    }

    public String encodeCronStr(String schedulerStr){
        String hourStr = schedulerStr.substring(0, 2);
        String minuteStr = schedulerStr.substring(2,4);
        StringBuffer customer_cron = new StringBuffer();
        customer_cron.append(Constants.default_cron_prefix).append(minuteStr).append(" ").
                append(hourStr).append(Constants.default_cron_suffix);
        return customer_cron.toString();
    }

    public String decodeCronStr(String cronStr){
        String hourStr = cronStr.substring(6,8);
        String minStr = cronStr.substring(3,5);
        return hourStr+minStr;
    }

    public boolean checkDashboardNameExisted(String name, String ownerId, Long dashboardId){
        Dashboard dashboard = dashboardRepository.findByNameAndOwnerAndIsVisible(name, ownerId, true);
        if (dashboard == null){
            return false;
        }else {
            if (dashboard.getId() == dashboardId){
                return false;
            }
            return true;
        }
    }
    public void wrapAndSaveProjectModule(JsonObject projectObj, Dashboard dashboard){
        ProjectModule project = new ProjectModule();
        project.setSonarTask(false);
        project.setName(projectObj.get("name").getAsString());
        project.setDashboard(dashboard);
        project.setSonarKey(projectObj.get("sonarKey").getAsString());
        project = projectModuleRepository.save(project);
        wrapAndSaveJenkinsJob(projectObj, project);
        wrapAndSaveSonarTask(projectObj, project, dashboard);


        JsonArray pmArray = projectObj.getAsJsonArray("modules");
        for (int i = 0;i < pmArray.size();i++){
            JsonObject moduleObj = (JsonObject) pmArray.get(i);
            ProjectModule module = new ProjectModule();
            module.setDashboard(dashboard);
            module.setName(moduleObj.get("name").getAsString());
            module.setParentId(project.getId());
            module.setSonarKey(moduleObj.get("sonarKey").getAsString());
            module.setSonarTask(false);
            module = projectModuleRepository.save(module);
            wrapAndSaveJenkinsJob(moduleObj, module);
            wrapAndSaveSonarTask(moduleObj, module, dashboard);
        }
    }

    public void wrapAndSaveSonarTask(JsonObject pmObj, ProjectModule pm, Dashboard dashboard){
        if(pmObj.getAsJsonArray("sonarTasks").size() > 0){
            for (int j = 0;j < pmObj.getAsJsonArray("sonarTasks").size();j++){
                JsonObject sonarTaskObj = (JsonObject) pmObj.getAsJsonArray("sonarTasks").get(j);
                ProjectModule sonarTask = new ProjectModule();
                sonarTask.setParentId(pm.getId());
                sonarTask.setSonarKey(sonarTaskObj.get("sonarKey").getAsString());
                sonarTask.setSonarTask(true);
                sonarTask.setName(sonarTaskObj.get("name").getAsString());
                sonarTask.setDashboard(dashboard);
                projectModuleRepository.save(sonarTask);
            }
        }
    }

    public void wrapAndSaveJenkinsJob(JsonObject pmObj, ProjectModule pm){
        List<JsonObject> jobObjList = new ArrayList<JsonObject>();
        if (pmObj.get("jenkinsJob") != null && !pmObj.get("jenkinsJob").isJsonNull()){
            jobObjList.add((JsonObject) pmObj.get("jenkinsJob"));
        }
        if (pmObj.get("jenkinsJobRef") != null && !pmObj.get("jenkinsJobRef").isJsonNull()){
            jobObjList.add((JsonObject) pmObj.get("jenkinsJobRef"));
        }
        for (int i = 0;i < jobObjList.size(); i++){
            if (jobObjList.get(i) != null && jobObjList.get(i).entrySet().size() > 0){
                JenkinsJob jenkinsJob = new JenkinsJob();
                jenkinsJob.setName(jobObjList.get(i).get("name").getAsString());
                jenkinsJob.setActive(jobObjList.get(i).get("isActive").getAsBoolean());
                jenkinsJob.setProjectModule(pm);
                if (i == 0){
                    jenkinsJob.setJenkinsServer(jenkinsServerRepository.findByDashboard_idAndIsMain(pm.getDashboard().getId(), true));
                }else {
                    jenkinsJob.setJenkinsServer(jenkinsServerRepository.findByDashboard_idAndIsMain(pm.getDashboard().getId(), false));
                }
                Gson gson = new Gson();
                List<ParsingRule> ruleList = new ArrayList<ParsingRule>();
                Set<Map.Entry<String, JsonElement>> ruleSet = jobObjList.get(i).get("rules").getAsJsonObject().entrySet();
                for (Map.Entry<String, JsonElement> ruleEntry : ruleSet){
                    ParsingRule rule = gson.fromJson(ruleEntry.getValue(), ParsingRule.class);
                    rule.setJenkinsJob(jenkinsJob);
                    rule.setType(ruleEntry.getKey());
                    ruleList.add(rule);
                }
                jenkinsJob.setParsingRuleList(ruleList);
                jenkinsJobRepository.save(jenkinsJob);
            }
        }
    }


    public JsonObject wrapEmptyJenkinsJobObj(){
        JsonParser jp = new JsonParser();
        List<ParsingRule> parsingRules = new ArrayList<ParsingRule>();
        JsonObject jenkinsJobObj = new JsonObject();
        jenkinsJobObj.addProperty("name", "");
        jenkinsJobObj.addProperty("isActive", false);
        jenkinsJobObj.add("rules", jp.parse("{}"));

        return jenkinsJobObj;
    }

    public void wrapAndSaveJenkinsServer(JsonObject jenkinsServerObj, Dashboard dashboard, boolean isMain){
        if (jenkinsServerObj != null && !jenkinsServerObj.isJsonNull() && jenkinsServerObj.entrySet().size() > 0){
            Gson gson = new Gson();
            JenkinsServer jenkinsServer = gson.fromJson(jenkinsServerObj, JenkinsServer.class);
            if (jenkinsServer.getUrl().endsWith("/")){
                jenkinsServer.setUrl(jenkinsServer.getUrl().substring(0, jenkinsServer.getUrl().length()-1));
            }
            jenkinsServer.setDashboard(dashboard);
            jenkinsServer.setMain(isMain);
            jenkinsServerRepository.save(jenkinsServer);
        }
    }

    public void wrapAndSaveSonarServer(JsonObject sonarObj, Dashboard dashboard){
        if (sonarObj != null && !sonarObj.isJsonNull() && sonarObj.entrySet().size() > 0){
            Gson gson = new Gson();
            SonarServer sonarServer = gson.fromJson(sonarObj, SonarServer.class);
            if (sonarServer.getUrl().endsWith("/")){
                sonarServer.setUrl(sonarServer.getUrl().substring(0, sonarServer.getUrl().length()-1));
            }
            sonarServer.setDashboard(dashboard);
            sonarServerRepository.save(sonarServer);
        }
    }

    public void wrapAndSaveBCPServer(JsonObject bcpObj, Dashboard dashboard){
        if (bcpObj != null && !bcpObj.isJsonNull() && bcpObj.entrySet().size() > 0){
            Gson gson = new Gson();
            BCPServer bcpServer = new BCPServer();
            bcpServer.setUrl("https://support.wdf.sap.corp");
            if (bcpObj.get("component") == null){
                bcpServer.setComponent(null);
            }else {
                bcpServer.setComponent(bcpObj.get("component").getAsString());
            }
            bcpServer.setDashboard(dashboard);
            JsonArray memberArray = bcpObj.getAsJsonArray("projectMembers");
            List<ProjectMember> memberList = new ArrayList<ProjectMember>();
            if (memberArray != null && memberArray.size() > 0){
                for (int i = 0;i < memberArray.size();i++){
                    ProjectMember member = new ProjectMember();
                    member.setBcpServer(bcpServer);
                    member.setUserId(memberArray.get(i).getAsString());
                    memberList.add(member);
                }
            }
            bcpServer.setProjectMemberList(memberList);
            bcpServerRepository.save(bcpServer);
        }
    }

    public void wrapAndUpdateProjectModule(JsonObject projectObj, Dashboard dashboard){
        if (projectObj.get("op").getAsInt() == 1){
            wrapAndSaveProjectModule(projectObj, dashboard);
        }else if(projectObj.get("op").getAsInt() == 0 || projectObj.get("op").getAsInt() == 2){
            ProjectModule project = projectModuleRepository.findOne(projectObj.get("moduleId").getAsLong());
            if (projectObj.get("op").getAsInt() == 2){
                project.setSonarTask(false);
                project.setName(projectObj.get("name").getAsString());
                project.setSonarKey(projectObj.get("sonarKey").getAsString());
                project = projectModuleRepository.save(project);
            }
            wrapAndUpdateJenkinsJob(projectObj, project);
            wrapAndUpdateSonarTask(projectObj, project, dashboard);
            JsonArray pmArray = projectObj.getAsJsonArray("modules");
            for (int i = 0;i < pmArray.size();i++){
                JsonObject moduleObj = (JsonObject) pmArray.get(i);
                if (moduleObj.get("op").getAsInt() == 1){
                    ProjectModule module = new ProjectModule();
                    module.setDashboard(dashboard);
                    module.setName(moduleObj.get("name").getAsString());
                    module.setParentId(project.getId());
                    module.setSonarKey(moduleObj.get("sonarKey").getAsString());
                    module.setSonarTask(false);
                    module = projectModuleRepository.save(module);
                    wrapAndSaveJenkinsJob(moduleObj, module);
                    wrapAndSaveSonarTask(moduleObj, module, dashboard);
                }else if(moduleObj.get("op").getAsInt() == 0 || moduleObj.get("op").getAsInt() == 2){
                    ProjectModule module = projectModuleRepository.findOne(moduleObj.get("moduleId").getAsLong());
                    if (moduleObj.get("op").getAsInt() == 2){
                        module.setName(moduleObj.get("name").getAsString());
                        module.setParentId(project.getId());
                        module.setSonarKey(moduleObj.get("sonarKey").getAsString());
                        module.setSonarTask(false);
                        module = projectModuleRepository.save(module);
                    }
                    wrapAndUpdateJenkinsJob(moduleObj, module);
                    wrapAndUpdateSonarTask(moduleObj, module, dashboard);
                }else if(moduleObj.get("op").getAsInt() == 3){
                    ProjectModule module = projectModuleRepository.findOne(moduleObj.get("moduleId").getAsLong());
                    module.setVisible(false);
                    projectModuleRepository.save(module);
                }
            }
        }else if(projectObj.get("op").getAsInt() == 3){
            ProjectModule project = projectModuleRepository.findOne(projectObj.get("moduleId").getAsLong());
            project.setVisible(false);
            projectModuleRepository.save(project);
        }
    }


    public void wrapAndUpdateBCPServer(JsonObject bcpObj){
        if (bcpObj != null && bcpObj.entrySet().size() > 0 && bcpObj.get("serverId") != null){
            if (bcpObj.get("op").getAsInt() == 2){
                BCPServer bcpServer = bcpServerRepository.findOne(bcpObj.get("serverId").getAsLong());
//                bcpServer.setUrl(bcpObj.get("url").getAsString());
                if (bcpObj.get("component") == null || bcpObj.get("component").isJsonNull()){
                    bcpServer.setComponent(null);
                }else {
                    bcpServer.setComponent(bcpObj.get("component").getAsString());
                }
                JsonArray memberArray = bcpObj.getAsJsonArray("projectMembers");
                bcpServer.getProjectMemberList().clear();
                projectMemberRepository.deleteByBcpServerId(bcpServer.getId());
                if (memberArray != null && memberArray.size() > 0){
                    for (int i = 0;i < memberArray.size();i++){
                        ProjectMember member = new ProjectMember();
                        member.setBcpServer(bcpServer);
                        member.setUserId(memberArray.get(i).getAsString());
                        bcpServer.getProjectMemberList().add(member);
                    }
                }
                bcpServerRepository.save(bcpServer);
            }
        }
    }

    public void wrapAndUpdateJenkinsJob(JsonObject pmObj, ProjectModule pm){
        List<JsonObject> jobObjList = new ArrayList<JsonObject>();
        if (pmObj.get("jenkinsJob") != null && !pmObj.get("jenkinsJob").isJsonNull()){
            jobObjList.add((JsonObject) pmObj.get("jenkinsJob"));
        }
        if (pmObj.get("jenkinsJobRef") != null && !pmObj.get("jenkinsJobRef").isJsonNull()){
            jobObjList.add((JsonObject) pmObj.get("jenkinsJobRef"));
        }

        for (JsonObject jobObj : jobObjList){
            if (jobObj != null && jobObj.entrySet().size() > 0){
                if (jobObj.get("op").getAsInt() == 1){
                    wrapAndSaveJenkinsJob(pmObj, pm);
                }else if(jobObj.get("op").getAsInt() == 0 || jobObj.get("op").getAsInt() == 2){
                    JenkinsJob jenkinsJob = jenkinsJobRepository.findOne(jobObj.get("jobId").getAsLong());
                    if (jobObj.get("op").getAsInt() == 2){
                        jenkinsJob.setName(jobObj.get("name").getAsString());
                        jenkinsJob.setActive(jobObj.get("isActive").getAsBoolean());
                        jenkinsJobRepository.save(jenkinsJob);
                    }
                    Set<Map.Entry<String, JsonElement>> ruleSet = jobObj.get("rules").getAsJsonObject().entrySet();
                    wrapAndUpdateParsingRule(ruleSet);

                }else if (jobObj.get("op").getAsInt() == 3){
                    JenkinsJob jenkinsJob = jenkinsJobRepository.findOne(jobObj.get("jobId").getAsLong());
                    jenkinsJob.setVisible(false);
                    jenkinsJobRepository.save(jenkinsJob);
                }
            }
        }
    }

    public void wrapAndUpdateParsingRule(Set<Map.Entry<String, JsonElement>> ruleSet){
        for (Map.Entry<String, JsonElement> ruleEntry : ruleSet){
            JsonObject ruleObj = ruleEntry.getValue().getAsJsonObject();
            if (ruleObj.get("op").getAsInt() == 2){
                ParsingRule rule = parsingRuleRepository.findOne(ruleObj.get("ruleId").getAsLong());
                rule.setStartIdentifier(ruleObj.get("startIdentifier").getAsString());
                rule.setEndIdentifier(ruleObj.get("endIdentifier").getAsString());
                rule.setReportFormat(ruleObj.get("reportFormat").getAsString());
                rule.setType(ruleEntry.getKey());
                parsingRuleRepository.save(rule);
            }
        }
    }

    public void wrapAndUpdateSonarTask(JsonObject pmObj, ProjectModule pm, Dashboard dashboard){
        if(pmObj != null && pmObj.getAsJsonArray("sonarTasks").size() > 0){
            for (int j = 0;j < pmObj.getAsJsonArray("sonarTasks").size();j++){
                JsonObject sonarTaskObj = (JsonObject) pmObj.getAsJsonArray("sonarTasks").get(j);
                if (sonarTaskObj.get("op").getAsInt() == 1){
                    ProjectModule sonarTask = new ProjectModule();
                    sonarTask.setParentId(pm.getId());
                    sonarTask.setSonarKey(sonarTaskObj.get("sonarKey").getAsString());
                    sonarTask.setSonarTask(true);
                    sonarTask.setName(sonarTaskObj.get("name").getAsString());
                    sonarTask.setDashboard(dashboard);
                    projectModuleRepository.save(sonarTask);
                }else if (sonarTaskObj.get("op").getAsInt() == 2){
                    ProjectModule sonarTask = projectModuleRepository.findOne(sonarTaskObj.get("taskId").getAsLong());
                    sonarTask.setParentId(pm.getId());
                    sonarTask.setSonarKey(sonarTaskObj.get("sonarKey").getAsString());
                    sonarTask.setSonarTask(true);
                    sonarTask.setName(sonarTaskObj.get("name").getAsString());
                    projectModuleRepository.save(sonarTask);
                }else if (sonarTaskObj.get("op").getAsInt() == 3){
                    ProjectModule sonarTask = projectModuleRepository.findOne(sonarTaskObj.get("taskId").getAsLong());
                    sonarTask.setVisible(false);
                    projectModuleRepository.save(sonarTask);
                }
            }
        }
    }

    public void wrapAndUpdateJenkinsServer(JsonObject jenkinsServerObj){
        if (jenkinsServerObj != null && !jenkinsServerObj.isJsonNull() && jenkinsServerObj.entrySet().size() > 0 && jenkinsServerObj.get("serverId") != null){
            if (jenkinsServerObj.get("op").getAsInt() == 2){
                JenkinsServer jenkinsServer = jenkinsServerRepository.findOne(jenkinsServerObj.get("serverId").getAsLong());
                jenkinsServer.setUserName(jenkinsServerObj.get("userName").getAsString());
                jenkinsServer.setPassword(jenkinsServerObj.get("password").getAsString());
                jenkinsServer.setUrl(jenkinsServerObj.get("url").getAsString());
                if (jenkinsServerObj.get("url").getAsString().endsWith("/")){
                    jenkinsServer.setUrl(jenkinsServerObj.get("url").getAsString().substring(0, jenkinsServer.getUrl().length()-1));
                }
//                jenkinsServer.setPipeLineJob(jenkinsServerObj.get("isPipelineJob").getAsBoolean());
                jenkinsServerRepository.save(jenkinsServer);
            }
        }
    }
    public void wrapAndUpdateSonarServer(JsonObject sonarObj){
        if (sonarObj != null && !sonarObj.isJsonNull() && sonarObj.entrySet().size() > 0 && sonarObj.get("serverId") != null){
            if (sonarObj.get("op").getAsInt() == 2){
                SonarServer sonarServer = sonarServerRepository.findOne(sonarObj.get("serverId").getAsLong());
                sonarServer.setUserName(sonarObj.get("userName").getAsString());
                sonarServer.setPassword(sonarObj.get("password").getAsString());
                sonarServer.setUrl(sonarObj.get("url").getAsString());
                sonarServerRepository.save(sonarServer);
            }
        }
    }

    public BasicResponse logicDeleteDashboard(Long dashboardId){
        Dashboard dashboard = dashboardRepository.findByIdAndIsVisible(dashboardId, true);
        BasicResponse deleteResponse = new BasicResponse();
        if (dashboard != null){
            dashboard.setVisible(false);
            deleteResponse.setStatus(Constants.STATUS_SUCCESSFUL);
        }else {
            final String DELETE_FAILED_MESSAGE = "No dashboard existed with id " + dashboardId;
            deleteResponse.setStatus(Constants.STATUS_FAILED);
            deleteResponse.setMessage(DELETE_FAILED_MESSAGE);
        }
        return deleteResponse;
    }

    public void logicDeleteDashboardConfigItem(String params){
        JsonParser jp = new JsonParser();
        JsonArray paramsArray = jp.parse(params).getAsJsonArray();
        for (int i = 0; i < paramsArray.size(); i++){
            JsonObject itemObj = paramsArray.get(i).getAsJsonObject();
            if (itemObj.get("type").getAsString().equalsIgnoreCase("module")){
                ProjectModule module = projectModuleRepository.findOne(itemObj.get("id").getAsLong());
                module.setVisible(false);
                projectModuleRepository.save(module);
            }else if(itemObj.get("type").getAsString().equalsIgnoreCase("jenkinsJob")){
                JenkinsJob jenkinsJob = jenkinsJobRepository.findOne(itemObj.get("id").getAsLong());
                jenkinsJob.setVisible(false);
                jenkinsJobRepository.save(jenkinsJob);
            }else if (itemObj.get("type").getAsString().equalsIgnoreCase("sonarTask")){
                ProjectModule sonarTask = projectModuleRepository.findOne(itemObj.get("id").getAsLong());
                sonarTask.setVisible(false);
                projectModuleRepository.save(sonarTask);
            }
        }
    }

}
