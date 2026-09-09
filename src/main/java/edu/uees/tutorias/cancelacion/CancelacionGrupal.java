package edu.uees.tutorias.cancelacion;

import edu.uees.tutorias.domain.Reserva;

/**
 * ConcreteStrategy: tutoria grupal, exige 24 horas porque cancelar afecta la
 * agenda de varios estudiantes y el aula asignada.
 */
public class CancelacionGrupal implements PoliticaCancelacion {

    private static final long HORAS_MINIMAS = 24;

    @Override
    public boolean puedeCancelar(Reserva reserva) {
        return reserva.horasRestantes() >= HORAS_MINIMAS;
    }

    @Override
    public String descripcion() {
        return "una tutoría grupal se cancela con al menos " + HORAS_MINIMAS + " horas de anticipación";
    }
}
