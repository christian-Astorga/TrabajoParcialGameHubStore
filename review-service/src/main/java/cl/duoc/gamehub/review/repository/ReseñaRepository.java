package cl.duoc.gamehub.review.repository;

import cl.duoc.gamehub.review.model.Reseña;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReseñaRepository extends JpaRepository<Reseña, Long> {
    List<Reseña> findByProductoId(Long productoId);
    List<Reseña> findByUsuarioId(Long usuarioId);
    Optional<Reseña> findByUsuarioIdAndProductoIdAndOrdenId(Long usuarioId, Long productoId, Long ordenId);
}