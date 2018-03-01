package com.mfg.Entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by I309908 on 4/17/2017.
 */
@Entity
public class ProjectModule {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String createBy;

    @Column(nullable = false)
    private Timestamp createdTime;

    @Column(nullable = true)
    private Timestamp updatedTime;

    @Column(nullable = true)
    private String updateBy;

    @Column(nullable = true)
    private Long parentId;

    @Column(nullable = true)
    private String componentName;

    @Column(nullable = true)
    private String sonarKey;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "projectModule", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<CodeQuality> codeQualityList = new ArrayList<CodeQuality>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "projectModule", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<JenkinsJob> jenkinsJobList;

    @ManyToOne(cascade= CascadeType.MERGE)
    @JoinColumn(name = "dashboard_id")
    private Dashboard dashboard;

    @Column(nullable = false)
    private boolean isSonarTask;

    @Column(nullable = false)
    private boolean isVisible = true;


    public ProjectModule() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
            this.createdTime = createdTime;
    }

    public Timestamp getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Timestamp updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getSonarKey() {
        return sonarKey;
    }

    public void setSonarKey(String sonarKey) {
        this.sonarKey = sonarKey;
    }

    @PrePersist
    protected void onPersist(){
        this.createdTime = now();
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedTime = now();
    }

    protected static Timestamp now(){
        return new Timestamp(new Date().getTime());
    }

    public List<JenkinsJob> getVisibleJenkinsJobList() {
        List<JenkinsJob> visibleList = new ArrayList<JenkinsJob>();
        if (jenkinsJobList != null) {
            for (JenkinsJob jenkinsJob : jenkinsJobList) {
                if (jenkinsJob.isVisible()) {
                    visibleList.add(jenkinsJob);
                }
            }
        }
        return visibleList;
    }

    public void setJenkinsJobList(List<JenkinsJob> jenkinsJobList) {
        this.jenkinsJobList = jenkinsJobList;
    }

    public boolean isSonarTask() {
        return isSonarTask;
    }

    public void setSonarTask(boolean sonarTask) {
        isSonarTask = sonarTask;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public List<CodeQuality> getCodeQualityList() {
        return codeQualityList;
    }

    public void setCodeQualityList(List<CodeQuality> codeQualityList) {
        this.codeQualityList = codeQualityList;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

}
