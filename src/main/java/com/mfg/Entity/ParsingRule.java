package com.mfg.Entity;

import javax.persistence.*;

/**
 * Created by I309908 on 4/18/2017.
 */
@Entity
public class ParsingRule {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = true)
    private String reportFormat;

    @Column(nullable = false)
    private String startIdentifier;

    @Column(nullable = false)
    private String endIdentifier;

    @ManyToOne(cascade= CascadeType.MERGE)
    @JoinColumn(name = "jenkinsJob_id")
    private JenkinsJob jenkinsJob;

    public ParsingRule() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReportFormat() {
        return reportFormat;
    }

    public void setReportFormat(String reportFormat) {
        this.reportFormat = reportFormat;
    }

    public String getStartIdentifier() {
        return startIdentifier;
    }

    public void setStartIdentifier(String startIdentifier) {
        this.startIdentifier = startIdentifier;
    }

    public String getEndIdentifier() {
        return endIdentifier;
    }

    public void setEndIdentifier(String endIdentifier) {
        this.endIdentifier = endIdentifier;
    }

    public JenkinsJob getJenkinsJob() {
        return jenkinsJob;
    }

    public void setJenkinsJob(JenkinsJob jenkinsJob) {
        this.jenkinsJob = jenkinsJob;
    }
}
