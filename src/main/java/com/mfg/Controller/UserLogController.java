package com.mfg.Controller;

import com.mfg.Service.UserLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

/**
 * Created by I309908 on 5/19/2017.
 */
@Api(value = "User Log Controller", description = "Operations pertaining to manage log for user.")
@RequestMapping("/api")
@RestController
public class UserLogController {

    @Autowired
    private UserLogService userLogService;

    @ApiOperation(value = "Return list of logs about data synchronization of this dashboard in latest version.")
    @GetMapping(value = "/log/{dashboardId}", produces = "application/json")
    public String getLogByDashboardId(@Valid @PathVariable("dashboardId") Long dashboardId){
        return userLogService.getLogByDashboardId(dashboardId);
    }
}
