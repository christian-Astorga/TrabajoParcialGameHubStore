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


    @PostMapping("/crear")
    public ResponseEntity<Producto> registrarProducto(@Valid @RequestBody Producto producto) {
        Producto nuevoProducto = productoService.guardarProducto(producto);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }


    @GetMapping("/listar")
    public ResponseEntity<List<Producto>> obtenerCatalogo() {
        List<Producto> lista = productoService.listarTodos();
        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }
}