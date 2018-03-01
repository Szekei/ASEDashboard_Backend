package com.mfg;

import com.mfg.Service.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


/**
 * Created by I309908 on 1/20/2017.
 */
@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private SchedulerService schedulerService;

    final Logger logger = LoggerFactory.getLogger("logger."+ this.getClass().toString());

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        try{
            schedulerService.initializeScheduler();
            logger.warn("Server started.");
        }catch(Exception e){
            e.printStackTrace();
            logger.error("Server failed to start.", e);
        }
    }

}
