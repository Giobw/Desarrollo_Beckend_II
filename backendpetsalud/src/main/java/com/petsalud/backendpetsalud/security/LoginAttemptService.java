package com.petsalud.backendpetsalud.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Monitoreo basico de actividad sospechosa: cuenta intentos de login fallidos por email + IP,
 * registra alertas en el log y bloquea temporalmente (mitiga fuerza bruta).
 */
@Service
public class LoginAttemptService {

    private static final Logger log = LoggerFactory.getLogger("SEGURIDAD_AUDITORIA");

    private record Intento(int cantidad, Instant bloqueadoHasta) {}

    private final Map<String, Intento> intentos = new ConcurrentHashMap<>();
    private final int maxIntentos;
    private final Duration bloqueo;

    public LoginAttemptService(@Value("${security.login.max-attempts}") int maxIntentos,
                               @Value("${security.login.lock-minutes}") long minutosBloqueo) {
        this.maxIntentos = maxIntentos;
        this.bloqueo = Duration.ofMinutes(minutosBloqueo);
    }

    private String clave(String email, String ip) {
        return (email == null ? "" : email.toLowerCase()) + "|" + ip;
    }

    public boolean estaBloqueado(String email, String ip) {
        Intento i = intentos.get(clave(email, ip));
        if (i == null || i.bloqueadoHasta() == null) return false;
        if (Instant.now().isAfter(i.bloqueadoHasta())) {
            intentos.remove(clave(email, ip));
            return false;
        }
        return true;
    }

    public void registrarFallo(String email, String ip) {
        Intento nuevo = intentos.merge(clave(email, ip), new Intento(1, null), (viejo, ignorado) -> {
            int c = viejo.cantidad() + 1;
            return new Intento(c, c >= maxIntentos ? Instant.now().plus(bloqueo) : null);
        });
        log.warn("LOGIN_FALLIDO email={} ip={} intentos={}", email, ip, nuevo.cantidad());
        if (nuevo.bloqueadoHasta() != null) {
            log.error("ACTIVIDAD_SOSPECHOSA posible fuerza bruta email={} ip={} bloqueado_hasta={}",
                    email, ip, nuevo.bloqueadoHasta());
        }
    }

    public void registrarExito(String email, String ip) {
        intentos.remove(clave(email, ip));
        log.info("LOGIN_OK email={} ip={}", email, ip);
    }
}
