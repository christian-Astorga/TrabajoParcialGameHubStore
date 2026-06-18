package cl.duoc.gamehub.inventory.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "product-service", path = "/api/productos")
public interface ProductoClient {

    @GetMapping("/buscar/{id}")
    Object buscarPorId(@PathVariable("id") Long id);
    
}