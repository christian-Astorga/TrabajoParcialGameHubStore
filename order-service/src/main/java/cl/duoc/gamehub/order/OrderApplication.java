package cl.duoc.gamehub.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableFeignClients(basePackages = "cl.duoc.gamehub.order.client")
public class OrderApplication {

    private static final Logger log = LoggerFactory.getLogger(OrderApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);

    }
}