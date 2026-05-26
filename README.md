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


MICROSERVICIO: auth-service (Puerto 8081)
Persistencia: Base de datos gamehub_auth

Registro de Nuevos Usuarios
Método: POST
URL de Consulta: http://localhost:8081/api/auth/register
Headers: Content-Type: application/json
Request Body:
{
"nombre": "Christian Astorga",
"email": "c.astorga@duocuc.cl",
"password": "GamerSecure2026!",
"rol": "ADMINISTRADOR"
}
Respuestas:

201 Created: Usuario registrado exitosamente. La contraseña es procesada mediante hashing criptográfico en el motor de persistencia.

400 Bad Request: Error de validación JSR 380 debido a campos obligatorios vacíos o duplicidad de registro por correo ya existente.

Autenticación de Usuarios (Login)
Método: POST
URL de Consulta: http://localhost:8081/api/auth/login
Headers: Content-Type: application/json
Request Body:
{
"email": "c.astorga@duocuc.cl",
"password": "GamerSecure2026!"
}
Respuesta Exitosa 200 OK:
{
"token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjLmFzdG9yZ2FAZHVvY3VjLmNsIiwicm9sZXMiOlsiQ0xJRU5URSJdLCJpYXQiOjE3NDAzODQwMDAsImV4cCI6MTc0MDM5MTYwMH0.th7x...",
"type": "Bearer",
"email": "c.astorga@duocuc.cl",
"rol": "ADMINISTRADOR"
}
Respuestas de Error:

401 Unauthorized: Credenciales inválidas. La contraseña no coincide o la verificación del hash falló.

404 Not Found: El correo electrónico ingresado no existe en los registros de la aplicación.

Validación y Extracción de Claims
Método: GET
URL de Consulta: http://localhost:8081/api/auth/validate
Headers Requeridos: Authorization: Bearer TOKEN_JWT
Respuestas:

200 OK: El token es válido y se encuentra vigente. Retorna la estructura de permisos asociada al usuario.

401 Unauthorized: El token ha expirado cronológicamente o presenta alteraciones estructurales.



MICROSERVICIO: product-service (Puerto 8082)
Persistencia: Base de datos gamehub_products

Crear Producto
Método: POST
URL de Consulta: http://localhost:8082/api/productos/crear
Headers: Content-Type: application/json
Request Body:
{
"nombre": "NVIDIA RTX 4090 24GB",
"descripcion": "Tarjeta gráfica de última generación para gaming 4K.",
"precio": 1950000,
"categoriaId": 1
}
Respuestas:

201 Created: Producto ingresado correctamente dentro del catálogo maestro de la tienda.

400 Bad Request: Error de Bean Validation debido a atributos obligatorios nulos, cadenas vacías o precio inferior al límite mínimo permitido.

Listar Catálogo Completo
Método: GET
URL de Consulta: http://localhost:8082/api/productos/listar
Headers: None
Respuestas:

200 OK: Operación exitosa. Devuelve un arreglo dinámico en formato JSON con la totalidad de los productos activos en el sistema.

Consultar Producto por Identificador Único
Método: GET
URL de Consulta: http://localhost:8082/api/productos/buscar/101
Headers: None
Respuestas:

200 OK: Registro localizado. Retorna el objeto JSON con el detalle técnico, precio y estado actual del artículo solicitado.

404 Not Found: El identificador del producto ingresado en la ruta no coincide con ningún registro en la base de datos gamehub_products.

Actualizar Datos de Producto Existente
Método: PUT
URL de Consulta: http://localhost:8082/api/productos/actualizar/101
Headers: Content-Type: application/json
Request Body:
{
"nombre": "NVIDIA RTX 4090 24GB OC Edition",
"descripcion": "Tarjeta gráfica con overclocking de fábrica y sistema de refrigeración mejorado.",
"precio": 2050000,
"categoriaId": 1
}
Respuestas:

200 OK: Modificación procesada de forma correcta en la capa de persistencia. Retorna el objeto actualizado.

400 Bad Request: Datos de entrada corruptos o violaciones en las restricciones de validación del DTO.

404 Not Found: No se encontró ningún producto asociado al ID especificado en la URL de la ruta.

