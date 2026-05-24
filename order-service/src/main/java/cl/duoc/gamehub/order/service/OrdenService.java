package cl.duoc.gamehub.order.service;

import cl.duoc.gamehub.order.client.InventarioClient;
import cl.duoc.gamehub.order.client.ProductoClient;
import cl.duoc.gamehub.order.dto.DetalleOrdenDTO;
import cl.duoc.gamehub.order.dto.OrdenDTO;
import cl.duoc.gamehub.order.model.DetalleOrden;
import cl.duoc.gamehub.order.model.Orden;
import cl.duoc.gamehub.order.repository.OrdenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrdenService {

    private static final Logger log = LoggerFactory.getLogger(OrdenService.class);

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private ProductoClient productoClient;

    @Autowired
    private InventarioClient inventarioClient;

    @Transactional
    public Orden crearOrden(OrdenDTO dto) {
        log.info("[ORDER-SERVICE] Iniciando creación de orden para usuario ID: {}", dto.getUsuarioId());

        Orden orden = new Orden();
        orden.setUsuarioId(dto.getUsuarioId());
        orden.setEstado("PENDIENTE");
        orden.setFecha(LocalDateTime.now());
        orden.setDescuento(0.0);

        double acumuladorSubtotal = 0.0;

        // Procesar y validar cada producto del detalle enviado por Postman
        for (DetalleOrdenDTO detDTO : dto.getDetalles()) {
            // 1. Comunicación por red: Valida existencia del producto y captura su precio real
            Map<String, Object> productoJson = productoClient.buscarPorId(detDTO.getProductoId());
            Double precioReal = Double.valueOf(productoJson.get("precio").toString());

            // 2. Comunicación por red: Intentar reservar stock en el inventario de forma síncrona
            Map<String, Object> reservaRequest = new HashMap<>();
            reservaRequest.put("productoId", detDTO.getProductoId());
            reservaRequest.put("cantidad", detDTO.getCantidad());

            // Si el inventario no tiene stock, arrojará excepción controlada automáticamente a través de Feign
            inventarioClient.reservarStockTemporal(reservaRequest);

            // 3. Crear el nodo de persistencia del detalle si las validaciones críticas pasaron
            DetalleOrden detalle = new DetalleOrden();
            detalle.setOrden(orden);
            detalle.setProductoId(detDTO.getProductoId());
            detalle.setCantidad(detDTO.getCantidad());
            detalle.setPrecioUnitario(precioReal);
            orden.getDetalles().add(detalle);

            acumuladorSubtotal += (precioReal * detDTO.getCantidad());
        }

        orden.setSubtotal(acumuladorSubtotal);
        orden.setTotal(acumuladorSubtotal - orden.getDescuento());

        log.info("[ORDER-SERVICE] Orden procesada y guardada con éxito en la base de datos. Total: ${}", orden.getTotal());
        return ordenRepository.save(orden);
    }

    public List<Orden> listarTodas() { return ordenRepository.findAll(); }

    public List<Orden> listarPorCliente(Long usuarioId) { return ordenRepository.findByUsuarioId(usuarioId); }

    public List<Orden> listarPorEstado(String estado) { return ordenRepository.findByEstado(estado.toUpperCase()); }

    public Orden buscarPorId(Long id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La orden solicitada no existe."));
    }

    @Transactional
    public Orden actualizarEstado(Long id, String nuevoEstado) {
        Orden orden = buscarPorId(id);

        // REGLA DE NEGOCIO OBLIGATORIA: No modificar órdenes ya cerradas o pagadas
        if ("PAGADA".equals(orden.getEstado()) || "CANCELADA".equals(orden.getEstado())) {
            throw new RuntimeException("Regla de negocio: No se puede modificar el estado de una orden que ya se encuentra en estado " + orden.getEstado());
        }

        log.info("[ORDER-SERVICE] Actualizando estado de orden ID: {} de {} a {}", id, orden.getEstado(), nuevoEstado);
        orden.setEstado(nuevoEstado.toUpperCase());
        return ordenRepository.save(orden);
    }

    @Transactional
    public void cancelarOrden(Long id) {
        Orden orden = buscarPorId(id);

        if ("PAGADA".equals(orden.getEstado())) {
            throw new RuntimeException("No se puede cancelar una orden que ya ha sido pagada con éxito.");
        }
        if ("CANCELADA".equals(orden.getEstado())) {
            throw new RuntimeException("La orden ya se encuentra cancelada.");
        }

        log.warn("[ORDER-SERVICE] Cancelando orden ID: {}. Iniciando devolución de stock reservado por red.", id);

        // REGLA DE NEGOCIO OBLIGATORIA: Cancelar orden debe liberar stock reservado
        for (DetalleOrden detalle : orden.getDetalles()) {
            // Mandamos a sumar la cantidad directamente al puerto 8085 de inventario
            inventarioClient.actualizarCantidades(detalle.getProductoId(), detalle.getCantidad());
        }

        orden.setEstado("CANCELADA");
        orden.setFecha(LocalDateTime.now());
        ordenRepository.save(orden);
        log.info("[ORDER-SERVICE] Orden ID: {} cancelada y stock liberado correctamente en el inventario.", id);
    }
}