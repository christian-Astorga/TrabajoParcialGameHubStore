package cl.duoc.gamehub.auth.service;

import cl.duoc.gamehub.auth.model.Usuario;
import cl.duoc.gamehub.auth.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Inyección de dependencias por constructor
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Lógica para registrar un nuevo usuario
    public String registrarUsuario(Usuario usuario) {
        Optional<Usuario> existente = usuarioRepository.findByUsername(usuario.getUsername());
        if (existente.isPresent()) {
            return "Error: El nombre de usuario ya está en uso.";
        }
        usuarioRepository.save(usuario);
        return "Usuario registrado exitosamente.";
    }

    // Lógica para iniciar sesión
    public String iniciarSesion (String username, String password) {
        Optional<Usuario> existente = usuarioRepository.findByUsername(username);
        if (existente.isPresent()) {
            Usuario user = existente.get();
            if (user.getPassword().equals(password)) {
                return "Login exitoso, Bienvenido " + user.getNombre();
            } else {
                return "Error: Contraseña incorrecta.";
            }
        }
        return "Error: El usuario no existe.";
    }
}