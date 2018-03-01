package com.mfg.Entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by I309908 on 4/18/2017.
 */
@Entity
public class BCPServer {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = true)
    private String url;

    @Column(nullable = true)
    private String component;

    @Column(nullable = true)
    private Blob certificate;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "dashboard_id")
    private Dashboard dashboard;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bcpServer", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<ProjectMember> projectMemberList = new ArrayList<ProjectMember>();

    public BCPServer() {
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

    public Blob getCertificate() {
        return certificate;
    }

    public void setCertificate(Blob certificate) {
        this.certificate = certificate;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public List<ProjectMember> getProjectMemberList() {
        return projectMemberList;
    }

    public void setProjectMemberList(List<ProjectMember> projectMemberList) {
        this.projectMemberList = projectMemberList;
    }
}
