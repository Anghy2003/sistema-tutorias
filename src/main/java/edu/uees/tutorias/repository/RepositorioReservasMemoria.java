package edu.uees.tutorias.repository;

import java.util.HashMap;
import java.util.Map;

import edu.uees.tutorias.domain.Reserva;

public class RepositorioReservasMemoria implements RepositorioReservas {

    private final Map<String, Reserva> reservas = new HashMap<>();

    @Override
    public void guardar(Reserva reserva) {
        reservas.put(reserva.getId(), reserva);
    }

    @Override
    public Reserva buscar(String id) {
        return reservas.get(id);
    }

    @Override
    public void actualizar(Reserva reserva) {
        reservas.put(reserva.getId(), reserva);
    }
}
