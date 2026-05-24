package cl.duoc.gamehub.product.service;

import cl.duoc.gamehub.product.model.Producto;
import cl.duoc.gamehub.product.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductoService {

    // Log idéntico al solicitado en la materia para trazabilidad en consola
    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    @Autowired
    private ProductoRepository productoRepository;

    public Producto guardarProducto(Producto producto) {
        log.info("Guardando un nuevo producto...");
        return productoRepository.save(producto);
    }

    public List<Producto> listarTodos() {
        log.info("Listando todos los productos...");
        return productoRepository.findAll();
    }
}