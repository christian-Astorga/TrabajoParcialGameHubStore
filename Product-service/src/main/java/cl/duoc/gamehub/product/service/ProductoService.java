package cl.duoc.gamehub.product.service;

import cl.duoc.gamehub.product.dto.ProductoDTO;
import cl.duoc.gamehub.product.model.Producto;
import cl.duoc.gamehub.product.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductoService {

    // 1. Declaración del Logger para SLF4J
    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    @Autowired
    private ProductoRepository productoRepository;

    public Producto guardarProducto(ProductoDTO dto) {
        // estructurado de creación
        log.info("[PRODUCT-SERVICE] Guardando nuevo producto gamer en catálogo: {}", dto.getNombre());

        Producto p = new Producto();
        p.setNombre(dto.getNombre());
        p.setMarca(dto.getMarca());
        p.setModelo(dto.getModelo());
        p.setPrecio(dto.getPrecio());
        p.setCategoriaId(dto.getCategoriaId());
        p.setDescripcion(dto.getDescripcion());
        p.setEstado("ACTIVO");
        return productoRepository.save(p);
    }

    public List<Producto> listarTodos() { return productoRepository.findAll(); }

    public List<Producto> listarPorCategoria(Long id) { return productoRepository.findByCategoriaId(id); }

    public List<Producto> listarPorMarca(String marca) { return productoRepository.findByMarca(marca); }

    public List<Producto> listarPorEstado(String estado) { return productoRepository.findByEstado(estado.toUpperCase()); }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id).orElseThrow(() -> {

            log.error("[PRODUCT-SERVICE] Error: Producto con ID {} no existe en inventario", id);
            return new RuntimeException("Producto no encontrado");
        });
    }

    public Producto actualizarProducto(Long id, ProductoDTO dto) {
        // estructurado de actualización
        log.info("[PRODUCT-SERVICE] Solicitud recibida para actualizar producto ID: {}", id);

        Producto p = buscarPorId(id);
        p.setNombre(dto.getNombre());
        p.setPrecio(dto.getPrecio());
        p.setDescripcion(dto.getDescripcion());
        if (dto.getEstado() != null) p.setEstado(dto.getEstado().toUpperCase());
        return productoRepository.save(p);
    }

    public Producto desactivarProducto(Long id) {
        //  advertencia por desactivación
        log.warn("[PRODUCT-SERVICE] Ejecutando desactivación lógica (no física) para el ID: {}", id);

        Producto p = buscarPorId(id);
        p.setEstado("INACTIVO");
        return productoRepository.save(p);
    }
}