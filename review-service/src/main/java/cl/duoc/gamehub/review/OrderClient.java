package cl.duoc.gamehub.review.client;

import cl.duoc.gamehub.review.dto.OrderValidationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service")
public interface OrderClient {

    @GetMapping("/api/ordenes/buscar/{id}")
    OrderValidationDTO buscarOrdenPorId(@PathVariable("id") Long id);
}