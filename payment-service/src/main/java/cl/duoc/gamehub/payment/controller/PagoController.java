package cl.duoc.gamehub.payment.controller;

import cl.duoc.gamehub.payment.dto.PaymentRequestDTO;
import cl.duoc.gamehub.payment.model.Pago;
import cl.duoc.gamehub.payment.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/procesar")
    public ResponseEntity<Pago> procesarPago(@Valid @RequestBody PaymentRequestDTO request) {
        Pago nuevoPago = pagoService.procesarPago(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPago);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Pago>> listarTodos() {
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Pago> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.buscarPorId(id));
    }

    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<List<Pago>> listarPorOrden(@PathVariable Long ordenId) {
        return ResponseEntity.ok(pagoService.listarPorOrden(ordenId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pago>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(pagoService.listarPorEstado(estado));
    }

    @PutMapping("/actualizar-estado/{id}")
    public ResponseEntity<Pago> actualizarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(pagoService.actualizarEstado(id, nuevoEstado));
    }

    @PutMapping("/anular/{id}")
    public ResponseEntity<Void> anularPago(@PathVariable Long id) {
        pagoService.anularPago(id);
        return ResponseEntity.noContent().build();
    }
}