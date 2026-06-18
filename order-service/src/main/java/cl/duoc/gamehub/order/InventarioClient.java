package cl.duoc.gamehub.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@FeignClient(name = "inventory-service") //
public interface InventarioClient {
    @PutMapping("/api/inventario/reservar")
    Map<String, Object> reservarStockTemporal(@RequestBody Map<String, Object> reservaRequest);

    @PutMapping("/api/inventario/actualizar/{productoId}")
    Map<String, Object> actualizarCantidades(
            @PathVariable("productoId") Long productoId,
            @RequestParam("stockDisponible") Integer stockDisponible);
}