package com.documind.security;

public record AccessToken(String value, long expiresInSeconds) {
}
