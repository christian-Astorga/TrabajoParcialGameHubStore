package cl.duoc.gamehub.payment.service;

import cl.duoc.gamehub.payment.client.OrderClient; // Ajustado a tu interfaz real sin la N
import cl.duoc.gamehub.payment.dto.PaymentRequestDTO; // Ajustado a tu DTO real
import cl.duoc.gamehub.payment.model.Pago;
import cl.duoc.gamehub.payment.repository.PagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private OrderClient orderClient; // Ajustado el tipo y el nombre de la variable

    @Transactional
    public Pago procesarPago(PaymentRequestDTO dto) { // Cambiado a PaymentRequestDTO
        log.info("[PAYMENT-SERVICE] Iniciando procesamiento de pago para la Orden ID: {} por ${}", dto.getOrdenId(), dto.getMonto());

        Pago pago = new Pago();
        pago.setOrdenId(dto.getOrdenId());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodo().toUpperCase()); // Corregido a dto.getMetodo() según tu clase
        pago.setFecha(LocalDateTime.now());

        pago.setEstado("APROBADO");
        pago.setTransaccionId("TRX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        Pago guardado = pagoRepository.save(pago);
        log.info("[PAYMENT-SERVICE] Pago registrado con éxito. Transacción: {}", guardado.getTransaccionId());

        try {
            log.info("[PAYMENT-SERVICE] Notificando cambio de estado a order-service para la orden ID: {}", dto.getOrdenId());
            orderClient.actualizarEstado(dto.getOrdenId(), "PAGADA"); // Usando orderClient
        } catch (Exception e) {
            log.error("[PAYMENT-SERVICE] Error crítico de comunicación al actualizar la orden: {}", e.getMessage());
            throw new RuntimeException("El pago fue procesado pero no se pudo actualizar el estado de la orden remota. Consistencia bajo revisión.");
        }

        return guardado;
    }

    public List<Pago> listarTodos() { return pagoRepository.findAll(); }

    public Pago buscarPorId(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de pago no encontrado."));
    }

    public List<Pago> buscarPorOrdenId(Long ordenId) { return pagoRepository.findByOrdenId(ordenId); }
}