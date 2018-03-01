package com.mfg.Entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by I309908 on 1/18/2017.
 */
@Entity
public class FunctionalQuality {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = true)
    private String createdBy;

    @Column(nullable = false)
    private Timestamp createdTime;

    @Column(nullable = false)
    private String type;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "version_id")
    private Version version;

    @Column(nullable = true)
    private int count1;

    @Column(nullable = true)
    private int count2;

    @Column(nullable = true)
    private float coverage;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "jenkinsJob_id")
    private JenkinsJob jenkinsJob;


    public FunctionalQuality() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
            this.createdTime = createdTime;

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public int getCount1() {
        return count1;
    }

    public void setCount1(int count1) {
        this.count1 = count1;
    }

    public int getCount2() {
        return count2;
    }

    public void setCount2(int count2) {
        this.count2 = count2;
    }

    public float getCoverage() {
        return coverage;
    }

    public void setCoverage(float coverage) {
        this.coverage = coverage;
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
