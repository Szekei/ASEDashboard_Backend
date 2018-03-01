package com.mfg.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mfg.Entity.*;
import com.mfg.Exception.NoDataFoundException;
import com.mfg.Exception.ParseErrorException;
import com.mfg.Model.CodeDebt;
import com.mfg.Model.CodeDebtResponse;
import com.mfg.Model.TechIssue;
import com.mfg.Model.TechIssueResponse;
import com.mfg.Repository.*;
import com.mfg.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by I309908 on 4/18/2017.
 */
@Component
public class CodeQualityService {

    @Autowired
    private ProjectModuleRepository projectModuleRepository;

    @Autowired
    private SonarServerRepository sonarServerRepository;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private CodeQualityRepository codeQualityRepository;

    @Autowired
    private UserLogService userLogService;

    @Autowired WebUtils webUtils;

    String CODE_DEBT = "CodeDebt";
    String TECH_ISSUE = "TechIssue";

    final Logger g_logger = LoggerFactory.getLogger(this.getClass());

    public CodeDebt[] getCodeDebtFromAPI(String url, String userName, String password) throws Exception{
        try {
            String response = webUtils.httpGetMethod(url, userName, password);
            Gson gson = new Gson();
            CodeDebt[] codeDebts = gson.fromJson(response, CodeDebt[].class);
            return codeDebts;
        }catch (IllegalStateException e){
            throw new ParseErrorException(String.format("Cannot parse codeDebt data from Sonar API response. Request Url: %s", url), e);
        }catch (JsonSyntaxException e){
            throw new ParseErrorException(String.format("Cannot parse codeDebt data from Sonar API response. Request Url: %s", url), e);
        }
    }

    public TechIssue getTechIssueFromAPI(String url, String userName, String password) throws Exception{
        try {
            String response = webUtils.httpGetMethod(url, userName, password);
            Gson gson = new Gson();
            TechIssue techIssue = gson.fromJson(response, TechIssue.class);
            return techIssue;
        } catch (IllegalStateException e){
            throw new ParseErrorException(String.format("Cannot parse codeDebt data from Sonar API response. Request Url: %s", url), e);
        } catch (JsonSyntaxException e){
            throw new ParseErrorException(String.format("Cannot parse codeDebt data from Sonar API response. Request Url: %s", url), e);
        }
    }

    public void saveCodeDebt(Long dashboardId, String type){
        Version latestVersion =  versionRepository.findTop1ByDashboardIdByOrderByIdDesc(dashboardId);
        final Logger logger = LoggerFactory.getLogger(Constants.LOGGER_NAME_PREFIX+dashboardId+Constants.LOGGER_NAME_SUFFIX+latestVersion.getId());
        try {
            String url_suffix = "/api/resources?metrics=sqale_rating,sqale_debt_ratio&format=json&resource=";
            Dashboard dashboard = dashboardRepository.findById(dashboardId);
            List<ProjectModule> pmList = getProjectOrModuleByDashboardId(dashboard, type);

            if(pmList != null && !pmList.isEmpty()){
                SonarServer sonarServer = dashboard.getSonarServer();
                if (sonarServer == null){
                    throw new NoDataFoundException("No Sonar server configuration.");
                }
                String sonar_Base_url = sonarServer.getUrl();
                String sonar_password = RSAUtils.decryptStringFromBase64(sonarServer.getPassword());
//                String sonar_password = sonarServer.getPassword();
                List<CodeDebt> codeDebtList = new ArrayList<CodeDebt>();
                for(ProjectModule pm : pmList){
                    if (pm.getSonarKey().isEmpty() || pm.getSonarKey() == null){
                        List<ProjectModule> sonarTaskList = projectModuleRepository.findByParentIdAndIsSonarTaskAndIsVisible(pm.getId(), true, true);
                        for (ProjectModule sonarTask : sonarTaskList){
                            String url = sonar_Base_url + url_suffix + sonarTask.getSonarKey();
                            CodeDebt[] codeDebtArray = getCodeDebtFromAPI(url, sonarServer.getUserName(), sonar_password);
                            if (codeDebtArray != null && codeDebtArray.length > 0){
                                codeDebtList.add(codeDebtArray[0]);
                            }
                        }
                        wrapAndInsertCodeDebt(latestVersion, dashboard, codeDebtList, pm);

                    }else {
                        String url = sonar_Base_url + url_suffix + pm.getSonarKey();
                        CodeDebt[] codeDebtArray = getCodeDebtFromAPI(url, sonarServer.getUserName(), sonar_password);
                        if (codeDebtArray != null && codeDebtArray.length > 0){
                            codeDebtList.add(codeDebtArray[0]);
                        }
                        wrapAndInsertCodeDebt(latestVersion, dashboard, codeDebtList, pm);
                    }
                }
            }
        }catch (Exception e){
            userLogService.processException(e, logger, dashboardId, latestVersion, type, "codeDebt");
        }
    }

