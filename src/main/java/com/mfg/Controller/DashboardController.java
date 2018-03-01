package com.mfg.Controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mfg.Model.BasicResponse;
import com.mfg.Service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by I309908 on 4/18/2017.
 */
@Api(value = "Dashboard Controller", description = "Operations pertaining to dashboard management")
@RequestMapping("/api")
@RestController
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    Gson gson = new Gson();

    // get all dashboards by owner id
    @ApiOperation(value = "Return list of dashboards of this owner.")
    @GetMapping(value = "/dashboardList/{ownerId}", produces = "application/json")
    public String getDashboardByOwnerId(@Valid @PathVariable("ownerId") String ownerId){
        JsonObject responseObj = dashboardService.getDashboardByOwnerId(ownerId);
        return gson.toJson(responseObj);
    }

    @ApiOperation(value = "Return the active dashboard of this owner.")
    @GetMapping(value = "/dashboard/active/{ownerId}", produces = "application/json")
    public String getActiveDashboardByOwnerId(@Valid @PathVariable("ownerId") String ownerId){
        JsonObject dashboardObj = dashboardService.getActiveDashboardByOwnerId(ownerId);
        return gson.toJson(dashboardObj);
    }

    @ApiOperation(value = "Return a dashboard by dashboard id.")
    @GetMapping(value = "/dashboard/{dashboardId}", produces = "application/json")
    public String getDashboardById(@Valid @PathVariable("dashboardId") Long dashboardId){
        JsonObject dashboardObj = dashboardService.getDashboardById(dashboardId);
        return gson.toJson(dashboardObj);
    }

    //delete dashboard by id
    @ApiOperation(value = "Delete a dashboard logically.")
    @DeleteMapping("/dashboard/{id}")
    @Transactional
    public BasicResponse deleteDashboardById(@Valid @PathVariable("id") long id){

        return dashboardService.logicDeleteDashboard(id);
    }

    @ApiOperation(value = "Update the active status of dashboards of an owner.")
    //update active status of dashboard
    @PostMapping("/dashboard/{id}")
    @Transactional
    public BasicResponse updateDashboardActiveStatus(@Valid @PathVariable("id") long id, @RequestBody String param) throws Exception{

        return dashboardService.updateDashboardActiveStatus(id, param);
    }

    @ApiOperation(value = "create or update dashboard config, id 0 to create, other for update.")
    //create or update dashboard config, id 0 to create, other for update
    @PostMapping("/dashboard/config/{id}")
    @Transactional
    public BasicResponse postDashboardConfig(@Valid @PathVariable("id") Long id, @RequestBody String jsonBodyStr) throws Exception{
        BasicResponse response = new BasicResponse();
        if (id == 0){
            response = dashboardService.createDashboardConfig(jsonBodyStr);
        }else {
            response = dashboardService.updateDashboardConfig(jsonBodyStr);
        }
        return response;
    }

    @ApiOperation(value = "Return dashboard config by id.")
    @GetMapping(value = "/dashboard/config/{id}", produces = "application/json")
    public String getDashboardConfig(@Valid @PathVariable("id") Long id){
        JsonObject responseBody = dashboardService.getDashboardConfig(id);
        return responseBody.toString();
    }

    @DeleteMapping("/dashboard/config")
    @Transactional
    public void deleteDashboardConfig(@RequestBody String params){
        dashboardService.logicDeleteDashboardConfigItem(params);
    }






}
