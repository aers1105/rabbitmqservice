package test;

import com.google.gson.JsonObject;
import interfaz.IServicioRabbitMQ;
import service.ServicioRabbitMQ;

public class testRabbit {

    public static void main(String[] args) {
        // Reemplaza "localhost" con la dirección de tu servidor RabbitMQ
        String host = "localhost";

        // Crear instancia del servicio
        IServicioRabbitMQ servicioRabbitMQ = new ServicioRabbitMQ(host);

        //Crear dos colas
        JsonObject cola1 = new JsonObject();
        cola1.addProperty("nombreCola", "AngelCola");
        servicioRabbitMQ.createQueue(cola1);

        JsonObject cola2 = new JsonObject();
        cola2.addProperty("nombreCola", "EliCola");
        servicioRabbitMQ.createQueue(cola2);

        //---------------------------------------------------------------
        //Probar enviar un mensaje desde AngelCola
        try {
            JsonObject mensaje = new JsonObject();
            mensaje.addProperty("data", "test data");
            mensaje.addProperty("nombreCola", "AngelCola");

            servicioRabbitMQ.producer(mensaje);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        //---------------------------------------------------------------

        //Probar consumidores
        try {

            JsonObject mensajeRecibido = servicioRabbitMQ.consumer(cola1);

            //AQUI PON TU LOGICA PARA DESESTRUCTURAR EL JSON

        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

}
