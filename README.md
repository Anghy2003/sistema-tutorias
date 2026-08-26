# Sistema de Gestión de Tutorías

Proyecto de la Actividad 5 (Ae1) de Diseño de Software (UCOM0310), Universidad Espíritu Santo. Implementa el modelo orientado a objetos de un sistema de gestión de tutorías académicas en Java con Maven.

Repositorio: https://github.com/Anghy2003/sistema-tutorias

## Descripción del problema

Los estudiantes necesitan reservar tutorías con los docentes de sus asignaturas. Los docentes publican horarios disponibles, el estudiante solicita una reserva sobre uno de esos horarios y la reserva pasa por distintos estados (pendiente, confirmada, cancelada o realizada). El sistema debe validar la disponibilidad, notificar los eventos importantes al estudiante y guardar la información sin que la lógica del dominio dependa de una tecnología concreta.

## Clases principales y responsabilidades

| Clase / Interfaz | Responsabilidad |
|---|---|
| `Usuario` (abstracta) | Generaliza la identidad común de estudiantes y docentes (id, nombre, correo). |
| `Estudiante` | Representa al estudiante y mantiene sus reservas realizadas. |
| `Docente` | Publica horarios de tutoría y gestiona su disponibilidad. |
| `HorarioTutoria` | Representa un bloque de tutoría de un docente y controla si está disponible u ocupado. |
| `Asignatura` | Identifica la materia sobre la que se da la tutoría. |
| `Reserva` | Registra el encuentro, protege las reglas de transición entre estados y permite reprogramar hacia otro horario disponible. |
| `EstadoReserva` | Enumera los estados válidos de una reserva. |
| `ServicioReservas` | Coordina la creación, confirmación y cancelación de reservas usando el repositorio y el notificador. |
| `RepositorioReservas` (interfaz) | Define cómo se guarda y se busca una reserva sin fijar la tecnología. |
| `RepositorioReservasMemoria` | Implementación en memoria del repositorio para esta etapa del proyecto. |
| `Notificador` (interfaz) | Define el contrato para comunicar eventos al usuario. |
| `NotificadorCorreo` | Implementación que simula el envío de correos por consola. |

## Decisiones de diseño

- La herencia se usó solo donde existe una relación "es un" real: `Estudiante` y `Docente` son tipos de `Usuario` y comparten identidad y correo de contacto; la superclase es abstracta porque un usuario genérico no existe en el dominio.
- Para el resto de relaciones se usó composición: una `Reserva` se compone de un `Estudiante`, un `HorarioTutoria` y una `Asignatura`, porque la relación real es "tiene un", no "es un".
- Las reglas de estado viven dentro de `Reserva` (`confirmar`, `cancelar`, `marcarRealizada`, `reprogramar`): el atributo `estado` no tiene setter público, así ningún otro componente puede saltarse las validaciones. Reprogramar libera el horario anterior, ocupa el nuevo y devuelve la reserva a pendiente.
- `HorarioTutoria` encapsula su disponibilidad con `reservar()` y `liberar()`, de modo que no se puede ocupar dos veces el mismo horario.
- `ServicioReservas` concentra la coordinación del caso de uso (validar disponibilidad, crear la reserva, guardar y notificar) para que las clases del dominio no dependan de infraestructura.

## Principios SOLID aplicados

- **SRP (Responsabilidad Única):** `ServicioReservas` solo coordina el caso de uso; las reglas de estado están en `Reserva`, la persistencia en el repositorio y la comunicación en el notificador. Un cambio en la forma de notificar no obliga a tocar el dominio.
- **DIP (Inversión de Dependencias):** `ServicioReservas` depende de las interfaces `RepositorioReservas` y `Notificador`, que recibe por constructor, y no de implementaciones concretas. Cambiar la persistencia en memoria por una base de datos, o el correo por otro canal, solo requiere una nueva implementación de la interfaz.
- **OCP (Abierto/Cerrado):** como consecuencia de lo anterior, se pueden agregar nuevos tipos de notificación o de persistencia sin modificar el código existente del servicio.

