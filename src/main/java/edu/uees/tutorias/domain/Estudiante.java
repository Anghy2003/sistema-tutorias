package edu.uees.tutorias.domain;

import java.util.ArrayList;
import java.util.List;

public class Estudiante {

    private final String id;
    private final String nombre;
    private final String correo;
    private final List<Reserva> reservas;

    public Estudiante(String id, String nombre, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.reservas = new ArrayList<>();
    }

    public void agregarReserva(Reserva reserva) {
        reservas.add(reserva);
    }

    public List<Reserva> consultarTutorias() {
        return new ArrayList<>(reservas);
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
