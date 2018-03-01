package com.mfg.Controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mfg.Entity.*;
import com.mfg.Model.APITestResponse;
import com.mfg.Model.FunctionalQualityResponse;
import com.mfg.Repository.DashboardRepository;
import com.mfg.Repository.FunctionalQualityRepository;
import com.mfg.Repository.ProjectModuleRepository;
import com.mfg.Repository.VersionRepository;
import com.mfg.Service.FunctionalQualityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by I309908 on 5/8/2017.
 */
@Api(value = "Functional Quality Controller", description = "Operations pertaining to functional quality data")
@RequestMapping("/api")
@RestController
public class FunctionalQualityController {
//    @Autowired
//    private ProjectModuleRepository projectModuleRepository;
//
//    @Autowired
//    private DashboardRepository dashboardRepository;
//
//    @Autowired
//    private VersionRepository versionRepository;
//
//    @Autowired
//    private FunctionalQualityRepository functionalQualityRepository;

    @Autowired
    private FunctionalQualityService functionalQualityService;

    @ApiOperation(value = "Return UT data of this dashboard in this type.")
    @GetMapping(value = "/functionalQuality/{utType}/{dashboardId}/{level}", produces = "application/json")
    public String getUTData(@PathVariable("dashboardId") Long dashboardId,
                                                     @PathVariable("level") String level,
                                                     @PathVariable("utType") String utType){

        if (!utType.equalsIgnoreCase("FrontendUT")&&!utType.equalsIgnoreCase("BackendUT")){
            throw new BadRequestException("UT type is not applicable.");
        }

        return functionalQualityService.getUTdataRecords(dashboardId, level, utType);
    }

    @ApiOperation(value = "Return apiTest data of this dashboard in this type.")
    @GetMapping(value = "/functionalQuality/apiTest/{dashboardId}/{level}", produces = "application/json")
    public String getApiTestData(@PathVariable("dashboardId") Long dashboardId, @PathVariable("level") String level){

        return functionalQualityService.getApiTestDataRecords(dashboardId, level);
    }

//    public List<ProjectModule> getProjectOrModuleByDashboardId(Dashboard dashboard, String level){
//        List<ProjectModule> pmList = new ArrayList<ProjectModule>();
//        if (level.equalsIgnoreCase("Project")){
//            pmList = projectModuleRepository.findProjectByDashId(dashboard);
//        }else if(level.equalsIgnoreCase("Module")){
//            pmList = projectModuleRepository.findModuleByDashId(dashboard);
//        }
//
//        return pmList;
//    }

//    public String wrapUTResponseObj(String main_url,String ref_url, List<FunctionalQualityResponse> dataList){
//        JsonObject responseObj = new JsonObject();
//        JsonObject mainDataObj = new JsonObject();
//        JsonObject refDataObj = new JsonObject();
//        List<FunctionalQualityResponse> mainList = new ArrayList<FunctionalQualityResponse>();
//        List<FunctionalQualityResponse> refList = new ArrayList<FunctionalQualityResponse>();
//        Gson gson = new Gson();
//        int mainTotal = 0;
//        int refTotal = 0;
//        List<String> coveredModules = new ArrayList<String>();
//        for (FunctionalQualityResponse fqr : dataList){
//            if (fqr.isMain()){
//                mainList.add(fqr);
//                mainTotal += fqr.getIssues();
//            }else {
//                refTotal += fqr.getIssues();
//                refList.add(fqr);
//            }
//        }
//        mainDataObj.addProperty("url", main_url);
//        mainDataObj.add("data", gson.toJsonTree(mainList));
//
//        refDataObj.addProperty("url", ref_url);
//        refDataObj.add("data", gson.toJsonTree(refList));
//
//        if (mainList.isEmpty() && !refList.isEmpty()){
//            responseObj.addProperty("count", refTotal);
//        }else {
//            responseObj.addProperty("count", mainTotal);
//        }
//        responseObj.add("mainData",mainDataObj);
//        responseObj.add("refData",refDataObj);
//        return gson.toJson(responseObj);
//    }
//
//    public String wrapApiResponseObj(String main_url,String ref_url, List<APITestResponse> dataList){
//        JsonObject responseObj = new JsonObject();
//        JsonObject mainDataObj = new JsonObject();
//        JsonObject refDataObj = new JsonObject();
//        List<APITestResponse> mainList = new ArrayList<APITestResponse>();
//        List<APITestResponse> refList = new ArrayList<APITestResponse>();
//        Gson gson = new Gson();
//        int mainTotal = 0;
//        int refTotal = 0;
//        for (APITestResponse atr : dataList){
//            if (atr.isMain()){
//                mainList.add(atr);
//                mainTotal += atr.getIssues();
//            }else {
//                refList.add(atr);
//                refTotal += atr.getIssues();
//            }
//        }
//        mainDataObj.addProperty("url", main_url);
//        mainDataObj.add("data", gson.toJsonTree(mainList));
//
//        refDataObj.addProperty("url", ref_url);
//        refDataObj.add("data", gson.toJsonTree(refList));
//        if (mainList.isEmpty() && !refList.isEmpty()){
//            responseObj.addProperty("count", refTotal);
//        }else {
//            responseObj.addProperty("count", mainTotal);
//        }
//        responseObj.add("mainData",mainDataObj);
//        responseObj.add("refData",refDataObj);
//        return gson.toJson(responseObj);
//    }


}