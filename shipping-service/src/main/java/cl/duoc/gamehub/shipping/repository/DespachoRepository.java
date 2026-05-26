package cl.duoc.gamehub.shipping.repository;

import cl.duoc.gamehub.shipping.model.Despacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DespachoRepository extends JpaRepository<Despacho, Long> {
    List<Despacho> findByOrdenId(Long ordenId);
    List<Despacho> findByEstado(String estado);
    Optional<Despacho> findByTracking(String tracking);
}