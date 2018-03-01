package com.mfg.Model;

/**
 * Created by I309908 on 1/18/2017.
 */
public class JenkinsStatus {
    public String moduleName;
    public String url;
    public String color;
    public boolean isLatest;
    public String saveAt;
    public boolean isMain;

    public JenkinsStatus() {
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    @Override
    public String toString() {
        return "JenkinsStatus{" +

                ", url='" + url + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
