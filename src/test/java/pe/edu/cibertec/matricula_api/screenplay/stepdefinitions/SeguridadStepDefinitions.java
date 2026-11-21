package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import pe.edu.cibertec.matricula_api.screenplay.tasks.CerrarSesion;
import pe.edu.cibertec.matricula_api.screenplay.tasks.IniciarSesion;
import pe.edu.cibertec.matricula_api.screenplay.tasks.RenovarToken;

public class SeguridadStepDefinitions {

    private static final String BASE_URL = "http://localhost:8080";

    @Dado("que el actor {string} tiene la habilidad de consultar la API")
    public void actorConHabilidadApi(String nombreActor) {
        OnStage.theActorCalled(nombreActor).whoCan(CallAnApi.at(BASE_URL));
    }

    @Dado("que el actor {string} inicia sesion con clave {string}")
    public void actorIniciaSesion(String nombreActor, String clave) {
        Actor actor = OnStage.theActorCalled(nombreActor);
        actor.whoCan(CallAnApi.at(BASE_URL));
        actor.attemptsTo(IniciarSesion.conCredenciales(nombreActor, clave));
    }

    @Cuando("renueva su token usando el refresh token actual")
    public void renuevaConRefreshActual() {
        Actor actor = OnStage.theActorInTheSpotlight();
        actor.attemptsTo(RenovarToken.usandoMemoria("refreshTokenActual"));
    }

    @Cuando("intenta renovar su token usando el refresh token original")
    public void intentaRenovarConRefreshOriginal() {
        Actor actor = OnStage.theActorInTheSpotlight();
        actor.attemptsTo(RenovarToken.usandoMemoria("refreshTokenOriginal"));
    }

    @Cuando("consulta su perfil con el nuevo access token")
    @Cuando("consulta su perfil con su access token actual")
    public void consultaPerfilConToken() {
        Actor actor = OnStage.theActorInTheSpotlight();
        String accessToken = actor.recall("accessToken");

        Response response = SerenityRest.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/auth/perfil");

        actor.remember("ultimaRespuesta", response);
    }

    @Cuando("cierra sesion con su access token actual")
    public void cierraSesionConToken() {
        Actor actor = OnStage.theActorInTheSpotlight();
        actor.attemptsTo(CerrarSesion.ahora());
    }

    @Cuando("intenta iniciar sesion con usuario {string} y clave {string}")
    public void intentaIniciarSesion(String usuario, String clave) {
        Actor actor = OnStage.theActorInTheSpotlight();
        actor.attemptsTo(IniciarSesion.conCredenciales(usuario, clave));
    }

    @Cuando("intenta listar todos los alumnos")
    public void intentaListarAlumnos() {
        Actor actor = OnStage.theActorInTheSpotlight();
        String accessToken = actor.recall("accessToken");

        Response response = SerenityRest.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/alumnos");

        actor.remember("ultimaRespuesta", response);
    }
}