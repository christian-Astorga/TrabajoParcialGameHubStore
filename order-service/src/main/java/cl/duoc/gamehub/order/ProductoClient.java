package cl.duoc.gamehub.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "product-service", path = "/api/productos")
public interface ProductoClient {

    @GetMapping("/buscar/{id}")
    Map<String, Object> buscarPorId(@PathVariable("id") Long id);
}