Eliminar Producto del Catálogo (Baja Lógica)
Método: DELETE
URL de Consulta: http://localhost:8082/api/productos/eliminar/101
Headers: None
Respuestas:

200 OK: El estado del producto ha sido modificado a inactivo de manera exitosa para preservar la integridad referencial histórica del ecosistema.

404 Not Found: El identificador proporcionado no corresponde a ningún artículo vigente dentro del maestro de datos.




MICROSERVICIO: user-service (Puerto 8083)
Persistencia: Base de datos gamehub_users

Obtener Perfil de Usuario por Identificador Único
Método: GET
URL de Consulta: http://localhost:8083/api/usuarios/perfil/5
Headers: None
Respuestas:

200 OK: Operación exitosa. Retorna el objeto JSON con los datos personales, correo electrónico corporativo o de cliente, y el rol asignado en el sistema.

404 Not Found: El identificador de usuario ingresado en la ruta no existe en los registros de la base de datos gamehub_users.

Actualizar Información de Perfil
Método: PUT
URL de Consulta: http://localhost:8083/api/usuarios/actualizar/5
Headers: Content-Type: application/json
Request Body:
{
"nombre": "Christian Astorga",
"telefono": "+56912345678",
"ciudad": "Viña del Mar"
}
Respuestas:

200 OK: Información modificada correctamente en la capa de persistencia. Retorna la entidad actualizada.

400 Bad Request: Error en la validación del contrato de entrada debido a formatos de teléfono incorrectos o campos obligatorios nulos.

404 Not Found: El ID de usuario proporcionado no coincide con ningún registro vigente.

Listar Todos los Usuarios Registrados (Exclusivo Administrador)
Método: GET
URL de Consulta: http://localhost:8083/api/usuarios/listar
Headers: None
Respuestas:

200 OK: Retorna un arreglo dinámico en formato JSON con la totalidad de los usuarios registrados en el ecosistema, útil para la reportería del panel de administración.

Registrar Dirección de Despacho Adicional
Método: POST
URL de Consulta: http://localhost:8083/api/usuarios/direcciones/agregar
Headers: Content-Type: application/json
Request Body:
{
"usuarioId": 5,
"direccion": "Av. Concón Reñaca 4200",
"comuna": "Viña del Mar",
"indicaciones": "Depto 1402, Torre B"
}
Respuestas:

201 Created: Dirección de despacho indexada y vinculada correctamente al perfil del cliente.

400 Bad Request: Faltan campos obligatorios para la gestión logística de despachos (dirección o comuna vacías).

404 Not Found: El usuarioIdpath especificado en el cuerpo de la petición no existe.

Desactivar Cuenta de Usuario (Baja Lógica)
Método: DELETE
URL de Consulta: http://localhost:8083/api/usuarios/desactivar/5
Headers: None
Respuestas:

200 OK: La cuenta ha sido dada de baja de manera exitosa en el sistema, modificando su estado a inactivo para cumplir con las políticas de privacidad y mantener la integridad referencial.

404 Not Found: El identificador proporcionado no corresponde a ningún usuario activo.



MICROSERVICIO: category-service (Puerto 8084)
Persistencia: Base de datos gamehub_categories

Registrar Nueva Categoría
Método: POST
URL de Consulta: http://localhost:8084/api/categorias/crear
Headers: Content-Type: application/json
Request Body:
{
"nombre": "Tarjetas Gráficas",
"codigoRef": "GPU"
}
Respuestas:

201 Created: Categoría persistida de forma exitosa en el esquema independiente de la base de datos.

400 Bad Request: Error de validación debido a nombre duplicado o código de referencia fuera del límite de caracteres establecido por Bean Validation.

Listar Todas las Categorías
Método: GET
URL de Consulta: http://localhost:8084/api/categorias/listar
Headers: None
Respuestas:

200 OK: Operación exitosa. Devuelve un arreglo dinámico en formato JSON con todas las clasificaciones registradas en el sistema.

Obtener Categoría por Identificador Único
Método: GET
URL de Consulta: http://localhost:8084/api/categorias/buscar/1
Headers: None
Respuestas:

200 OK: Registro localizado. Retorna el objeto JSON con el nombre, código de referencia y estado de la categoría solicitada.

