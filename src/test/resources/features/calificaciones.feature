# language: es
Característica: Calificación de matrículas

  Esquema del escenario: El docente registra la nota y el historial se actualiza
    Dado que la alumna "i202610005" inicia sesion con clave "Cibertec2026"
    Y obtiene el id de su matricula del curso "PI5620"
    Cuando el docente "D0001" inicia sesion con clave "Cibertec2026"
    Y califica la matricula con nota "<nota>"
    Entonces el codigo de respuesta es 200
    Cuando consulta el historial academico de la alumna "i202610005"
    Entonces el codigo de respuesta es 200
    Y la respuesta cumple el esquema "historial.json"
    Y en el historial el curso "PI5620" tiene condicion "<condicion>"
    Y en el historial los creditos aprobados son <creditos> y el promedio ponderado es <promedio>

    Ejemplos:
      | nota | condicion   | creditos | promedio |
      | 19   | APROBADO    | 13       | 17.08    |
      | 12.5 | DESAPROBADO | 7        | 14.08    |

  Escenario: Un docente que no dicta el curso intenta registrar la nota
    Dado que la alumna "i202610005" inicia sesion con clave "Cibertec2026"
    Y obtiene el id de su matricula del curso "PI5620"
    Cuando el docente "D0002" inicia sesion con clave "Cibertec2026"
    Y califica la matricula con nota "18"
    Entonces el codigo de respuesta es 403
    Y el codigo de error es "ACCESO_DENEGADO"
    Y la respuesta cumple el esquema "error.json"

  Escenario: No se puede calificar una matrícula que no está pagada
    Dado que la alumna "i202610005" inicia sesion con clave "Cibertec2026"
    Y obtiene el id de su matricula del curso "PS5511"
    Cuando el docente "D0001" inicia sesion con clave "Cibertec2026"
    Y califica la matricula con nota "18"
    Entonces el codigo de respuesta es 409
    Y el codigo de error es "MATRICULA_NO_PAGADA"
    Y la respuesta cumple el esquema "error.json"