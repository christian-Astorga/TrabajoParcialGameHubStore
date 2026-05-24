package cl.duoc.gamehub.inventory.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "product-service", url = "http://localhost:8082")
public interface ProductoClient {
    @GetMapping("/api/productos/buscar/{id}")
    void  buscarPorId(@PathVariable("id") Long id);
}