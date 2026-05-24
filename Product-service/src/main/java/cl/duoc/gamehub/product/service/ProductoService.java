package cl.duoc.gamehub.product.service;

import cl.duoc.gamehub.product.model.Producto;
import cl.duoc.gamehub.product.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    @Autowired
    private ProductoRepository productoRepository;

    // Guardar un producto nuevo
    public Producto guardarProducto(Producto producto) {
        log.info("Guardando un nuevo producto: {}", producto.getNombre());
        return productoRepository.save(producto);
    }

    // Listar todo el catálogo
    public List<Producto> listarTodos() {
        log.info("Listando todos los productos...");
        return productoRepository.findAll();
    }

    // Buscar producto por ID
    public Optional<Producto> buscarPorId(Long id) {
        log.info("Buscando producto con ID: {}", id);
        return productoRepository.findById(id);
    }

    // Actualizar producto completo
    public Producto actualizarProducto(Long id, Producto datosNuevos) {
        log.info("Actualizando datos del producto con ID: {}", id);
        return productoRepository.findById(id).map(producto -> {
            producto.setNombre(datosNuevos.getNombre());
            producto.setMarca(datosNuevos.getMarca());
            producto.setModelo(datosNuevos.getModelo());
            producto.setPrecio(datosNuevos.getPrecio());
            producto.setDescripcion(datosNuevos.getDescripcion());
            return productoRepository.save(producto);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    // Desactivar producto (Eliminación lógica exigida por rúbrica)
    public Producto desactivarProducto(Long id) {
        log.info("Desactivando lógicamente el producto con ID: {}", id);
        return productoRepository.findById(id).map(producto -> {
            producto.setEstado("INACTIVO");
            return productoRepository.save(producto);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }
}