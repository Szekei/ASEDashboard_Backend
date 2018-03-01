package com.mfg.Model;

import com.mfg.Entity.Version;
import org.slf4j.Logger;

/**
 * Created by I309908 on 6/26/2017.
 */
public class LoggerParam {
    private Logger logger;
    private Version latestVersion;
    private Long dashboardId;
    private String level;
    private String dataType;

    public LoggerParam(Logger logger, Version latestVersion, Long dashboardId, String level, String dataType) {
        this.logger = logger;
        this.latestVersion = latestVersion;
        this.dashboardId = dashboardId;
        this.level = level;
        this.dataType = dataType;
    }

    public LoggerParam(Logger logger, Version latestVersion, Long dashboardId, String level) {
        this.logger = logger;
        this.latestVersion = latestVersion;
        this.dashboardId = dashboardId;
        this.level = level;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Version getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(Version latestVersion) {
        this.latestVersion = latestVersion;
    }

    public Long getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(Long dashboardId) {
        this.dashboardId = dashboardId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
