package com.documind.auth;

public record TokenResponse(String accessToken, String tokenType, long expiresIn) {
}
