package com.mfg.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mfg.Entity.*;
import com.mfg.Exception.NoDataFoundException;
import com.mfg.Exception.ParseErrorException;
import com.mfg.Exception.RequestErrorException;
import com.mfg.Model.APITestResponse;
import com.mfg.Model.BackEndCoverage;
import com.mfg.Model.FunctionalQualityResponse;
import com.mfg.Model.LoggerParam;
import com.mfg.Repository.*;
import com.mfg.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.NoContentException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by I309908 on 4/21/2017.
 */
@Component
public class FunctionalQualityService {

    @Autowired
    private ProjectModuleRepository projectModuleRepository;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private WebUtils webUtils;

    @Autowired
    private ParsingRuleRepository parsingRuleRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private FunctionalQualityRepository functionalQualityRepository;

    @Autowired
    private UserLogService userLogService;

    final Logger g_logger = LoggerFactory.getLogger(this.getClass());


    public void saveBackendUT(Long dashboardId, String type){

        Version latestVersion =  versionRepository.findTop1ByDashboardIdByOrderByIdDesc(dashboardId);
        final Logger logger = LoggerFactory.getLogger(Constants.LOGGER_NAME_PREFIX+dashboardId+ Constants.LOGGER_NAME_SUFFIX+latestVersion.getId());
        LoggerParam loggerParam = new LoggerParam(logger, latestVersion, dashboardId, type, "backendUT");

        try {
            Dashboard dashboard = dashboardRepository.findByIdAndIsVisible(dashboardId, true);
            List<ProjectModule> pmList = getProjectOrModuleByDashboardId(dashboard, type);

            SonarServer sonarServer = dashboard.getSonarServer();
            for (ProjectModule jenkinsModule : pmList){
                for (JenkinsJob jenkinsJob : jenkinsModule.getVisibleJenkinsJobList()){
                    if (jenkinsJob.isActive() && jenkinsJob.isVisible()){
                        String job_name = jenkinsJob.getName();
                        JenkinsServer jenkinsServer = jenkinsJob.getJenkinsServer();
                        if (jenkinsServer == null){
                            throw new NoDataFoundException("No Jenkins server configuration.");
                        }
                        String jenkins_password = RSAUtils.decryptStringFromBase64(jenkinsServer.getPassword());
                        String ut_url = jenkinsServer.getUrl() + "/job/" + job_name + "/lastCompletedBuild/consoleText";
                        ParsingRule backendUT_rule = parsingRuleRepository.findByJenkinsJob_idAndType(jenkinsJob.getId(), "BackendUT");
                        String startIdentifier = backendUT_rule.getStartIdentifier();
                        String endIdentifier = backendUT_rule.getEndIdentifier();

                        int failureNumber = getBackendUTIssueFromApi(ut_url, startIdentifier, endIdentifier, jenkinsServer.getUserName(), jenkins_password,loggerParam);
                        if(failureNumber < 0){
                            continue;
                        }
                        float[] coverage_info = new float[2];
                        //if there is no sonar server configuration, skip the process to get the coverage data of backendUT
                        if (sonarServer != null && sonarServer.getUrl() != null &&
                                sonarServer.getUserName() != null && sonarServer.getPassword() != null){
                            String sonar_password = RSAUtils.decryptStringFromBase64(sonarServer.getPassword());
//                    String sonar_password = sonarServer.getPassword();
                            List<ProjectModule> sonarTaskList = projectModuleRepository.findByParentIdAndIsSonarTaskAndIsVisible(jenkinsModule.getId(), true, true);
                            if (sonarTaskList.isEmpty()){
                                sonarTaskList.add(jenkinsModule);
                            }
                            List<BackEndCoverage> backEndCoveragesForCalculation = new ArrayList<BackEndCoverage>();
                            for (int i = 0; i < sonarTaskList.size(); i++) {
                                StringBuffer coverage_url = new StringBuffer();
                                coverage_url.append(sonarServer.getUrl())
                                        .append("/api/resources?metrics=ncloc,coverage&format=json&resource=")
                                        .append(sonarTaskList.get(i).getSonarKey());
                                BackEndCoverage[] backEndCoverages = getBackendUTCoverageFromApi(coverage_url.toString(), sonarServer.getUserName(), sonar_password,
                                                                                        logger, dashboardId, latestVersion, type );
                                if (backEndCoverages != null) {
                                    backEndCoveragesForCalculation.add(backEndCoverages[0]);
//                                    coverage_info = calculateCoverage(backEndCoveragesForCalculation, dashboardId);
                                }
                            }
                            coverage_info = calculateCoverage(backEndCoveragesForCalculation, dashboardId);
                        }
                        FunctionalQuality functionalQuality = new FunctionalQuality();
                        functionalQuality.setVersion(latestVersion);
                        functionalQuality.setType("BackendUT");
                        functionalQuality.setCount1(failureNumber);// field count1 for failures test number
                        functionalQuality.setCount2((int)coverage_info[1]);// field count2 for total lines number
                        functionalQuality.setCoverage(coverage_info[0]);
                        functionalQuality.setJenkinsJob(jenkinsJob);
                        functionalQualityRepository.save(functionalQuality);
                    }
                }
            }
        }catch (Exception e){
            userLogService.processException(e, logger, dashboardId, latestVersion, type, "backendUT");
        }
    }

