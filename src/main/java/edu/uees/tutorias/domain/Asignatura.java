package edu.uees.tutorias.domain;

public class Asignatura {

    private final String id;
    private final String nombre;

    public Asignatura(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}
