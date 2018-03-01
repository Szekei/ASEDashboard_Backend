package com.mfg.Service;

import com.mfg.Entity.*;
import com.mfg.Exception.NoDataFoundException;
import com.mfg.Model.BasicResponse;
import com.mfg.Model.TicketResponse;
import com.mfg.Repository.DashboardRepository;
import com.mfg.Repository.ProjectModuleRepository;
import com.mfg.Repository.TicketRepository;
import com.mfg.Repository.VersionRepository;
import com.mfg.config.Constants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by I309908 on 5/8/2017.
 */
@Component
public class BCPService {

    @Autowired
    private UserLogService userLogService;

    @Autowired
    private WebUtils webUtils;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private ProjectModuleRepository projectModuleRepository;

    public BasicResponse saveBCPTicket(String type, String dataStr, Long dashboardId) throws Exception{
        Version latestVersion = versionRepository.findTop1ByDashboardIdByOrderByIdDesc(dashboardId);
//        final Logger logger = LoggerFactory.getLogger(Constants.LOGGER_NAME_PREFIX+dashboardId+Constants.LOGGER_NAME_SUFFIX+latestVersion.getId());
        BasicResponse response = new BasicResponse();

        JSONObject jsonObj = XML.toJSONObject(dataStr);

        if (jsonObj.isNull("asx:abap") || jsonObj.getJSONObject("asx:abap").isNull("asx:values")){
            throw new NoDataFoundException("Ticket data from BCP system API is incomplete.");
        }
        JSONObject resultNodes = jsonObj.getJSONObject("asx:abap").getJSONObject("asx:values");
        Iterator it = resultNodes.keys();
        int[] priorityCountArr = new int[3];//index 0 for high, 1 for middle, 2 for low
        List<String> ticketIdStrList = new ArrayList<String>();

        Dashboard dashboard = dashboardRepository.findById(dashboardId);

        List<String> memberList = new ArrayList<String>();
        if (dashboard.getBcpServer() == null || dashboard.getBcpServer().getProjectMemberList().isEmpty()){
            throw new NoDataFoundException("No bcp server info found.");
        }
        for (ProjectMember projectMember : dashboard.getBcpServer().getProjectMemberList()){
            memberList.add(projectMember.getUserId());
        }
        String component = dashboard.getBcpServer().getComponent();
        while(it.hasNext()){
            String key = (String)it.next();
            if(!resultNodes.get(key).equals("")){
                if (resultNodes.getJSONObject(key).get("_-SID_-CN_IF_DEVDB_INC_OUT_S") instanceof JSONArray){
                    JSONArray jArray = resultNodes.getJSONObject(key).getJSONArray("_-SID_-CN_IF_DEVDB_INC_OUT_S");
                    for(int i = 0; i < jArray.length();i++){
                        JSONObject ticketObj = jArray.getJSONObject(i);
                        //process accessbility ticket
                        if(type.equals(Constants.BCP_INTERBAL_ACC) && ticketObj.getString("DESCRIPTION").contains("ACC-")){
                            //only save the ticket whose processor is in team
                            priorityCountArr = countTicketPriority(ticketObj, memberList, component, priorityCountArr, ticketIdStrList);
                        } else if(type.equals(Constants.BCP_INTERNAL) || type.equals(Constants.BCP_CUSTOMER)){
                            //only save the ticket whose processor is in team
                            priorityCountArr = countTicketPriority(ticketObj, memberList, component, priorityCountArr, ticketIdStrList);
                        }
                    }
                }else if (resultNodes.getJSONObject(key).get("_-SID_-CN_IF_DEVDB_INC_OUT_S") instanceof JSONObject){
                    JSONObject ticketObj = resultNodes.getJSONObject(key).getJSONObject("_-SID_-CN_IF_DEVDB_INC_OUT_S");
                    if(type.equals(Constants.BCP_INTERBAL_ACC) && ticketObj.getString("DESCRIPTION").contains("ACC-")){
                        //only save the ticket whose processor is in team
                        priorityCountArr = countTicketPriority(ticketObj, memberList, component, priorityCountArr, ticketIdStrList);
                    } else if(type.equals(Constants.BCP_INTERNAL) || type.equals(Constants.BCP_CUSTOMER)){
                        //only save the ticket whose processor is in team
                        priorityCountArr = countTicketPriority(ticketObj, memberList, component, priorityCountArr, ticketIdStrList);
                    }
                }
            }
        }

        for (int i = 0; i < priorityCountArr.length; i++){
            String priority = null;
            if (i == 0){
                priority = "High";
            }else if(i == 1){
                priority = "Medium";
            }else {
                priority = "Low";
            }

            Ticket ticket = new Ticket();
            ticket.setDashboard(dashboard);
            ticket.setComponent(component);
            ticket.setCount(priorityCountArr[i]);
            ticket.setPriority(priority);
            ticket.setType(type);
            ticket.setVersion(latestVersion);
            ticketRepository.save(ticket);
        }
        response.setStatus(Constants.STATUS_SUCCESSFUL);
        return response;

    }