    public void saveTechIssue(Long dashboardId, String type){

        Version latestVersion =  versionRepository.findTop1ByDashboardIdByOrderByIdDesc(dashboardId);
        final Logger logger = LoggerFactory.getLogger(Constants.LOGGER_NAME_PREFIX+dashboardId+Constants.LOGGER_NAME_SUFFIX+latestVersion.getId());

        try {
            String url_suffix = "/api/issues/search?statuses=OPEN,REOPENED&componentRoots=";

            Dashboard dashboard = dashboardRepository.findById(dashboardId);
            List<ProjectModule> pmList = getProjectOrModuleByDashboardId(dashboard, type);


            if(pmList != null && !pmList.isEmpty()){
                List<TechIssue> techIssueList = new ArrayList<TechIssue>();
                SonarServer sonarServer = dashboard.getSonarServer();
                if (sonarServer == null){
                    throw new NoDataFoundException("No Sonar server configuration.");
                }
                String sonar_Base_url = sonarServer.getUrl();
                String sonar_password = RSAUtils.decryptStringFromBase64(sonarServer.getPassword());
//                String sonar_password = sonarServer.getPassword();
                for(ProjectModule pm : pmList){
                    if (pm.getSonarKey().isEmpty() || pm.getSonarKey() == null){
                        List<ProjectModule> sonarTaskList = projectModuleRepository.findByParentIdAndIsSonarTaskAndIsVisible(pm.getId(), true, true);
                        for (ProjectModule sonarTask : sonarTaskList){
                            String url = sonar_Base_url + url_suffix + sonarTask.getSonarKey();
                            TechIssue techIssue = getTechIssueFromAPI(url, sonarServer.getUserName(), sonar_password);
                            techIssueList.add(techIssue);
                        }
                        wrapAndInsertTechIssue(latestVersion, dashboard, techIssueList, pm);
                    }else{
                        String url = sonar_Base_url + url_suffix + pm.getSonarKey();
                        TechIssue techIssue = getTechIssueFromAPI(url, sonarServer.getUserName(), sonar_password);
                        techIssueList.add(techIssue);
                        wrapAndInsertTechIssue(latestVersion, dashboard, techIssueList, pm);
                    }
                }
            }
        }catch (Exception e){
            userLogService.processException(e, logger, dashboardId, latestVersion, type, "techIssue");
        }

    }

    public void wrapAndInsertCodeDebt(Version latestVersion, Dashboard dashboard, List<CodeDebt> codeDebtList, ProjectModule pm){
        CodeQuality codeQuality = new CodeQuality();

        if(codeDebtList == null || codeDebtList.isEmpty()){
            return ;
        }

        CodeQuality mergedCodeDebt = mergeCodeDebts(codeDebtList, dashboard.getId());

        codeQuality.setVersion(latestVersion);
        codeQuality.setCreatedBy(dashboard.getOwner());
        codeQuality.setCount(mergedCodeDebt.getCount());
        codeQuality.setMaintainability(mergedCodeDebt.getMaintainability());
        codeQuality.setType(CODE_DEBT);
        codeQuality.setProjectModule(pm);

        codeQualityRepository.save(codeQuality);
    }

