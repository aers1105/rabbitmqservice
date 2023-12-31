package interfaz;

import com.google.gson.JsonObject;

public interface IServicioRabbitMQ {

    void producer(JsonObject accion) throws Exception;

    JsonObject consumer(JsonObject nombre) throws Exception;

    void createQueue(JsonObject nombre)throws Exception;

    void deleteQueue(JsonObject nombre)throws Exception;
}
