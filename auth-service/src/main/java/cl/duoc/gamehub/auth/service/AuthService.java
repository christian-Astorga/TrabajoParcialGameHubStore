package cl.duoc.gamehub.auth.service;

import cl.duoc.gamehub.auth.model.Auth;
import cl.duoc.gamehub.auth.repository.AuthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final AuthRepository authRepository;

    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public String registrarUsuario(Auth auth) {
        log.info("Intentando registrar al usuario: {}", auth.getUsername());
        Optional<Auth> existente = authRepository.findByUsername(auth.getUsername());
        if (existente.isPresent()) {
            log.warn("Registro fallido: El usuario {} ya existe", auth.getUsername());
            return "Error: El nombre de usuario ya está en uso.";
        }
        authRepository.save(auth);
        log.info("Usuario {} registrado exitosamente.", auth.getUsername());
        return "Usuario registrado exitosamente.";
    }

    public String iniciarSesion(String username, String password) {
        log.info("Intento de login para el usuario: {}", username);
        Optional<Auth> existente = authRepository.findByUsername(username);
        if (existente.isPresent()) {
            Auth user = existente.get();
            if (user.getPassword().equals(password)) {
                log.info("Login exitoso para el usuario: {}", username);
                return "Login exitoso. Bienvenido " + user.getNombre();
            } else {
                log.warn("Login fallido para {}: Contraseña incorrecta", username);
                return "Error: Contraseña incorrecta";
            }
        }
        log.warn("Login fallido: El usuario {} no existe", username);
        return "Error: El usuario no existe";
    }
}