package cl.duoc.gamehub.payment.controller;

import cl.duoc.gamehub.payment.dto.PaymentRequestDTO; // Ajustado a tu DTO real
import cl.duoc.gamehub.payment.model.Pago;
import cl.duoc.gamehub.payment.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/pagos")
@Tag(name = "Pagos", description = "Endpoints para el procesamiento financiero y registro de transacciones de GameHub")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @PostMapping("/procesar")
    @Operation(summary = "Procesar Pago", description = "Registra una transacción y dispara un evento OpenFeign síncrono para dar por pagada la orden")
    public ResponseEntity<EntityModel<Pago>> procesarPago(@Valid @RequestBody PaymentRequestDTO dto) { // Cambiado a PaymentRequestDTO
        Pago nuevo = pagoService.procesarPago(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapearHateoas(nuevo));
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar todos los pagos registrados")
    public ResponseEntity<List<EntityModel<Pago>>> listarTodos() {
        List<EntityModel<Pago>> lista = pagoService.listarTodos().stream()
                .map(this::mapearHateoas)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/buscar/{id}")
    @Operation(summary = "Buscar pago por ID de registro")
    public ResponseEntity<EntityModel<Pago>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapearHateoas(pagoService.buscarPorId(id)));
    }

    @GetMapping("/orden/{ordenId}")
    @Operation(summary = "Buscar pagos asociados a una Orden ID")
    public ResponseEntity<List<EntityModel<Pago>>> buscarPorOrdenId(@PathVariable Long ordenId) {
        List<EntityModel<Pago>> lista = pagoService.buscarPorOrdenId(ordenId).stream()
                .map(this::mapearHateoas)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    private EntityModel<Pago> mapearHateoas(Pago pago) {
        Link selfLink = linkTo(methodOn(PagoController.class).buscarPorId(pago.getId())).withSelfRel();
        Link ordenLink = linkTo(methodOn(PagoController.class).buscarPorOrdenId(pago.getOrdenId())).withRel("pagos_de_esta_orden");
        Link listarLink = linkTo(methodOn(PagoController.class).listarTodos()).withRel("historial_pagos");
        return EntityModel.of(pago, selfLink, ordenLink, listarLink);
    }
}