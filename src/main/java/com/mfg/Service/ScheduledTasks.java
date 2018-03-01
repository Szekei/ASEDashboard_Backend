package com.mfg.Service;

import com.mfg.config.Constants;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

/**
 * Created by I309908 on 5/8/2017.
 */
@Component
public class ScheduledTasks implements Job{

    public void execute(JobExecutionContext context) throws JobExecutionException {

        DataSynchronizationService dataSynchronizationService = (DataSynchronizationService)context.getJobDetail().getJobDataMap()
                                                                .get(Constants.DATA_SYNCHRONIZATION_SERVICE);
        Long dashboardId = (Long)context.getJobDetail().getJobDataMap().get(Constants.DASHBOARD_ID_UPPERCASE);
        if (dataSynchronizationService != null){
            dataSynchronizationService.RetrieveAllData(dashboardId);
        }
    }


}