    public void saveFrontendUT(Long dashboardId, String type){

        Version latestVersion =  versionRepository.findTop1ByDashboardIdByOrderByIdDesc(dashboardId);
        final Logger logger = LoggerFactory.getLogger(Constants.LOGGER_NAME_PREFIX+dashboardId+Constants.LOGGER_NAME_SUFFIX+latestVersion.getId());
        LoggerParam loggerParam = new LoggerParam(logger, latestVersion, dashboardId, type, "frontendUT");
        try {
            Dashboard dashboard = dashboardRepository.findByIdAndIsVisible(dashboardId, true);
            List<ProjectModule> pmList = getProjectOrModuleByDashboardId(dashboard, type);

            for (ProjectModule jenkinsModule : pmList){
                for (JenkinsJob jenkinsJob : jenkinsModule.getVisibleJenkinsJobList()){
                    if (jenkinsJob.isActive() && jenkinsJob.isVisible()){
                        JenkinsServer jenkinsServer = jenkinsJob.getJenkinsServer();
                        if (jenkinsServer == null){
                            throw new NoDataFoundException("No Jenkins server configuration.");
                        }
                        String jenkins_password = RSAUtils.decryptStringFromBase64(jenkinsServer.getPassword());

                        String job_name = jenkinsJob.getName();
                        String ut_url = jenkinsServer.getUrl() + "/job/" + job_name + "/lastCompletedBuild/consoleText";
                        ParsingRule frontendUT_rule = parsingRuleRepository.findByJenkinsJob_idAndType(jenkinsJob.getId(), "FrontendUT");
                        String startIdentifier = frontendUT_rule.getStartIdentifier();
                        String endIdentifier = frontendUT_rule.getEndIdentifier();
                        float[] frontTestData = new float[3];
                        frontTestData = getFrontUTData(ut_url, startIdentifier, endIdentifier, "Results :", jenkinsServer.getUserName(), jenkins_password, loggerParam);
                        if (frontTestData == null){
                            continue;
                        }
                        FunctionalQuality functionalQuality = new FunctionalQuality();
                        functionalQuality.setVersion(latestVersion);
                        functionalQuality.setType("FrontendUT");
                        functionalQuality.setCount1((int)frontTestData[0]);// field count1 for failures test number
                        functionalQuality.setCount2((int)(frontTestData[2]));// field count2 for total test number
                        functionalQuality.setCoverage(frontTestData[1]);
                        functionalQuality.setJenkinsJob(jenkinsJob);
                        functionalQualityRepository.save(functionalQuality);
                    }
                }
            }
        } catch(Exception e){
            userLogService.processException(e, logger, dashboardId, latestVersion, type, "frontendUT");
        }
    }

