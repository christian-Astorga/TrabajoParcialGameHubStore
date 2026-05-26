package cl.duoc.gamehub.payment;

import cl.duoc.gamehub.payment.dto.OrderExternalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service", url = "http://localhost:8086/api/ordenes")
public interface OrderClient {

    @GetMapping("/buscar/{id}")
    OrderExternalDTO buscarOrdenPorId(@PathVariable("id") Long id);

    @PutMapping("/actualizar-estado/{id}")
    void actualizarEstadoOrden(@PathVariable("id") Long id, @RequestParam("estado") String estado);
}