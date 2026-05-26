package cl.duoc.gamehub.shipping.service;

import cl.duoc.gamehub.shipping.client.OrderClient;
import cl.duoc.gamehub.shipping.dto.OrderExternalDTO;
import cl.duoc.gamehub.shipping.dto.ShippingRequestDTO;
import cl.duoc.gamehub.shipping.model.Despacho;
import cl.duoc.gamehub.shipping.repository.DespachoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class DespachoService {

    private static final Logger log = LoggerFactory.getLogger(DespachoService.class);

    private final DespachoRepository despachoRepository;
    private final OrderClient orderClient;

    public DespachoService(DespachoRepository despachoRepository, OrderClient orderClient) {
        this.despachoRepository = despachoRepository;
        this.orderClient = orderClient;
    }

    public Despacho crearDespacho(ShippingRequestDTO request) {
        log.info("[AUDITORIA] Solicitando creacion de despacho para la Orden ID: {}", request.getOrdenId());

        OrderExternalDTO orden;
        try {
            orden = orderClient.buscarOrdenPorId(request.getOrdenId());
        } catch (Exception e) {
            log.error("[ERROR RED] No se encontro la orden especificada en el microservicio externo.");
            throw new IllegalArgumentException("La orden indicada no existe en el sistema comercial.");
        }

        if (!"PAGADA".equals(orden.getEstado())) {
            log.warn("[VALIDACION FALLIDA] Intento de despachar una orden no pagada. Estado: {}", orden.getEstado());
            throw new IllegalArgumentException("No se puede generar un despacho si la orden no se encuentra PAGADA.");
        }

        Despacho despacho = new Despacho();
        despacho.setOrdenId(request.getOrdenId());
        despacho.setUsuarioId(request.getUsuarioId());
        despacho.setDireccion(request.getDireccion());
        despacho.setTransportista(request.getTransportista().toUpperCase());
        despacho.setEstado("PREPARACION");
        despacho.setTracking("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        despacho.setFechaEnvio(LocalDate.now());

        Despacho guardado = despachoRepository.save(despacho);
        log.info("[EXITO] Despacho registrado correctamente con Tracking unico: {}", guardado.getTracking());
        return guardado;
    }

    public List<Despacho> listarTodos() {
        return despachoRepository.findAll();
    }

    public List<Despacho> listarPorOrden(Long ordenId) {
        return despachoRepository.findByOrdenId(ordenId);
    }

    public List<Despacho> listarPorEstado(String estado) {
        return despachoRepository.findByEstado(estado.toUpperCase());
    }

    public Despacho buscarPorId(Long id) {
        return despachoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro ningun registro de despacho con el ID: " + id));
    }

    public Despacho actualizarEstadoYTracking(Long id, String nuevoEstado, String nuevoTracking) {
        log.info("[AUDITORIA] Actualizando despacho ID: {} a Estado: {}", id, nuevoEstado);
        Despacho despacho = buscarPorId(id);

        if ("ENTREGADO".equalsIgnoreCase(nuevoEstado)) {
            despacho.setFechaEntrega(LocalDate.now());
        }

        if (nuevoTracking != null && !nuevoTracking.isBlank()) {
            despachoRepository.findByTracking(nuevoTracking).ifPresent(d -> {
                if (!d.getId().equals(id)) {
                    throw new IllegalArgumentException("El numero de tracking ingresado ya esta asignado a otra entrega.");
                }
            });
            despacho.setTracking(nuevoTracking.toUpperCase());
        }

        despacho.setEstado(nuevoEstado.toUpperCase());
        return despachoRepository.save(despacho);
    }

    public void cancelarDespacho(Long id) {
        log.info("[AUDITORIA] Ejecutando cancelacion del despacho ID: {}", id);
        Despacho despacho = buscarPorId(id);

        if ("ENTREGADO".equals(despacho.getEstado())) {
            throw new IllegalArgumentException("No se puede cancelar un envio que ya fue entregado conforme al cliente.");
        }

        despacho.setEstado("CANCELADO");
        despachoRepository.save(despacho);
        log.info("[EXITO] Despacho anulado de forma logica.");
    }
}