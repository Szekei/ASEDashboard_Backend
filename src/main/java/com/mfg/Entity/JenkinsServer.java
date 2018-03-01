package com.mfg.Entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by I309908 on 4/17/2017.
 */
@Entity
public class JenkinsServer {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    @Size(max = 512)
    private String password;

    @Column(nullable = true)
    private boolean isPipeLineJob;

    @Column(nullable = false)
    private boolean isMain = true;

    @OneToMany(mappedBy = "jenkinsServer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    List<JenkinsJob> jenkinsJobList = new ArrayList<JenkinsJob>();

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "dashboard_id")
    private Dashboard dashboard;

    public JenkinsServer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPipeLineJob() {
        return isPipeLineJob;
    }

    public void setPipeLineJob(boolean pipeLineJob) {
        isPipeLineJob = pipeLineJob;
    }

    public List<JenkinsJob> getJenkinsJobList() {
        return jenkinsJobList;
    }

    public void setJenkinsJobList(List<JenkinsJob> jenkinsJobList) {
        this.jenkinsJobList = jenkinsJobList;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }



}