404 Not Found: El identificador ingresado en la ruta no corresponde a ninguna categoría existente.

Actualizar Categoría Existente
Método: PUT
URL de Consulta: http://localhost:8084/api/categorias/actualizar/1
Headers: Content-Type: application/json
Request Body:
{
"nombre": "Tarjetas de Video y Gráficas",
"codigoRef": "GPU-NEW"
}
Respuestas:

200 OK: Modificación procesada correctamente en la capa de persistencia. Retorna la entidad actualizada.

400 Bad Request: El cuerpo de la petición contiene datos inválidos o mal formateados.

404 Not Found: No se encontró la categoría asociada al ID especificado en la URL.

Eliminar Categoría (Baja Lógica)
Método: DELETE
URL de Consulta: http://localhost:8084/api/categorias/eliminar/1
Headers: None
Respuestas:

200 OK: La categoría ha sido desactivada con éxito del sistema para no afectar los productos históricos asociados.

404 Not Found: El identificador proporcionado no coincide con ningún registro activo en la base de datos.


MICROSERVICIO: inventory-service (Puerto 8085)
Persistencia: Base de datos gamehub_inventory

Consultar Stock de un Producto Específico
Método: GET
URL de Consulta: http://localhost:8085/api/inventario/producto/101
Headers: None
Respuestas:

200 OK: Operación exitosa. Retorna un objeto JSON con el identificador del producto y la cantidad de unidades disponibles en tiempo real.

404 Not Found: El identificador del producto ingresado no registra existencias ni movimientos en el inventario.

Cargar o Incrementar Stock (Abastecimiento)
Método: POST
URL de Consulta: http://localhost:8085/api/inventario/abastecer
Headers: Content-Type: application/json
Request Body:
{
"productoId": 101,
"cantidad": 50
}
Respuestas:

200 OK: Stock actualizado correctamente en la capa de persistencia. Retorna el nuevo balance total de unidades.

400 Bad Request: La cantidad ingresada es menor o igual a cero, violando las reglas de validación del negocio.

Reservar y Descontar Stock (Consumo Interno / OpenFeign)
Método: PUT
URL de Consulta: http://localhost:8085/api/inventario/descontar
Headers: Content-Type: application/json
Request Body:
{
"productoId": 101,
"cantidad": 2
}
Respuestas:

200 OK: Descuento procesado de forma correcta. Retorna el balance de stock remanente en bodega para ese producto.

400 Bad Request: Quiebre de stock físico debido a cantidad insuficiente para cubrir la solicitud de compra.

404 Not Found: El producto especificado no existe en los registros de inventario.

Listar Todo el Inventario de Bodega
Método: GET
URL de Consulta: http://localhost:8085/api/inventario/listar
Headers: None
Respuestas:

200 OK: Retorna un arreglo dinámico en formato JSON con el estado de existencias de todos los productos del ecosistema.

Registrar Alerta de Stock Mínimo
Método: PUT
URL de Consulta: http://localhost:8085/api/inventario/alerta/configurar
Headers: Content-Type: application/json
Request Body:
{
"productoId": 101,
"stockMinimo": 5
}
Respuestas:

200 OK: Umbral de alerta configurado con éxito. El sistema notificará de forma interna si las existencias bajan del límite establecido.

404 Not Found: El identificador del producto no coincide con ningún registro vigente.




MICROSERVICIO: order-service (Puerto 8086)
Persistencia: Base de datos gamehub_orders

Procesar y Crear Nueva Orden
Método: POST
URL de Consulta: http://localhost:8086/api/ordenes/procesar
Headers: Content-Type: application/json
Request Body:
{
"usuarioId": 5,
"items": [
{
"productoId": 101,
"cantidad": 1
}
]
}
Respuestas:

201 Created: Orden generada, registrada en la base de datos y reservada de forma exitosa en estado PENDIENTE_PAGO.

400 Bad Request: La petición fue rechazada debido a que la comunicación síncrona por OpenFeign con el microservicio de inventario reportó que no quedan existencias físicas del producto.

Consultar Orden por Identificador Único
Método: GET
URL de Consulta: http://localhost:8086/api/ordenes/buscar/4552
Headers: None
Respuestas:

