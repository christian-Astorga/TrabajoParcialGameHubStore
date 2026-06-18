package cl.duoc.gamehub.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service", path = "/api/ordenes")
public interface OrderClient {

    // Cambia el estado de la orden en el puerto 8086 cuando el pago se concrete
    @PutMapping("/actualizar-estado/{id}")
    Object actualizarEstado(@PathVariable("id") Long id, @RequestParam("estado") String estado);
}