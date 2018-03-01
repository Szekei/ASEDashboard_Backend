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
public class Dashboard {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Timestamp createdTime;

    @Column(nullable = true)
    private String createdBy;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private String owner;

    @Column(nullable = false)
    private boolean isVisible = true;

    //scheduled time to trigger data job
    @Column(nullable = false)
    private String cronStr = "00 00 07 * * ?";

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "dashboard", fetch = FetchType.EAGER)
    private SonarServer sonarServer;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dashboard", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<ProjectModule> projectModuleList =  new ArrayList<ProjectModule>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dashboard", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<JenkinsServer> jenkinsServerList =  new ArrayList<JenkinsServer>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dashboard", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Version> versionList =  new ArrayList<Version>();

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "dashboard", fetch = FetchType.EAGER)
    private BCPServer bcpServer;

    public Dashboard() {

    }

    public Long getId() {
        return id;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public String getOwner() {
        return owner;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @PrePersist
    protected void onPersist(){
        this.createdTime = now();
    }

    protected static Timestamp now(){
        return new Timestamp(new Date().getTime());
    }

    public SonarServer getSonarServer() {
        return sonarServer;
    }

    public void setSonarServer(SonarServer sonarServer) {
        this.sonarServer = sonarServer;
    }

    public List<ProjectModule> getVisibleProjectModuleList() {
        List<ProjectModule> visiblePMList = new ArrayList<ProjectModule>();
        if (projectModuleList != null && !projectModuleList.isEmpty()){
            for (ProjectModule pm : projectModuleList){
                if (pm.isVisible()){
                    visiblePMList.add(pm);
                }
            }
            return visiblePMList;
        }
        return projectModuleList;
    }

    public void setProjectModuleList(List<ProjectModule> projectModuleList) {
        this.projectModuleList = projectModuleList;
    }

    public List<Version> getVersionList() {
        return versionList;
    }

    public void setVersionList(List<Version> versionList) {
        this.versionList = versionList;
    }

    public BCPServer getBcpServer() {
        return bcpServer;
    }

    public void setBcpServer(BCPServer bcpServer) {
        this.bcpServer = bcpServer;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public String getCronStr() {
        return cronStr;
    }

    public void setCronStr(String cronStr) {
        this.cronStr = cronStr;
    }

    public List<ProjectModule> getProjectModuleList() {
        return projectModuleList;
    }

    public List<JenkinsServer> getJenkinsServerList() {
        return jenkinsServerList;
    }

    public void setJenkinsServerList(List<JenkinsServer> jenkinsServerList) {
        this.jenkinsServerList = jenkinsServerList;
    }
}