200 OK: Registro localizado con éxito. Devuelve el objeto JSON con el detalle de los productos, montos totales y el estado actual del pedido.

404 Not Found: El identificador de orden ingresado en la ruta no coincide con ningún registro en el esquema gamehub_orders.

Listar Historial de Órdenes de un Usuario
Método: GET
URL de Consulta: http://localhost:8086/api/ordenes/usuario/5
Headers: None
Respuestas:

200 OK: Operación exitosa. Retorna un arreglo dinámico en formato JSON con todas las órdenes de compra asociadas al perfil del cliente.

Actualizar Estado de la Orden (Uso Inter-Servicio / OpenFeign)
Método: PUT
URL de Consulta: http://localhost:8086/api/ordenes/actualizar-estado/4552
Headers: Content-Type: application/json
Request Body:
{
"nuevoEstado": "PAGADA"
}
Respuestas:

200 OK: Transición de estado procesada correctamente en la capa de persistencia (ej. de PENDIENTE_PAGO a PAGADA o DESPACHADA).

400 Bad Request: El estado proporcionado no corresponde a los estados válidos definidos en las reglas del negocio.

404 Not Found: El número de orden especificado en la URL no existe.

Cancelar Orden de Compra
Método: DELETE
URL de Consulta: http://localhost:8086/api/ordenes/cancelar/4552
Headers: None
Respuestas:

200 OK: Orden cancelada de manera exitosa. El sistema libera los productos reservados devolviendo de forma interna el stock al microservicio de inventario.

404 Not Found: No se encontró ninguna orden vigente asociada al ID suministrado para ejecutar la cancelación.





MICROSERVICIO: payment-service (Puerto 8087)
Persistencia: Base de datos gamehub_payments

Procesar y Registrar Nueva Transacción
Método: POST
URL de Consulta: http://localhost:8087/api/pagos/procesar
Headers: Content-Type: application/json
Request Body:
{
"ordenId": 4552,
"monto": 1950000,
"metodoPago": "TRANSACCION_DEBITO"
}
Respuestas:

200 OK: Transacción aprobada con éxito. El servicio invoca de forma remota y síncrona a order-service vía OpenFeign para mutar el estado del pedido a PAGADA.

402 Payment Required: Error en la simulación bancaria debido a fondos insuficientes en la cuenta o rechazo de los parámetros de la tarjeta.

400 Bad Request: Error de Bean Validation por montos inferiores o iguales a cero o ausencia del identificador de la orden.

Consultar Detalle de un Pago Realizado
Método: GET
URL de Consulta: http://localhost:8087/api/pagos/buscar/8812
Headers: None
Respuestas:

200 OK: Registro de pago localizado. Retorna el objeto JSON con el número de transacción bancaria, monto, método de pago, fecha exacta de la operación y el estado de la transacción.

404 Not Found: El identificador del pago ingresado en la ruta no figura en los esquemas de la base de datos gamehub_payments.

Listar Historial de Pagos de una Orden
Método: GET
URL de Consulta: http://localhost:8087/api/pagos/orden/4552
Headers: None
Respuestas:

200 OK: Operación exitosa. Devuelve un arreglo dinámico en formato JSON con todos los intentos de pago asociados a la orden especificada, permitiendo verificar la trazabilidad de rechazos previos.

Simular Reversa o Reembolso de Pago
Método: PUT
URL de Consulta: http://localhost:8087/api/pagos/reembolsar/8812
Headers: None
Respuestas:

200 OK: Reembolso visado y ejecutado correctamente. Modifica el estado del pago a REEMBOLSADO y notifica internamente al ecosistema para la liberación del pedido.

404 Not Found: El identificador del pago suministrado no corresponde a ninguna transacción registrada.

Conciliar Estado de Pago Externo
Método: GET
URL de Consulta: http://localhost:8087/api/pagos/conciliar/8812
Headers: None
Respuestas:

200 OK: Sincronización finalizada. Verifica el estado de la transacción contra la pasarela simulada para asegurar que los datos locales coincidan con el estado bancario real.

404 Not Found: No se registran movimientos para la transacción solicitada.



