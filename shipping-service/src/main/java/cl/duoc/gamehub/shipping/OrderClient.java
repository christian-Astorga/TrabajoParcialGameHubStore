package cl.duoc.gamehub.shipping.client;

import cl.duoc.gamehub.shipping.dto.OrderExternalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", url = "http://localhost:8086/api/ordenes")
public interface OrderClient {
    @GetMapping("/buscar/{id}")
    OrderExternalDTO buscarOrdenPorId(@PathVariable("id") Long id);
}