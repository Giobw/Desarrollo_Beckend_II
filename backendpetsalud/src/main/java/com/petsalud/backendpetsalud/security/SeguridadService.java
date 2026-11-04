package com.petsalud.backendpetsalud.security;

import com.petsalud.backendpetsalud.repository.MascotaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Reglas de propiedad usadas desde @PreAuthorize("@seguridad.esUsuario(authentication, #id)").
 * Evita que un cliente acceda a datos de otro cliente (IDOR).
 */
@Component("seguridad")
public class SeguridadService {

    private final MascotaRepository mascotaRepository;

    public SeguridadService(MascotaRepository mascotaRepository) {
        this.mascotaRepository = mascotaRepository;
    }

    public boolean esUsuario(Authentication auth, Long usuarioId) {
        return auth != null && auth.getPrincipal() instanceof UsuarioDetails d
                && d.getId() != null && d.getId().equals(usuarioId);
    }

    public boolean esPropietarioMascota(Authentication auth, Long mascotaId) {
        if (auth == null || !(auth.getPrincipal() instanceof UsuarioDetails d)) return false;
        return mascotaRepository.findById(mascotaId)
                .map(m -> m.getPropietario() != null && d.getId().equals(m.getPropietario().getId()))
                .orElse(false);
    }
}
