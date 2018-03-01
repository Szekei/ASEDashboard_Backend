package com.mfg.Entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by I309908 on 1/13/2017.
 */
@Entity
public class Version {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Timestamp createdTime;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "dashboard_id")
    private Dashboard dashboard;

    public Version() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    @PrePersist
    protected void onPersist(){
        this.createdTime = now();
    }

    protected static Timestamp now(){
        return new Timestamp(new Date().getTime());
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }
}
