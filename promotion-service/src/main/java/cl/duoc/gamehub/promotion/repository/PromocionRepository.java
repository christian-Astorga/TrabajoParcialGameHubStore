package cl.duoc.gamehub.promotion.repository;

import cl.duoc.gamehub.promotion.model.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {
    Optional<Promocion> findByCodigo(String codigo);
    List<Promocion> findByEstado(String estado);
}