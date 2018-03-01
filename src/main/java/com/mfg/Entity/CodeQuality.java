package com.mfg.Entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by I309908 on 2/15/2017.
 */
@Entity
public class CodeQuality {
    @Id
    @GeneratedValue
    private Long Id;

    @Column(nullable = true)
    private String priority;

    @Column(nullable = true)
    private String maintainability;

    @Column(nullable = true)
    private float count;

    @Column(nullable = true)
    private String createdBy;

    @Column(nullable = false)
    private Timestamp createdTime;

    @Column(nullable = false)
    private String type;

    @ManyToOne(cascade= CascadeType.MERGE)
    @JoinColumn(name = "version_id")
    private Version version;

    @ManyToOne(cascade= CascadeType.MERGE)
    @JoinColumn(name = "projectModule_id")
    private ProjectModule projectModule;

    public CodeQuality() {
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getMaintainability() {
        return maintainability;
    }

    public void setMaintainability(String maintainability) {
        this.maintainability = maintainability;
    }

    public float getCount() {
        return count;
    }

    public void setCount(float count) {
        this.count = count;
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

    public ProjectModule getProjectModule() {
        return projectModule;
    }

    public void setProjectModule(ProjectModule projectModule) {
        this.projectModule = projectModule;
    }

    @PrePersist
    protected void onPersist(){
        this.createdTime = now();
    }


    protected static Timestamp now(){
        return new Timestamp(new Date().getTime());
    }

}

