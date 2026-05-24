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
    public ResponseEntity<Usuario> registrarUsuario(@Valid @RequestBody UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRol(dto.getRol());
        usuario.setEstado("ACTIVO");

        Usuario nuevo = usuarioService.guardarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> obtenerUsuarios() {
        List<Usuario> lista = usuarioService.listarTodos();
        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(usuario -> ResponseEntity.status(HttpStatus.OK).body(usuario))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto) {
        Usuario datosNuevos = new Usuario();
        datosNuevos.setNombre(dto.getNombre());
        datosNuevos.setTelefono(dto.getTelefono());
        datosNuevos.setRol(dto.getRol());

        Usuario actualizado = usuarioService.actualizarUsuario(id, datosNuevos);
        return ResponseEntity.status(HttpStatus.OK).body(actualizado);
    }

    @DeleteMapping("/desactivar/{id}")
    public ResponseEntity<Usuario> desactivar(@PathVariable Long id) {
        Usuario desactivado = usuarioService.desactivarUsuario(id);
        return ResponseEntity.status(HttpStatus.OK).body(desactivado);
    }

    @PostMapping("/direcciones/crear")
    public ResponseEntity<Direccion> registrarDireccion(@Valid @RequestBody DireccionDTO dto) {
        Direccion direccion = new Direccion();
        direccion.setComuna(dto.getComuna());
        direccion.setCiudad(dto.getCiudad());
        direccion.setCalle(dto.getCalle());
        direccion.setNumero(dto.getNumero());
        direccion.setUsuarioId(dto.getUsuarioId());

        Direccion nueva = usuarioService.guardarDireccion(direccion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping("/direcciones/usuario/{usuarioId}")
    public ResponseEntity<List<Direccion>> obtenerDireccionesPorUsuario(@PathVariable Long usuarioId) {
        List<Direccion> lista = usuarioService.listarDireccionesPorUsuario(usuarioId);
        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }
}