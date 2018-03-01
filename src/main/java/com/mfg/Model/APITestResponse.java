package com.mfg.Model;

/**
 * Created by I309908 on 1/19/2017.
 */
public class APITestResponse {
    public String moduleName;
    public int issues = -1;
    public int cases = -1;
    public boolean isLatest;
    public String saveAt;
    private boolean isMain;
    private String jenkinsUrl;

    public APITestResponse() {
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

    public int getCases() {
        return cases;
    }

    public void setCases(int cases) {
        this.cases = cases;
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

    public String getJenkinsUrl() {
        return jenkinsUrl;
    }

    public void setJenkinsUrl(String jenkinsUrl) {
        this.jenkinsUrl = jenkinsUrl;
    }
}
