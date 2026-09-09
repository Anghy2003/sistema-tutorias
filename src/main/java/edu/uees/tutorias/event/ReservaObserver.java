package edu.uees.tutorias.event;

import edu.uees.tutorias.domain.Reserva;

/**
 * Observer: contrato de todo componente que deba reaccionar cuando algo ocurre
 * con una reserva.
 *
 * La reserva solo conoce esta interfaz. Quien envia correos, quien actualiza el
 * calendario y quien registra la bitacora son detalles que el dominio ignora.
 */
public interface ReservaObserver {

    void alOcurrir(EventoReserva evento, Reserva reserva);
}
