package cl.duoc.gamehub.inventory;

import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableFeignClients(basePackages = "cl.duoc.gamehub.inventory.client")
public class InventoryApplication {

    private static final Logger log = LoggerFactory.getLogger(InventoryApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
        log.info("[GAMEHUB-STORE] Microservicio Inventory-Service iniciado correctamente en puerto 8085 usando Spring Boot v{}", SpringBootVersion.getVersion());
    }
}