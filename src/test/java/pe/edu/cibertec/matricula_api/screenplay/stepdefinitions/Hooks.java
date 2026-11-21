package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.Before;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

import java.time.LocalDateTime;

public class Hooks {

    @Before
    public void beforeScenario() {
        OnStage.setTheStage(new OnlineCast());
        System.out.println("=== Alumno: Fran Chanel Calcina Vargas | Fecha y Hora: "
                + LocalDateTime.now() + " ===");
    }
}