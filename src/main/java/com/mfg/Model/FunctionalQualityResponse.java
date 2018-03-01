package com.mfg.Model;

/**
 * Created by I309908 on 1/18/2017.
 */
public class FunctionalQualityResponse {
    public String moduleName;
    public int issues = -1;
    public String coverage = "%";
    public boolean isLatest;
    public String saveAt;
    public boolean isMain;
    public int codeLine;
    public String jenkinsUrl;
    public String sonarUrl;

    public FunctionalQualityResponse() {

    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public int getIssues() {
        return issues;
    }

    public void setIssues(int issues) {
        this.issues = issues;
    }

    public String getCoverage() {
        return coverage;
    }

    public void setCoverage(String coverage) {
        this.coverage = coverage;
    }

    public boolean isLatest() {
        return isLatest;
    }

    public void setLatest(boolean latest) {
        isLatest = latest;
    }

    public String getSaveAt() {
        return saveAt;
    }

    public void setSaveAt(String saveAt) {
        this.saveAt = saveAt;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    public int getCodeLine() {
        return codeLine;
    }

    public void setCodeLine(int codeLine) {
        this.codeLine = codeLine;
    }

    public String getJenkinsUrl() {
        return jenkinsUrl;
    }

    public void setJenkinsUrl(String jenkinsUrl) {
        this.jenkinsUrl = jenkinsUrl;
    }

    public String getSonarUrl() {
        return sonarUrl;
    }

    public void setSonarUrl(String sonarUrl) {
        this.sonarUrl = sonarUrl;
    }
}
