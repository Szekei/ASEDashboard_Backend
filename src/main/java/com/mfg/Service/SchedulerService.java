package com.mfg.Service;

import com.mfg.Entity.Dashboard;
import com.mfg.Repository.DashboardRepository;
import com.mfg.config.Constants;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;



/**
 * Created by I309908 on 6/5/2017.
 */
@Component
public class SchedulerService {

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private UserLogService userLogService;

    @Autowired
    private DataSynchronizationService dataSynchronizationService;


    public void initializeScheduler() throws Exception{

        List<Dashboard> activeList = dashboardRepository.findByIsVisibleAndIsActive(true, true);
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();
        for (Dashboard dashboard : activeList){
            if (dashboard.getCronStr() != null){
                JobDetail job = newJob(ScheduledTasks.class)
                        .withIdentity(Constants.SCHEDULER_NAME_PREFIX +dashboard.getId(), Constants.SCHEDULER_SERVICE_GROUP)
                        .build();

                Trigger trigger = newTrigger()
                        .withIdentity(Constants.TRIGGER_NAME_PREFIX +dashboard.getId(), Constants.SCHEDULER_SERVICE_GROUP)
                        .withSchedule(cronSchedule(dashboard.getCronStr()))
                        .build();

                job.getJobDataMap().put(Constants.DATA_SYNCHRONIZATION_SERVICE, dataSynchronizationService);
                job.getJobDataMap().put(Constants.DASHBOARD_ID_UPPERCASE, dashboard.getId());

                sched.scheduleJob(job, trigger);
            }
        }

        sched.start();

    }

    public void updateScheduler(Long dashboardId){
        final Logger logger = LoggerFactory.getLogger(Constants.LOGGER_NAME_PREFIX + dashboardId);
        try {
            Dashboard dashboard = dashboardRepository.findById(dashboardId);
            SchedulerFactory sf = new StdSchedulerFactory();
            Scheduler sched = sf.getScheduler();
            // retrieve the trigger
            Trigger oldTrigger = sched.getTrigger(triggerKey(Constants.TRIGGER_NAME_PREFIX +dashboardId, Constants.SCHEDULER_SERVICE_GROUP));
            if (oldTrigger == null){
                scheduleJob(dashboardId);
            }else {
                // obtain a builder that would produce the trigger
                TriggerBuilder tb = oldTrigger.getTriggerBuilder();

                // update the schedule associated with the builder, and build the new trigger
                // (other builder methods could be called, to change the trigger in any desired way)
                Trigger newTrigger = tb
                        .withSchedule(cronSchedule(dashboard.getCronStr()))
                        .build();

                sched.rescheduleJob(oldTrigger.getKey(), newTrigger);
            }
        }catch (Exception e){
            userLogService.processException(e, logger, dashboardId, "update scheduler");
        }

    }

    public void scheduleJob(Long dashboardId){
        final Logger logger = LoggerFactory.getLogger(Constants.LOGGER_NAME_PREFIX + dashboardId);
        try{
            Dashboard dashboard = dashboardRepository.findById(dashboardId);
            SchedulerFactory sf = new StdSchedulerFactory();
            Scheduler sched = sf.getScheduler();

            JobDetail job = newJob(ScheduledTasks.class)
                    .withIdentity(Constants.SCHEDULER_NAME_PREFIX +dashboard.getId(), Constants.SCHEDULER_SERVICE_GROUP)
                    .build();

            Trigger trigger = newTrigger()
                    .withIdentity(Constants.TRIGGER_NAME_PREFIX +dashboard.getId(), Constants.SCHEDULER_SERVICE_GROUP)
                    .withSchedule(cronSchedule(dashboard.getCronStr()))
                    .build();

            job.getJobDataMap().put(Constants.DATA_SYNCHRONIZATION_SERVICE, dataSynchronizationService);
            job.getJobDataMap().put(Constants.DASHBOARD_ID_UPPERCASE, dashboardId);

            sched.scheduleJob(job, trigger);
        }catch (Exception e){
            userLogService.processException(e, logger, dashboardId, "start the scheduler");
        }
    }

    public void unscheduleJob(Long dashboardId){
        final Logger logger = LoggerFactory.getLogger(Constants.LOGGER_NAME_PREFIX + dashboardId);
        try{
            SchedulerFactory sf = new StdSchedulerFactory();
            Scheduler sched = sf.getScheduler();
            sched.deleteJob(JobKey.jobKey(Constants.SCHEDULER_NAME_PREFIX+dashboardId, Constants.SCHEDULER_SERVICE_GROUP));
        }catch (Exception e){
            userLogService.processException(e, logger, dashboardId, "delete the scheduler");
        }
    }

}
