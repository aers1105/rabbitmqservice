
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import service.ServicioRabbitMQ;

public class main {

    public static void main(String[] args) {
        // Configura tus parámetros según tu entorno
        String host = "localhost";
        int port = 5672;
        String username = "guest";
        String password = "guest";

        ServicioRabbitMQ servicio = new ServicioRabbitMQ(host, port, username, password);

        try {
            // Prueba el método producer
            JsonObject accionProducer1 = new JsonObject();
            accionProducer1.addProperty("data", "Mensaje de prueba 1");
            accionProducer1.addProperty("nombreCola", "miCola1");
            servicio.producer(accionProducer1);

            // Prueba el método producer
            JsonObject accionProducer2 = new JsonObject();
            accionProducer2.addProperty("data", "Mensaje de prueba 2");
            accionProducer2.addProperty("nombreCola", "miCola2");
            servicio.producer(accionProducer2);


            // Espera un momento para que el mensaje se procese
            Thread.sleep(2000);

            // Prueba el método consumer
            JsonObject accionConsumer = new JsonObject();
            accionConsumer.addProperty("nombreCola", "miCola2");
            JsonObject resultado = servicio.consumer(accionConsumer);

            // Imprime el resultado
            System.out.println("Resultados de la cola:");
            System.out.println(resultado);

            // Prueba los métodos createQueue y deleteQueue
            JsonObject accionCreateQueue = new JsonObject();
            accionCreateQueue.addProperty("nombreCola", "miCola3");
            servicio.createQueue(accionCreateQueue);

            JsonObject accionDeleteQueue = new JsonObject();
            accionDeleteQueue.addProperty("nombreCola", "miCola2");
            servicio.deleteQueue(accionDeleteQueue);

        } catch (IOException | TimeoutException | InterruptedException e) {
        }
    }
}
