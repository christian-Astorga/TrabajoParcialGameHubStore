package cl.duoc.gamehub.product.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "category-service", path = "/api/categorias")
public interface CategoriaClient {

    @GetMapping("/buscar/{id}")
    Object buscarPorId(@PathVariable("id") Long id);
}