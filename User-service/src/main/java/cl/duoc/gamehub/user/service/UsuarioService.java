package cl.duoc.gamehub.user.service;

import cl.duoc.gamehub.user.model.Direccion;
import cl.duoc.gamehub.user.model.Usuario;
import cl.duoc.gamehub.user.repository.DireccionRepository;
import cl.duoc.gamehub.user.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    public Usuario guardarUsuario(Usuario usuario) {
        log.info("Guardando un nuevo usuario: {}", usuario.getNombre());
        Optional<Usuario> existente = usuarioRepository.findByEmail(usuario.getEmail());
        if (existente.isPresent()) {
            throw new RuntimeException("El correo electronico ya esta registrado");
        }
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        log.info("Listando todos los usuarios...");
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        log.info("Buscando usuario con ID: {}", id);
        return usuarioRepository.findById(id);
    }

    public Usuario actualizarUsuario(Long id, Usuario datosNuevos) {
        log.info("Actualizando datos del usuario con ID: {}", id);
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNombre(datosNuevos.getNombre());
            usuario.setTelefono(datosNuevos.getTelefono());
            usuario.setRol(datosNuevos.getRol());
            return usuarioRepository.save(usuario);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario desactivarUsuario(Long id) {
        log.info("Desactivando lógicamente el usuario con ID: {}", id);
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setEstado("INACTIVO");
            return usuarioRepository.save(usuario);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Direccion guardarDireccion(Direccion direccion) {
        log.info("Guardando direccion para el usuario ID: {}", direccion.getUsuarioId());
        usuarioRepository.findById(direccion.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario asociado no existe"));
        return direccionRepository.save(direccion);
    }

    public List<Direccion> listarDireccionesPorUsuario(Long usuarioId) {
        log.info("Listando direcciones del usuario ID: {}", usuarioId);
        return direccionRepository.findByUsuarioId(usuarioId);
    }
}