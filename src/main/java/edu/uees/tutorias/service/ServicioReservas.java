package edu.uees.tutorias.service;

import edu.uees.tutorias.cancelacion.CatalogoPoliticas;
import edu.uees.tutorias.cancelacion.PoliticaCancelacion;
import edu.uees.tutorias.domain.Asignatura;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.HorarioTutoria;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.domain.ReservaBuilder;
import edu.uees.tutorias.notification.Notificador;
import edu.uees.tutorias.repository.RepositorioReservas;

public class ServicioReservas {

    private final RepositorioReservas repositorio;
    private final Notificador notificador;
    private final CatalogoPoliticas politicas;

    public ServicioReservas(RepositorioReservas repositorio, Notificador notificador) {
        this(repositorio, notificador, new CatalogoPoliticas());
    }

    public ServicioReservas(RepositorioReservas repositorio, Notificador notificador,
                            CatalogoPoliticas politicas) {
        this.repositorio = repositorio;
        this.notificador = notificador;
        this.politicas = politicas;
    }

    /**
     * Crea una reserva presencial y de prioridad normal, que es el caso mas comun.
     */
    public Reserva crearReserva(Estudiante estudiante, HorarioTutoria horario, Asignatura asignatura) {
        return crearReserva(new ReservaBuilder()
                .estudiante(estudiante)
                .horario(horario)
                .asignatura(asignatura)
                .build());
    }

    /**
     * Registra una reserva ya construida con {@link ReservaBuilder}, lo que permite
     * indicar modalidad, prioridad y observaciones sin multiplicar sobrecargas.
     */
    public Reserva crearReserva(Reserva reserva) {
        HorarioTutoria horario = reserva.getHorario();
        if (!horario.verificarDisponibilidad()) {
            throw new IllegalStateException("El horario seleccionado no está disponible");
        }
        horario.reservar();
        reserva.getEstudiante().agregarReserva(reserva);
        repositorio.guardar(reserva);
        notificador.enviarNotificacion(reserva.getEstudiante().getCorreo(),
                "Se registró su reserva de " + reserva.getAsignatura().getNombre()
                        + " para el " + horario.getFecha());
        return reserva;
    }

    public void confirmarReserva(String reservaId) {
        Reserva reserva = obtenerReserva(reservaId);
        reserva.confirmar();
        repositorio.actualizar(reserva);
        notificador.enviarNotificacion(reserva.getEstudiante().getCorreo(),
                "Su reserva de " + reserva.getAsignatura().getNombre() + " fue confirmada");
    }

    /**
     * Cancela una reserva si la politica correspondiente a su prioridad lo permite.
     * El servicio no conoce ningun plazo: solo sabe a quien preguntar.
     */
    public void cancelarReserva(String reservaId) {
        Reserva reserva = obtenerReserva(reservaId);
        PoliticaCancelacion politica = politicas.politicaPara(reserva.getPrioridad());
        if (!politica.puedeCancelar(reserva)) {
            throw new IllegalStateException(
                    "No se puede cancelar: " + politica.descripcion());
        }
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
