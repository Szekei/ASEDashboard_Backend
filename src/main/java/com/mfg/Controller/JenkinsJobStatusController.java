package com.mfg.Controller;

import com.mfg.Model.JenkinsStatus;
import com.mfg.Service.JenkinsJobStatusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by I309908 on 5/8/2017.
 */
@Api(value = "Jenkins Job Status Controller", description = "Operations pertaining to jenkins job status data")
@RequestMapping("/api")
@RestController
public class JenkinsJobStatusController {

    @Autowired
    private JenkinsJobStatusService jenkinsJobStatusService;

    @ApiOperation(value = "Return jenkins job status data of this dashboard in this level")
    @GetMapping(value = "/jenkinsJobStatus/{dashboardId}/{level}", produces = "application/json")
    public List<JenkinsStatus> getJenkinsJobStatus(@PathVariable("dashboardId") Long dashboardId, @PathVariable("level") String level){
        return jenkinsJobStatusService.getJenkinsJobStatusRecords(dashboardId, level);
    }

}
