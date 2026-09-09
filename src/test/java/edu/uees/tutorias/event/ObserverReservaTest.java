package edu.uees.tutorias.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.uees.tutorias.cancelacion.CatalogoPoliticas;
import edu.uees.tutorias.domain.Asignatura;
import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.HorarioTutoria;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.domain.ReservaBuilder;
import edu.uees.tutorias.repository.RepositorioReservasMemoria;
import edu.uees.tutorias.service.ServicioReservas;

/**
 * Verifica que la reserva publique sus eventos a todos los observadores
 * registrados y que agregar o quitar uno no afecte al resto.
 */
class ObserverReservaTest {

    /**
     * Observador de prueba que solo anota los eventos que recibio.
     */
    private static class ObservadorEspia implements ReservaObserver {

        private final List<EventoReserva> eventos = new ArrayList<>();

        @Override
        public void alOcurrir(EventoReserva evento, Reserva reserva) {
            eventos.add(evento);
        }
    }

    private ServicioReservas servicio;
    private ObservadorEspia espia;
    private ObservadorBitacora bitacora;
    private Docente docente;
    private Estudiante estudiante;
    private Asignatura asignatura;

    @BeforeEach
    void configurar() {
        servicio = new ServicioReservas(new RepositorioReservasMemoria(), new CatalogoPoliticas(), null);
        espia = new ObservadorEspia();
        bitacora = new ObservadorBitacora();
        servicio.agregarObservador(espia);
        servicio.agregarObservador(bitacora);
        docente = new Docente("D01", "María Torres", "mtorres@uees.edu.ec");
        estudiante = new Estudiante("E01", "Carlos Vera", "cvera@uees.edu.ec");
        asignatura = new Asignatura("A01", "Diseño de Software");
    }

    private Reserva nuevaReserva(String idHorario) {
        HorarioTutoria horario = new HorarioTutoria(idHorario, docente,
                LocalDate.now().plusDays(3), LocalTime.of(10, 0), LocalTime.of(11, 0));
        docente.publicarHorario(horario);
        return servicio.crearReserva(new ReservaBuilder()
                .estudiante(estudiante)
                .horario(horario)
                .asignatura(asignatura)
                .build());
    }

    @Test
    void crearUnaReservaPublicaElEventoCreada() {
        nuevaReserva("H01");

        assertEquals(List.of(EventoReserva.CREADA), espia.eventos);
    }

    @Test
    void cadaCambioDeEstadoPublicaSuPropioEvento() {
        Reserva reserva = nuevaReserva("H02");

        servicio.confirmarReserva(reserva.getId());
        servicio.marcarRealizada(reserva.getId());

        assertEquals(
                List.of(EventoReserva.CREADA, EventoReserva.CONFIRMADA, EventoReserva.REALIZADA),
                espia.eventos);
    }

    @Test
    void todosLosObservadoresRegistradosRecibenElMismoEvento() {
        Reserva reserva = nuevaReserva("H03");

        servicio.cancelarReserva(reserva.getId());

        assertEquals(2, espia.eventos.size());
        assertEquals(2, bitacora.getRegistros().size());
        assertTrue(bitacora.getRegistros().get(1).contains("CANCELADA"));
    }

    @Test
    void unObservadorEliminadoDejaDeRecibirEventos() {
        Reserva reserva = nuevaReserva("H04");
        reserva.eliminarObservador(espia);

        servicio.confirmarReserva(reserva.getId());

        assertEquals(List.of(EventoReserva.CREADA), espia.eventos);
        assertEquals(2, bitacora.getRegistros().size());
    }

    @Test
    void agregarUnObservadorNuevoNoObligaAModificarElServicio() {
        ObservadorEspia panel = new ObservadorEspia();
        servicio.agregarObservador(panel);

        Reserva reserva = nuevaReserva("H05");
        servicio.confirmarReserva(reserva.getId());

        assertEquals(List.of(EventoReserva.CREADA, EventoReserva.CONFIRMADA), panel.eventos);
    }

    @Test
    void elServicioYaNoEnviaNotificacionesPorSuCuenta() {
        List<String> correosEnviados = new ArrayList<>();
        ServicioReservas soloConCorreo = new ServicioReservas(new RepositorioReservasMemoria(),
                (destino, mensaje) -> correosEnviados.add(destino + ": " + mensaje));
        HorarioTutoria horario = new HorarioTutoria("H06", docente,
                LocalDate.now().plusDays(3), LocalTime.of(15, 0), LocalTime.of(16, 0));
        docente.publicarHorario(horario);

        Reserva reserva = soloConCorreo.crearReserva(estudiante, horario, asignatura);
        soloConCorreo.confirmarReserva(reserva.getId());

        // Los dos mensajes llegan por el observador de notificaciones, no por el servicio.
        assertEquals(2, correosEnviados.size());
        assertTrue(correosEnviados.get(0).contains("Se registró su reserva"));
        assertTrue(correosEnviados.get(1).contains("fue confirmada"));
        assertFalse(correosEnviados.get(1).contains("null"));
    }
}
