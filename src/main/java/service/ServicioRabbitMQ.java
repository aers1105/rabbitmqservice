package service;

import com.google.gson.JsonObject;
import com.rabbitmq.client.*;
import interfaz.IServicioRabbitMQ;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ServicioRabbitMQ implements IServicioRabbitMQ {

    private static final List<String> listaMensajesRecibidos = new ArrayList<>();
    private final ConnectionFactory factory;

    /**
     * Creación de instancia del servicio.
     * @param host host en el cual se alojará RabbitMQ, recomendado localhost
     */
    public ServicioRabbitMQ(String host) {
        this.factory = new ConnectionFactory();
        this.factory.setHost(host);
    }

    /**
     * Envia un mensaje a una cola determinada
     *
     * @param accion JsonObject con (data, nombreCola).
     * @throws java.lang.Exception
     */
    @Override //✅ 
    public void producer(JsonObject accion) throws Exception {
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel();) {
            // Convierte los JsonObjects a objetos Java
            String contenidoMensaje = accion.get("data").getAsString();
            String nombreCola = accion.get("nombreCola").getAsString();

            // Declara la cola a la que se enviará el mensaje
            channel.queueDeclare(nombreCola, false, false, false, null);

            // Publica el mensaje en la cola
            channel.basicPublish("", nombreCola, null, contenidoMensaje.getBytes(StandardCharsets.UTF_8));

            System.out.println("Mensaje enviado a la cola '" + nombreCola + "': " + contenidoMensaje);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Obten la lista de mensajes de una cola en específico.
     *
     * @param nombre JsonObject con el nombre de la cola a leer.
     * @return JsonObject con la data.
     */
    @Override //✅.
    public JsonObject consumer(JsonObject nombre) {
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel();) {
            // Convierte el JsonObject a un objeto Java
            String nombreCola = nombre.get("nombreCola").getAsString();

            // Declara la cola desde la que se recibirán los mensajes
            channel.queueDeclare(nombreCola, false, false, false, null);

            // Crea un consumidor
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String mensajeRecibido = new String(delivery.getBody(), StandardCharsets.UTF_8);
                listaMensajesRecibidos.add(mensajeRecibido);
                System.out.println("Mensaje recibido: " + mensajeRecibido);
            };

            // Registra el consumidor en la cola
            channel.basicConsume(nombreCola, true, deliverCallback, consumerTag -> {
            });

            // Espera un tiempo para que el consumidor pueda procesar mensajes
            Thread.sleep(5000);

            // Imprime la lista de mensajes recibidos
            System.out.println("Lista de Mensajes Recibidos: " + listaMensajesRecibidos);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Crea una cola.
     * @param nombre JsonObject con (nombreCola).
     */
    @Override //✅
    public void createQueue(JsonObject nombre) {

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            // Convierte el JsonObject a un objeto Java
            String nombreCola = nombre.get("nombreCola").getAsString();

            // Declarar la cola con la configuración proporcionada en el JsonObject
            channel.queueDeclare(nombreCola, false, false, false, null);

            System.out.println("Cola '" + nombreCola + "' creada exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
