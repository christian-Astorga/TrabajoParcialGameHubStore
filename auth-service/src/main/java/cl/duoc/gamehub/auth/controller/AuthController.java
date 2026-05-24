package cl.duoc.gamehub.auth.controller;

import cl.duoc.gamehub.auth.dto.ActualizarCuentaDTO;
import cl.duoc.gamehub.auth.dto.LoginRequestDTO;
import cl.duoc.gamehub.auth.dto.RegistroRequestDTO;
import cl.duoc.gamehub.auth.model.CuentaAcceso;
import cl.duoc.gamehub.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Endpoint obligatorio: Inicio de sesión generando token
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO dto) {
        try {
            String token = authService.autenticar(dto.getEmail(), dto.getPassword());
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // CRUD: Crear cuenta de acceso
    @PostMapping("/cuentas/crear")
    public ResponseEntity<CuentaAcceso> crear(@Valid @RequestBody RegistroRequestDTO dto) {
        CuentaAcceso nueva = authService.crearCuenta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    // CRUD: Listar cuentas
    @GetMapping("/cuentas/listar")
    public ResponseEntity<List<CuentaAcceso>> listar() {
        return ResponseEntity.ok(authService.listarTodas());
    }

    // CRUD: Buscar cuenta por ID
    @GetMapping("/cuentas/buscar/id/{id}")
    public ResponseEntity<CuentaAcceso> buscarId(@PathVariable Long id) {
        return authService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CRUD: Buscar cuenta por correo
    @GetMapping("/cuentas/buscar/correo")
    public ResponseEntity<CuentaAcceso> buscarCorreo(@RequestParam String email) {
        return authService.buscarPorCorreo(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CRUD: Actualizar contraseña, rol o estado
    @PutMapping("/cuentas/actualizar/{id}")
    public ResponseEntity<CuentaAcceso> actualizar(@PathVariable Long id, @RequestBody ActualizarCuentaDTO dto) {
        return ResponseEntity.ok(authService.actualizar(id, dto));
    }

    // CRUD: Desactivar cuenta
    @DeleteMapping("/cuentas/desactivar/{id}")
    public ResponseEntity<CuentaAcceso> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(authService.desactivar(id));
    }
}