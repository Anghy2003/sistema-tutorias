package edu.uees.tutorias.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public class HorarioTutoria {

    private final String id;
    private final Docente docente;
    private final LocalDate fecha;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;
    private boolean disponible;

    public HorarioTutoria(String id, Docente docente, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        if (!horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }
        this.id = id;
        this.docente = docente;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.disponible = true;
    }

    public boolean verificarDisponibilidad() {
        return disponible;
    }

    public void reservar() {
        if (!disponible) {
            throw new IllegalStateException("El horario ya se encuentra ocupado");
        }
        disponible = false;
    }

    public void liberar() {
        disponible = true;
    }

    public String getId() {
        return id;
    }

    public Docente getDocente() {
        return docente;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }
}
