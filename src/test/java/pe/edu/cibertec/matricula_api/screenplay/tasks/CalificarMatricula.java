package pe.edu.cibertec.matricula_api.screenplay.tasks;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

import java.util.Map;

public class CalificarMatricula implements Task {

    private final Long idMatricula;
    private final String notaTexto;

    public CalificarMatricula(Long idMatricula, String notaTexto) {
        this.idMatricula = idMatricula;
        this.notaTexto = notaTexto;
    }

    public static CalificarMatricula conNota(Long idMatricula, String notaTexto) {
        return Tasks.instrumented(CalificarMatricula.class, idMatricula, notaTexto);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String baseUrl = CallAnApi.as(actor).resolve("");
        String accessToken = actor.recall("accessToken");
        Object valorNota = parsearNota(notaTexto);

        Response response = SerenityRest.given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(Map.of("nota", valorNota))
                .when()
                .patch("/api/matriculas/" + idMatricula + "/nota");

        actor.remember("ultimaRespuesta", response);
    }

    private Object parsearNota(String texto) {
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            // Si viene con coma (ej. "12,5"), se envía como texto para ver cómo responde Jackson
            return texto;
        }
    }
}