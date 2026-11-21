package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import pe.edu.cibertec.matricula_api.screenplay.questions.ElHistorialAcademico;
import pe.edu.cibertec.matricula_api.screenplay.tasks.CalificarMatricula;
import pe.edu.cibertec.matricula_api.screenplay.tasks.ConsultarHistorial;
import pe.edu.cibertec.matricula_api.screenplay.tasks.IniciarSesion;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;

public class CalificacionStepDefinitions {

    private static final String BASE_URL = "http://localhost:8080";

    @Dado("que la alumna {string} inicia sesion con clave {string}")
    public void alumnaIniciaSesion(String codigoAlumna, String clave) {
        Actor alumna = OnStage.theActorCalled(codigoAlumna);
        alumna.whoCan(CallAnApi.at(BASE_URL));
        alumna.attemptsTo(IniciarSesion.conCredenciales(codigoAlumna, clave));
    }

    @Y("obtiene el id de su matricula del curso {string}")
    public void obtieneIdMatricula(String codigoCurso) {
        Actor alumna = OnStage.theActorInTheSpotlight();
        String accessToken = alumna.recall("accessToken");

        Response response = SerenityRest.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + accessToken)
                .queryParam("alumno", alumna.getName())
                .queryParam("curso", codigoCurso)
                .when()
                .get("/api/matriculas");

        Long idMatricula = response.jsonPath().getLong("[0].id");
        assertNotNull(idMatricula, "No se encontro matricula para el curso " + codigoCurso);
        alumna.remember("idMatriculaObjetivo", idMatricula);
    }

    @Cuando("el docente {string} inicia sesion con clave {string}")
    public void docenteIniciaSesion(String codigoDocente, String clave) {
        Actor alumna = OnStage.theActorInTheSpotlight();
        Long idMatricula = alumna.recall("idMatriculaObjetivo");

        Actor docente = OnStage.theActorCalled(codigoDocente);
        docente.whoCan(CallAnApi.at(BASE_URL));
        docente.remember("idMatriculaObjetivo", idMatricula);
        docente.attemptsTo(IniciarSesion.conCredenciales(codigoDocente, clave));
    }

    @Y("califica la matricula con nota {string}")
    public void calificaMatriculaConNota(String nota) {
        Actor docente = OnStage.theActorInTheSpotlight();
        Long idMatricula = docente.recall("idMatriculaObjetivo");
        docente.attemptsTo(CalificarMatricula.conNota(idMatricula, nota));
    }

    @Cuando("consulta el historial academico de la alumna {string}")
    public void consultaHistorialAlumna(String codigoAlumna) {
        Actor alumna = OnStage.theActorCalled(codigoAlumna);
        alumna.attemptsTo(ConsultarHistorial.delAlumno(codigoAlumna));
    }

    @Y("en el historial el curso {string} tiene condicion {string}")
    public void verificaCondicionCurso(String codigoCurso, String condicionEsperada) {
        Actor actor = OnStage.theActorInTheSpotlight();
        Response historial = actor.asksFor(ElHistorialAcademico.obtenido());
        String condicionReal = historial.jsonPath()
                .getString("cursos.find { it.codigoCurso == '" + codigoCurso + "' }.condicion");

        actor.should(seeThat("condicion del curso", a -> condicionReal, equalTo(condicionEsperada)));
    }

    @Y("en el historial los creditos aprobados son {int} y el promedio ponderado es {double}")
    public void verificaCreditosYPromedio(int creditosEsperados, double promedioEsperado) {
        Actor actor = OnStage.theActorInTheSpotlight();
        Response historial = actor.asksFor(ElHistorialAcademico.obtenido());
        int creditosReales = historial.jsonPath().getInt("creditosAprobados");
        double promedioReal = historial.jsonPath().getDouble("promedioPonderado");

        actor.should(
                seeThat("creditos aprobados", a -> creditosReales, equalTo(creditosEsperados)),
                seeThat("promedio ponderado", a -> promedioReal, closeTo(promedioEsperado, 0.01))
        );
    }
}