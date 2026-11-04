package com.petsalud.backendpetsalud.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Respuestas JSON uniformes para 401 (sin autenticar) y 403 (sin permiso), con registro en el log. */
@Component
public class RestSecurityHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger("SEGURIDAD_AUDITORIA");

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res,
                         org.springframework.security.core.AuthenticationException ex) throws IOException {
        log.warn("ACCESO_NO_AUTENTICADO ip={} {} {}", req.getRemoteAddr(), req.getMethod(), req.getRequestURI());
        escribir(res, HttpServletResponse.SC_UNAUTHORIZED, "No autenticado: token ausente, invalido o expirado");
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res,
                       org.springframework.security.access.AccessDeniedException ex) throws IOException {
        log.warn("ACCESO_DENEGADO ip={} {} {} usuario={}", req.getRemoteAddr(), req.getMethod(),
                req.getRequestURI(), req.getUserPrincipal() != null ? req.getUserPrincipal().getName() : "anonimo");
        escribir(res, HttpServletResponse.SC_FORBIDDEN, "Acceso denegado: no tienes permisos para este recurso");
    }

    private void escribir(HttpServletResponse res, int status, String mensaje) throws IOException {
        res.setStatus(status);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{\"status\":" + status + ",\"mensaje\":\"" + mensaje + "\"}");
    }
}
