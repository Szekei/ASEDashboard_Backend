package com.mfg.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mfg.Entity.UserLog;
import com.mfg.Entity.Version;
import com.mfg.Exception.NoDataFoundException;
import com.mfg.Exception.ParseErrorException;
import com.mfg.Exception.RequestErrorException;
import com.mfg.Repository.DashboardRepository;
import com.mfg.Repository.UserLogRepository;
import com.mfg.Repository.VersionRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.NoContentException;
import java.util.List;

/**
 * Created by I309908 on 5/18/2017.
 */
@Component
public class UserLogService {

    @Autowired
    private UserLogRepository userLogRepository;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    public String getLogByDashboardId(Long dashboardId){
        Version version = versionRepository.findTop1ByDashboardIdByOrderByIdDesc(dashboardId);
        Gson gson = new Gson();
        JsonArray responseArray = new JsonArray();
        if (version != null){
            List<UserLog> userLogList = userLogRepository.findByDashboardIdAndVersionId(dashboardId, version.getId());
            StringBuffer message = new StringBuffer();

            for (UserLog userLog : userLogList){
                JsonObject logObj = new JsonObject();
                logObj.addProperty("dashboardId", dashboardId);
                logObj.addProperty("createTime", userLog.getCreatedTime().toString());
                logObj.addProperty("dataType", userLog.getDataType());
                logObj.addProperty("level", userLog.getLevel());
                logObj.addProperty("message", userLog.getMessage());
                responseArray.add(logObj);
            }

            return gson.toJson(responseArray);
        }
        else {
            return gson.toJson(responseArray);
        }
    }

    public void insertLogRecord(Long dashboardId, Long versionId, String message, String level, String dataType, String moduleType){
        UserLog userLog = new UserLog();
        userLog.setDashboardId(dashboardId);
        userLog.setVersionId(versionId);
        userLog.setMessage(message);
        userLog.setLevel(level);
        userLog.setDataType(dataType);
        userLog.setModuleType(moduleType);
        userLogRepository.save(userLog);
    }

    public void insertLogRecord(Long dashboardId, Long versionId, String message, String level, String dataType){
        UserLog userLog = new UserLog();
        userLog.setDashboardId(dashboardId);
        userLog.setVersionId(versionId);
        userLog.setMessage(message);
        userLog.setLevel(level);
        userLog.setDataType(dataType);
        userLogRepository.save(userLog);
    }

    public void processException(Exception e, Logger logger, Long dashboardId, Version latestVersion, String moduleType, String dataType){
        if (e instanceof RequestErrorException || e instanceof ParseErrorException
                || e instanceof NoContentException || e instanceof NoDataFoundException){
            logger.error(e.getMessage(), e);
            StringBuffer message = new StringBuffer();
            message.append(String.format("Failed to get and save data of %s.", dataType)).append("\n").append(e.getMessage());
            while (e.getCause() != null){
                message.append("\n").append(e.getCause().getMessage());
                e = (Exception) e.getCause();
            }
            insertLogRecord(dashboardId, latestVersion.getId(),
                    message.toString(), "Error", dataType, moduleType);
        } else {
            logger.error(String.format("Failed to get and save data of %s.", dataType), e);
            StringBuffer message = new StringBuffer();
            message.append(String.format("Failed to get and save data of %s.", dataType)).append("\n").append(e.getMessage());
            while (e.getCause() != null){
                message.append("\n").append(e.getCause().getMessage());
                e = (Exception) e.getCause();
            }
            insertLogRecord(dashboardId, latestVersion.getId(),
                    message.toString(), "Error", dataType, moduleType);
        }
    }

    public void processException(Exception e, Logger logger, Long dashboardId, String type){
        Version latestVersion = versionRepository.findTop1ByDashboardIdByOrderByIdDesc(dashboardId);
        if (latestVersion == null){
            Version version = new Version();
            version.setDashboard(dashboardRepository.findById(dashboardId));
            latestVersion = versionRepository.save(version);
        }
        logger.error(String.format("Failed to %s", type), e);
        StringBuffer message = new StringBuffer();
        message.append(String.format("Failed to %s.Please contact us.", type));
        insertLogRecord(dashboardId, latestVersion.getId(),
                message.toString(), "Error", type);
    }

    public void processException(Logger logger, Long dashboardId, String type, String message){
        Version latestVersion = versionRepository.findTop1ByDashboardIdByOrderByIdDesc(dashboardId);
        if (latestVersion == null){
            Version version = new Version();
            version.setDashboard(dashboardRepository.findById(dashboardId));
            latestVersion = versionRepository.save(version);
        }
        logger.error(message);
        insertLogRecord(dashboardId, latestVersion.getId(),
                message, "Error", type);
    }

}
