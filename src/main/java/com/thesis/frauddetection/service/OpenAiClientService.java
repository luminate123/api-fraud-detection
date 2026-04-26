package com.thesis.frauddetection.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thesis.frauddetection.dto.AnalyzeRequestDto;
import com.thesis.frauddetection.dto.OpenAiFraudResultDto;
import com.thesis.frauddetection.exception.OpenAiIntegrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class OpenAiClientService {

    private static final Logger log = LoggerFactory.getLogger(OpenAiClientService.class);
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openai.api-key}")
    private String openAiApiKey;

    @Value("${openai.model:gpt-4o}")
    private String model;

    public OpenAiClientService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public OpenAiFraudResultDto analyze(AnalyzeRequestDto request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openAiApiKey);

            Map<String, Object> payload = buildPayload(request);
            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(payload, headers);

            String rawResponse = restTemplate.postForObject(OPENAI_URL, httpEntity, String.class);
            JsonNode root = objectMapper.readTree(rawResponse);
            String content = root.path("choices").path(0).path("message").path("content").asText();

            if (content == null || content.isBlank()) {
                throw new OpenAiIntegrationException("OpenAI no devolvio contenido interpretable", null);
            }

            return objectMapper.readValue(content, OpenAiFraudResultDto.class);
        } catch (OpenAiIntegrationException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error consumiendo OpenAI", ex);
            throw new OpenAiIntegrationException("No se pudo completar el analisis con OpenAI", ex);
        }
    }

    private Map<String, Object> buildPayload(AnalyzeRequestDto request) {
        String systemPrompt = "Eres analista senior anti-fraude digital. Evalua phishing/smishing y responde SOLO JSON valido con: " +
                "score (0-100), level (BAJO|MEDIO|ALTO|CRITICO), reasons (array de strings). " +
                "No incluyas texto fuera del JSON.";

        String analysisInstruction = "Analiza el contenido para detectar urgencia artificial, suplantacion de identidad, " +
                "solicitud de credenciales, enlaces sospechosos, amenazas y engaños financieros.";

        Object userContent;
        if (request.getImageBase64() != null && !request.getImageBase64().isBlank()) {
            userContent = List.of(
                    Map.of("type", "text", "text", analysisInstruction),
                    Map.of("type", "image_url", "image_url", Map.of("url", "data:image/png;base64," + request.getImageBase64()))
            );
        } else {
            userContent = analysisInstruction + "\nTexto/SMS a analizar:\n" + request.getText();
        }

        return Map.of(
                "model", model,
                "temperature", 0.1,
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userContent)
                )
        );
    }
}
