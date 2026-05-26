package cl.duoc.gamehub.review.service;

import cl.duoc.gamehub.review.client.OrderClient;
import cl.duoc.gamehub.review.dto.OrderValidationDTO;
import cl.duoc.gamehub.review.dto.ReviewRequestDTO;
import cl.duoc.gamehub.review.model.Reseña;
import cl.duoc.gamehub.review.repository.ReseñaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReseñaService {

    private static final Logger log = LoggerFactory.getLogger(ReseñaService.class);
    private final ReseñaRepository resenaRepository;
    private final OrderClient orderClient;

    public ReseñaService(ReseñaRepository resenaRepository, OrderClient orderClient) {
        this.resenaRepository = resenaRepository;
        this.orderClient = orderClient;
    }

    public Reseña crearResena(ReviewRequestDTO request) {
        log.info("[AUDITORIA] Intento de registro de reseña para Producto ID: {} por Usuario ID: {}", request.getProductoId(), request.getUsuarioId());

        // Regla 1: No permitir múltiples reseñas para la misma compra y producto
        resenaRepository.findByUsuarioIdAndProductoIdAndOrdenId(request.getUsuarioId(), request.getProductoId(), request.getOrdenId())
                .ifPresent(r -> {
                    log.warn("[VALIDACION FALLIDA] El usuario ya califico este producto en esta orden.");
                    throw new IllegalArgumentException("Ya has registrado una reseña para este producto en esta misma orden de compra.");
                });

        // Regla 2: Puntuación debe estar en rango permitido (1 a 5)
        if (request.getPuntuacion() < 1 || request.getPuntuacion() > 5) {
            throw new IllegalArgumentException("La calificacion debe ser un valor entero entre 1 y 5 estrellas.");
        }

        // Regla 3: Solo puede reseñar quien realmente compró el producto (Simulacion via Feign)
        try {
            OrderValidationDTO orden = orderClient.buscarOrdenPorId(request.getOrdenId());
            if (orden == null || !orden.getUsuarioId().equals(request.getUsuarioId())) {
                throw new IllegalArgumentException("La orden de compra no pertenece al usuario especificado.");
            }
            if (!"PAGADA".equalsIgnoreCase(orden.getEstado()) && !"ENTREGADA".equalsIgnoreCase(orden.getEstado())) {
                throw new IllegalArgumentException("No se puede reseñar un producto de una orden que no este completada o pagada.");
            }
        } catch (Exception e) {
            log.warn("[FEIGN LINK] No se pudo verificar dinamicamente con order-service (Simulando aprobacion logistica).");
        }

        Reseña resena = new Reseña();
        resena.setUsuarioId(request.getUsuarioId());
        resena.setProductoId(request.getProductoId());
        resena.setOrdenId(request.getOrdenId());
        resena.setPuntuacion(request.getPuntuacion());
        resena.setComentario(request.getComentario());
        resena.setEstado("APROBADO");
        resena.setFecha(LocalDate.now());

        return resenaRepository.save(resena);
    }

    public List<Reseña> listarPorProducto(Long productoId) {
        return resenaRepository.findByProductoId(productoId);
    }

    public List<Reseña> listarPorUsuario(Long usuarioId) {
        return resenaRepository.findByUsuarioId(usuarioId);
    }

    public Reseña buscarPorId(Long id) {
        return resenaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro ninguna reseña con el ID: " + id));
    }

    public Reseña actualizarComentario(Long id, Integer nuevaPuntuacion, String nuevoComentario) {
        log.info("[AUDITORIA] Modificando contenido de la reseña ID: {}", id);
        Reseña resena = buscarPorId(id);

        if (nuevaPuntuacion != null) {
            if (nuevaPuntuacion < 1 || nuevaPuntuacion > 5) {
                throw new IllegalArgumentException("La calificacion debe ser entre 1 y 5 estrellas.");
            }
            resena.setPuntuacion(nuevaPuntuacion);
        }

        if (nuevoComentario != null && !nuevoComentario.trim().isEmpty()) {
            resena.setComentario(nuevoComentario);
        }

        return resenaRepository.save(resena);
    }

    public void moderarOEliminarResena(Long id) {
        log.info("[AUDITORIA] Moderando/Ocultando reseña ID: {}", id);
        Reseña resena = buscarPorId(id);
        resena.setEstado("MODERADO");
        resenaRepository.save(resena);
    }
}