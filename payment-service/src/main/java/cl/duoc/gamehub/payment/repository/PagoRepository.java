package cl.duoc.gamehub.payment.repository;

import cl.duoc.gamehub.payment.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByOrdenId(Long ordenId);
    List<Pago> findByEstado(String estado);
    Optional<Pago> findByOrdenIdAndEstado(Long ordenId, String estado);
}