package cl.duoc.gamehub.auth.controller;

import cl.duoc.gamehub.auth.model.Usuario;
import cl.duoc.gamehub.auth.service.UsuarioService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Ruta para registrar un usuario: POST http://localhost:8081/auth/registrar
    @PostMapping("/registrar")
    public String registrar(@RequestBody Usuario usuario) {
        return usuarioService.registrarUsuario(usuario);
    }

    // Ruta para iniciar sesión: POST http://localhost:8081/auth/login
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        return usuarioService.iniciarSesion(username, password);
    }
}