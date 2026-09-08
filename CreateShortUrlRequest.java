package com.schwab.urlshortener.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateShortUrlRequest(
        @NotBlank(message = "longUrl is required")
        @Size(max = 2048, message = "longUrl must not exceed 2048 characters")
        String longUrl,
        @Min(value = 1, message = "expirationHours must be at least 1")
        @Max(value = 8760, message = "expirationHours must not exceed 8760")
        Integer expirationHours) {
}
