package edu.uees.tutorias.domain;

import java.util.ArrayList;
import java.util.List;

public class Estudiante extends Usuario {

    private final List<Reserva> reservas;

    public Estudiante(String id, String nombre, String correo) {
        super(id, nombre, correo);
        this.reservas = new ArrayList<>();
    }

    public void agregarReserva(Reserva reserva) {
        reservas.add(reserva);
    }

    public List<Reserva> consultarTutorias() {
        return new ArrayList<>(reservas);
    }
}
