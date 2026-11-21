package pe.edu.cibertec.matricula_api.screenplay.tasks;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

import java.util.Map;

public class RenovarToken implements Task {

    private final String claveMemoriaRefreshToken;

    public RenovarToken(String claveMemoriaRefreshToken) {
        this.claveMemoriaRefreshToken = claveMemoriaRefreshToken;
    }

    public static RenovarToken usandoMemoria(String claveMemoriaRefreshToken) {
        return Tasks.instrumented(RenovarToken.class, claveMemoriaRefreshToken);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String baseUrl = CallAnApi.as(actor).resolve("");
        String refreshToken = actor.recall(claveMemoriaRefreshToken);

        Response response = SerenityRest.given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .body(Map.of("refreshToken", refreshToken != null ? refreshToken : ""))
                .when()
                .post("/api/auth/refresh");

        actor.remember("ultimaRespuesta", response);

        if (response.statusCode() == 200) {
            String nuevoAccess = response.jsonPath().getString("accessToken");
            String nuevoRefresh = response.jsonPath().getString("refreshToken");
            if (nuevoAccess != null) {
                actor.remember("accessToken", nuevoAccess);
            }
            if (nuevoRefresh != null) {
                actor.remember("refreshTokenActual", nuevoRefresh);
            }
        }
    }
}