    public void wrapAndInsertTechIssue(Version latestVersion, Dashboard dashboard, List<TechIssue> techIssueList, ProjectModule pm){

        if (techIssueList.isEmpty()){
            return;
        }
        final String[] severities = {"BLOCKER", "CRITICAL", "MAJOR"};

        //index 0 for blocker, 1 for critical, 2 for major
        int[] issue_number = new int[3];
        for (TechIssue techIssue : techIssueList){
            if (techIssue.issues == null){
                continue;
            }
            for (int i = 0; i < techIssue.issues.length;i++){
                if (techIssue.issues[i].severity.equalsIgnoreCase(severities[0]))
                    issue_number[0]++;
                else if (techIssue.issues[i].severity.equalsIgnoreCase(severities[1]))
                    issue_number[1]++;
                else if (techIssue.issues[i].severity.equalsIgnoreCase(severities[2]))
                    issue_number[2]++;
            }
        }
        for (int j = 0; j < 3;j++){
            CodeQuality codeQuality = new CodeQuality();
            codeQuality.setType(TECH_ISSUE);
            codeQuality.setVersion(latestVersion);
            codeQuality.setCount(issue_number[j]);
            codeQuality.setPriority(severities[j]);
            codeQuality.setProjectModule(pm);
            codeQuality.setCreatedBy(dashboard.getOwner());

            codeQualityRepository.save(codeQuality);
        }

    }

    public List<ProjectModule> getProjectOrModuleByDashboardId(Dashboard dashboard, String type){
        List<ProjectModule> pmList = null;
        if (type.equalsIgnoreCase("Project")){
            pmList = projectModuleRepository.findProjectByDashId(dashboard);
        }else{
            pmList = projectModuleRepository.findModuleByDashId(dashboard);
        }
        return pmList;
    }

    public CodeQuality mergeCodeDebts(List<CodeDebt> codeDebtList, Long dashboardId){
        float max_value = 0;
        String maintainability = "A";
        float totalDebt = 0;
        for (CodeDebt codeDebt : codeDebtList){
            try {
                if (codeDebt.msr != null && codeDebt.msr.length == 2){
                    if (codeDebt.msr[0].key.equalsIgnoreCase("sqale_rating")){
                        if (codeDebt.msr[0].val > max_value){
                            max_value = codeDebt.msr[0].val;
                            maintainability = codeDebt.msr[0].frmt_val;
                        }
                        totalDebt +=  codeDebt.msr[1].val;
                    }else if (codeDebt.msr[0].key.equalsIgnoreCase("sqale_debt_ratio")){
                        if (codeDebt.msr[1].val > max_value){
                            max_value = codeDebt.msr[1].val;
                            maintainability = codeDebt.msr[1].frmt_val;
                        }
                        totalDebt +=  codeDebt.msr[0].val;
                    }
                }else {
                    String message = "Codedebt data from sonar api with key [" + codeDebt.key + "] is incomplete";
//                    userLogService.processException(g_logger, dashboardId, "Codedebt", message);
                    throw new NoDataFoundException(message);
                }
            }catch (Exception e){
                userLogService.processException(g_logger, dashboardId, "Codedebt", e.getMessage());
            }
        }
        float debtRatio = totalDebt/codeDebtList.size();
        CodeQuality mergedCodeDebt = new CodeQuality();
        mergedCodeDebt.setMaintainability(maintainability);
        mergedCodeDebt.setCount(debtRatio);
        return mergedCodeDebt;
    }

    public String wrapCodeDebtResponseObj(Dashboard dashboard, List dataList){
        JsonObject responseObj = new JsonObject();
        Gson gson = new Gson();
        if (dashboard.getSonarServer() != null && dashboard.getSonarServer().getUrl() != null){
            responseObj.addProperty("url", dashboard.getSonarServer().getUrl());
        }else {
            responseObj.addProperty("url", "");
        }
        responseObj.add("data",gson.toJsonTree(dataList));
        return gson.toJson(responseObj);
    }

