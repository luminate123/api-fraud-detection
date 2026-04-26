package com.thesis.frauddetection.service;

import com.thesis.frauddetection.dto.AnalyzeRequestDto;
import com.thesis.frauddetection.dto.AnalyzeResponseDto;
import com.thesis.frauddetection.dto.OpenAiFraudResultDto;
import com.thesis.frauddetection.exception.BadRequestException;
import com.thesis.frauddetection.model.AnalysisResult;
import com.thesis.frauddetection.model.RiskLevel;
import com.thesis.frauddetection.repository.AnalysisResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
public class FraudDetectionService {

    private static final Logger log = LoggerFactory.getLogger(FraudDetectionService.class);

    private final OpenAiClientService openAiClientService;
    private final AnalysisResultRepository analysisResultRepository;

    public FraudDetectionService(OpenAiClientService openAiClientService,
                                 AnalysisResultRepository analysisResultRepository) {
        this.openAiClientService = openAiClientService;
        this.analysisResultRepository = analysisResultRepository;
    }

    public AnalyzeResponseDto analyze(AnalyzeRequestDto request, String sourceIp) {
        validateRequest(request);

        OpenAiFraudResultDto openAiResult = openAiClientService.analyze(request);

        // Normalizamos salida del modelo para evitar valores fuera de contrato.
        int score = normalizeScore(openAiResult.getScore());
        RiskLevel riskLevel = normalizeRiskLevel(openAiResult.getLevel(), score);
        List<String> reasons = normalizeReasons(openAiResult.getReasons());

        AnalysisResult analysis = new AnalysisResult();
        analysis.setCreatedAt(Instant.now());
        analysis.setSourceIp(normalizeSourceIp(sourceIp));
        analysis.setType(resolveType(request));
        analysis.setScore(score);
        analysis.setRiskLevel(riskLevel);
        analysis.setMotives(String.join("\n", reasons));
        analysis.setContentAnalyzed(extractContent(request));

        AnalysisResult saved = analysisResultRepository.save(analysis);
        log.info("Analisis {} guardado con score={} nivel={}", saved.getId(), saved.getScore(), saved.getRiskLevel());

        AnalyzeResponseDto response = new AnalyzeResponseDto();
        response.setAnalysisId(saved.getId());
        response.setRiskScore(saved.getScore());
        response.setRiskLevel(saved.getRiskLevel());
        response.setReasons(reasons);
        response.setAnalyzedAt(saved.getCreatedAt());
        return response;
    }

    private void validateRequest(AnalyzeRequestDto request) {
        boolean hasText = request.getText() != null && !request.getText().isBlank();
        boolean hasImage = request.getImageBase64() != null && !request.getImageBase64().isBlank();

        if (!hasText && !hasImage) {
            throw new BadRequestException("Debes enviar text o imageBase64");
        }

        if (hasText && hasImage) {
            throw new BadRequestException("Envia solo text o solo imageBase64, no ambos");
        }
    }

    private int normalizeScore(Integer score) {
        if (score == null) {
            return 50;
        }
        return Math.max(0, Math.min(100, score));
    }

    private RiskLevel normalizeRiskLevel(String level, int score) {
        if (level != null && !level.isBlank()) {
            String normalized = level.trim().toUpperCase(Locale.ROOT)
                    .replace('Í', 'I');
            if ("CRITICO".equals(normalized)) {
                return RiskLevel.CRITICO;
            }
            try {
                return RiskLevel.valueOf(normalized);
            } catch (IllegalArgumentException ignored) {
                log.warn("Nivel de riesgo no reconocido desde OpenAI: {}. Se inferira por score", level);
            }
        }

        if (score <= 25) return RiskLevel.BAJO;
        if (score <= 50) return RiskLevel.MEDIO;
        if (score <= 75) return RiskLevel.ALTO;
        return RiskLevel.CRITICO;
    }

    private List<String> normalizeReasons(List<String> reasons) {
        if (reasons == null || reasons.isEmpty()) {
            return List.of("No se recibieron motivos detallados del modelo");
        }
        return reasons.stream()
                .filter(item -> item != null && !item.isBlank())
                .map(String::trim)
                .limit(8)
                .toList();
    }

    private String resolveType(AnalyzeRequestDto request) {
        if (request.getImageBase64() != null && !request.getImageBase64().isBlank()) {
            return "IMAGE";
        }
        if (request.getChannel() != null && "SMS".equalsIgnoreCase(request.getChannel().trim())) {
            return "SMS";
        }
        return "TEXT";
    }

    private String extractContent(AnalyzeRequestDto request) {
        if (request.getText() != null && !request.getText().isBlank()) {
            return request.getText().trim();
        }
        return request.getImageBase64();
    }

    private String normalizeSourceIp(String sourceIp) {
        if (sourceIp == null || sourceIp.isBlank()) {
            return null;
        }
        if (sourceIp.length() <= 45) {
            return sourceIp;
        }
        return sourceIp.substring(0, 45);
    }
}
