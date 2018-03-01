package com.mfg.Entity;

import org.hibernate.annotations.Cascade;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by I309908 on 1/13/2017.
 */
@Entity
public class Ticket {
    @Id
    @GeneratedValue
    private Long ticketId;

    @Column(nullable = false)
    private String priority;

    @Column(nullable = true)
    private String createdBy;

    @Column(nullable = false)
    private Timestamp createdTime;

    @Column(nullable = true)
    private String component;

    @Column(nullable = false)
    private String type;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "version_id")
    private Version version;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "dashboard_id")
    private Dashboard dashboard;

    @Column
    private int count;

    public Ticket() {
    }

    public Long getTicketId() {
        return ticketId;
    }

    public String getPriority() {
        return priority;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public String getComponent() {
        return component;
    }

    public String getType() {
        return type;
    }

    public Version getVersion() {
        return version;
    }

    public int getCount() {
        return count;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    @PrePersist
    protected void onPersist(){
        this.createdTime = now();
    }

    protected static Timestamp now(){
        return new Timestamp(new Date().getTime());
    }

}
