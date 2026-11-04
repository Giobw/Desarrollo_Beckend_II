package com.petsalud.backendpetsalud.dto;

public record AuthResponse(String token, String tipo, long expiraEnMs, String email, String rol) {
}
