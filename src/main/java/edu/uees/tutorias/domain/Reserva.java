package edu.uees.tutorias.domain;

public class Reserva {

    private final String id;
    private final Estudiante estudiante;
    private HorarioTutoria horario;
    private final Asignatura asignatura;
    private EstadoReserva estado;

    public Reserva(String id, Estudiante estudiante, HorarioTutoria horario, Asignatura asignatura) {
        this.id = id;
        this.estudiante = estudiante;
        this.horario = horario;
        this.asignatura = asignatura;
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
}
