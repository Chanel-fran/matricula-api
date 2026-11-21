package pe.edu.cibertec.matricula_api.screenplay.tasks;

import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

public class ConsultarHistorial implements Task {

    private final String codigoAlumno;

    public ConsultarHistorial(String codigoAlumno) {
        this.codigoAlumno = codigoAlumno;
    }

    public static ConsultarHistorial delAlumno(String codigoAlumno) {
        return Tasks.instrumented(ConsultarHistorial.class, codigoAlumno);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String baseUrl = CallAnApi.as(actor).resolve("");
        String accessToken = actor.recall("accessToken");

        Response response = SerenityRest.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/alumnos/" + codigoAlumno + "/historial");

        actor.remember("ultimaRespuesta", response);
    }
}