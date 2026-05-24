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

    @PostMapping("/crear")
    public ResponseEntity<Producto> registrarProducto(@Valid @RequestBody ProductoDTO dto) {
        // Traspaso manual de DTO a Entidad (Nivel esperado en la materia)
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setMarca(dto.getMarca());
        producto.setModelo(dto.getModelo());
        producto.setPrecio(dto.getPrecio());
        producto.setDescripcion(dto.getDescripcion());
        producto.setEstado("ACTIVO"); // Lógica de negocio por defecto

        Producto nuevo = productoService.guardarProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Producto>> obtenerCatalogo() {
        List<Producto> lista = productoService.listarTodos();
        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }
}