package edu.uees.tutorias.cancelacion;

import edu.uees.tutorias.domain.Reserva;

/**
 * Strategy: regla que decide si una reserva todavia se puede cancelar.
 *
 * Es el punto de variacion del incremento 1. El proceso de cancelacion permanece
 * estable en ServicioReservas; lo que cambia es el plazo minimo de anticipacion.
 */
public interface PoliticaCancelacion {

    boolean puedeCancelar(Reserva reserva);

    /**
     * Texto que explica la regla al estudiante cuando la cancelacion se rechaza.
     */
    String descripcion();
}
