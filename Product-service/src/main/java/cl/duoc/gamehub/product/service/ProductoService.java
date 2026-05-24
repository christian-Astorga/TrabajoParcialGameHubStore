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

    // Instancia obligatoria por rúbrica para Logs estructurados
    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    @Autowired
    private ProductoRepository productoRepository;

    // Guardar Producto en Laragon
    public Producto guardarProducto(Producto producto) {
        log.info("Iniciando persistencia del producto gamer: [{}]", producto.getNombre());
        Producto guardado = productoRepository.save(producto);
        log.info("Producto registrado exitosamente en la BD. ID Generado: {}", guardado.getId());
        return guardado;
    }

    // Listar todo el catálogo
    public List<Producto> listarTodos() {
        log.info("Se ha solicitado la lista completa de productos para el catálogo.");
        return productoRepository.findAll();
    }
}