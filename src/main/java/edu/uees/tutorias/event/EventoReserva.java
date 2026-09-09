package edu.uees.tutorias.event;

/**
 * Hechos del ciclo de vida de una reserva que el sistema publica.
 *
 * Tener el evento explicito evita que cada observador tenga que deducir que
 * ocurrio a partir del estado, y permite que reaccionen solo a lo que les
 * interesa.
 */
public enum EventoReserva {
    CREADA,
    CONFIRMADA,
    CANCELADA,
    REPROGRAMADA,
    REALIZADA
}
