package com.petsalud.backendpetsalud.dto;

import com.petsalud.backendpetsalud.entity.Usuario;

/** Vista publica de un usuario: nunca incluye la contrasena. */
public record UsuarioResponse(Long id, String email, String nombre, String apellido,
                              String telefono, String direccion, String rol) {
    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getEmail(), u.getNombre(), u.getApellido(),
                u.getTelefono(), u.getDireccion(), u.getRol().name());
    }
}
