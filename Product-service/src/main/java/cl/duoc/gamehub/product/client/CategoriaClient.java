package cl.duoc.gamehub.product.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;


@FeignClient(name = "category-service", url = "http://localhost:8084/api/categorias")
public interface CategoriaClient {

  
    @GetMapping("/buscar/{id}")
    Map<String, Object> buscarPorId(@PathVariable("id") Long id);
}