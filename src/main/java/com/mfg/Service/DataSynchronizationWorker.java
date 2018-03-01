package com.mfg.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by I309908 on 5/8/2017.
 */
@Component
public class DataSynchronizationWorker implements Runnable{


    @Autowired
    private DataSynchronizationService dataSynchronizationService;

    private Long dashboardId;

    public DataSynchronizationWorker(Long dashboardId){
        super();
        this.dashboardId = dashboardId;
    }
    public DataSynchronizationWorker(){}

    @Override
    public void run() {
        dataSynchronizationService.RetrieveAllData(dashboardId);
    }

    public Long getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(Long dashboardId) {
        this.dashboardId = dashboardId;
    }
}
