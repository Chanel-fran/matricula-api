package pe.edu.cibertec.matricula_api.screenplay.tasks;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

import java.util.Map;

public class IniciarSesion implements Task {

    private final String username;
    private final String password;

    public IniciarSesion(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static IniciarSesion conCredenciales(String username, String password) {
        return Tasks.instrumented(IniciarSesion.class, username, password);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String baseUrl = CallAnApi.as(actor).resolve("");

        Response response = SerenityRest.given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "username", username,
                        "password", password
                ))
                .when()
                .post("/api/auth/login");

        actor.remember("ultimaRespuesta", response);

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            String accessToken = response.jsonPath().getString("accessToken");
            String refreshToken = response.jsonPath().getString("refreshToken");

            if (accessToken != null) {
                actor.remember("accessToken", accessToken);
            }
            if (refreshToken != null) {
                if (!actor.recallAll().containsKey("refreshTokenOriginal")) {
                    actor.remember("refreshTokenOriginal", refreshToken);
                }
                actor.remember("refreshTokenActual", refreshToken);
            }
        }
    }
}