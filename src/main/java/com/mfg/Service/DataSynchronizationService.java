package com.mfg.Service;

import com.mfg.Entity.Dashboard;
import com.mfg.Entity.Version;
import com.mfg.Model.BasicResponse;
import com.mfg.Repository.DashboardRepository;
import com.mfg.Repository.VersionRepository;
import com.mfg.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by I309908 on 7/19/2017.
 */
@Service
public class DataSynchronizationService {

    @Autowired
    private DataSynchronizationWorker dataSynchronizationWorker;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private CodeQualityService codeQualityService;

    @Autowired
    private FunctionalQualityService functionalQualityService;

    @Autowired
    private JenkinsJobStatusService jenkinsJobStatusService;

    @Autowired
    private BCPService bcpService;

    public BasicResponse triggerDataSynchronizationJob(Long dashboardId){
        final Logger logger = LoggerFactory.getLogger(this.getClass());
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        BasicResponse response = new BasicResponse();
        for (Thread thread : threadSet){
            //check if there is a running job of this dashboard
            if (thread.getName().equals(Constants.JOB_THREAD_NAME_PREFIX + dashboardId)){
                response.setStatus(Constants.STATUS_FAILED);
                response.setMessage("Cannot start a new job when there is a running job of this dashboard.");
                logger.warn("Cannot start a new job when there is a running job of this dashboard.");
                return response;
            }
        }
        dataSynchronizationWorker.setDashboardId(dashboardId);
        Thread jobThread = new Thread(dataSynchronizationWorker);
        jobThread.setName(Constants.JOB_THREAD_NAME_PREFIX+dashboardId);
        jobThread.start();
        logger.warn(Constants.JOB_THREAD_NAME_PREFIX+dashboardId + " started.");
        response.setStatus(Constants.STATUS_SUCCESSFUL);
        response.setMessage("Job started.");
        return response;
    }

    public void RetrieveAllData(Long dashboardId){
        try {
            System.out.println("-------------------------------------------------------------------");
            System.out.println("Dashboard "+ dashboardId +" start to retrieve all kinds of data and save them into database.");
            System.out.println("-------------------------------------------------------------------");

            Dashboard dashboard = dashboardRepository.findById(dashboardId);
            Version version = new Version();
            version.setDashboard(dashboard);
            versionRepository.save(version);
            final Logger logger = LoggerFactory.getLogger(Constants.LOGGER_NAME_PREFIX+dashboardId+Constants.LOGGER_NAME_SUFFIX+version.getId());
            System.out.println("-------------------------------------------------------------------");
            System.out.println("Dashboard "+ dashboardId + " : " + "jenkins job status job started");
            System.out.println("-------------------------------------------------------------------");
            jenkinsJobStatusService.saveJenkinsJobStatus(dashboardId, "Module");
            jenkinsJobStatusService.saveJenkinsJobStatus(dashboardId, "Project");

            System.out.println("-------------------------------------------------------------------");
            System.out.println("Dashboard "+ dashboardId + " : " + "APITest job started");
            System.out.println("-------------------------------------------------------------------");
            functionalQualityService.saveAPITestData(dashboardId, "Module");
            functionalQualityService.saveAPITestData(dashboardId, "Project");

            System.out.println("-------------------------------------------------------------------");
            System.out.println("Dashboard "+ dashboardId + " : " + "frontendUT job started");
            System.out.println("-------------------------------------------------------------------");
            functionalQualityService.saveFrontendUT(dashboardId, "Module");
            functionalQualityService.saveFrontendUT(dashboardId, "Project");

            System.out.println("-------------------------------------------------------------------");
            System.out.println("Dashboard "+ dashboardId + " : " + "backendUT job started");
            System.out.println("-------------------------------------------------------------------");
            functionalQualityService.saveBackendUT(dashboardId, "Module");
            functionalQualityService.saveBackendUT(dashboardId, "Project");


            System.out.println("-------------------------------------------------------------------");
            System.out.println("Dashboard "+ dashboardId + " : " + "codeDebt job started");
            System.out.println("-------------------------------------------------------------------");
            codeQualityService.saveCodeDebt(dashboardId, "Module");
            codeQualityService.saveCodeDebt(dashboardId, "Project");

            System.out.println("-------------------------------------------------------------------");
            System.out.println("Dashboard "+ dashboardId + " : " + "techIssue job started");
            System.out.println("-------------------------------------------------------------------");
            codeQualityService.saveTechIssue(dashboardId, "Module");
            codeQualityService.saveTechIssue(dashboardId, "Project");

            System.out.println("-------------------------------------------------------------------");
            System.out.println("Dashboard " + dashboardId + " : " + "bcp job started");
            System.out.println("-------------------------------------------------------------------");
            bcpService.retrieveAndSaveTicket("internal", dashboardId);
            bcpService.retrieveAndSaveTicket("customer", dashboardId);
            bcpService.retrieveAndSaveTicket("internal-acc", dashboardId);
            System.out.println("-------------------------------------------------------------------");
            System.out.println("Dashboard " + dashboardId + " : finished!");
            System.out.println("-------------------------------------------------------------------");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
