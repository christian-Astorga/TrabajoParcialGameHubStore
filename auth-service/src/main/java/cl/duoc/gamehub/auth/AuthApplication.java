package cl.duoc.gamehub.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients; // <-- Este import ahora se pondrá gris/normal, ya no rojo

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients     
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}