    public void saveAPITestData(Long dashboardId, String type){

        Version latestVersion =  versionRepository.findTop1ByDashboardIdByOrderByIdDesc(dashboardId);
        final Logger logger = LoggerFactory.getLogger(Constants.LOGGER_NAME_PREFIX+dashboardId+Constants.LOGGER_NAME_SUFFIX+latestVersion.getId());
        LoggerParam loggerParam = new LoggerParam(logger, latestVersion, dashboardId, type, "apiTest");
        try {
            Dashboard dashboard = dashboardRepository.findByIdAndIsVisible(dashboardId, true);
            List<ProjectModule> pmList = getProjectOrModuleByDashboardId(dashboard, type);

            for (ProjectModule jenkinsModule : pmList){
                for (JenkinsJob jenkinsJob : jenkinsModule.getVisibleJenkinsJobList()){
                    if (jenkinsJob.isActive() && jenkinsJob.isVisible()){
                        JenkinsServer jenkinsServer = jenkinsJob.getJenkinsServer();
                        if (jenkinsServer == null){
                            throw new NoDataFoundException("No Jenkins server configuration.");
                        }
                        String jenkins_password = RSAUtils.decryptStringFromBase64(jenkinsServer.getPassword());
                        String job_name = jenkinsJob.getName();
                        String log_url = jenkinsServer.getUrl() + "/job/" + job_name + "/lastCompletedBuild/consoleText";
                        ParsingRule apiTest_rule = parsingRuleRepository.findByJenkinsJob_idAndType(jenkinsJob.getId(), "apiTest");
                        String startIdentifier = apiTest_rule.getStartIdentifier();
                        String endIdentifier = apiTest_rule.getEndIdentifier();
                        int[] apiTestData = getAPITestFailureNumAndSumCases(log_url, startIdentifier, endIdentifier, jenkinsServer.getUserName(), jenkins_password, loggerParam);
                        if (apiTestData == null){
                            continue;
                        }
                        FunctionalQuality functionalQuality = new FunctionalQuality();
                        functionalQuality.setVersion(latestVersion);
                        functionalQuality.setType("APITest");
                        functionalQuality.setCount1(apiTestData[1]);// field count1 for failures test assertion
                        functionalQuality.setCount2(apiTestData[0]);// field count2 for total test assertion
//            functionalQuality.setCoverage(frontTestData[1]);
                        functionalQuality.setJenkinsJob(jenkinsJob);
                        functionalQualityRepository.save(functionalQuality);
                    }
                }
            }
        }catch (Exception e){
            userLogService.processException(e, logger, dashboardId, latestVersion, type, "apiTest");
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

    //get issues number from log for backEndUT
    public int getBackendUTIssueFromApi(String url, String startFrom, String endTo, String userName, String password, LoggerParam loggerParam){
        try{
            String response = webUtils.httpGetMethod(url, userName, password);
            String responseStr = getContentFromFullLog(response, startFrom, endTo);
            boolean hasValue = false;
            int failureNumber = 0;
            if(responseStr != null && responseStr.length() > 0){
                int first = 0;
                int last = 0;
                while(first != -1){
                    first = responseStr.indexOf("Results :", first);
                    if(first != -1){
                        hasValue = true;
                        last = responseStr.indexOf("Skipped:", first);
                        if(last != -1 && last > first){
                            String handStr = responseStr.substring(first, last);
                            failureNumber += (getIntResultFromStr(handStr, "Failures") + getIntResultFromStr(handStr, "Errors"));
                            first = last;
                        }
                    }
                }
            }
            if(hasValue){
                return failureNumber;
            }
            throw new ParseErrorException("Cannot parse backendUT issues number from Jenkins log!" + "<" + url + ">");
        }catch (Exception e){
            userLogService.processException(e, loggerParam.getLogger(), loggerParam.getDashboardId(),
                    loggerParam.getLatestVersion(), loggerParam.getLevel(), loggerParam.getDataType());
            return -1;
        }
    }

    //get the related part of log from full console log
    public String getContentFromFullLog(String fullLog, String startFrom, String endTo){
        int first = fullLog.indexOf(startFrom);
        String content = null;
        if(first != -1){
            int last = fullLog.indexOf(endTo, first);
            if(first != -1 && last != -1 && last > first){
                content = fullLog.substring(first, last);
            }
        }
        return content;
    }

    //parse integer from result string
    public int getIntResultFromStr(String handStr, String item){
        int first = handStr.indexOf(item);
        first = handStr.indexOf(":", first)+1;
        int last = handStr.indexOf(",", first);
        return Integer.parseInt(handStr.substring(first, last).trim());
    }

    public float getFloatResultFromStr(String handStr,String item, String spliter) {
        int first = handStr.indexOf(item);
        first = handStr.indexOf(":", first)+1;
        int last = handStr.indexOf(spliter, first);
        return Float.parseFloat(handStr.substring(first, last).trim());
    }

    public BackEndCoverage[] getBackEndCoverageFromAPI(String url, String userName, String password) throws Exception{
        try {
            String response = webUtils.httpGetMethod(url, userName, password);
            Gson gson = new Gson();
            BackEndCoverage[] backEndCoverages = gson.fromJson(response, BackEndCoverage[].class);
            return backEndCoverages;
        } catch (IllegalStateException e){
            throw new ParseErrorException(String.format("Cannot parse codeDebt data from Sonar API response. Request Url: %s", url), e);
        } catch (JsonSyntaxException e){
            throw new ParseErrorException(String.format("Cannot parse codeDebt data from Sonar API response. Request Url: %s", url), e);
        }
    }

    public float[] calculateCoverage(List<BackEndCoverage> backEndCoverages, Long dashboardId) throws Exception{
        float coveredLine = 0;
        float totalLine = 0;
        for (BackEndCoverage backEndCoverage : backEndCoverages){
            try{
                if (backEndCoverage.msr != null && backEndCoverage.msr.length == 2){
                    if (backEndCoverage.msr[0].key.equalsIgnoreCase("ncloc")){
                        coveredLine += backEndCoverage.msr[0].val*backEndCoverage.msr[1].val/100;
                        totalLine += backEndCoverage.msr[0].val;
                    }else if (backEndCoverage.msr[0].key.equalsIgnoreCase("coverage")){
                        coveredLine += backEndCoverage.msr[0].val*backEndCoverage.msr[1].val/100;
                        totalLine += backEndCoverage.msr[1].val;
                    }
                }else {
                    String message = "BackendUt coverage data from sonar api with key [" + backEndCoverage.key + "] is incomplete";
                    throw new NoDataFoundException(message);
                }
            }catch (Exception e){
                userLogService.processException(g_logger, dashboardId, "BackendUT", e.getMessage());
            }
        }
        if (totalLine > 0){
            return new float[]{coveredLine*100/totalLine, totalLine};
        }else {
//            throw new NoDataFoundException("Extract backendUT coverage error!");
            userLogService.processException(g_logger, dashboardId, "BackendUT", "Extract backendUT coverage error!");
            // ensure that even if coverage from sonar is null, issue data from jenkins will be saved
            return new float[]{0, 0};
        }
    }


    //get frontend ut failure number and coverage for FrontEndUT from log
    public float[] getFrontUTData(String url, String startFrom, String endTo, String keyStr,  String userName, String password, LoggerParam loggerParam) throws Exception{
        try{
            String initialString = webUtils.httpGetMethod(url, userName, password);
            String responseStr = getContentFromFullLog(initialString, startFrom, endTo);
            int first = 0;
            int last = 0;
            float[] result = new float[3];
            boolean hasValue = false;
            if(responseStr != null && responseStr.length() > 0){
                while (first != -1){
                    first = responseStr.indexOf(keyStr, first);
                    if(first != -1){
                        first = responseStr.indexOf("Results :", first);
                        if(first != -1){
                            hasValue = true;
                            last = responseStr.indexOf("Errors:", first);
                            if(last != -1 && last > first){
                                //when parsing the log, there are more than one module,so we need to average them.
                                //here is to get the weighted sum
                                String handStr = responseStr.substring(first, last);
                                //index 0 for failure test number
                                result[0] += getIntResultFromStr(handStr, "Failures");
                                //index 1 for covered test number
                                result[1] += getFloatResultFromStr(handStr, "Coverage", "%,") * getIntResultFromStr(handStr, "run");
                                //index 2 for run test number
                                result[2] += getIntResultFromStr(handStr, "run");
                                first = last;
                            }
                        }
                    }

                }
                //get weighted average
                if(result[2] > 0 && result[1] >= 0 && hasValue){
                    result[1] = result[1]/result[2];
                    return result;
                }
            }
            throw new ParseErrorException("Cannot parse frontendUT data from Jenkins log." + "<" + url + ">");
        }catch (Exception e){
            userLogService.processException(e, loggerParam.getLogger(), loggerParam.getDashboardId(),
                    loggerParam.getLatestVersion(), loggerParam.getLevel(), loggerParam.getDataType());
            return null;
        }

    }

    //get apitest failure number and case number from log
    public int[] getAPITestFailureNumAndSumCases(String url, String startFrom, String endTo,  String userName, String password, LoggerParam loggerParam) throws Exception{
        try{
            String initialResponse = webUtils.httpGetMethod(url, userName, password);
            String responseStr = getContentFromFullLog(initialResponse, startFrom, endTo);
            int[] result = new int[2];
            if(responseStr != null && responseStr.length() > 0){
                String requestStr = "Total Request Assertions:";
                String failureStr = "Total Failed Assertions:";
                String exportedStr = "Total Exported Results:";
                int first = responseStr.indexOf(requestStr);
                int middle = 0;
                int last = 0;
                if(first != -1){
                    middle = responseStr.indexOf(failureStr, first);
                    if(middle != -1){
                        result[0] = Integer.parseInt(responseStr.substring(first+requestStr.length(), middle).trim());
                        last = responseStr.indexOf(exportedStr, middle);
                        if(last != -1){
                            result[1] = Integer.parseInt(responseStr.substring(middle+failureStr.length(), last).trim());
                            return result;
                        }
                    }
                }
            }
            throw new ParseErrorException("Cannot parse apiTest data from Jenkins log." + "<" + url + ">");
        }catch (Exception e){
            userLogService.processException(e, loggerParam.getLogger(), loggerParam.getDashboardId(),
                    loggerParam.getLatestVersion(), loggerParam.getLevel(), loggerParam.getDataType());
            return null;
        }
    }

    public BackEndCoverage[] getBackendUTCoverageFromApi(String url, String userName, String password,
                                                          Logger logger, Long dashboardId, Version latestVersion, String type){
        try {
            String response = webUtils.httpGetMethod(url, userName, password);
            Gson gson = new Gson();
            BackEndCoverage[] backEndCoverages = gson.fromJson(response, BackEndCoverage[].class);
            if (backEndCoverages == null){
                throw new NoContentException(String.format("No content found from the api. Request Url: %s", url));
            }
            return backEndCoverages;
        } catch (IllegalStateException e){
            ParseErrorException ex =  new ParseErrorException(String.format("Cannot parse codeDebt data from Sonar API response. Request Url: %s", url), e);
            userLogService.processException(ex, logger, dashboardId, latestVersion, type, "backendUT");
            return null;
        } catch (JsonSyntaxException e){
            ParseErrorException ex =  new ParseErrorException(String.format("Cannot parse codeDebt data from Sonar API response. Request Url: %s", url), e);
            userLogService.processException(ex, logger, dashboardId, latestVersion, type, "backendUT");
            return null;
        } catch (Exception e){
            RequestErrorException ex =  new RequestErrorException(String.format("%s. Request Url: %s",e.getCause(), url), e);
            userLogService.processException(ex, logger, dashboardId, latestVersion, type, "backendUT");
            return null;
        }
    }

    public String getUTdataRecords(Long dashboardId, String level, String utType){
        Dashboard dashboard = dashboardRepository.findById(dashboardId);
        List<ProjectModule> pmList = getProjectOrModuleByDashboardId(dashboard, level);
        List<FunctionalQualityResponse> fqrList = new ArrayList<FunctionalQualityResponse>();
        String main_url = null;
        String ref_url = null;
        int total = 0;
        for (ProjectModule pm : pmList){
            for (JenkinsJob jenkinsJob : pm.getVisibleJenkinsJobList()){
                if (jenkinsJob.getJenkinsServer().isMain()){
                    main_url = jenkinsJob.getJenkinsServer().getUrl();
                }else if (!jenkinsJob.getJenkinsServer().isMain()){
                    ref_url = jenkinsJob.getJenkinsServer().getUrl();
                }
                boolean isLatest = true;
                Version latestVersion = versionRepository.findTop1ByDashboardIdByOrderByIdDesc(dashboardId);
                if (latestVersion == null){
                    return wrapUTResponseObj(main_url, ref_url, fqrList);
                }
                Long vid = latestVersion.getId();
                List<FunctionalQuality> utList = functionalQualityRepository.findByTypeAndVersion_idAndJenkinsJob_id(utType, vid, jenkinsJob.getId());
                while(utList.isEmpty() && vid > 0){
                    isLatest = false;
                    vid--;
                    utList = functionalQualityRepository.findByTypeAndVersion_idAndJenkinsJob_id(utType, vid, jenkinsJob.getId());
                }
                if (utList != null){
                    for (FunctionalQuality ut : utList){
                        FunctionalQualityResponse fqr = new FunctionalQualityResponse();
                        fqr.setModuleName(pm.getName());
                        fqr.setCoverage(String.format("%.2f", ut.getCoverage())+"%");
                        fqr.setIssues(ut.getCount1());
                        total += ut.getCount1();// calculate the total count of issue
                        fqr.setLatest(isLatest);
                        fqr.setSaveAt(ut.getCreatedTime().toString());
                        fqr.setMain(jenkinsJob.getJenkinsServer().isMain());
                        fqr.setCodeLine(ut.getCount2());
                        StringBuffer jobUrl = new StringBuffer();
                        jobUrl.append(jenkinsJob.getJenkinsServer().getUrl()).
                                append("/job/").
                                append(jenkinsJob.getName()).
                                append("/lastCompletedBuild");
                        fqr.setJenkinsUrl(jobUrl.toString());
                        //clear the buffer for new url
                        jobUrl.delete(0, jobUrl.length());
                        if (dashboard.getSonarServer() != null && dashboard.getSonarServer().getUrl() != null){
                            List<ProjectModule> list = projectModuleRepository.findProjectByDashId(dashboard);
                            if (list != null && !list.isEmpty()){
                                String sonarKey = list.get(0).getSonarKey();
                                if (sonarKey == null || sonarKey.isEmpty()){
                                    jobUrl.append(dashboard.getSonarServer().getUrl());
                                }else {
                                    jobUrl.append(dashboard.getSonarServer().getUrl()).
                                            append("/dashboard?id=").
                                            append(sonarKey);
                                }
                            }
                        }
                        fqr.setSonarUrl(jobUrl.toString());
                        fqrList.add(fqr);
                    }
                }
            }
        }
        return wrapUTResponseObj(main_url, ref_url, fqrList);
    }


    public String getApiTestDataRecords(Long dashboardId, String level){
        Dashboard dashboard = dashboardRepository.findById(dashboardId);
        List<ProjectModule> pmList = getProjectOrModuleByDashboardId(dashboard, level);
        List<APITestResponse> atrList = new ArrayList<APITestResponse>();
        String main_url = null;
        String ref_url = null;
        int total = 0;// used to calculate the total count of issue
        for (ProjectModule pm : pmList){
            for (JenkinsJob jenkinsJob : pm.getVisibleJenkinsJobList()){
                if (jenkinsJob.getJenkinsServer().isMain()){
                    main_url = jenkinsJob.getJenkinsServer().getUrl();
                }else{
                    ref_url = jenkinsJob.getJenkinsServer().getUrl();
                }
                boolean isLatest = true;
                Version latestVersion = versionRepository.findTop1ByDashboardIdByOrderByIdDesc(dashboardId);
                if (latestVersion == null){
                    return wrapApiResponseObj(main_url, ref_url, atrList);
                }
                Long vid = latestVersion.getId();
                List<FunctionalQuality> apiTestList = functionalQualityRepository.findByTypeAndVersion_idAndJenkinsJob_id("APITest", vid, jenkinsJob.getId());
                while(apiTestList.isEmpty() && vid > 0){
                    isLatest = false;
                    vid--;
                    apiTestList = functionalQualityRepository.findByTypeAndVersion_idAndJenkinsJob_id("APITest", vid, jenkinsJob.getId());
                }
                if (apiTestList != null){
                    for (FunctionalQuality apiTest : apiTestList){
                        APITestResponse atr = new APITestResponse();
                        atr.setModuleName(pm.getName());
                        atr.setIssues(apiTest.getCount1());
                        total += apiTest.getCount1();// calculate the total count of issue
                        atr.setCases(apiTest.getCount2());
                        atr.setLatest(isLatest);
                        atr.setSaveAt(apiTest.getCreatedTime().toString());
                        atr.setMain(jenkinsJob.getJenkinsServer().isMain());
                        StringBuffer jobUrl = new StringBuffer();
                        jobUrl.append(jenkinsJob.getJenkinsServer().getUrl()).
                                append("/job/").
                                append(jenkinsJob.getName()).
                                append("/lastCompletedBuild");
                        atr.setJenkinsUrl(jobUrl.toString());
                        atrList.add(atr);
                    }

                }
            }
        }

        return wrapApiResponseObj(main_url, ref_url, atrList);
    }

    public String wrapUTResponseObj(String main_url,String ref_url, List<FunctionalQualityResponse> dataList){
        JsonObject responseObj = new JsonObject();
        JsonObject mainDataObj = new JsonObject();
        JsonObject refDataObj = new JsonObject();
        List<FunctionalQualityResponse> mainList = new ArrayList<FunctionalQualityResponse>();
        List<FunctionalQualityResponse> refList = new ArrayList<FunctionalQualityResponse>();
        Gson gson = new Gson();
        int mainTotal = 0;
        int refTotal = 0;
        List<String> coveredModules = new ArrayList<String>();
        for (FunctionalQualityResponse fqr : dataList){
            if (fqr.isMain()){
                mainList.add(fqr);
                mainTotal += fqr.getIssues();
            }else {
                refTotal += fqr.getIssues();
                refList.add(fqr);
            }
        }
        mainDataObj.addProperty("url", main_url);
        mainDataObj.add("data", gson.toJsonTree(mainList));

        refDataObj.addProperty("url", ref_url);
        refDataObj.add("data", gson.toJsonTree(refList));

        if (mainList.isEmpty() && !refList.isEmpty()){
            responseObj.addProperty("count", refTotal);
        }else {
            responseObj.addProperty("count", mainTotal);
        }
        responseObj.add("mainData",mainDataObj);
        responseObj.add("refData",refDataObj);
        return gson.toJson(responseObj);
    }

    public String wrapApiResponseObj(String main_url,String ref_url, List<APITestResponse> dataList){
        JsonObject responseObj = new JsonObject();
        JsonObject mainDataObj = new JsonObject();
        JsonObject refDataObj = new JsonObject();
        List<APITestResponse> mainList = new ArrayList<APITestResponse>();
        List<APITestResponse> refList = new ArrayList<APITestResponse>();
        Gson gson = new Gson();
        int mainTotal = 0;
        int refTotal = 0;
        for (APITestResponse atr : dataList){
            if (atr.isMain()){
                mainList.add(atr);
                mainTotal += atr.getIssues();
            }else {
                refList.add(atr);
                refTotal += atr.getIssues();
            }
        }
        mainDataObj.addProperty("url", main_url);
        mainDataObj.add("data", gson.toJsonTree(mainList));

        refDataObj.addProperty("url", ref_url);
        refDataObj.add("data", gson.toJsonTree(refList));
        if (mainList.isEmpty() && !refList.isEmpty()){
            responseObj.addProperty("count", refTotal);
        }else {
            responseObj.addProperty("count", mainTotal);
        }
        responseObj.add("mainData",mainDataObj);
        responseObj.add("refData",refDataObj);
        return gson.toJson(responseObj);
    }

}
