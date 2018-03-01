package com.mfg.Controller;

import com.mfg.Model.BasicResponse;
import com.mfg.Service.DataSynchronizationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Created by I309908 on 5/8/2017.
 */
@Api(value = "Data Retrieving Controller", description = "Operations pertaining to synchronizing data from all related servers.")
@RequestMapping("/api")
@RestController
public class DataSynchronizationController {

    @Autowired
    private DataSynchronizationService dataSynchronizationService;

    @ApiOperation(value = "Trigger a synchronization job for a dashboard.")
    @GetMapping(value = "/job/{dashboardId}", produces = "application/json")
    public BasicResponse triggerDataJob(@Valid @PathVariable("dashboardId") Long dashboardId){

        return dataSynchronizationService.triggerDataSynchronizationJob(dashboardId);
    }
}
