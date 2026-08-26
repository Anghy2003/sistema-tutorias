package edu.uees.tutorias.domain;

public abstract class Usuario {

    private final String id;
    private final String nombre;
    private final String correo;

    protected Usuario(String id, String nombre, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
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
