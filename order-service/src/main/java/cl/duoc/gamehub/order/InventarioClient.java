package cl.duoc.gamehub.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

@FeignClient(name = "inventory-service", path = "/api/inventario")
public interface InventarioClient {

    @PutMapping("/reservar")
    Map<String, Object> reservarStockTemporal(@RequestBody Map<String, Object> reservaRequest);

    @PutMapping("/actualizar/{productoId}")
    Map<String, Object> actualizarCantidades(
            @PathVariable("productoId") Long productoId,
            @RequestParam("stockDisponible") Integer stockDisponible);
}