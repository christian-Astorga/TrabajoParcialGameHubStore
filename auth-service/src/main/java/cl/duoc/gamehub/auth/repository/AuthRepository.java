package cl.duoc.gamehub.auth.repository;

import cl.duoc.gamehub.auth.model.CuentaAcceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<CuentaAcceso, Long> {
    Optional<CuentaAcceso> findByEmail(String email);
}