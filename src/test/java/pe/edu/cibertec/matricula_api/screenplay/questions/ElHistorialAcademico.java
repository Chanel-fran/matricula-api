package pe.edu.cibertec.matricula_api.screenplay.questions;

import io.restassured.response.Response;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class ElHistorialAcademico implements Question<Response> {

    public static ElHistorialAcademico obtenido() {
        return new ElHistorialAcademico();
    }

    @Override
    public Response answeredBy(Actor actor) {
        return actor.recall("ultimaRespuesta");
    }
}