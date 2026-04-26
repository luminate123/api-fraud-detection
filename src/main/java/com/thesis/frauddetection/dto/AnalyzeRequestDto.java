package com.thesis.frauddetection.dto;

import jakarta.validation.constraints.Size;

public class AnalyzeRequestDto {

    @Size(max = 10000, message = "El texto no puede exceder 10000 caracteres")
    private String text;

    @Size(max = 2000000, message = "La imagen en base64 excede el limite permitido")
    private String imageBase64;

    @Size(max = 20)
    private String channel;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