## Diagrama UML

El diagrama fuente está en [docs/modelo-clases.puml](docs/modelo-clases.puml) y la imagen exportada en [docs/modelo-clases.png](docs/modelo-clases.png).

```mermaid
classDiagram
    class Usuario {
        <<abstract>>
        -String id
        -String nombre
        -String correo
    }
    class Estudiante {
        +agregarReserva(Reserva) void
        +consultarTutorias() List~Reserva~
    }
    class Docente {
        +publicarHorario(HorarioTutoria) void
        +gestionarDisponibilidad(String, boolean) void
        +consultarHorariosDisponibles() List~HorarioTutoria~
    }
    class HorarioTutoria {
        -String id
        -LocalDate fecha
        -LocalTime horaInicio
        -LocalTime horaFin
        -boolean disponible
        +verificarDisponibilidad() boolean
        +reservar() void
        +liberar() void
    }
    class Asignatura {
        -String id
        -String nombre
    }
    class Reserva {
        -String id
        -EstadoReserva estado
        +confirmar() void
        +cancelar() void
        +marcarRealizada() void
        +reprogramar(HorarioTutoria) void
    }
    class EstadoReserva {
        <<enumeration>>
        PENDIENTE
        CONFIRMADA
        CANCELADA
        REALIZADA
    }
    class ServicioReservas {
        +crearReserva(Estudiante, HorarioTutoria, Asignatura) Reserva
        +confirmarReserva(String) void
        +cancelarReserva(String) void
    }
    class Notificador {
        <<interface>>
        +enviarNotificacion(String, String) void
    }
    class NotificadorCorreo {
        +enviarNotificacion(String, String) void
    }
    class RepositorioReservas {
        <<interface>>
        +guardar(Reserva) void
        +buscar(String) Reserva
        +actualizar(Reserva) void
    }
    class RepositorioReservasMemoria {
        +guardar(Reserva) void
        +buscar(String) Reserva
        +actualizar(Reserva) void
    }
    Usuario <|-- Estudiante
    Usuario <|-- Docente
    Estudiante "1" -- "0..*" Reserva : realiza
    Docente "1" -- "0..*" HorarioTutoria : publica
    Reserva "0..1" -- "1" HorarioTutoria : se asigna a
    Reserva "0..*" -- "1" Asignatura : corresponde a
    Reserva ..> EstadoReserva
    ServicioReservas ..> RepositorioReservas
    ServicioReservas ..> Notificador
    NotificadorCorreo ..|> Notificador
    RepositorioReservasMemoria ..|> RepositorioReservas
```

## Estructura del proyecto

```
sistema-tutorias/
├── README.md
├── pom.xml
├── docs/
│   ├── modelo-clases.puml
│   └── modelo-clases.png
└── src/
    ├── main/java/edu/uees/tutorias/
    │   ├── App.java
    │   ├── domain/
    │   ├── service/
    │   ├── repository/
    │   └── notification/
    └── test/java/edu/uees/tutorias/
```

## Requisitos

- Java 21 o superior
- Maven 3.9 o superior

## Compilación y ejecución

```
mvn clean compile
mvn clean test
```

El sistema se usa desde un menú de consola que permite ver horarios, solicitar una tutoría, confirmarla, cancelarla, reprogramarla, marcarla como realizada y consultar las tutorías del estudiante. Para iniciarlo en Windows:

```
ejecutar.bat
```

O de forma manual:

```
chcp 65001
mvn compile
java -Dstdout.encoding=UTF-8 -cp target/classes edu.uees.tutorias.App
```

## Declaración de uso de IA

Durante el desarrollo de esta actividad utilicé herramientas de inteligencia artificial como apoyo para organizar la estructura del proyecto y revisar las decisiones de diseño. Verifiqué y adapté los resultados obtenidos, y puedo explicar y justificar el código y las decisiones presentadas.
