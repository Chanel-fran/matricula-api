package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Y;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MatriculaStepDefinitions {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String PASSWORD_DEFAULT = "Cibertec2026";

    private static String bearerToken;
    private static Integer matriculaId;
    private static Response ultimaRespuesta;

    @Dado("que el alumno con codigo {string} inicia sesion en la API")
    public void iniciaSesionEnLaApi(String codigoAlumno) {
        OnStage.theActorCalled(codigoAlumno).whoCan(CallAnApi.at(BASE_URL));

        Response loginResponse = SerenityRest.given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "username", codigoAlumno,
                        "password", PASSWORD_DEFAULT
                ))
                .when()
                .post("/api/auth/login");

        loginResponse.then().statusCode(anyOf(is(200), is(201)));

        String accessToken = loginResponse.jsonPath().getString("accessToken");
        assertThat("No se recibió accessToken en la respuesta del login", accessToken, not(emptyOrNullString()));
        bearerToken = accessToken;
    }

    @Cuando("cotiza la matricula del alumno {string} para el curso {string}")
    public void cotizaLaMatricula(String codigoAlumno, String codigoCurso) {
        ultimaRespuesta = SerenityRest.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + bearerToken)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "codigoAlumno", codigoAlumno,
                        "cursos", List.of(codigoCurso)
                ))
                .when()
                .post("/api/matriculas/cotizacion");
    }

    @Y("^el monto total de la cotizacion es (\\d+(?:\\.\\d+)?)$")
    public void elMontoTotalDeLaCotizacionEs(String montoEsperado) {
        double esperado = Double.parseDouble(montoEsperado);
        Double total = ultimaRespuesta.jsonPath().getDouble("total");
        assertThat(total, closeTo(esperado, 0.01));
    }

    @Cuando("registra una matricula para el alumno {string} en el curso {string}")
    public void registraUnaMatricula(String codigoAlumno, String codigoCurso) {
        ultimaRespuesta = SerenityRest.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + bearerToken)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "codigoAlumno", codigoAlumno,
                        "codigoCurso", codigoCurso
                ))
                .when()
                .post("/api/matriculas");

        Integer id = ultimaRespuesta.jsonPath().getObject("id", Integer.class);
        if (id != null && id > 0) {
            matriculaId = id;
        }
    }

    @Y("el estado de la matricula es {string}")
    public void elEstadoDeLaMatriculaEs(String estadoEsperado) {
        ultimaRespuesta.then().body("estado", equalTo(estadoEsperado));
    }

    @Y("^el monto de la matricula coincide con la cotizacion de (\\d+(?:\\.\\d+)?)$")
    public void elMontoDeLaMatriculaCoincideConLaCotizacion(String montoEsperado) {
        double esperado = Double.parseDouble(montoEsperado);
        Double monto = ultimaRespuesta.jsonPath().getDouble("monto");
        assertThat(monto, closeTo(esperado, 0.01));
    }

    @Cuando("paga la matricula registrada con el metodo {string}")
    public void pagaLaMatriculaRegistradaConElMetodo(String metodoPago) {
        assertThat("No se capturó el id de matrícula previamente", matriculaId, notNullValue());

        ultimaRespuesta = SerenityRest.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + bearerToken)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "metodo", metodoPago,
                        "metodoPago", metodoPago
                ))
                .when()
                .patch("/api/matriculas/{id}/pagar", matriculaId);
    }

    @Y("el campo {string} tiene valor")
    public void elCampoTieneValor(String campo) {
        ultimaRespuesta.then().body(campo, allOf(notNullValue(), not(emptyString())));
    }

    @Y("en el listado de pagos existe el pago de la matricula con metodo {string} y numero de operacion con formato {string}")
    public void enElListadoDePagosExisteElPago(String metodoEsperado, String formato) {
        Response pagosResponse = SerenityRest.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + bearerToken)
                .when()
                .get("/api/pagos");

        pagosResponse.then().statusCode(200);

        List<Object> idsMatricula = pagosResponse.jsonPath().getList("idMatricula");
        if (idsMatricula == null || idsMatricula.isEmpty()) {
            idsMatricula = pagosResponse.jsonPath().getList("matriculaId");
        }
        List<String> metodos = pagosResponse.jsonPath().getList("metodo");
        if (metodos == null || metodos.isEmpty()) {
            metodos = pagosResponse.jsonPath().getList("metodoPago");
        }
        List<String> operaciones = pagosResponse.jsonPath().getList("numeroOperacion");

        boolean encontrado = false;
        if (idsMatricula != null && metodos != null && operaciones != null) {
            int limite = Math.min(idsMatricula.size(), Math.min(metodos.size(), operaciones.size()));
            for (int i = 0; i < limite; i++) {
                String idActual = String.valueOf(idsMatricula.get(i));
                String metodoActual = String.valueOf(metodos.get(i));
                String opActual = String.valueOf(operaciones.get(i));

                if (String.valueOf(matriculaId).equals(idActual)
                        && metodoEsperado.equals(metodoActual)
                        && opActual.matches("^OP-\\d{6}$")) {
                    encontrado = true;
                    break;
                }
            }
        }

        assertThat("No se encontró el pago con formato " + formato + " para la matrícula " + matriculaId,
                encontrado, is(true));
    }

    @Dado("que existe una matricula registrada y pagada para el alumno {string} en el curso {string}")
    public void existeMatriculaRegistradaYPagada(String codigoAlumno, String codigoCurso) {
        if (matriculaId == null) {
            registraUnaMatricula(codigoAlumno, codigoCurso);
            ultimaRespuesta.then().statusCode(201);
            pagaLaMatriculaRegistradaConElMetodo("TARJETA");
            ultimaRespuesta.then().statusCode(200);
        }
    }

    @Cuando("intenta matricular al alumno {string} en el curso {string}")
    public void intentaMatricularEnCurso(String codigoAlumno, String codigoCurso) {
        registraUnaMatricula(codigoAlumno, codigoCurso);
    }
}