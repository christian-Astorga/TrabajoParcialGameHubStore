package cl.duoc.gamehub.user.controller;

import cl.duoc.gamehub.user.dto.DireccionDTO;
import cl.duoc.gamehub.user.dto.UsuarioDTO;
import cl.duoc.gamehub.user.model.Direccion;
import cl.duoc.gamehub.user.model.Usuario;
import cl.duoc.gamehub.user.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/crear")
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crearUsuario(usuarioDTO));
    }

    @PostMapping("/direcciones/crear")
    public ResponseEntity<Direccion> crearDireccion(@Valid @RequestBody DireccionDTO direccionDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crearDireccion(direccionDTO));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> listarUsuarios(
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String estado) {
        if (rol != null) {
            return ResponseEntity.ok(usuarioService.listarPorRol(rol));
        }
        if (estado != null) {
            return ResponseEntity.ok(usuarioService.listarPorEstado(estado));
        }
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, dto));
    }

    @PutMapping("/direcciones/actualizar/{id}")
    public ResponseEntity<Direccion> actualizarDireccion(@PathVariable Long id, @Valid @RequestBody DireccionDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarDireccion(id, dto));
    }

    @DeleteMapping("/desactivar/{id}")
    public ResponseEntity<Usuario> desactivarUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.desactivarUsuario(id));
    }
}