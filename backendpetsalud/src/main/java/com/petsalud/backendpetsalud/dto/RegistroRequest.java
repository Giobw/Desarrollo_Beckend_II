package com.petsalud.backendpetsalud.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Registro publico: no incluye rol; todo usuario auto-registrado es CLIENTE. */
public record RegistroRequest(
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Size(min = 8, max = 72, message = "La contrasena debe tener entre 8 y 72 caracteres") String password,
        @NotBlank @Size(max = 60) String nombre,
        @NotBlank @Size(max = 60) String apellido,
        @Size(max = 20) String telefono,
        @Size(max = 150) String direccion) {
}
