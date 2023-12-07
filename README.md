# rabbitmqservice
Servicio de mensajería para Arquitectura de Software

------------------------------------------------------------------------------------------------
* Se requiere instalar RabbitMQ con Docker
  - docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.12-management
------------------------------------------------------------------------------------------------

+ Crea colas
+ Elimina colas
+ Agrega mensajes a las colas
+ Lee mensajes
