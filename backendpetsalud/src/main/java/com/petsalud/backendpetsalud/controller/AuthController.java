package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.dto.AuthResponse;
import com.petsalud.backendpetsalud.dto.LoginRequest;
import com.petsalud.backendpetsalud.dto.RegistroRequest;
import com.petsalud.backendpetsalud.dto.UsuarioResponse;
import com.petsalud.backendpetsalud.entity.Rol;
import com.petsalud.backendpetsalud.entity.Usuario;
import com.petsalud.backendpetsalud.security.JwtUtil;
import com.petsalud.backendpetsalud.security.LoginAttemptService;
import com.petsalud.backendpetsalud.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;
    private final LoginAttemptService loginAttemptService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                          UsuarioService usuarioService, LoginAttemptService loginAttemptService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req, HttpServletRequest http) {
        String ip = http.getRemoteAddr();

        if (loginAttemptService.estaBloqueado(req.email(), ip)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("mensaje", "Demasiados intentos fallidos. Intenta nuevamente mas tarde."));
        }
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email(), req.password()));
            String rol = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
            String token = jwtUtil.generarToken(auth.getName(), rol);
            loginAttemptService.registrarExito(req.email(), ip);
            return ResponseEntity.ok(new AuthResponse(token, "Bearer", jwtUtil.getExpirationMs(), auth.getName(), rol));
        } catch (AuthenticationException e) {
            loginAttemptService.registrarFallo(req.email(), ip);
            // Mensaje generico: no revela si el email existe (evita enumeracion de usuarios).
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Credenciales invalidas"));
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registro(@Valid @RequestBody RegistroRequest req) {
        Usuario u = Usuario.builder()
                .email(req.email())
                .password(req.password())
                .nombre(req.nombre())
                .apellido(req.apellido())
                .telefono(req.telefono())
                .direccion(req.direccion())
                .rol(Rol.CLIENTE) // el rol nunca viene del cliente
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioResponse.de(usuarioService.registrarUsuario(u)));
    }
}
