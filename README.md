# GameHub Store para proyecto DuoUC

## Descripción del Proyecto
Proyecto desarrollado para la asignatura de Desarrollo Fullstack I. GameHub Store es un sistema backend para una plataforma de comercio electrónico orientada a la venta de videojuegos y artículos relacionados (Hipotetico). 

El sistema está construido bajo una arquitectura de **microservicios**, garantizando escalabilidad, bajo acoplamiento y alta cohesión entre los distintos dominios del negocio.

## Tecnologías Utilizadas
* **Java 21**
* **Spring Boot 3.X**
* **OpenFeign** (Comunicación síncrona entre microservicios)
* **JUnit 5 & Mockito** (Pruebas unitarias y simulaciones)
* **Maven**
* **Eureka**
* **Api Gateway**
* **Swagger Hateoas**

## Arquitectura del Sistema
El ecosistema se compone de 10 microservicios independientes, cada uno responsable de un dominio específico:

1. `auth-service`: Gestión de credenciales, autenticación y seguridad.
2. `category-service`: Clasificación y agrupación del catálogo.
3. `inventory-service`: Control de stock, movimientos y reservas temporales.
4. `order-service`: Procesamiento de carritos de compra y cálculo de totales.
5. `payment-service`: Simulación y registro de transacciones de pago.
6. `product-service`: Gestión central del catálogo de artículos.
7. `promotion-service`: Lógica de validación para cupones y descuentos.
8. `review-service`: Calificaciones, comentarios y moderación de usuarios.
9. `shipping-service`: Asignación de tracking y logística de entregas.
10. `user-service`: Administración de perfiles e información de contacto.

## Ejecución de Pruebas Unitarias 
Para ejecutar las pruebas unitarias ejecutar desde la CARPETA TEST!
