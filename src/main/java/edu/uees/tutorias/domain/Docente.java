package edu.uees.tutorias.domain;

import java.util.ArrayList;
import java.util.List;

public class Docente extends Usuario {

    private final List<HorarioTutoria> horarios;

    public Docente(String id, String nombre, String correo) {
        super(id, nombre, correo);
        this.horarios = new ArrayList<>();
    }

    public void publicarHorario(HorarioTutoria horario) {
        horarios.add(horario);
    }

    public void gestionarDisponibilidad(String horarioId, boolean disponible) {
        for (HorarioTutoria horario : horarios) {
            if (horario.getId().equals(horarioId)) {
                if (disponible) {
                    horario.liberar();
                } else {
                    horario.reservar();
                }
                return;
            }
        }
        throw new IllegalArgumentException("El docente no tiene un horario con el id " + horarioId);
    }

    public List<HorarioTutoria> consultarHorariosDisponibles() {
        List<HorarioTutoria> disponibles = new ArrayList<>();
        for (HorarioTutoria horario : horarios) {
            if (horario.verificarDisponibilidad()) {
                disponibles.add(horario);
            }
        }
        return disponibles;
    }
}
