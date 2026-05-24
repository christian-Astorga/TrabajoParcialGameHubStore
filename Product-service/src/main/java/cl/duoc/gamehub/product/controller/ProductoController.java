package cl.duoc.gamehub.product.controller;

import cl.duoc.gamehub.product.dto.ProductoDTO;
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

    // 1. CREAR
    @PostMapping("/crear")
    public ResponseEntity<Producto> registrarProducto(@Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.guardarProducto(dto));
    }

    // 2. LISTAR (Con filtros incluidos)
    @GetMapping("/listar")
    public ResponseEntity<List<Producto>> obtenerCatalogo(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String estado) {

        if (categoriaId != null) {
            return ResponseEntity.ok(productoService.listarPorCategoria(categoriaId));
        }
        if (marca != null) {
            return ResponseEntity.ok(productoService.listarPorMarca(marca));
        }
        if (estado != null) {
            return ResponseEntity.ok(productoService.listarPorEstado(estado));
        }
        return ResponseEntity.ok(productoService.listarTodos());
    }

    // 3. BUSCAR POR ID
    @GetMapping("/buscar/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    // 4. ACTUALIZAR
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, dto));
    }

    // 5. DESACTIVAR
    @DeleteMapping("/desactivar/{id}")
    public ResponseEntity<Producto> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.desactivarProducto(id));
    }
}