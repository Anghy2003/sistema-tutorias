package edu.uees.tutorias.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.uees.tutorias.domain.Asignatura;
import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.EstadoReserva;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.HorarioTutoria;
import edu.uees.tutorias.domain.Prioridad;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.domain.ReservaBuilder;
import edu.uees.tutorias.repository.RepositorioReservasMemoria;

/**
 * Verifica que el servicio delegue la decision de cancelar en la politica que
 * corresponde a la prioridad de la reserva.
 */
class CancelacionConPoliticaTest {

    private ServicioReservas servicio;
    private Docente docente;
    private Estudiante estudiante;
    private Asignatura asignatura;

    @BeforeEach
    void configurar() {
        servicio = new ServicioReservas(new RepositorioReservasMemoria(), (destino, mensaje) -> {});
        docente = new Docente("D01", "María Torres", "mtorres@uees.edu.ec");
        estudiante = new Estudiante("E01", "Carlos Vera", "cvera@uees.edu.ec");
        asignatura = new Asignatura("A01", "Diseño de Software");
    }

    private Reserva reservaEnMinutos(long minutos, Prioridad prioridad) {
        LocalDateTime inicio = LocalDateTime.now().plusMinutes(minutos);
        HorarioTutoria horario = new HorarioTutoria("H" + minutos + prioridad, docente,
                inicio.toLocalDate(), inicio.toLocalTime(), finDeSesion(inicio.toLocalTime()));
        docente.publicarHorario(horario);
        return servicio.crearReserva(new ReservaBuilder()
                .estudiante(estudiante)
                .horario(horario)
                .asignatura(asignatura)
                .prioridad(prioridad)
                .build());
    }

    /**
     * Hora de fin de una sesion de una hora. Si el inicio cae dentro de la ultima
     * hora del dia, la sesion termina a las 23:59 para no cruzar la medianoche.
     */
    private LocalTime finDeSesion(LocalTime horaInicio) {
        LocalTime fin = horaInicio.plusHours(1);
        return fin.isAfter(horaInicio) ? fin : LocalTime.of(23, 59);
    }

    @Test
    void unaReservaNormalConCincoHorasSePuedeCancelar() {
        Reserva reserva = reservaEnMinutos(5 * 60, Prioridad.NORMAL);

        servicio.cancelarReserva(reserva.getId());

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        assertTrue(reserva.getHorario().verificarDisponibilidad());
    }

    @Test
    void unaReservaNormalConNoventaMinutosNoSePuedeCancelar() {
        Reserva reserva = reservaEnMinutos(90, Prioridad.NORMAL);

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> servicio.cancelarReserva(reserva.getId()));

        assertTrue(error.getMessage().contains("2 horas"));
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
        assertFalse(reserva.getHorario().verificarDisponibilidad());
    }

    @Test
    void laMismaAnticipacionSiSePermiteCuandoLaReservaEsPrioritaria() {
        Reserva reserva = reservaEnMinutos(90, Prioridad.PRIORITARIA);

        servicio.cancelarReserva(reserva.getId());

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
    }

    @Test
    void unaReservaGrupalConCincoHorasNoSePuedeCancelar() {
        Reserva reserva = reservaEnMinutos(5 * 60, Prioridad.GRUPAL);

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> servicio.cancelarReserva(reserva.getId()));

        assertTrue(error.getMessage().contains("24 horas"));
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
    }
}
