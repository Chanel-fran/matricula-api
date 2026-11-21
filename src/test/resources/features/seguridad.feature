# language: es
Característica: Seguridad de la sesión

  Escenario: Rotación del refresh token
    Dado que el actor "i202610001" inicia sesion con clave "Cibertec2026"
    Cuando renueva su token usando el refresh token actual
    Entonces el codigo de respuesta es 200
    Cuando intenta renovar su token usando el refresh token original
    Entonces el codigo de respuesta es 401
    Y el codigo de error es "REFRESH_REUTILIZADO"
    Y la respuesta cumple el esquema "error.json"
    Cuando consulta su perfil con el nuevo access token
    Entonces el codigo de respuesta es 401
    Y el codigo de error es "TOKEN_INVALIDO"
    Y la respuesta cumple el esquema "error.json"

  Escenario: Cierre de sesión
    Dado que el actor "i202610001" inicia sesion con clave "Cibertec2026"
    Cuando cierra sesion con su access token actual
    Entonces el codigo de respuesta es 204
    Cuando consulta su perfil con su access token actual
    Entonces el codigo de respuesta es 401
    Y el codigo de error es "TOKEN_INVALIDO"
    Y la respuesta cumple el esquema "error.json"

  Esquema del escenario: Bloqueo de cuenta por intentos fallidos
    Dado que el actor "i202610004" tiene la habilidad de consultar la API
    Cuando intenta iniciar sesion con usuario "i202610004" y clave "<clave>"
    Entonces el codigo de respuesta es <estado>
    Y el codigo de error es "<codigo>"
    Y la respuesta cumple el esquema "error.json"

    Ejemplos:
      | clave         | estado | codigo                |
      | ClaveErrada1  | 401    | CREDENCIALES_INVALIDAS |
      | ClaveErrada2  | 401    | CREDENCIALES_INVALIDAS |
      | ClaveErrada3  | 401    | CREDENCIALES_INVALIDAS |
      | ClaveErrada4  | 401    | CREDENCIALES_INVALIDAS |
      | ClaveErrada5  | 423    | CUENTA_BLOQUEADA       |
      | Cibertec2026  | 423    | CUENTA_BLOQUEADA       |

  Escenario: Alumno sin permiso de administrador
    Dado que el actor "i202610001" inicia sesion con clave "Cibertec2026"
    Cuando intenta listar todos los alumnos
    Entonces el codigo de respuesta es 403
    Y el codigo de error es "ACCESO_DENEGADO"
    Y la respuesta cumple el esquema "error.json"