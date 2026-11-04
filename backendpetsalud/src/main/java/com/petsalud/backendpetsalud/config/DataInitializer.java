package com.petsalud.backendpetsalud.config;

import com.petsalud.backendpetsalud.entity.Rol;
import com.petsalud.backendpetsalud.entity.Usuario;
import com.petsalud.backendpetsalud.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Crea usuarios de demostracion (uno por rol) para poder probar la autorizacion.
 * Solo para desarrollo/pruebas: se desactiva con app.seed-demo-users=false.
 */
@Configuration
@ConditionalOnProperty(name = "app.seed-demo-users", havingValue = "true", matchIfMissing = true)
public class DataInitializer {

    @Bean
    CommandLineRunner cargarUsuariosDemo(UsuarioRepository repo, PasswordEncoder encoder) {
        return args -> {
            crear(repo, encoder, "gerente@petsalud.cl", "Gerente123!", "Gina", "Gerente", Rol.GERENTE);
            crear(repo, encoder, "empleado@petsalud.cl", "Empleado123!", "Esteban", "Empleado", Rol.EMPLEADO);
            crear(repo, encoder, "cliente@petsalud.cl", "Cliente123!", "Camila", "Cliente", Rol.CLIENTE);
        };
    }

    private void crear(UsuarioRepository repo, PasswordEncoder encoder, String email, String pass,
                       String nombre, String apellido, Rol rol) {
        if (repo.findByEmail(email).isEmpty()) {
            repo.save(Usuario.builder().email(email).password(encoder.encode(pass))
                    .nombre(nombre).apellido(apellido).rol(rol).build());
        }
    }
}
