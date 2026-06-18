package cl.duoc.gamehub.payment.controller;

import cl.duoc.gamehub.payment.dto.PaymentRequestDTO;
import cl.duoc.gamehub.payment.model.Pago;
import cl.duoc.gamehub.payment.service.PagoService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@Tag(name = "Módulo de Pagos", description = "Controlador para la gestión de transacciones")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @Operation(summary = "Procesar Pago", description = "Permite registrar un nuevo intento de pago en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pago creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos de la solicitud")
    })
    @PostMapping("/procesar")
    public ResponseEntity<Pago> procesarPago(@Valid @RequestBody PaymentRequestDTO request) {
        Pago nuevoPago = pagoService.procesarPago(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPago);
    }

    @Operation(summary = "Listar todos los pagos", description = "Obtiene la lista completa de registros.")
    @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida con éxito")
    @GetMapping("/listar")
    public ResponseEntity<List<Pago>> listarTodos() {
        List<Pago> lista = pagoService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Buscar por ID", description = "Busca un pago específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado con éxito"),
            @ApiResponse(responseCode = "404", description = "El pago no fue encontrado")
    })
    @GetMapping("/buscar/{id}")
    public ResponseEntity<Pago> buscarPorId(@PathVariable Long id) {
        Pago pago = pagoService.buscarPorId(id);
        return ResponseEntity.ok(pago);
    }

    @Operation(summary = "Listar por Orden", description = "Obtiene los pagos asociados a una orden de compra.")
    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<List<Pago>> listarPorOrden(@PathVariable Long ordenId) {
        List<Pago> lista = pagoService.listarPorOrden(ordenId);
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Listar por Estado", description = "Filtra los pagos según su condición")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pago>> listarPorEstado(@PathVariable String estado) {
        List<Pago> lista = pagoService.listarPorEstado(estado);
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Actualizar Estado", description = "Modifica el estado de una transacción.")
    @PutMapping("/actualizar-estado/{id}")
    public ResponseEntity<Pago> actualizarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        Pago actualizado = pagoService.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.ok(actualizado);
    }

    @Operation(summary = "Anular Pago", description = "Cambia el estado del pago a anulado de forma definitiva.")
    @ApiResponse(responseCode = "204", description = "Pago correcto")
    @PutMapping("/anular/{id}")
    public ResponseEntity<Void> anularPago(@PathVariable Long id) {
        pagoService.anularPago(id);
        return ResponseEntity.noContent().build();
    }
}