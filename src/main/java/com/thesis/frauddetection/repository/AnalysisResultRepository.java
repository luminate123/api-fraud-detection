package com.thesis.frauddetection.repository;

import com.thesis.frauddetection.model.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
}
