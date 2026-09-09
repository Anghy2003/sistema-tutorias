package edu.uees.tutorias.event;

import edu.uees.tutorias.domain.Reserva;

/**
 * ConcreteObserver: mantiene el calendario institucional del docente.
 *
 * Solo reacciona a los eventos que afectan la agenda; los demas los ignora.
 */
public class ObservadorCalendario implements ReservaObserver {

    @Override
    public void alOcurrir(EventoReserva evento, Reserva reserva) {
        switch (evento) {
            case CONFIRMADA -> System.out.println("[Calendario] Sesión agendada el "
                    + reserva.getHorario().getFecha() + " a las " + reserva.getHorario().getHoraInicio()
                    + " con " + reserva.getHorario().getDocente().getNombre());
            case CANCELADA -> System.out.println("[Calendario] Sesión del "
                    + reserva.getHorario().getFecha() + " retirada de la agenda");
            case REPROGRAMADA -> System.out.println("[Calendario] Sesión movida al "
                    + reserva.getHorario().getFecha() + " a las " + reserva.getHorario().getHoraInicio());
            default -> {
                // CREADA y REALIZADA no modifican la agenda del docente.
            }
        }
    }
}
