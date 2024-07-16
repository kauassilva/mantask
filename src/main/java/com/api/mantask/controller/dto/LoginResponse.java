package com.api.mantask.controller.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
}
