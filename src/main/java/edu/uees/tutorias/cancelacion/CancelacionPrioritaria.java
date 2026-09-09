package edu.uees.tutorias.cancelacion;

import edu.uees.tutorias.domain.Reserva;

/**
 * ConcreteStrategy: tutoria prioritaria, admite cancelar hasta 1 hora antes.
 */
public class CancelacionPrioritaria implements PoliticaCancelacion {

    private static final long HORAS_MINIMAS = 1;

    @Override
    public boolean puedeCancelar(Reserva reserva) {
        return reserva.horasRestantes() >= HORAS_MINIMAS;
    }

    @Override
    public String descripcion() {
        return "una tutoría prioritaria se cancela con al menos " + HORAS_MINIMAS + " hora de anticipación";
    }
}
