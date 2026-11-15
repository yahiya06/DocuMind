package com.documind.auth;

import java.util.UUID;

public record UserResponse(UUID id, String email, String role) {
}
