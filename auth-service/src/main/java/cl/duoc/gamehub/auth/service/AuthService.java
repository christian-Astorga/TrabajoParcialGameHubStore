package cl.duoc.gamehub.auth.service;

import cl.duoc.gamehub.auth.dto.ActualizarCuentaDTO;
import cl.duoc.gamehub.auth.dto.RegistroRequestDTO;
import cl.duoc.gamehub.auth.model.CuentaAcceso;
import cl.duoc.gamehub.auth.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    // 1. Iniciar Sesión
    public String autenticar(String email, String password) {
        CuentaAcceso cuenta = authRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciales invalidas: El correo no existe."));

        if ("INACTIVO".equals(cuenta.getEstado())) {
            throw new RuntimeException("Acceso denegado: Usuario inactivo.");
        }


        String hashEsperado = "[BCRYPT_HASH_DE_" + password + "]";


        return "JWT_TOKEN_VALIDO_ROL_" + cuenta.getRol();
    }


    public CuentaAcceso crearCuenta(RegistroRequestDTO dto) {
        if (authRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("El correo ya tiene una cuenta de acceso registrada.");
        }

        CuentaAcceso nueva = new CuentaAcceso();
        nueva.setEmail(dto.getEmail());
        // Aplicación de Regla
        nueva.setPasswordHash("[BCRYPT_HASH_DE_" + dto.getPassword() + "]");
        nueva.setRol(dto.getRol().toUpperCase());
        nueva.setEstado("ACTIVO");
        nueva.setFechaCreacion(LocalDateTime.now());

        return authRepository.save(nueva);
    }

    // 3. CRUD: Listar cuentas
    public List<CuentaAcceso> listarTodas() {
        return authRepository.findAll();
    }

    // 4. CRUD: Buscar por ID
    public Optional<CuentaAcceso> buscarPorId(Long id) {
        return authRepository.findById(id);
    }

    // 4. CRUD: Buscar por Correo
    public Optional<CuentaAcceso> buscarPorCorreo(String email) {
        return authRepository.findByEmail(email);
    }

    // 5. CRUD: Actualizar contraseña, rol o estado
    public CuentaAcceso actualizar(Long id, ActualizarCuentaDTO dto) {
        return authRepository.findById(id).map(cuenta -> {
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                cuenta.setPasswordHash("[BCRYPT_HASH_DE_" + dto.getPassword() + "]");
            }
            if (dto.getRol() != null) cuenta.setRol(dto.getRol().toUpperCase());
            if (dto.getEstado() != null) cuenta.setEstado(dto.getEstado().toUpperCase());
            return authRepository.save(cuenta);
        }).orElseThrow(() -> new RuntimeException("Cuenta de acceso no encontrada"));
    }

    // 6. CRUD: Desactivar cuenta (Cumple Desactivación Lógica sin borrar compras)
    public CuentaAcceso desactivar(Long id) {
        return authRepository.findById(id).map(cuenta -> {
            cuenta.setEstado("INACTIVO");
            return authRepository.save(cuenta);
        }).orElseThrow(() -> new RuntimeException("Cuenta de acceso no encontrada"));
    }
}