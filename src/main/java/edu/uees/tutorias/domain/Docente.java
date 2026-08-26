package edu.uees.tutorias.domain;

import java.util.ArrayList;
import java.util.List;

public class Docente {

    private final String id;
    private final String nombre;
    private final String correo;
    private final List<HorarioTutoria> horarios;

    public Docente(String id, String nombre, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
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

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }
}
