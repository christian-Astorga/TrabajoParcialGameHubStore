package cl.duoc.gamehub.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "product-service") // Limpio, dinámico y alineado a la rúbrica
public interface ProductoClient {

    @GetMapping("/api/productos/buscar/{id}")
    Map<String, Object> buscarPorId(@PathVariable("id") Long id);
}