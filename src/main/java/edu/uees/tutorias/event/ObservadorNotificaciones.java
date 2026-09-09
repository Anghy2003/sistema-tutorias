package edu.uees.tutorias.event;

import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.Notificador;

/**
 * ConcreteObserver: avisa al estudiante por el canal configurado.
 *
 * Antes del incremento, este texto se armaba dentro de ServicioReservas. Ahora
 * el servicio no sabe que existe una notificacion.
 */
public class ObservadorNotificaciones implements ReservaObserver {

    private final Notificador notificador;

    public ObservadorNotificaciones(Notificador notificador) {
        this.notificador = notificador;
    }

    @Override
    public void alOcurrir(EventoReserva evento, Reserva reserva) {
        notificador.enviarNotificacion(reserva.getEstudiante().getCorreo(), mensaje(evento, reserva));
    }

    private String mensaje(EventoReserva evento, Reserva reserva) {
        String asignatura = reserva.getAsignatura().getNombre();
        return switch (evento) {
            case CREADA -> "Se registró su reserva de " + asignatura
                    + " para el " + reserva.getHorario().getFecha()
                    + (reserva.esVirtual() ? ". Enlace: " + reserva.getEnlaceSesion() : "");
            case CONFIRMADA -> "Su reserva de " + asignatura + " fue confirmada";
            case CANCELADA -> "Su reserva de " + asignatura + " fue cancelada";
            case REPROGRAMADA -> "Su reserva de " + asignatura + " se reprogramó para el "
                    + reserva.getHorario().getFecha();
            case REALIZADA -> "Su tutoría de " + asignatura + " fue registrada como realizada";
        };
    }
}
