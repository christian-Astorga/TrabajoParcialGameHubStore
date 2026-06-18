package cl.duoc.gamehub.auth.controller;

import cl.duoc.gamehub.auth.dto.ActualizarCuentaDTO;
import cl.duoc.gamehub.auth.dto.LoginRequestDTO;
import cl.duoc.gamehub.auth.dto.RegistroRequestDTO;
import cl.duoc.gamehub.auth.model.CuentaAcceso;
import cl.duoc.gamehub.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para la gestión de cuentas de acceso, login y seguridad")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar Sesión", description = "Valida las credenciales y retorna un token simulado")
    @ApiResponse(responseCode = "200", description = "Autenticación exitosa")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO dto) {
        String token = authService.autenticar(dto.getEmail(), dto.getPassword());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/crear")
    @Operation(summary = "Crear Cuenta de Acceso", description = "Registra una nueva cuenta en estado ACTIVO")
    @ApiResponse(responseCode = "200", description = "Cuenta creada exitosamente")
    public ResponseEntity<EntityModel<CuentaAcceso>> crear(@RequestBody RegistroRequestDTO dto) {
        CuentaAcceso nueva = authService.crearCuenta(dto);
        return ResponseEntity.ok(mapearHateoas(nueva));
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar Cuentas", description = "Retorna el listado completo de cuentas registradas")
    public ResponseEntity<List<EntityModel<CuentaAcceso>>> listar() {
        List<EntityModel<CuentaAcceso>> cuentas = authService.listarTodas().stream()
                .map(this::mapearHateoas)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cuentas);
    }

    @GetMapping("/buscar/{id}")
    @Operation(summary = "Buscar Cuenta por ID")
    public ResponseEntity<EntityModel<CuentaAcceso>> buscarPorId(@PathVariable Long id) {
        CuentaAcceso cuenta = authService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        return ResponseEntity.ok(mapearHateoas(cuenta));
    }

    @GetMapping("/buscar/correo")
    @Operation(summary = "Buscar Cuenta por Correo")
    public ResponseEntity<EntityModel<CuentaAcceso>> buscarPorCorreo(@RequestParam String email) {
        CuentaAcceso cuenta = authService.buscarPorCorreo(email)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        return ResponseEntity.ok(mapearHateoas(cuenta));
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar Parámetros", description = "Actualiza contraseña, rol o estado")
    public ResponseEntity<EntityModel<CuentaAcceso>> actualizar(@PathVariable Long id, @RequestBody ActualizarCuentaDTO dto) {
        CuentaAcceso actualizada = authService.actualizar(id, dto);
        return ResponseEntity.ok(mapearHateoas(actualizada));
    }

    @PutMapping("/desactivar/{id}")
    @Operation(summary = "Desactivar Cuenta (Lógico)", description = "Cambia el estado a INACTIVO sin borrar datos históricos")
    public ResponseEntity<EntityModel<CuentaAcceso>> desactivar(@PathVariable Long id) {
        CuentaAcceso desactivada = authService.desactivar(id);
        return ResponseEntity.ok(mapearHateoas(desactivada));
    }

    // Inyección precisa de Enlaces Hipermedia 
    private EntityModel<CuentaAcceso> mapearHateoas(CuentaAcceso cuenta) {
        Link selfLink = linkTo(methodOn(AuthController.class).buscarPorId(cuenta.getId())).withSelfRel();
        Link desactivarLink = linkTo(methodOn(AuthController.class).desactivar(cuenta.getId())).withRel("desactivar");
        Link listarLink = linkTo(methodOn(AuthController.class).listar()).withRel("lista_completa");

        return EntityModel.of(cuenta, selfLink, desactivarLink, listarLink);
    }
}