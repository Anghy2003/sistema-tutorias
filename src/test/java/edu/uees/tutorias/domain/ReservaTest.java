package edu.uees.tutorias.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

class ReservaTest {

    private Reserva crearReserva() {
        Docente docente = new Docente("D01", "María Torres", "mtorres@uees.edu.ec");
        HorarioTutoria horario = new HorarioTutoria("H01", docente,
                LocalDate.now().plusDays(3), LocalTime.of(10, 0), LocalTime.of(11, 0));
        Estudiante estudiante = new Estudiante("E01", "Carlos Vera", "cvera@uees.edu.ec");
        Asignatura asignatura = new Asignatura("A01", "Diseño de Software");
        return new ReservaBuilder()
                .id("R01")
                .estudiante(estudiante)
                .horario(horario)
                .asignatura(asignatura)
                .build();
    }

    @Test
    void unaReservaNuevaQuedaPendiente() {
        assertEquals(EstadoReserva.PENDIENTE, crearReserva().getEstado());
    }

    @Test
    void confirmarUnaReservaPendienteCambiaSuEstado() {
        Reserva reserva = crearReserva();
        reserva.confirmar();
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
    }

    @Test
    void noSePuedeConfirmarUnaReservaCancelada() {
        Reserva reserva = crearReserva();
        reserva.cancelar();
        assertThrows(IllegalStateException.class, reserva::confirmar);
    }

    @Test
    void soloUnaReservaConfirmadaSePuedeMarcarRealizada() {
        Reserva reserva = crearReserva();
        assertThrows(IllegalStateException.class, reserva::marcarRealizada);
        reserva.confirmar();
        reserva.marcarRealizada();
        assertEquals(EstadoReserva.REALIZADA, reserva.getEstado());
    }

    @Test
    void unaReservaRealizadaNoSePuedeCancelar() {
        Reserva reserva = crearReserva();
        reserva.confirmar();
        reserva.marcarRealizada();
        assertThrows(IllegalStateException.class, reserva::cancelar);
    }

    @Test
    void reprogramarCambiaElHorarioYVuelveAPendiente() {
        Reserva reserva = crearReserva();
        reserva.getHorario().reservar();
        reserva.confirmar();
        HorarioTutoria nuevoHorario = new HorarioTutoria("H02", reserva.getHorario().getDocente(),
                LocalDate.now().plusDays(4), LocalTime.of(14, 0), LocalTime.of(15, 0));
        HorarioTutoria horarioAnterior = reserva.getHorario();
        reserva.reprogramar(nuevoHorario);
        assertEquals(nuevoHorario, reserva.getHorario());
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
        assertEquals(true, horarioAnterior.verificarDisponibilidad());
        assertEquals(false, nuevoHorario.verificarDisponibilidad());
    }

    @Test
    void noSePuedeReprogramarUnaReservaCancelada() {
        Reserva reserva = crearReserva();
        reserva.cancelar();
        HorarioTutoria nuevoHorario = new HorarioTutoria("H02", reserva.getHorario().getDocente(),
                LocalDate.now().plusDays(4), LocalTime.of(14, 0), LocalTime.of(15, 0));
        assertThrows(IllegalStateException.class, () -> reserva.reprogramar(nuevoHorario));
    }

    @Test
    void noSePuedeReprogramarHaciaUnHorarioOcupado() {
        Reserva reserva = crearReserva();
        HorarioTutoria nuevoHorario = new HorarioTutoria("H02", reserva.getHorario().getDocente(),
                LocalDate.now().plusDays(4), LocalTime.of(14, 0), LocalTime.of(15, 0));
        nuevoHorario.reservar();
        assertThrows(IllegalStateException.class, () -> reserva.reprogramar(nuevoHorario));
    }
}
