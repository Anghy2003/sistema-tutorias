package edu.uees.tutorias.domain;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Reserva de una tutoria.
 *
 * Incremento 1: ademas de su estado, la reserva conoce su modalidad, su
 * prioridad y, cuando es virtual, el enlace de la sala. El constructor completo
 * tiene visibilidad de paquete, de modo que solo {@link ReservaBuilder} puede
 * invocarlo.
 */
public class Reserva {

    private final String id;
    private final Estudiante estudiante;
    private HorarioTutoria horario;
    private final Asignatura asignatura;
    private final Modalidad modalidad;
    private final Prioridad prioridad;
    private final String observacion;
    private String enlaceSesion = "";
    private EstadoReserva estado;

    Reserva(String id, Estudiante estudiante, HorarioTutoria horario, Asignatura asignatura,
            Modalidad modalidad, Prioridad prioridad, String observacion) {
        this.id = id;
        this.estudiante = estudiante;
        this.horario = horario;
        this.asignatura = asignatura;
        this.modalidad = modalidad;
        this.prioridad = prioridad;
        this.observacion = observacion;
        this.estado = EstadoReserva.PENDIENTE;
    }

    public void confirmar() {
        if (estado != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException("Solo se puede confirmar una reserva pendiente");
        }
        estado = EstadoReserva.CONFIRMADA;
    }

    public void cancelar() {
        if (estado == EstadoReserva.REALIZADA || estado == EstadoReserva.CANCELADA) {
            throw new IllegalStateException("La reserva ya no se puede cancelar");
        }
        estado = EstadoReserva.CANCELADA;
    }

    public void marcarRealizada() {
        if (estado != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException("Solo una reserva confirmada se puede marcar como realizada");
        }
        estado = EstadoReserva.REALIZADA;
    }

    public void reprogramar(HorarioTutoria nuevoHorario) {
        if (estado == EstadoReserva.CANCELADA || estado == EstadoReserva.REALIZADA) {
            throw new IllegalStateException("Solo se puede reprogramar una reserva pendiente o confirmada");
        }
        if (!nuevoHorario.verificarDisponibilidad()) {
            throw new IllegalStateException("El nuevo horario no está disponible");
        }
        horario.liberar();
        nuevoHorario.reservar();
        horario = nuevoHorario;
        estado = EstadoReserva.PENDIENTE;
    }

    /**
     * Momento en que inicia la tutoria, combinando la fecha y la hora del horario.
     */
    public LocalDateTime inicio() {
        return LocalDateTime.of(horario.getFecha(), horario.getHoraInicio());
    }

    /**
     * Horas completas que faltan para el inicio de la tutoria. Es el dato que
     * consultan las politicas de cancelacion; la regla en si vive en la politica.
     */
    public long horasRestantes() {
        return Duration.between(LocalDateTime.now(), inicio()).toHours();
    }

    public void asignarEnlaceSesion(String enlaceSesion) {
        if (modalidad != Modalidad.VIRTUAL) {
            throw new IllegalStateException("Solo una tutoría virtual tiene enlace de sesión");
        }
        this.enlaceSesion = enlaceSesion;
    }

    public boolean esVirtual() {
        return modalidad == Modalidad.VIRTUAL;
    }

    public String getId() {
        return id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public HorarioTutoria getHorario() {
        return horario;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public Modalidad getModalidad() {
        return modalidad;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public String getObservacion() {
        return observacion;
    }

    public String getEnlaceSesion() {
        return enlaceSesion;
    }
}
