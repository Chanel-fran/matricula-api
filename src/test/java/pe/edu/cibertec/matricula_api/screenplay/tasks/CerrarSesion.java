package pe.edu.cibertec.matricula_api.screenplay.tasks;

import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

public class CerrarSesion implements Task {

    public static CerrarSesion ahora() {
        return Tasks.instrumented(CerrarSesion.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String baseUrl = CallAnApi.as(actor).resolve("");
        String accessToken = actor.recall("accessToken");

        Response response = SerenityRest.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .post("/api/auth/logout");

        actor.remember("ultimaRespuesta", response);
    }
}