MICROSERVICIO: shipping-service (Puerto 8088)
Persistencia: Base de datos gamehub_shipping

Generar Orden y Guía de Despacho
Método: POST
URL de Consulta: http://localhost:8088/api/despachos/generar
Headers: Content-Type: application/json
Request Body:
{
"ordenId": 4552,
"direccion": "Av. Concón Reñaca 4200, Viña del Mar",
"comuna": "Viña del Mar"
}
Respuestas:

201 Created: Despacho programado con éxito en el sistema logístico. Retorna el objeto JSON con el código de seguimiento (tracking number) único y la fecha estimada de entrega.

400 Bad Request: Error de validación de datos debido a dirección en blanco o comuna no cubierta por la cobertura de distribución de la tienda.

Consultar Estado de Seguimiento (Tracking)
Método: GET
URL de Consulta: http://localhost:8088/api/despachos/tracking/GH-998231-CL
Headers: None
Respuestas:

200 OK: Registro localizado. Devuelve el historial de estados del envío (ej. EN_PREPARACION, EN_TRANSITO, ENTREGADO) junto a la información del transportista asignado.

404 Not Found: El código de seguimiento ingresado no coincide con ninguna guía de despacho registrada en la base de datos gamehub_shipping.

Actualizar Estado de Avance del Envío (Uso del Transportista)
Método: PUT
URL de Consulta: http://localhost:8088/api/despachos/actualizar/GH-998231-CL
Headers: Content-Type: application/json
Request Body:
{
"nuevoEstadoLoger": "EN_TRANSITO",
"comentario": "El transportista va en camino a la comuna de destino."
}
Respuestas:

200 OK: Transición de estado logístico actualizada correctamente en la capa de persistencia.

400 Bad Request: Formato del estado inválido o inconsistente con el flujo lógico del envío.

404 Not Found: No se encontró el código de tracking especificado en la ruta.

Consultar Despacho por Número de Orden de Compra
Método: GET
URL de Consulta: http://localhost:8088/api/despachos/orden/4552
Headers: None
Respuestas:

200 OK: Operación exitosa. Retorna los datos y coordenadas del despacho asociados a la compra interna para asegurar la trazabilidad del cliente.

404 Not Found: La orden ingresada no cuenta con un proceso de despacho asignado aún.

Cancelar o Reprogramar Despacho antes de Salida
Método: DELETE
URL de Consulta: http://localhost:8088/api/despachos/cancelar/GH-998231-CL
Headers: None
Respuestas:

200 OK: El envío ha sido cancelado y sacado del flujo de despacho de forma exitosa antes de su distribución física.

404 Not Found: El identificador proporcionado no corresponde a ninguna guía activa que pueda ser removida de la hoja de ruta.





MICROSERVICIO: promotion-service (Puerto 8089)
Persistencia: Base de datos gamehub_promotions

Evaluar y Aplicar Cupón de Descuento
Método: PUT
URL de Consulta: http://localhost:8089/api/promociones/evaluar
Headers: Content-Type: application/json
Request Body:
{
"codigoCupon": "FPSBOOST20",
"montoCarrito": 150000
}
Respuestas:

200 OK: Cupón válido y aplicable. Retorna un objeto JSON con el porcentaje de rebaja, el monto descontado y el total recalculado para el carrito de compras.

400 Bad Request: El cupón ha expirado temporalmente o el monto total del carrito actual no alcanza el valor mínimo requerido para activar el beneficio.

404 Not Found: El código de cupón ingresado no existe en los registros de la base de datos gamehub_promotions.

Crear Nuevo Cupón de Campaña (Exclusivo Administrador)
Método: POST
URL de Consulta: http://localhost:8089/api/promociones/cupones/crear
Headers: Content-Type: application/json
Request Body:
{
"codigoCupon": "FPSBOOST20",
"porcentajeDescuento": 20,
"montoMinimoCompra": 100000,
"fechaExpiracion": "2026-12-31"
}
Respuestas:

201 Created: Cupón de descuento indexado con éxito en el sistema de ofertas y reglas de negocio.

400 Bad Request: Error de validación por porcentajes fuera del rango permitido (1-100) o fechas de expiración inconsistentes.

