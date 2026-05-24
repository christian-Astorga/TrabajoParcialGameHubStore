package cl.duoc.gamehub.auth.service;

import cl.duoc.gamehub.auth.dto.ActualizarCuentaDTO;
import cl.duoc.gamehub.auth.dto.LoginRequestDTO;
import cl.duoc.gamehub.auth.dto.RegistroRequestDTO;
import cl.duoc.gamehub.auth.model.CuentaAcceso;
import cl.duoc.gamehub.auth.repository.AuthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    // 1. Inicialización de SLF4J
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthRepository authRepository;

    // Conecta con: authService.autenticar(dto.getEmail(), dto.getPassword())
    public String autenticar(String email, String password) {
        log.info("[AUTH-SERVICE] Procesando solicitud de inicio de sesión para el correo: {}", email);

        CuentaAcceso cuenta = authRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[AUTH-SERVICE] Login Fallido: El correo '{}' no se encuentra registrado.", email);
                    return new RuntimeException("Credenciales inválidas");
                });

        // Verificación de contraseña plana frente a passwordHash
        if (!cuenta.getPasswordHash().equals(password)) {
            log.error("[AUTH-SERVICE] Login Fallido: Contraseña errónea para el usuario '{}'", email);
            throw new RuntimeException("Credenciales inválidas");
        }

        if ("INACTIVO".equalsIgnoreCase(cuenta.getEstado())) {
            log.warn("[AUTH-SERVICE] Intento de acceso bloqueado: Cuenta bloqueada o INACTIVA para '{}'", email);
            throw new RuntimeException("La cuenta se encuentra inactiva.");
        }

        log.info("[AUTH-SERVICE] Autenticación completada. Login Exitoso para el usuario: {}", email);
        return "TOKEN_SIMULADO_GAMEHUB_SUCCESS";
    }

    // Conecta con: authService.crearCuenta(dto)
    public CuentaAcceso crearCuenta(RegistroRequestDTO dto) {
        log.info("[AUTH-SERVICE] Solicitud de creación de cuenta nueva recibida para: {}", dto.getEmail());

        // Evitar duplicados
        authRepository.findByEmail(dto.getEmail()).ifPresent(c -> {
            log.error("[AUTH-SERVICE] Registro denegado: El email {} ya existe en la plataforma.", dto.getEmail());
            throw new RuntimeException("El correo ya está registrado.");
        });

        CuentaAcceso nuevaCuenta = new CuentaAcceso();
        nuevaCuenta.setEmail(dto.getEmail());
        nuevaCuenta.setPasswordHash(dto.getPassword()); // Se almacena temporalmente como texto plano/hash simulado
        nuevaCuenta.setRol(dto.getRol().toUpperCase());
        nuevaCuenta.setEstado("ACTIVO");
        nuevaCuenta.setFechaCreacion(LocalDateTime.now());

        log.info("[AUTH-SERVICE] Registro exitoso. Guardando CuentaAcceso para '{}' con Rol: {}", dto.getEmail(), dto.getRol());
        return authRepository.save(nuevaCuenta);
    }

    // Conecta con: authService.listarTodas()
    public List<CuentaAcceso> listarTodas() {
        log.info("[AUTH-SERVICE] Solicitando listado completo de cuentas de usuario.");
        return authRepository.findAll();
    }

    // Conecta con: authService.buscarPorId(id)
    public Optional<CuentaAcceso> buscarPorId(Long id) {
        log.info("[AUTH-SERVICE] Buscando cuenta asociada al ID de base de datos: {}", id);
        return authRepository.findById(id);
    }

    // Conecta con: authService.buscarPorCorreo(email)
    public Optional<CuentaAcceso> buscarPorCorreo(String email) {
        log.info("[AUTH-SERVICE] Buscando cuenta por criterio de Email: {}", email);
        return authRepository.findByEmail(email);
    }

    // Conecta con: authService.actualizar(id, dto)
    public CuentaAcceso actualizar(Long id, ActualizarCuentaDTO dto) {
        log.info("[AUTH-SERVICE] Solicitud recibida para actualizar parámetros de la cuenta ID: {}", id);

        CuentaAcceso cuenta = authRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[AUTH-SERVICE] Error en actualización: Cuenta con ID {} inexistente.", id);
                    return new RuntimeException("Cuenta no encontrada");
                });

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            log.info("[AUTH-SERVICE] Actualizando contraseña de acceso para ID: {}", id);
            cuenta.setPasswordHash(dto.getPassword());
        }
        if (dto.getRol() != null && !dto.getRol().isBlank()) {
            cuenta.setRol(dto.getRol().toUpperCase());
        }
        if (dto.getEstado() != null && !dto.getEstado().isBlank()) {
            cuenta.setEstado(dto.getEstado().toUpperCase());
        }

        log.info("[AUTH-SERVICE] Modificaciones guardadas de manera exitosa para ID: {}", id);
        return authRepository.save(cuenta);
    }

    // Conecta con: authService.desactivar(id)
    public CuentaAcceso desactivar(Long id) {
        log.warn("[AUTH-SERVICE] Ejecutando restricción/desactivación lógica de la cuenta ID: {}", id);

        CuentaAcceso cuenta = authRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[AUTH-SERVICE] Error al desactivar: Cuenta con ID {} no encontrada.", id);
                    return new RuntimeException("Cuenta no encontrada");
                });

        cuenta.setEstado("INACTIVO");
        return authRepository.save(cuenta);
    }
}