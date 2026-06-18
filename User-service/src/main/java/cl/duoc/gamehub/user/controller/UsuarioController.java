package cl.duoc.gamehub.user.controller;

import cl.duoc.gamehub.user.dto.UsuarioDTO;
import cl.duoc.gamehub.user.model.Usuario;
import cl.duoc.gamehub.user.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Endpoints para la gestión de perfiles comerciales de GameHub")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/crear")
    @Operation(summary = "Crear Usuario", description = "Registra el perfil de un nuevo cliente u operador")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    public ResponseEntity<EntityModel<Usuario>> crear(@Valid @RequestBody UsuarioDTO dto) {
        Usuario nuevo = usuarioService.registrarUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapearHateoas(nuevo));
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar Usuarios", description = "Retorna el listado filtrado por Rol, Estado, o el catálogo completo")
    public ResponseEntity<List<EntityModel<Usuario>>> listar(
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String estado) {

        List<Usuario> resultado;
        if (rol != null) {
            resultado = usuarioService.listarPorRol(rol);
        } else if (estado != null) {
            resultado = usuarioService.listarPorEstado(estado);
        } else {
            resultado = usuarioService.listarTodos();
        }

        List<EntityModel<Usuario>> hateoasList = resultado.stream()
                .map(this::mapearHateoas)
                .collect(Collectors.toList());

        return ResponseEntity.ok(hateoasList);
    }

    @GetMapping("/buscar/{id}")
    @Operation(summary = "Buscar por ID", description = "Obtiene los detalles del perfil de un usuario por su ID primario")
    public ResponseEntity<EntityModel<Usuario>> porId(@PathVariable Long id) {
        return ResponseEntity.ok(mapearHateoas(usuarioService.buscarPorId(id)));
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar Usuario", description = "Modifica los datos de contacto o estado de un usuario")
    public ResponseEntity<EntityModel<Usuario>> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(mapearHateoas(usuarioService.actualizarUsuario(id, dto)));
    }

    @DeleteMapping("/desactivar/{id}")
    @Operation(summary = "Desactivar Usuario", description = "Realiza una baja lógica cambiando el estado a INACTIVO")
    public ResponseEntity<EntityModel<Usuario>> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(mapearHateoas(usuarioService.desactivarUsuario(id)));
    }

    private EntityModel<Usuario> mapearHateoas(Usuario usuario) {
        Link selfLink = linkTo(methodOn(UsuarioController.class).porId(usuario.getId())).withSelfRel();
        Link desactivarLink = linkTo(methodOn(UsuarioController.class).desactivar(usuario.getId())).withRel("desactivar_perfil");
        Link listarLink = linkTo(methodOn(UsuarioController.class).listar(null, null)).withRel("lista_completa");

        return EntityModel.of(usuario, selfLink, desactivarLink, listarLink);
    }
}