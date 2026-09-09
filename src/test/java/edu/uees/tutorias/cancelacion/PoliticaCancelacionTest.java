package edu.uees.tutorias.cancelacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import edu.uees.tutorias.domain.Asignatura;
import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.HorarioTutoria;
import edu.uees.tutorias.domain.Prioridad;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.domain.ReservaBuilder;

/**
 * Verifica que cada politica aplique su propio plazo y que el catalogo entregue
 * la politica que corresponde a cada prioridad.
 */
class PoliticaCancelacionTest {

    private Reserva reservaEnMinutos(long minutos, Prioridad prioridad) {
        LocalDateTime inicio = LocalDateTime.now().plusMinutes(minutos);
        Docente docente = new Docente("D01", "María Torres", "mtorres@uees.edu.ec");
        HorarioTutoria horario = new HorarioTutoria("H01", docente,
                inicio.toLocalDate(), inicio.toLocalTime(), finDeSesion(inicio.toLocalTime()));
        return new ReservaBuilder()
                .estudiante(new Estudiante("E01", "Carlos Vera", "cvera@uees.edu.ec"))
                .horario(horario)
                .asignatura(new Asignatura("A01", "Diseño de Software"))
                .prioridad(prioridad)
                .build();
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
    void laPoliticaNormalExigeDosHoras() {
        PoliticaCancelacion normal = new CancelacionNormal();

        assertTrue(normal.puedeCancelar(reservaEnMinutos(5 * 60, Prioridad.NORMAL)));
        assertFalse(normal.puedeCancelar(reservaEnMinutos(90, Prioridad.NORMAL)));
        assertFalse(normal.puedeCancelar(reservaEnMinutos(15, Prioridad.NORMAL)));
    }

    @Test
    void laPoliticaPrioritariaAdmiteHastaUnaHoraAntes() {
        PoliticaCancelacion prioritaria = new CancelacionPrioritaria();

        assertTrue(prioritaria.puedeCancelar(reservaEnMinutos(90, Prioridad.PRIORITARIA)));
        assertFalse(prioritaria.puedeCancelar(reservaEnMinutos(30, Prioridad.PRIORITARIA)));
    }

    @Test
    void laPoliticaGrupalExigeVeinticuatroHoras() {
        PoliticaCancelacion grupal = new CancelacionGrupal();

        assertTrue(grupal.puedeCancelar(reservaEnMinutos(30 * 60, Prioridad.GRUPAL)));
        assertFalse(grupal.puedeCancelar(reservaEnMinutos(20 * 60, Prioridad.GRUPAL)));
    }

    @Test
    void elCatalogoEntregaLaPoliticaDeCadaPrioridad() {
        CatalogoPoliticas catalogo = new CatalogoPoliticas();

        assertEquals(CancelacionNormal.class,
                catalogo.politicaPara(Prioridad.NORMAL).getClass());
        assertEquals(CancelacionPrioritaria.class,
                catalogo.politicaPara(Prioridad.PRIORITARIA).getClass());
        assertEquals(CancelacionGrupal.class,
                catalogo.politicaPara(Prioridad.GRUPAL).getClass());
    }

    @Test
    void sePuedeRegistrarUnaPoliticaNuevaSinModificarElCatalogo() {
        CatalogoPoliticas catalogo = new CatalogoPoliticas();
        // Politica definida aqui mismo: el catalogo nunca la conocio (OCP).
        PoliticaCancelacion sinRestriccion = new PoliticaCancelacion() {
            @Override
            public boolean puedeCancelar(Reserva reserva) {
                return true;
            }

            @Override
            public String descripcion() {
                return "sin restricción";
            }
        };

        catalogo.registrar(Prioridad.GRUPAL, sinRestriccion);

        assertSame(sinRestriccion, catalogo.politicaPara(Prioridad.GRUPAL));
        assertTrue(catalogo.politicaPara(Prioridad.GRUPAL)
                .puedeCancelar(reservaEnMinutos(5, Prioridad.GRUPAL)));
    }
}
