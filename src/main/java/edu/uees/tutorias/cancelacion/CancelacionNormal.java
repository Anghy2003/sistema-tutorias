package edu.uees.tutorias.cancelacion;

import edu.uees.tutorias.domain.Reserva;

/**
 * ConcreteStrategy: tutoria comun, se cancela con al menos 2 horas de anticipacion.
 */
public class CancelacionNormal implements PoliticaCancelacion {

    private static final long HORAS_MINIMAS = 2;

    @Override
    public boolean puedeCancelar(Reserva reserva) {
        return reserva.horasRestantes() >= HORAS_MINIMAS;
    }

    @Override
    public String descripcion() {
        return "Una tutoría normal se cancela con al menos " + HORAS_MINIMAS + " horas de anticipación";
    }
}
