GameHub Store - Ecosistema de Microservicios Backend

Descripción del Proyecto
GameHub Store es una solución de software empresarial distribuida y de alta disponibilidad, diseñada específicamente para gestionar 
las operaciones del mercado de hardware gamer y componentes de computación de alta gama. 

El núcleo del sistema está estructurado bajo un enfoque de Arquitectura de Microservicios Independientes.
rompiendo con el esquema tradicional monolítico para garantizar el escalamiento horizontal, la tolerancia a fallos y la autonomía de despliegue. 
Cada módulo funcional ha sido modelado siguiendo el patrón de diseño *Controller-Service-Repository (CSR)*.
asegurando una separación estricta de responsabilidades (capa de presentación REST, capa de lógica de negocio y capa de acceso a datos).

Pilares Técnicos Implementados:
* Persistencia Aislada (Database-per-Service): Cada microservicio posee un esquema de base de datos relacional independiente en el motor MySQL (Laragon).
* comunicándose únicamente mediante interfaces de red y eliminando dependencias físicas o acoplamientos rígidos mediante llaves foráneas inter-servidor.
* Comunicación Inter-Servicio: Integración síncrona y reactiva mediante *OpenFeign (Feign Clients)* para la validación cruzada de reglas de negocio en tiempo real.
* Robustez y Gestión de Errores: Centralización del flujo de excepciones mediante un interceptor global @RestControllerAdvice.
* validación estricta de contratos de entrada utilizando *Bean Validation (JSR 380)* para garantizar la integridad de los datos recibidos.


Nombres de los Estudiantes (Equipo de Desarrollo)
El diseño, construcción, documentación y pruebas de este ecosistema de microservicios ha sido ejecutado colaborativamente por:

* Christian Astorga** - Desarrollador Principal / Líder de Arquitectura e Infraestructura Git
* Diego Velozo - Ingeniero de Software Backend / Especialista en Capa de Persistencia
* Felipe Gonzales - Ingeniero de Software Backend / Especialista en Lógica de Negocio y DTOs

* Asignatura: Desarrollo FullStack I
* Fecha actual: 26/05/2026

* 
