package edu.uees.tutorias.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.uees.tutorias.domain.Asignatura;
import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.EstadoReserva;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.HorarioTutoria;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.repository.RepositorioReservasMemoria;

class ServicioReservasTest {

    private ServicioReservas servicio;
    private RepositorioReservasMemoria repositorio;
    private Estudiante estudiante;
    private HorarioTutoria horario;
    private Asignatura asignatura;

    @BeforeEach
    void configurar() {
        repositorio = new RepositorioReservasMemoria();
        servicio = new ServicioReservas(repositorio, (destinatario, mensaje) -> {});
        Docente docente = new Docente("D01", "María Torres", "mtorres@uees.edu.ec");
        horario = new HorarioTutoria("H01", docente,
                LocalDate.of(2026, 8, 28), LocalTime.of(10, 0), LocalTime.of(11, 0));
        docente.publicarHorario(horario);
        estudiante = new Estudiante("E01", "Carlos Vera", "cvera@uees.edu.ec");
        asignatura = new Asignatura("A01", "Diseño de Software");
    }

    @Test
    void crearReservaOcupaElHorarioYLaGuarda() {
        Reserva reserva = servicio.crearReserva(estudiante, horario, asignatura);
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
        assertFalse(horario.verificarDisponibilidad());
        assertNotNull(repositorio.buscar(reserva.getId()));
    }

    @Test
    void noSePuedeReservarUnHorarioOcupado() {
        servicio.crearReserva(estudiante, horario, asignatura);
        assertThrows(IllegalStateException.class,
                () -> servicio.crearReserva(estudiante, horario, asignatura));
    }

    @Test
    void cancelarUnaReservaLiberaElHorario() {
        Reserva reserva = servicio.crearReserva(estudiante, horario, asignatura);
        servicio.cancelarReserva(reserva.getId());
        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        assertTrue(horario.verificarDisponibilidad());
    }

    @Test
    void confirmarUnaReservaInexistenteLanzaError() {
        assertThrows(IllegalArgumentException.class, () -> servicio.confirmarReserva("no-existe"));
    }
}
