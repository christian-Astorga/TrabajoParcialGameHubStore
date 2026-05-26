package cl.duoc.gamehub.payment.service;

import cl.duoc.gamehub.payment.OrderClient;
import cl.duoc.gamehub.payment.dto.OrderExternalDTO;
import cl.duoc.gamehub.payment.dto.PaymentRequestDTO;
import cl.duoc.gamehub.payment.model.Pago;
import cl.duoc.gamehub.payment.repository.PagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);

    private final PagoRepository pagoRepository;
    private final OrderClient orderClient;

    public PagoService(PagoRepository pagoRepository, OrderClient orderClient) {
        this.pagoRepository = pagoRepository;
        this.orderClient = orderClient;
    }

    public Pago procesarPago(PaymentRequestDTO request) {
        log.info("[AUDITORIA] Solicitando registro de pago para la Orden ID: {}", request.getOrdenId());

        pagoRepository.findByOrdenIdAndEstado(request.getOrdenId(), "PROCESADO")
                .ifPresent(p -> {
                    log.warn("[VALIDACION FALLIDA] La orden ya fue cancelada con exito.");
                    throw new IllegalArgumentException("No se puede duplicar el pago. Esta orden ya registra un pago aprobado.");
                });

        OrderExternalDTO orden;
        try {
            orden = orderClient.buscarOrdenPorId(request.getOrdenId());
        } catch (Exception e) {
            log.error("[LOG EXCEPCION] La orden indicada no existe en la base de datos.");
            throw new IllegalArgumentException("La orden solicitada no existe en el sistema.");
        }

        if (!orden.getTotal().equals(request.getMonto())) {
            log.warn("[VALIDACION FALLIDA] Descuadre de dinero en Postman.");
            throw new IllegalArgumentException("El monto ingresado no coincide con el total registrado de la orden.");
        }

        Pago pago = new Pago();
        pago.setOrdenId(request.getOrdenId());
        pago.setMonto(request.getMonto());
        pago.setMetodo(request.getMetodo().toUpperCase());
        pago.setEstado("PROCESADO");
        pago.setCodigoTransaccion("TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        pago.setFecha(LocalDateTime.now());

        Pago guardado = pagoRepository.save(pago);
        log.info("[EXITO] Pago registrado de forma conforme en Laragon.");

        try {
            orderClient.actualizarEstadoOrden(request.getOrdenId(), "PAGADA");
            log.info("[SINCRO] Estado de la orden actualizado exitosamente.");
        } catch (Exception e) {
            log.error("[ERROR RED] El pago se guardo pero falto sincronizar con el servicio externo.");
        }

        return guardado;
    }

    public List<Pago> listarTodos() {
        return pagoRepository.findAll();
    }

    public List<Pago> listarPorOrden(Long ordenId) {
        return pagoRepository.findByOrdenId(ordenId);
    }

    public List<Pago> listarPorEstado(String estado) {
        return pagoRepository.findByEstado(estado.toUpperCase());
    }

    public Pago buscarPorId(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro ninguna transaccion de pago asociada al ID: " + id));
    }

    public Pago actualizarEstado(Long id, String nuevoEstado) {
        log.info("[AUDITORIA] Actualizando estado del pago ID: {} a {}", id, nuevoEstado);
        Pago pago = buscarPorId(id);
        pago.setEstado(nuevoEstado.toUpperCase());
        return pagoRepository.save(pago);
    }

    public void anularPago(Long id) {
        log.info("[AUDITORIA] Solicitando anulacion logica para el pago ID: {}", id);
        Pago pago = buscarPorId(id);
        if ("ANULADO".equals(pago.getEstado())) {
            throw new IllegalArgumentException("Este pago ya se encuentra anulado in el sistema.");
        }
        pago.setEstado("ANULADO");
        pagoRepository.save(pago);
        log.info("[EXITO] Transaccion anulada correctamente.");
    }
}