package com.mfg.Controller;

import com.mfg.Entity.DashboardViewer;
import com.mfg.Model.BasicResponse;
import com.mfg.Service.AccessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by I309908 on 5/8/2017.
 */
@Api(value = "Access Controller", description = "Dashboard access permission management Operations")
@RequestMapping("/api")
@RestController
public class AccessController {

    @Autowired
    private AccessService accessService;

    @ApiOperation(value = "Check if this user has permission to access this dashboard.")
    @GetMapping(value = "/access/{dashboardId}/{userId}", produces = "application/json")
    public BasicResponse checkViewAccess(@Valid @PathVariable("dashboardId") Long dashboardId, @Valid @PathVariable("userId") String userId) throws Exception{
        BasicResponse response = new BasicResponse();
        boolean viewerAccess = accessService.checkViewAccess(dashboardId, userId);
        if (viewerAccess){
            response.setStatus("Success");
            return response;
        }
        response.setStatus("Fail");
        return response;
    }

    @ApiOperation(value = "Add viewers to this dashboard.")
    @Transactional
    @PostMapping(value = "/access/viewer/{dashboardId}", produces = "application/json")
    public BasicResponse saveViewer(@RequestBody List<DashboardViewer> viewerList,@Valid @PathVariable("dashboardId") Long dashboardId) throws Exception{

        BasicResponse response = new BasicResponse();
        viewerList = accessService.saveOrUpdateViewer(dashboardId, viewerList);
        if (viewerList != null){
            response.setStatus("Success");
            return response;
        }
        response.setStatus("Fail");
        response.setMessage("Save or update failed.");
        return response;
    }

    @ApiOperation(value = "Return a list of viewers of this dashboard.")
    @GetMapping(value = "/access/viewer/{dashboardId}", produces = "application/json")
    public List<DashboardViewer> getViewerByDashboardId(@Valid @PathVariable("dashboardId") Long dashboardId) throws Exception{
        return accessService.getViewerByDashboardId(dashboardId);
    }

}