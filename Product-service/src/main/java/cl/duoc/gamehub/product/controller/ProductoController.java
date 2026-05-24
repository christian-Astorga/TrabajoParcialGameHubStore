package cl.duoc.gamehub.product.controller;

import cl.duoc.gamehub.product.model.Producto;
import cl.duoc.gamehub.product.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // Crear producto usando el formato exacto del PDF de la materia
    @PostMapping("/crear")
    public ResponseEntity<Producto> registrarProducto(@Valid @RequestBody Producto producto) {
        Producto nuevoProducto = productoService.guardarProducto(producto);
        // Formato exacto exigido en la diapo de Duoc:
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    // Listar catálogo
    @GetMapping("/listar")
    public ResponseEntity<List<Producto>> obtenerCatalogo() {
        List<Producto> lista = productoService.listarTodos();
        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }
}