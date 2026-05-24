package cl.duoc.gamehub.auth.controller;

import cl.duoc.gamehub.auth.model.Usuario;
import cl.duoc.gamehub.auth.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<String> registrar(@Valid @RequestBody Usuario usuario) {
        String resultado = usuarioService.registrarUsuario(usuario);
        if (resultado.startsWith("Error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        String resultado = usuarioService.iniciarSesion(username, password);
        if (resultado.startsWith("Error")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultado);
        }
        return ResponseEntity.ok(resultado);
    }
}