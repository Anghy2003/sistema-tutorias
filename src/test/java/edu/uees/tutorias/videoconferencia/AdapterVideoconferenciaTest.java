package edu.uees.tutorias.videoconferencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.uees.tutorias.cancelacion.CatalogoPoliticas;
import edu.uees.tutorias.domain.Asignatura;
import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.HorarioTutoria;
import edu.uees.tutorias.domain.Modalidad;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.domain.ReservaBuilder;
import edu.uees.tutorias.repository.RepositorioReservasMemoria;
import edu.uees.tutorias.service.ServicioReservas;

/**
 * Verifica que el sistema obtenga la sala virtual a traves del contrato
 * Videoconferencia y que cambiar de proveedor no obligue a tocar el servicio.
 */
class AdapterVideoconferenciaTest {

    private Docente docente;
    private Estudiante estudiante;
    private Asignatura asignatura;

    @BeforeEach
    void configurar() {
        docente = new Docente("D01", "María Torres", "mtorres@uees.edu.ec");
        estudiante = new Estudiante("E01", "Carlos Vera", "cvera@uees.edu.ec");
        asignatura = new Asignatura("A01", "Diseño de Software");
    }

    private HorarioTutoria nuevoHorario(String id) {
        HorarioTutoria horario = new HorarioTutoria(id, docente,
                LocalDate.now().plusDays(3), LocalTime.of(10, 0), LocalTime.of(11, 0));
        docente.publicarHorario(horario);
        return horario;
    }

    private ServicioReservas servicioCon(Videoconferencia proveedor) {
        return new ServicioReservas(new RepositorioReservasMemoria(),
                (destino, mensaje) -> {}, new CatalogoPoliticas(), proveedor);
    }

    private Reserva reservaVirtual(ServicioReservas servicio, String idHorario) {
        return servicio.crearReserva(new ReservaBuilder()
                .estudiante(estudiante)
                .horario(nuevoHorario(idHorario))
                .asignatura(asignatura)
                .modalidad(Modalidad.VIRTUAL)
                .build());
    }

    @Test
    void elAdapterTraduceElContratoInternoALaApiDeZoom() {
        ProveedorZoomAPI api = new ProveedorZoomAPI();
        Videoconferencia videoconferencia = new ZoomAdapter(api);

        String enlace = videoconferencia.crearSala("Tutoría de Diseño", "mtorres@uees.edu.ec");

        assertEquals(api.generarMeeting("Tutoría de Diseño", "mtorres@uees.edu.ec"), enlace);
        assertTrue(enlace.startsWith("https://zoom.us/j/"));
    }

    @Test
    void unaTutoriaVirtualRecibeElEnlaceDeLaSala() {
        ServicioReservas servicio = servicioCon(new ZoomAdapter(new ProveedorZoomAPI()));

        Reserva reserva = reservaVirtual(servicio, "H01");

        assertTrue(reserva.esVirtual());
        assertTrue(reserva.getEnlaceSesion().startsWith("https://zoom.us/j/"));
    }

    @Test
    void cambiarDeProveedorNoObligaAModificarElServicio() {
        ServicioReservas servicio = servicioCon(new TeamsAdapter(new MicrosoftTeamsAPI()));

        Reserva reserva = reservaVirtual(servicio, "H02");

        assertTrue(reserva.getEnlaceSesion().startsWith("https://teams.microsoft.com/"));
    }

    @Test
    void unaTutoriaPresencialNoPideSalaNiTieneEnlace() {
        ServicioReservas servicio = servicioCon(new ZoomAdapter(new ProveedorZoomAPI()));

        Reserva reserva = servicio.crearReserva(new ReservaBuilder()
                .estudiante(estudiante)
                .horario(nuevoHorario("H03"))
                .asignatura(asignatura)
                .build());

        assertFalse(reserva.esVirtual());
        assertEquals("", reserva.getEnlaceSesion());
    }

    @Test
    void unaTutoriaVirtualSinProveedorConfiguradoFalla() {
        ServicioReservas servicio = servicioCon(null);

        assertThrows(IllegalStateException.class, () -> reservaVirtual(servicio, "H04"));
    }
}
