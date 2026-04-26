package com.thesis.frauddetection.model;

import jakarta.persistence.*;

import java.time.Instant;
@Entity
@Table(name = "analysis_results")
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_analyzed", nullable = false, columnDefinition = "TEXT")
    private String contentAnalyzed;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private RiskLevel riskLevel;

    @Column(name = "motives", columnDefinition = "TEXT")
    private String motives;

    @Column(name = "source_ip", length = 45)
    private String sourceIp;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public String getContentAnalyzed() {
        return contentAnalyzed;
    }

    public void setContentAnalyzed(String contentAnalyzed) {
        this.contentAnalyzed = contentAnalyzed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getMotives() {
        return motives;
    }

    public void setMotives(String motives) {
        this.motives = motives;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
