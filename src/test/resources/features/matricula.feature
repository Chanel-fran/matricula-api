# language: es
Característica: Matrícula del alumno

  Antecedentes:
    Dado que el alumno con codigo "i202610001" inicia sesion en la API

  Escenario: Cotizar el curso
    Cuando cotiza la matricula del alumno "i202610001" para el curso "QA5517"
    Entonces el codigo de respuesta es 200
    Y el monto total de la cotizacion es 288.0

  Escenario: Matricularse y pagar
    Cuando registra una matricula para el alumno "i202610001" en el curso "QA5517"
    Entonces el codigo de respuesta es 201
    Y el estado de la matricula es "REGISTRADA"
    Y el monto de la matricula coincide con la cotizacion de 288.0
    Y la respuesta cumple el esquema "matricula.json"
    Cuando paga la matricula registrada con el metodo "TARJETA"
    Entonces el codigo de respuesta es 200
    Y el estado de la matricula es "PAGADA"
    Y el campo "fechaPago" tiene valor
    Y en el listado de pagos existe el pago de la matricula con metodo "TARJETA" y numero de operacion con formato "OP-000000"

  Escenario: Regla de negocio que debe fallar
    Dado que existe una matricula registrada y pagada para el alumno "i202610001" en el curso "QA5517"
    Cuando intenta matricular al alumno "i202610001" en el curso "PS5511"
    Entonces el codigo de respuesta es 409
    Y el codigo de error es "CRUCE_HORARIO"
    Y la respuesta cumple el esquema "error.json"