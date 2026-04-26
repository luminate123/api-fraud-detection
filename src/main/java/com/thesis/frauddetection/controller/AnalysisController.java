package com.thesis.frauddetection.controller;

import com.thesis.frauddetection.dto.AnalyzeRequestDto;
import com.thesis.frauddetection.dto.AnalyzeResponseDto;
import com.thesis.frauddetection.service.FraudDetectionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AnalysisController {

    private final FraudDetectionService fraudDetectionService;

    public AnalysisController(FraudDetectionService fraudDetectionService) {
        this.fraudDetectionService = fraudDetectionService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AnalyzeResponseDto> analyze(@Valid @RequestBody AnalyzeRequestDto request,
                                                      HttpServletRequest httpRequest) {
        String sourceIp = resolveClientIp(httpRequest);
        AnalyzeResponseDto response = fraudDetectionService.analyze(request, sourceIp);
        return ResponseEntity.ok(response);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
