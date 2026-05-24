package cl.duoc.gamehub.auth.service;

import cl.duoc.gamehub.auth.model.Usuario;
import cl.duoc.gamehub.auth.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public String registrarUsuario(Usuario usuario) {
        log.info("Intentando registrar al usuario: {}", usuario.getUsername());
        Optional<Usuario> existente = usuarioRepository.findByUsername(usuario.getUsername());
        if (existente.isPresent()) {
            log.warn("Registro fallido: El usuario {} ya existe", usuario.getUsername());
            return "Error: El nombre de usuario ya está en uso.";
        }
        usuarioRepository.save(usuario);
        log.info("Usuario {} registrado exitosamente.", usuario.getUsername());
        return "Usuario registrado exitosamente.";
    }

    public String iniciarSesion(String username, String password) {
        log.info("Intento de login para el usuario: {}", username);
        Optional<Usuario> existente = usuarioRepository.findByUsername(username);
        if (existente.isPresent()) {
            Usuario user = existente.get();
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