    public String wrapTechIssueResponseObj(Dashboard dashboard, List dataList, int total){
        JsonObject responseObj = new JsonObject();
        Gson gson = new Gson();
        if (dashboard.getSonarServer() != null && dashboard.getSonarServer().getUrl() != null){
            responseObj.addProperty("url", dashboard.getSonarServer().getUrl());
            responseObj.addProperty("count", total);
        }else {
            responseObj.addProperty("url", "");
        }
        responseObj.add("data",gson.toJsonTree(dataList));
        return gson.toJson(responseObj);
    }

    public String getCodeDebtRecords(Long dashboardId, String type){
        Dashboard dashboard = dashboardRepository.findById(dashboardId);
        List<ProjectModule> pmList = getProjectOrModuleByDashboardId(dashboard, type);
        List<CodeDebtResponse> cdrList = new ArrayList<CodeDebtResponse>();
        for (ProjectModule pm : pmList){
            boolean isLatest = true;
            Version latestVersion = versionRepository.findTop1ByDashboardIdByOrderByIdDesc(dashboardId);
            if (latestVersion == null){
                return wrapCodeDebtResponseObj(dashboard, cdrList);
            }
            Long vid = latestVersion.getId();
            Long evid = versionRepository.findTop1ByDashboardIdByOrderByIdAsc(dashboardId).getId();
            List<CodeQuality> codeDebts = codeQualityRepository.findByTypeAndVersion_idAndProjectModule_id(Constants.codeDebt, vid, pm.getId());
            while(codeDebts.isEmpty() && vid > evid){
                isLatest = false;
                vid--;
                codeDebts = codeQualityRepository.findByTypeAndVersion_idAndProjectModule_id(Constants.codeDebt, vid, pm.getId());
            }
            CodeDebtResponse cdtResponse = new CodeDebtResponse();
            if (codeDebts != null && !codeDebts.isEmpty()){
                cdtResponse.setModuleName(pm.getName());
                cdtResponse.setDebtRatio(codeDebts.get(0).getCount()+"%");
                cdtResponse.setMaintainability(codeDebts.get(0).getMaintainability());
                cdtResponse.setLatest(isLatest);
                cdtResponse.setSaveAt(codeDebts.get(0).getCreatedTime().toString());
                cdrList.add(cdtResponse);
            }
        }
        return wrapCodeDebtResponseObj(dashboard, cdrList);
    }

    public String getTechIssueRecords(Long dashboardId, String type){
        Dashboard dashboard = dashboardRepository.findById(dashboardId);
        List<ProjectModule> pmList = getProjectOrModuleByDashboardId(dashboard, type);
        List<TechIssueResponse> tirList = new ArrayList<TechIssueResponse>();
        JsonObject responseBody = new JsonObject();
        int total = 0;
        Gson gson = new Gson();
        for (ProjectModule pm : pmList){
            boolean isLatest = true;
            Version latestVersion = versionRepository.findTop1ByDashboardIdByOrderByIdDesc(dashboardId);
            if (latestVersion == null){
                return wrapTechIssueResponseObj(dashboard, tirList, total);
            }
            Long vid = latestVersion.getId();
            List<CodeQuality> techIssues = codeQualityRepository.findByTypeAndVersion_idAndProjectModule_id(Constants.techIssue, vid, pm.getId());
            while(techIssues.isEmpty() && vid > 0){
                isLatest = false;
                vid--;
                techIssues = codeQualityRepository.findByTypeAndVersion_idAndProjectModule_id(Constants.techIssue, vid, pm.getId());
            }
            if (techIssues != null){
                for (CodeQuality techIssue : techIssues){
                    TechIssueResponse tir = new TechIssueResponse();
                    tir.setModuleName(pm.getName());
                    tir.setPriority(techIssue.getPriority());
                    tir.setIssues((int)techIssue.getCount());
                    total += (int)techIssue.getCount();
                    tir.setLatest(isLatest);
                    tir.setSaveAt(techIssue.getCreatedTime().toString());
                    tirList.add(tir);
                }
            }
        }
        return wrapTechIssueResponseObj(dashboard, tirList, total);
    }

}
