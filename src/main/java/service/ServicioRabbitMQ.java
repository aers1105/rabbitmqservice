package service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rabbitmq.client.*;
import interfaz.IServicioRabbitMQ;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServicioRabbitMQ implements IServicioRabbitMQ {

    private static final List<String> listaMensajesRecibidos = Collections.synchronizedList(new ArrayList<>());
    public ConnectionFactory factory;

    //Para manejar el log de una mejor manera.
    private static final Logger logger = LogManager.getLogger(ServicioRabbitMQ.class);

    // Usar CountDownLatch para esperar la recepción de mensajes
    private final CountDownLatch latch = new CountDownLatch(1);

    // Nuevas variables para la configuración
    private final String host;
    private final int port;
    private final String username;
    private final String password;

    /**
     * Creación de instancia del servicio.
     *
     * @param host host en el cual se alojará RabbitMQ, recomendado localhost
     * @param port
     * @param username
     * @param password
     */
    public ServicioRabbitMQ(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;

        this.factory = new ConnectionFactory();
        this.factory.setHost(host);
        this.factory.setPort(port);
        this.factory.setUsername(username);
        this.factory.setPassword(password);
    }

    /**
     * Envia un mensaje a una cola determinada
     *
     * @param accion JsonObject con (data, nombreCola).
     * @throws java.io.IOException
     * @throws java.util.concurrent.TimeoutException
     */
    @Override //✅ 
    public void producer(JsonObject accion) throws IOException, TimeoutException {
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel();) {
            // Convierte los JsonObjects a objetos Java
            String contenidoMensaje = accion.get("data").getAsString();
            String nombreCola = accion.get("nombreCola").getAsString();

            // Declara la cola a la que se enviará el mensaje
            channel.queueDeclare(nombreCola, false, false, false, null);

            // Publica el mensaje en la cola
            channel.basicPublish("", nombreCola, null, contenidoMensaje.getBytes(StandardCharsets.UTF_8));

            logger.info("Mensaje enviado a la cola '{}': {}", nombreCola, contenidoMensaje);

        } catch (Exception e) {
            logger.error("Error al enviar el mensaje a la cola", e);
        }
    }

    /**
     * Obten la lista de mensajes de una cola en específico.
     *
     * @param nombre JsonObject con el nombre de la cola a leer.
     * @return JsonObject con la data.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     * @throws java.util.concurrent.TimeoutException
     */
    @Override //✅.
    public JsonObject consumer(JsonObject nombre) throws IOException, InterruptedException, TimeoutException {
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel();) {
            // Convierte el JsonObject a un objeto Java
            String nombreCola = nombre.get("nombreCola").getAsString();

            // Declara la cola desde la que se recibirán los mensajes
            channel.queueDeclare(nombreCola, false, false, false, null);

            // Crea un consumidor
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String mensajeRecibido = new String(delivery.getBody(), StandardCharsets.UTF_8);
                listaMensajesRecibidos.add(mensajeRecibido);

                //System.out.println("Mensaje recibido: " + mensajeRecibido);
                logger.info("Mensaje recibido '{}' de la cola '{}'", mensajeRecibido, nombreCola);
            };

            // Registra el consumidor en la cola
            channel.basicConsume(nombreCola, true, deliverCallback, consumerTag -> {
            });

            // Espera un tiempo para que el consumidor pueda procesar mensajes
            Thread.sleep(2000);

            // Construye un JsonObject con la lista de mensajes recibidos
            JsonArray mensajesArray = new JsonArray();
            for (String mensaje : listaMensajesRecibidos) {
                mensajesArray.add(mensaje);
            }

            JsonObject resultado = new JsonObject();
            resultado.add("mensajes", mensajesArray);

            // Imprime la lista de mensajes recibidos
//            System.out.println("Lista de Mensajes Recibidos: " + listaMensajesRecibidos);
            logger.info("Lista de mensajes recibidos '{}'", listaMensajesRecibidos);

            return resultado;
        } catch (Exception e) {
            logger.error("Error al recibir el mensaje", e);
        }
        return null;
    }

    /**
     * Crea una cola.
     *
     * @param nombre JsonObject con (nombreCola).
     * @throws java.io.IOException
     * @throws java.util.concurrent.TimeoutException
     */
    @Override //✅
    public void createQueue(JsonObject nombre) throws IOException, TimeoutException {

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            // Convierte el JsonObject a un objeto Java
            String nombreCola = nombre.get("nombreCola").getAsString();

            // Declarar la cola con la configuración proporcionada en el JsonObject
            channel.queueDeclare(nombreCola, false, false, false, null);

            System.out.println("Cola '" + nombreCola + "' creada exitosamente.");
            logger.info("Cola creada correctamente '{}'", nombreCola);

        } catch (Exception e) {
            logger.error("Error al crear la cola", e);
        }

    }

    /**
     * Elimina una cola.
     *
     * @param nombre JsonObject con (nombreCola).
     * @throws java.io.IOException
     * @throws java.util.concurrent.TimeoutException
     */
    @Override //✅
    public void deleteQueue(JsonObject nombre) throws IOException, TimeoutException {
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            String nombreCola = nombre.get("nombreCola").getAsString();
            // Elimina la cola
            channel.queueDelete(nombreCola);

            logger.info("La cola '{}' ha sido eliminada correctamente.", nombreCola);

        } catch (Exception e) {
            logger.error("Error al eliminar la cola", e);
        }
    }
}
