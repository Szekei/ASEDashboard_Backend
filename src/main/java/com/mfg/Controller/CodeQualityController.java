package com.mfg.Controller;

import com.mfg.Repository.CodeQualityRepository;
import com.mfg.Repository.DashboardRepository;
import com.mfg.Repository.ProjectModuleRepository;
import com.mfg.Repository.VersionRepository;
import com.mfg.Service.CodeQualityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Created by I309908 on 5/5/2017.
 */
@Api(value = "Code Quality Controller", description = "Operations pertaining to code quality data")
@RequestMapping("/api")
@RestController
public class CodeQualityController {

    @Autowired
    private ProjectModuleRepository projectModuleRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private CodeQualityRepository codeQualityRepository;

    @Autowired
    private CodeQualityService codeQualityService;

    @ApiOperation(value = "Return code debt data of this dashboard in this type.")
    @GetMapping(value = "/codeQuality/codeDebt/{dashboardId}/{type}", produces = "application/json")
    public String getCodeDebt(@Valid @PathVariable("dashboardId") Long dashboardId, @Valid @PathVariable("type") String type){

        return codeQualityService.getCodeDebtRecords(dashboardId, type);
    }

    @ApiOperation(value = "Return techIssue data of this dashboard in this type.")
    @GetMapping(value = "/codeQuality/techIssue/{dashboardId}/{type}", produces = "application/json")
    public String getTechIssue(@Valid @PathVariable("dashboardId") Long dashboardId, @Valid @PathVariable("type") String type){

        return codeQualityService.getTechIssueRecords(dashboardId, type);
    }

}
