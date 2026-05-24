package cl.duoc.gamehub.user.service;

import cl.duoc.gamehub.user.dto.UsuarioDTO;
import cl.duoc.gamehub.user.model.Usuario;
import cl.duoc.gamehub.user.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario registrarUsuario(UsuarioDTO dto) {
        log.info("[USER-SERVICE] Registrando nuevo usuario con email: {}", dto.getEmail());
        Usuario u = new Usuario();
        u.setNombre(dto.getNombre());
        u.setEmail(dto.getEmail());
        u.setTelefono(dto.getTelefono());
        u.setRol(dto.getRol().toUpperCase());
        u.setEstado("ACTIVO");
        return usuarioRepository.save(u);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarPorRol(String rol) {
        return usuarioRepository.findByRol(rol.toUpperCase());
    }

    public List<Usuario> listarPorEstado(String estado) {
        return usuarioRepository.findByEstado(estado.toUpperCase());
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[USER-SERVICE] Usuario con ID {} no encontrado", id);
                    return new RuntimeException("Usuario no encontrado");
                });
    }

    public Usuario actualizarUsuario(Long id, UsuarioDTO dto) {
        log.info("[USER-SERVICE] Actualizando datos para ID: {}", id);
        Usuario u = buscarPorId(id);
        u.setNombre(dto.getNombre());
        u.setTelefono(dto.getTelefono());

        if (dto.getEstado() != null) {
            u.setEstado(dto.getEstado().toUpperCase());
        }
        return usuarioRepository.save(u);
    }

    public Usuario desactivarUsuario(Long id) {
        log.warn("[USER-SERVICE] Desactivando lógicamente al usuario con ID: {}", id);
        Usuario u = buscarPorId(id);
        u.setEstado("INACTIVO");
        return usuarioRepository.save(u);
    }
}