    public void retrieveAndSaveTicket(String type, Long dashboardId) throws Exception{
        Version latestVersion = versionRepository.findTop1ByDashboardIdByOrderByIdDesc(dashboardId);
        final Logger logger = LoggerFactory.getLogger(Constants.LOGGER_NAME_PREFIX+dashboardId+Constants.LOGGER_NAME_SUFFIX+latestVersion.getId());
        try{
            Dashboard dashboard = dashboardRepository.findById(dashboardId);
            BCPServer bcpServer = dashboard.getBcpServer();
            if (bcpServer == null){
                throw new NoDataFoundException("No Bcp server configuration.");
            }
            List<ProjectMember> memberList = bcpServer.getProjectMemberList();
//        String url = "https://support.wdf.sap.corp/sap/bc/devdb/internal_incid?sap-client=001&user_id=I302473";
            StringBuffer url_buffer = new StringBuffer();
            if(type.contains(Constants.BCP_INTERNAL)){
                url_buffer.append(Constants.BCPInternalUrlBase);
            } else{
                url_buffer.append(Constants.BCPCustomerUrlBase);
            }
            for(ProjectMember member : memberList){
                url_buffer.append("&user_id=").append(member.getUserId());
            }

            String dataStr = webUtils.httpsGet(url_buffer.toString());
            saveBCPTicket(type, dataStr, dashboardId);
        }catch (Exception e){
            logger.error("Failed to save BCP ticket.", e);
            StringBuffer message = new StringBuffer();
            message.append("Failed to save BCP ticket.").append("\n").append(e.getMessage());
            userLogService.insertLogRecord(dashboardId, latestVersion.getId(),
                    message.toString(), "Error","BCP", null);
        }
    }


    public int[] countTicketPriority(JSONObject ticketObj, List projectMembers, String component, int[] priorityArr, List ticketIdStrList){
//        int[] priorityArr = new int[3];//index 0 for high, 1 for middle, 2 for low

        if(projectMembers.contains(ticketObj.getString("PROCESSOR_ID"))
                && (ticketObj.getString("CATEGORY").equalsIgnoreCase(component) || component == null || component.isEmpty())
                    && !ticketIdStrList.contains(ticketObj.get("OBJECT_ID").toString())){
            String ticketId = ticketObj.get("OBJECT_ID").toString();
            if(ticketObj.getString("PRIORITY_DESCR").equalsIgnoreCase("High")){
                priorityArr[0]++;
                ticketIdStrList.add(ticketId);
            }else if(ticketObj.getString("PRIORITY_DESCR").equalsIgnoreCase("Medium")){
                priorityArr[1]++;
                ticketIdStrList.add(ticketId);
            }else if(ticketObj.getString("PRIORITY_DESCR").equalsIgnoreCase("Low")){
                priorityArr[2]++;
                ticketIdStrList.add(ticketId);
            }
        }
        return priorityArr;
    }

    public List<TicketResponse> getBCPTicketData(Long dashboardId, String ticketType){
        Dashboard dashboard = dashboardRepository.findById(dashboardId);
        Version latestVersion = versionRepository.findTop1ByDashboardIdByOrderByIdDesc(dashboardId);
        List<TicketResponse> ticketResponses = new ArrayList<TicketResponse>();
        if (latestVersion == null){
            return ticketResponses;
        }
        Long vid = latestVersion.getId();
        Long evid = versionRepository.findTop1ByDashboardIdByOrderByIdAsc(dashboardId).getId();
        List<Ticket> tickets = ticketRepository.findByDashboard_idAndVersion_idAndType(dashboardId, vid, ticketType);
        boolean isLatest = true;
        while(tickets.isEmpty() && vid > evid){
            isLatest = false;
            vid--;
            tickets = ticketRepository.findByDashboard_idAndVersion_idAndType(dashboardId, vid, ticketType);
        }

        if (tickets != null){
            for (Ticket ticket : tickets){
                TicketResponse response = new TicketResponse();
                if (ticket.getComponent().isEmpty() || ticket.getComponent() == null){
                    response.setProject(getProjectName(dashboard));
                }else {
                    response.setProject(ticket.getComponent());
                }
                response.setCount(ticket.getCount());
                response.setPriority(ticket.getPriority());
                response.setLatest(isLatest);
                response.setSaveAt(ticket.getCreatedTime().toString());
                ticketResponses.add(response);
            }
        }
        return ticketResponses;
    }
    public String getProjectName(Dashboard dashboard){
        List<ProjectModule> projectList = projectModuleRepository.findProjectByDashId(dashboard);
        String projectName = null;
        if (projectList!= null && projectList.get(0).getName() != null){
            projectName = projectList.get(0).getName();
        }else {
            projectName = dashboard.getName();
        }
        return projectName;
    }

    public List<String> getBCPProjectMember(Long dashboardId){
        Dashboard dashboard = dashboardRepository.findById(dashboardId);
        List<String> stringList = new ArrayList<String>();
        if (dashboard.getBcpServer() != null){
            List<ProjectMember> projectMembers = dashboard.getBcpServer().getProjectMemberList();
            for (ProjectMember projectMember : projectMembers){
                stringList.add(projectMember.getUserId());
            }
        }
        return stringList;
    }

}
