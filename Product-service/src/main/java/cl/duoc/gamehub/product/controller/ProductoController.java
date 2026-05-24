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
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setMarca(dto.getMarca());
        producto.setModelo(dto.getModelo());
        producto.setPrecio(dto.getPrecio());
        producto.setDescripcion(dto.getDescripcion());
        producto.setEstado("ACTIVO");

        Producto nuevo = productoService.guardarProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // 2. LISTAR
    @GetMapping("/listar")
    public ResponseEntity<List<Producto>> obtenerCatalogo() {
        List<Producto> lista = productoService.listarTodos();
        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }

    // 3. BUSCAR POR ID
    @GetMapping("/buscar/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoService.buscarPorId(id)
                .map(producto -> ResponseEntity.status(HttpStatus.OK).body(producto))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // 4. ACTUALIZAR
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO dto) {
        Producto datosNuevos = new Producto();
        datosNuevos.setNombre(dto.getNombre());
        datosNuevos.setMarca(dto.getMarca());
        datosNuevos.setModelo(dto.getModelo());
        datosNuevos.setPrecio(dto.getPrecio());
        datosNuevos.setDescripcion(dto.getDescripcion());

        Producto actualizado = productoService.actualizarProducto(id, datosNuevos);
        return ResponseEntity.status(HttpStatus.OK).body(actualizado);
    }


    @DeleteMapping("/desactivar/{id}")
    public ResponseEntity<Producto> desactivar(@PathVariable Long id) {
        Producto desactivado = productoService.desactivarProducto(id);
        return ResponseEntity.status(HttpStatus.OK).body(desactivado);
    }
}