package com.mfg.Controller;

import com.mfg.Model.TicketResponse;
import com.mfg.Service.BCPService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by I309908 on 5/8/2017.
 */
@Api(value = "Bcp Controller", description = "Operations pertaining to bcp ticket")
@RequestMapping("/api")
@RestController
public class BCPController {

    @Autowired
    private BCPService bcpService;

    @ApiOperation(value = "Return a list of ticket data grouped by priority.")
    @GetMapping(value = "/ticket/{dashboardId}/{ticketType}", produces = "application/json")
    public List<TicketResponse> getBCPTicket(@Valid @PathVariable("dashboardId") Long dashboardId,
                                             @Valid @PathVariable("ticketType") String ticketType){

        return bcpService.getBCPTicketData(dashboardId, ticketType);
    }

    @ApiOperation(value = "Return a list of project members whose tickets are counted in the total ticket number in this dashboard.")
    @GetMapping(value = "/bcp/projectMember/{dashboardId}", produces = "application/json")
    public List<String> getProjectMember(@Valid @PathVariable("dashboardId") Long dashboardId){

        return bcpService.getBCPProjectMember(dashboardId);
    }

    @GetMapping(value = "/bcp/trigger/{dashboardId}", produces = "application/json")
    public void trigger(@Valid @PathVariable("dashboardId") Long dashboardId) throws Exception{

        bcpService.retrieveAndSaveTicket("internal", dashboardId);
    }

}
