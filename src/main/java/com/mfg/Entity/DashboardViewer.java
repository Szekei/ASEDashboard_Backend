package com.mfg.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by I309908 on 5/8/2017.
 */
@Entity
public class DashboardViewer {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String viewerId;

    @Column(nullable = false)
    private Long dashboardId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getViewerId() {
        return viewerId;
    }

    public void setViewerId(String viewerId) {
        this.viewerId = viewerId;
    }

    public Long getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(Long dashboardId) {
        this.dashboardId = dashboardId;
    }
}