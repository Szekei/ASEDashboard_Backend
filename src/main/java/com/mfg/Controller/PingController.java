package com.mfg.Controller;

import com.mfg.Model.BasicResponse;
import com.mfg.Service.PingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by I309908 on 5/4/2017.
 */
@Api(value = "Ping Controller", description = "Operations pertaining to pinging servers with credentials.")
@RequestMapping("/api/ping")
@RestController
public class PingController {

    @Autowired
    private PingService pingService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @ApiOperation(value = "Return if the credential used to ping jenkins server is authorized.")
    @PostMapping(value = "/jenkinsServer", produces = "application/json")
        public BasicResponse pingJenkinsServer(@RequestBody String jsonBodyStr) throws Exception{
        BasicResponse basicResponse = new BasicResponse();
        StringBuffer ping_url = new StringBuffer();
        try {
            boolean result = pingService.pingJenkinsServer(jsonBodyStr, ping_url);
            if (result){
                basicResponse.setStatus("Success");
                return basicResponse;
            }else {
                basicResponse.setStatus("Fail");
                return basicResponse;
            }
        }catch (Exception e){
//            e.printStackTrace();
            logger.error("Failed to access Jenkins server.Url: " + ping_url.toString(),e);
            throw e;
        }
    }

    @ApiOperation(value = "Return if the credential used to ping sonar server is authorized.")
    @PostMapping(value = "/sonarServer", produces = "application/json")
    public BasicResponse pingSonarServer(@RequestBody String jsonBodyStr) throws Exception{
        BasicResponse basicResponse = new BasicResponse();
        StringBuffer ping_url = new StringBuffer();
        try {
            boolean result = pingService.pingSonarServer(jsonBodyStr, ping_url);
            if (result){
                basicResponse.setStatus("Success");
                return basicResponse;
            }else {
                basicResponse.setStatus("Fail");
                return basicResponse;
            }
        }catch (Exception e){
//            e.printStackTrace();
            logger.error("Failed to access Sonar server.Url: "+ ping_url.toString(),e);
            throw e;
        }
    }
}
