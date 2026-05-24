package cl.duoc.gamehub.product.service;

import cl.duoc.gamehub.product.dto.ProductoDTO;
import cl.duoc.gamehub.product.model.Producto;
import cl.duoc.gamehub.product.repository.ProductoRepository;
import cl.duoc.gamehub.product.client.CategoriaClient; // Importante para OpenFeign
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private CategoriaClient categoriaClient; // Inyección de nuestro puente OpenFeign
    public Producto guardarProducto(ProductoDTO dto) {
        log.info("[PRODUCT-SERVICE] Guardando nuevo producto gamer en catálogo: {}. Validando categoría ID: {}", dto.getNombre(), dto.getCategoriaId());

        // LLAMADA PURA DE OPENFEIGN (Deja que viaje la excepción si la categoría no existe)
        Map<String, Object> categoria = categoriaClient.buscarPorId(dto.getCategoriaId());

        if (categoria != null) {
            String estado = (String) categoria.get("estado");
            if (estado != null && estado.equals("INACTIVO")) {
                log.error("[PRODUCT-SERVICE] Error de negocio: La categoría ID {} está INACTIVA.", dto.getCategoriaId());
                throw new RuntimeException("No se pueden asociar productos a una categoría inactiva.");
            }
            log.info("[PRODUCT-SERVICE] Validación exitosa. La categoría '{}' está ACTIVA.", categoria.get("nombre"));
        }
        // Lógica de guardado que ya tenías impecable
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
        log.info("[PRODUCT-SERVICE] Solicitud recibida para actualizar producto ID: {}", id);

        Producto p = buscarPorId(id);
        // Validación extra si intentan cambiar la categoría en la edición
        if (dto.getCategoriaId() != null && !dto.getCategoriaId().equals(p.getCategoriaId())) {
            Map<String, Object> categoria = categoriaClient.buscarPorId(dto.getCategoriaId());
            if (categoria != null) {
                String estado = (String) categoria.get("estado");
                if (estado != null && estado.equals("INACTIVO")) {
                    throw new RuntimeException("No se pueden asociar productos a una categoría inactiva.");
                }
            }
            p.setCategoriaId(dto.getCategoriaId());
        }
        p.setNombre(dto.getNombre());
        p.setPrecio(dto.getPrecio());
        p.setDescripcion(dto.getDescripcion());
        if (dto.getEstado() != null) p.setEstado(dto.getEstado().toUpperCase());
        return productoRepository.save(p);
    }
    public Producto desactivarProducto(Long id) {
        log.warn("[PRODUCT-SERVICE] Ejecutando desactivación lógica (no física) para el ID: {}", id);

        Producto p = buscarPorId(id);
        p.setEstado("INACTIVO");
        return productoRepository.save(p);
    }
}