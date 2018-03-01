package com.mfg.Entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by I309908 on 1/18/2017.
 */
@Entity
public class JenkinsJob {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = true)
    private boolean isPipelineJob;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "projectModule_id")
    private ProjectModule projectModule;

    @OneToMany(mappedBy = "jenkinsJob", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    List<ParsingRule> parsingRuleList = new ArrayList<ParsingRule>();

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "jenkinsServer_id")
    private JenkinsServer jenkinsServer;

    @Column(nullable = false)
    private boolean isVisible = true;



    public JenkinsJob() {
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isPipelineJob() {
        return isPipelineJob;
    }

    public void setPipelineJob(boolean pipelineJob) {
        isPipelineJob = pipelineJob;
    }

    public ProjectModule getProjectModule() {
        return projectModule;
    }

    public void setProjectModule(ProjectModule projectModule) {
        this.projectModule = projectModule;
    }

    public List<ParsingRule> getParsingRuleList() {
        return parsingRuleList;
    }

    public void setParsingRuleList(List<ParsingRule> parsingRuleList) {
        this.parsingRuleList = parsingRuleList;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public JenkinsServer getJenkinsServer() {
        return jenkinsServer;
    }

    public void setJenkinsServer(JenkinsServer jenkinsServer) {
        this.jenkinsServer = jenkinsServer;
    }


}