Listar Cupones y Promociones Vigentes
Método: GET
URL de Consulta: http://localhost:8089/api/promociones/cupones/vigentes
Headers: None
Respuestas:

200 OK: Operación exitosa. Devuelve un arreglo dinámico en formato JSON con todos los cupones activos que cumplen con los criterios temporales de vigencia.

Modificar Parámetros de una Promoción Existente
Método: PUT
URL de Consulta: http://localhost:8089/api/promociones/cupones/actualizar/12
Headers: Content-Type: application/json
Request Body:
{
"porcentajeDescuento": 25,
"montoMinimoCompra": 120000,
"fechaExpiracion": "2026-12-31"
}
Respuestas:

200 OK: Ajustes guardados correctamente en la capa de persistencia. Retorna la promoción modificada.

404 Not Found: El identificador numérico de la promoción especificado en la ruta no existe.

Desactivar Cupón o Campaña (Baja Lógica)
Método: DELETE
URL de Consulta: http://localhost:8089/api/promociones/cupones/desactivar/12
Headers: None
Respuestas:

200 OK: La campaña o cupón ha sido revocado de forma inmediata, quedando deshabilitado para futuras evaluaciones en el flujo de pagos.

404 Not Found: El ID proporcionado no coincide con ninguna promoción activa en el maestro de datos.


MICROSERVICIO: review-service (Puerto 8090)
Persistencia: Base de datos gamehub_reviews

Crear Reseña Validada de un Producto
Método: POST
URL de Consulta: http://localhost:8090/api/reseas/crear
Headers: Content-Type: application/json
Request Body:
{
"productoId": 101,
"usuarioId": 5,
"puntuacion": 5,
"comentario": "Excelente tarjeta gráfica, temperaturas bajísimas en overclocking."
}
Respuestas:

201 Created: Reseña almacenada e indexada de forma exitosa en el módulo de feedback.

400 Bad Request: Error en la validación del contrato de entrada debido a que la puntuación está fuera del rango permitido (1 a 5) o el texto del comentario se encuentra vacío.

403 Forbidden: La validación cruzada síncrona por OpenFeign bloquea la petición debido a que el servicio remoto de órdenes (order-service) indica que el usuario no registra la compra previa del artículo, impidiendo reseñas falsas.

Listar Reseñas de un Producto Específico
Método: GET
URL de Consulta: http://localhost:8090/api/reseas/producto/101
Headers: None
Respuestas:

200 OK: Operación exitosa. Retorna un arreglo dinámico en formato JSON con todas las valoraciones, comentarios y notas asignadas por los usuarios al producto consultado.

Obtener Promedio de Calificación de un Producto (Uso Interno)
Método: GET
URL de Consulta: http://localhost:8090/api/reseas/producto/101/promedio
Headers: None
Respuestas:

200 OK: Retorna un objeto JSON con el puntaje promedio ponderado del artículo y el número total de reseñas recibidas, ideal para inyectar este dato en la vista del catálogo maestro.

404 Not Found: El producto no registra valoraciones en la base de datos para calcular un promedio.

Modificar Comentario o Nota de una Reseña Propia
Método: PUT
URL de Consulta: http://localhost:8090/api/reseas/actualizar/742
Headers: Content-Type: application/json
Request Body:
{
"puntuacion": 4,
"comentario": "Actualizo mi reseña: después de dos meses de uso intensivo, rinde espectacular, aunque los ventiladores hacen un poco de ruido bajo carga máxima."
}
Respuestas:

200 OK: Edición procesada de forma correcta en la capa de persistencia. Retorna la entidad actualizada.

400 Bad Request: Argumentos de entrada inválidos o que violan los límites de caracteres permitidos.

404 Not Found: El identificador de la reseña especificado en la ruta no existe.

Eliminar o Moderar Reseña (Baja Física o Lógica)
Método: DELETE
URL de Consulta: http://localhost:8090/api/reseas/eliminar/742
Headers: None
Respuestas:

200 OK: Reseña removida del sistema con éxito, actualizando los contadores y promedios asociados de forma automática.

404 Not Found: El identificador provisto no coincide con ningún registro activo en el esquema gamehub_reviews.

