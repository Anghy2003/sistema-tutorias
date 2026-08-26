package edu.uees.tutorias.service;

import java.util.UUID;

import edu.uees.tutorias.domain.Asignatura;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.HorarioTutoria;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.Notificador;
import edu.uees.tutorias.repository.RepositorioReservas;

public class ServicioReservas {

    private final RepositorioReservas repositorio;
    private final Notificador notificador;

    public ServicioReservas(RepositorioReservas repositorio, Notificador notificador) {
        this.repositorio = repositorio;
        this.notificador = notificador;
    }

    public Reserva crearReserva(Estudiante estudiante, HorarioTutoria horario, Asignatura asignatura) {
        if (!horario.verificarDisponibilidad()) {
            throw new IllegalStateException("El horario seleccionado no está disponible");
        }
        Reserva reserva = new Reserva(UUID.randomUUID().toString(), estudiante, horario, asignatura);
        horario.reservar();
        estudiante.agregarReserva(reserva);
        repositorio.guardar(reserva);
        notificador.enviarNotificacion(estudiante.getCorreo(),
                "Se registró su reserva de " + asignatura.getNombre() + " para el " + horario.getFecha());
        return reserva;
    }

    public void confirmarReserva(String reservaId) {
        Reserva reserva = obtenerReserva(reservaId);
        reserva.confirmar();
        repositorio.actualizar(reserva);
        notificador.enviarNotificacion(reserva.getEstudiante().getCorreo(),
                "Su reserva de " + reserva.getAsignatura().getNombre() + " fue confirmada");
    }

    public void cancelarReserva(String reservaId) {
        Reserva reserva = obtenerReserva(reservaId);
        reserva.cancelar();
        reserva.getHorario().liberar();
        repositorio.actualizar(reserva);
        notificador.enviarNotificacion(reserva.getEstudiante().getCorreo(),
                "Su reserva de " + reserva.getAsignatura().getNombre() + " fue cancelada");
    }

    private Reserva obtenerReserva(String reservaId) {
        Reserva reserva = repositorio.buscar(reservaId);
        if (reserva == null) {
            throw new IllegalArgumentException("No existe una reserva con el id " + reservaId);
        }
        return reserva;
    }
}
