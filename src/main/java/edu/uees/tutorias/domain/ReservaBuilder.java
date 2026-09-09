package edu.uees.tutorias.domain;

import java.util.UUID;

/**
 * Builder de Reserva, conservado del trabajo de Ae2.
 *
 * En Ae1 la reserva tenia cuatro datos obligatorios y un constructor era
 * suficiente. En el incremento 1 aparecieron cuatro datos opcionales
 * (modalidad, prioridad, observacion y enlace de la sesion), de modo que el
 * constructor pasaria a tener ocho parametros posicionales. El Builder nombra
 * cada valor, aplica los valores por defecto en un solo lugar y valida antes de
 * construir.
 */
public class ReservaBuilder {

    private String id = UUID.randomUUID().toString();
    private Estudiante estudiante;
    private HorarioTutoria horario;
    private Asignatura asignatura;
    private Modalidad modalidad = Modalidad.PRESENCIAL;
    private Prioridad prioridad = Prioridad.NORMAL;
    private String observacion = "";

    public ReservaBuilder id(String id) {
        this.id = id;
        return this;
    }

    public ReservaBuilder estudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
        return this;
    }

    public ReservaBuilder horario(HorarioTutoria horario) {
        this.horario = horario;
        return this;
    }

    public ReservaBuilder asignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
        return this;
    }

    public ReservaBuilder modalidad(Modalidad modalidad) {
        this.modalidad = modalidad;
        return this;
    }

    public ReservaBuilder prioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
        return this;
    }

    public ReservaBuilder observacion(String observacion) {
        this.observacion = observacion;
        return this;
    }

    public Reserva build() {
        validar();
        return new Reserva(id, estudiante, horario, asignatura, modalidad, prioridad, observacion);
    }

    private void validar() {
        if (id == null || id.isBlank()) {
            throw new IllegalStateException("La reserva necesita un identificador");
        }
        if (estudiante == null) {
            throw new IllegalStateException("El estudiante es obligatorio");
        }
        if (horario == null) {
            throw new IllegalStateException("El horario es obligatorio");
        }
        if (asignatura == null) {
            throw new IllegalStateException("La asignatura es obligatoria");
        }
        if (modalidad == null || prioridad == null) {
            throw new IllegalStateException("La modalidad y la prioridad no pueden ser nulas");
        }
        if (observacion == null) {
            throw new IllegalStateException("La observacion no puede ser nula");
        }
    }
}
