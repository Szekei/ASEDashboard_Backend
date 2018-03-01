package com.mfg.Entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by I309908 on 4/17/2017.
 */
@Entity
public class JenkinsJobStatus {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Timestamp createdTime;

    @ManyToOne(cascade= CascadeType.MERGE)
    @JoinColumn(name = "version_id")
    private Version version;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "jenkinsJob_id")
    private JenkinsJob jenkinsJob;


    @Column(nullable = false)
    private String url;

    public JenkinsJobStatus() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @PrePersist
    protected void onPersist(){
        this.createdTime = now();
    }

    protected static Timestamp now(){
        return new Timestamp(new Date().getTime());
    }

    public JenkinsJob getJenkinsJob() {
        return jenkinsJob;
    }

    public void setJenkinsJob(JenkinsJob jenkinsJob) {
        this.jenkinsJob = jenkinsJob;
    }
}
