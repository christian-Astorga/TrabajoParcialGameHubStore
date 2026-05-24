package cl.duoc.gamehub.user.service;

import cl.duoc.gamehub.user.dto.DireccionDTO;
import cl.duoc.gamehub.user.dto.UsuarioDTO;
import cl.duoc.gamehub.user.model.Direccion;
import cl.duoc.gamehub.user.model.Usuario;
import cl.duoc.gamehub.user.repository.DireccionRepository;
import cl.duoc.gamehub.user.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    // 1. Crear Usuario
    public Usuario crearUsuario(UsuarioDTO dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Error de negocio: El correo electronico ya esta registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRol(dto.getRol().toUpperCase());
        usuario.setEstado("ACTIVO");
        return usuarioRepository.save(usuario);
    }

    // 2. Crear Dirección
    public Direccion crearDireccion(DireccionDTO dto) {
        usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para asociar la direccion"));

        Direccion direccion = new Direccion();
        direccion.setUsuarioId(dto.getUsuarioId());
        direccion.setComuna(dto.getComuna());
        direccion.setCiudad(dto.getCiudad());
        direccion.setCalle(dto.getCalle());
        direccion.setNumero(dto.getNumero());
        return direccionRepository.save(direccion);
    }

    // 3. Listar todos
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // 4. Listar por Rol (Exigido en Rúbrica)
    public List<Usuario> listarPorRol(String rol) {
        return usuarioRepository.findByRol(rol.toUpperCase());
    }

    // 5. Listar por Estado (Exigido en Rúbrica)
    public List<Usuario> listarPorEstado(String estado) {
        return usuarioRepository.findByEstado(estado.toUpperCase());
    }

    // 6. Buscar por ID
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // 7. Actualizar Usuario (Corregido: YA NO USA getEstado para evitar la línea roja)
    public Usuario actualizarUsuario(Long id, UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(dto.getNombre());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRol(dto.getRol().toUpperCase());
        // El estado se mantiene intacto como estaba en la base de datos

        return usuarioRepository.save(usuario);
    }

    // 8. Actualizar Dirección
    public Direccion actualizarDireccion(Long id, DireccionDTO dto) {
        Direccion dir = direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Direccion no encontrada"));

        dir.setComuna(dto.getComuna());
        dir.setCiudad(dto.getCiudad());
        dir.setCalle(dto.getCalle());
        dir.setNumero(dto.getNumero());

        return direccionRepository.save(dir);
    }

    // 9. Desactivar Usuario (Eliminación Lógica)
    public Usuario desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setEstado("INACTIVO");
        return usuarioRepository.save(usuario);
    }
}