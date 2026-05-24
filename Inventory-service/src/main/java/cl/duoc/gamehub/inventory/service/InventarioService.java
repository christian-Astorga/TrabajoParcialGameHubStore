package cl.duoc.gamehub.inventory.service;

import cl.duoc.gamehub.inventory.client.ProductoClient;
import cl.duoc.gamehub.inventory.dto.InventarioDTO;
import cl.duoc.gamehub.inventory.dto.ReservaDTO;
import cl.duoc.gamehub.inventory.model.Inventario;
import cl.duoc.gamehub.inventory.model.MovimientoInventario;
import cl.duoc.gamehub.inventory.repository.InventarioRepository;
import cl.duoc.gamehub.inventory.repository.MovimientoInventarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventarioService {

    private static final Logger log = LoggerFactory.getLogger(InventarioService.class);

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    @Autowired
    private ProductoClient productoClient; // Cliente OpenFeign

    @Transactional
    public Inventario crearRegistroStock(InventarioDTO dto) {
        log.info("[INVENTORY-SERVICE] Creando registro de stock. Validando existencia del producto ID: {}", dto.getProductoId());

        // Valida si el producto existe
        productoClient.buscarPorId(dto.getProductoId());

        // Valida si ya existe un registro de inventario para este producto
        if (inventarioRepository.findByProductoId(dto.getProductoId()).isPresent()) {
            throw new RuntimeException("El producto ya cuenta con un registro de inventario activo. Use actualización de cantidades.");
        }

        Inventario inv = new Inventario();
        inv.setProductoId(dto.getProductoId());
        inv.setStockDisponible(dto.getStockDisponible());
        inv.setStockReservado(0); // Inicia sin reservas
        inv.setStockMinimo(dto.getStockMinimo());
        inv.setUbicacion(dto.getUbicacion().toUpperCase());

        Inventario guardado = inventarioRepository.save(inv);

        // Registro obligatorio
        movimientoRepository.save(new MovimientoInventario(dto.getProductoId(), "ENTRADA", dto.getStockDisponible(), LocalDateTime.now()));
        log.info("[INVENTORY-SERVICE] Registro de stock inicial creado con éxito para el producto ID: {}", dto.getProductoId());

        return guardado;
    }

    public List<Inventario> listarTodo() { return inventarioRepository.findAll(); }

    public Inventario buscarPorId(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de inventario no encontrado."));
    }

    public Inventario buscarPorProductoId(Long productoId) {
        return inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("No existe registro de inventario para el producto ID especificado."));
    }

    @Transactional
    public Inventario reservarStockTemporal(ReservaDTO dto) {
        log.info("[INVENTORY-SERVICE] Procesando solicitud de reserva temporal para producto ID: {} - Cantidad: {}", dto.getProductoId(), dto.getCantidad());

        Inventario inv = buscarPorProductoId(dto.getProductoId());

        // REGLA DE NEGOCIO: No reservar más unidades que el stock disponible
        if (dto.getCantidad() > inv.getStockDisponible()) {
            log.error("[INVENTORY-SERVICE] Fallo de reserva: Stock insuficiente. Disponible: {}, Solicitado: {}", inv.getStockDisponible(), dto.getCantidad());
            throw new RuntimeException("No se pueden reservar más unidades que el stock disponible actualmente.");
        }

        // Ajustar cantidades
        inv.setStockDisponible(inv.getStockDisponible() - dto.getCantidad());
        inv.setStockReservado(inv.getStockReservado() + dto.getCantidad());

        // Registrar movimiento
        movimientoRepository.save(new MovimientoInventario(dto.getProductoId(), "RESERVA", dto.getCantidad(), LocalDateTime.now()));

        return inventarioRepository.save(inv);
    }

    @Transactional
    public Inventario actualizarCantidades(Long productoId, Integer nuevoStockDisponible) {
        log.info("[INVENTORY-SERVICE] Actualizando stock disponible manualmente para producto ID: {}", productoId);

        if (nuevoStockDisponible < 0) {
            throw new RuntimeException("El stock disponible no puede quedar negativo.");
        }

        Inventario inv = buscarPorProductoId(productoId);
        int diferencia = nuevoStockDisponible - inv.getStockDisponible();

        inv.setStockDisponible(nuevoStockDisponible);
        Inventario actualizado = inventarioRepository.save(inv);

        if (diferencia != 0) {
            String tipoMovimiento = diferencia > 0 ? "ENTRADA" : "SALIDA";
            movimientoRepository.save(new MovimientoInventario(productoId, tipoMovimiento, Math.abs(diferencia), LocalDateTime.now()));
        }

        return actualizado;
    }

    @Transactional
    public void eliminarRegistroStockObsoleto(Long id) {
        log.warn("[INVENTORY-SERVICE] Eliminando de forma física registro de inventario obsoleto ID: {}", id);
        Inventario inv = buscarPorId(id);

        // Antes de borrar, dejamos un movimiento de salida final como rastro histórico
        movimientoRepository.save(new MovimientoInventario(inv.getProductoId(), "SALIDA", inv.getStockDisponible(), LocalDateTime.now()));
        inventarioRepository.delete(inv);
    }
}