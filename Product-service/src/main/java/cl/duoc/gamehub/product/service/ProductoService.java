package cl.duoc.gamehub.product.service;

import cl.duoc.gamehub.product.client.CategoriaClient;
import cl.duoc.gamehub.product.model.Producto;
import cl.duoc.gamehub.product.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    @Autowired
    private ProductoRepository repository;

    @Autowired
    private CategoriaClient categoriaClient; // Inyección con el nombre correcto en español

    public Producto crear(Producto producto) {
        log.info("[PRODUCT-SERVICE] Validando existencia de categoría ID: {}", producto.getCategoriaId());

        try {
            // Comunicación remota vía OpenFeign usando CategoriaClient
            categoriaClient.buscarPorId(producto.getCategoriaId());
        } catch (Exception e) {
            log.error("[PRODUCT-SERVICE] Error: La categoría {} no existe en el sistema.", producto.getCategoriaId());
            throw new RuntimeException("No se puede crear el producto: La categoría especificada no existe.");
        }

        producto.setEstado("ACTIVO");
        log.info("[PRODUCT-SERVICE] Producto '{}' guardado con éxito.", producto.getNombre());
        return repository.save(producto);
    }

    public List<Producto> listarTodos() {
        log.info("[PRODUCT-SERVICE] Listando todos los productos del catálogo.");
        return repository.findAll();
    }

    public Producto buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    public List<Producto> listarPorCategoria(Long categoriaId) {
        return repository.findByCategoriaId(categoriaId);
    }

    public Producto actualizar(Long id, Producto datos) {
        log.info("[PRODUCT-SERVICE] Actualizando datos del producto ID: {}", id);
        Producto prod = buscarPorId(id);
        prod.setNombre(datos.getNombre());
        prod.setDescripcion(datos.getDescripcion());
        prod.setPrecio(datos.getPrecio());
        if (datos.getEstado() != null) prod.setEstado(datos.getEstado().toUpperCase());
        return repository.save(prod);
    }

    public Producto desactivar(Long id) {
        log.warn("[PRODUCT-SERVICE] Desactivando lógicamente el producto ID: {}", id);
        Producto prod = buscarPorId(id);
        prod.setEstado("INACTIVO");
        return repository.save(prod);
    }
}