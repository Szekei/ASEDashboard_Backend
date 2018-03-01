package com.mfg.Model;

/**
 * Created by I309908 on 1/16/2017.
 */
public class CodeDebtResponse {
    public String moduleName;
    public String maintainability;
    public String debtRatio;
    public boolean isLatest;
    public String saveAt;

    public CodeDebtResponse() {
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getMaintainability() {
        return maintainability;
    }

    public void setMaintainability(String maintainability) {
        this.maintainability = maintainability;
    }

    public String getDebtRatio() {
        return debtRatio;
    }

    public void setDebtRatio(String debtRatio) {
        this.debtRatio = debtRatio;
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


}
