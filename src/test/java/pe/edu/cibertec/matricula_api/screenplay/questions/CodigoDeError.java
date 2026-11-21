package pe.edu.cibertec.matricula_api.screenplay.questions;

import io.restassured.response.Response;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class CodigoDeError implements Question<String> {

    public static CodigoDeError deLaUltimaRespuesta() {
        return new CodigoDeError();
    }

    @Override
    public String answeredBy(Actor actor) {
        Response response = actor.recall("ultimaRespuesta");
        if (response == null) {
            return null;
        }
        return response.jsonPath().getString("codigo");
    }
}