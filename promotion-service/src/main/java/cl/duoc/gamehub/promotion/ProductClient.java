package cl.duoc.gamehub.promotion;

import cl.duoc.gamehub.promotion.dto.ProductExternalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "http://localhost:8081/api/productos")
public interface ProductClient {

    @GetMapping("/buscar/{id}")
    ProductExternalDTO buscarProductoPorId(@PathVariable("id") Long id);
}