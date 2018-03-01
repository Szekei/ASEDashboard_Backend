package com.mfg.Service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mfg.Entity.*;
import com.mfg.Exception.ParseErrorException;
import com.mfg.Model.JenkinsStatus;
import com.mfg.Repository.DashboardRepository;
import com.mfg.Repository.JenkinsJobStatusRepository;
import com.mfg.Repository.ProjectModuleRepository;
import com.mfg.Repository.VersionRepository;
import com.mfg.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by I309908 on 5/4/2017.
 */
@Component
public class JenkinsJobStatusService {

    @Autowired
    private ProjectModuleRepository projectModuleRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private JenkinsJobStatusRepository jenkinsJobStatusRepository;

    @Autowired
    private WebUtils webUtils;

    @Autowired
    private UserLogService userLogService;

    public void saveJenkinsJobStatus(Long dashboardId, String type){

        Version latestVersion =  versionRepository.findTop1ByDashboardIdByOrderByIdDesc(dashboardId);
        final Logger logger = LoggerFactory.getLogger(Constants.LOGGER_NAME_PREFIX+dashboardId+Constants.LOGGER_NAME_SUFFIX+latestVersion.getId());

        try {
            Dashboard dashboard = dashboardRepository.findById(dashboardId);
            List<ProjectModule> pmList = getProjectOrModuleByDashboardId(dashboard, type);
//            List<ProjectModule> active_jenkins_module_list = new ArrayList<ProjectModule>();
            String url_suffix = "/api/json";
            String url_middle = "/job/";

            for (ProjectModule jenkins_module : pmList){
                for (JenkinsJob jenkinsJob : jenkins_module.getVisibleJenkinsJobList()){
                    if(jenkinsJob.isActive() && jenkinsJob.isVisible()){
                        JenkinsServer jenkinsServer = jenkinsJob.getJenkinsServer();
                        String url_base = jenkinsServer.getUrl();
                        String jenkins_password = RSAUtils.decryptStringFromBase64(jenkinsServer.getPassword());
                        StringBuffer url_buffer = new StringBuffer();
                        url_buffer.append(url_base).append(url_middle).append(jenkinsJob.getName()).append(url_suffix);
                        JenkinsStatus jenkinsStatus = getJenkinsJobStatusFromApi(url_buffer.toString(), jenkinsServer.getUserName(), jenkins_password);
//                    JenkinsStatus jenkinsStatus = (JenkinsStatus) webUtils.getDataFromApiByResttemplate(url_buffer.toString(), jenkinsServer.getUserName(), jenkinsServer.getPassword(), Class.forName("com.mfg.Model.JenkinsStatus"));
                        JenkinsJobStatus jenkinsJobStatus = new JenkinsJobStatus();
                        jenkinsJobStatus.setJenkinsJob(jenkinsJob);
                        jenkinsJobStatus.setStatus(jenkinsStatus.getColor());
                        jenkinsJobStatus.setUrl(jenkinsStatus.getUrl());
                        jenkinsJobStatus.setVersion(latestVersion);
                        jenkinsJobStatusRepository.save(jenkinsJobStatus);
                    }
                }
            }
        }catch (Exception e){
            userLogService.processException(e, logger, dashboardId, latestVersion, type, "jenkinsJobStatus");
        }
    }

    public JenkinsStatus getJenkinsJobStatusFromApi(String url, String userName, String password) throws Exception{
        try {
            String response = webUtils.httpGetMethod(url, userName, password);
            Gson gson = new Gson();
            JenkinsStatus jenkinsStatus = gson.fromJson(response, JenkinsStatus.class);
            return jenkinsStatus;
        } catch (IllegalStateException e){
            throw new ParseErrorException(String.format("Cannot parse codeDebt data from Sonar API response. Request Url: %s", url), e);
        } catch (JsonSyntaxException e){
            throw new ParseErrorException(String.format("Cannot parse codeDebt data from Sonar API response. Request Url: %s", url), e);
        }
    }

    public List<ProjectModule> getProjectOrModuleByDashboardId(Dashboard dashboard, String type){
        List<ProjectModule> pmList = new ArrayList<ProjectModule>();
        if (type.equalsIgnoreCase("Project")){
            pmList = projectModuleRepository.findProjectByDashId(dashboard);
        }else{
            pmList = projectModuleRepository.findModuleByDashId(dashboard);
        }
        return pmList;
    }

    public List<JenkinsStatus> getJenkinsJobStatusRecords(Long dashboardId, String level){
        Dashboard dashboard = dashboardRepository.findById(dashboardId);
        List<ProjectModule> pmList = getProjectOrModuleByDashboardId(dashboard, level);
        List<JenkinsStatus> jsList = new ArrayList<JenkinsStatus>();
        for (ProjectModule pm : pmList){
            for (JenkinsJob jenkinsJob : pm.getVisibleJenkinsJobList()){
                boolean isLatest = true;
                Version latestVersion = versionRepository.findTop1ByDashboardIdByOrderByIdDesc(dashboardId);
                if (latestVersion == null){
                    return jsList;
                }
                Long vid = latestVersion.getId();
                List<JenkinsJobStatus> jjsList = jenkinsJobStatusRepository.findByVersion_idAndJenkinsJob_id(vid, jenkinsJob.getId());
                while(jjsList.isEmpty() && vid > 0) {
                    isLatest = false;
                    vid--;
                    jjsList = jenkinsJobStatusRepository.findByVersion_idAndJenkinsJob_id(vid, jenkinsJob.getId());
                }
                if (jjsList != null){
                    for (JenkinsJobStatus jjs : jjsList){
                        JenkinsStatus js = new JenkinsStatus();
                        js.setModuleName(pm.getName());
                        js.setUrl(jjs.getUrl());
                        js.setColor(jjs.getStatus());
                        js.setLatest(isLatest);
                        js.setSaveAt(jjs.getCreatedTime().toString());
                        js.setMain(jenkinsJob.getJenkinsServer().isMain());
                        jsList.add(js);
                    }
                }
            }
        }
        return mergeJobStatus(jsList);
    }

    public List<JenkinsStatus> mergeJobStatus(List<JenkinsStatus> jobStatusList){
        List<JenkinsStatus> mergedList = new ArrayList<JenkinsStatus>();
        Map<String, List<JenkinsStatus>> groupByedMap =
                jobStatusList.stream().collect(Collectors.groupingBy(JenkinsStatus::getModuleName));
        for(Map.Entry<String, List<JenkinsStatus>> entryList :groupByedMap.entrySet()){
            boolean isBlue = true;
            JenkinsStatus mergedStatus = new JenkinsStatus();
            JenkinsStatus mainJobStatus = new JenkinsStatus();
            for (JenkinsStatus jenkinsStatus : entryList.getValue()){
                if (jenkinsStatus.getColor().equalsIgnoreCase("red")){
                    isBlue = false;
                    mergedStatus = jenkinsStatus;
                    break;
                }
                if (jenkinsStatus.isMain() || (!jenkinsStatus.isMain() && entryList.getValue().size() == 1)){
                    mainJobStatus = jenkinsStatus;
                }
            }
            if (isBlue){
                mergedList.add(mainJobStatus);
            }else {
                mergedList.add(mergedStatus);
            }
        }
        return mergedList;
    }
}
