package edu.uees.tutorias.event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.uees.tutorias.domain.Reserva;

/**
 * ConcreteObserver: registra en memoria todo lo que ocurre con las reservas.
 *
 * Es el observador que hizo falta al implementar el patron: sin el, cada vez que
 * se queria auditar algo habia que volver a tocar el servicio.
 */
public class ObservadorBitacora implements ReservaObserver {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final List<String> registros = new ArrayList<>();

    @Override
    public void alOcurrir(EventoReserva evento, Reserva reserva) {
        registros.add(LocalDateTime.now().format(FORMATO) + " | " + evento
                + " | reserva " + reserva.getId().substring(0, Math.min(8, reserva.getId().length()))
                + " | " + reserva.getAsignatura().getNombre()
                + " | " + reserva.getEstudiante().getNombre());
    }

    public List<String> getRegistros() {
        return Collections.unmodifiableList(registros);
    }
}
