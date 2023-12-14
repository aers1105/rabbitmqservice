# rabbitmqservice
Servicio de mensajería para Arquitectura de Software

------------------------------------------------------------------------------------------------
* Se requiere instalar RabbitMQ con Docker
  - Instalador de docker: https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe?utm_source=docker&utm_medium=webreferral&utm_campaign=dd-smartbutton&utm_location=module&_gl=1*185yy0w*_ga*NzE0NTk5MTkyLjE3MDA3MTIxNDc.*_ga_XJWPQMJYHQ*MTcwMjU3NjkwNC4zLjEuMTcwMjU3NjkwNi41OC4wLjA.
  - En cmd: docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.12-management
------------------------------------------------------------------------------------------------
Funcionalidades:
+ Crea colas
+ Elimina colas
+ Agrega mensajes a las colas
+ Lee mensajes
