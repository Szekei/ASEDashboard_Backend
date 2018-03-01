package com.mfg.Entity;

import javax.persistence.*;
import javax.persistence.Id;

/**
 * Created by I309908 on 4/17/2017.
 */
@Entity
public class ProjectMember {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String userId;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "bcpServer_id")
    private BCPServer bcpServer;

    public ProjectMember() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BCPServer getBcpServer() {
        return bcpServer;
    }

    public void setBcpServer(BCPServer bcpServer) {
        this.bcpServer = bcpServer;
    }
}

