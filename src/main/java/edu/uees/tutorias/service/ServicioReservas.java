package edu.uees.tutorias.service;

import java.util.ArrayList;
import java.util.List;

import edu.uees.tutorias.cancelacion.CatalogoPoliticas;
import edu.uees.tutorias.cancelacion.PoliticaCancelacion;
import edu.uees.tutorias.domain.Asignatura;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.HorarioTutoria;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.domain.ReservaBuilder;
import edu.uees.tutorias.event.EventoReserva;
import edu.uees.tutorias.event.ObservadorNotificaciones;
import edu.uees.tutorias.event.ReservaObserver;
import edu.uees.tutorias.notification.Notificador;
import edu.uees.tutorias.repository.RepositorioReservas;
import edu.uees.tutorias.videoconferencia.Videoconferencia;

/**
 * Coordina el ciclo de vida de las reservas.
 *
 * Incremento 1: el servicio dejo de decidir plazos de cancelacion y de armar
 * mensajes de notificacion. Ahora consulta la politica que corresponde a la
 * prioridad de la reserva (Strategy), obtiene la sala virtual a traves de un
 * contrato estable (Adapter) y se limita a publicar lo que ocurre para que
 * reaccione quien tenga que reaccionar (Observer).
 */
public class ServicioReservas {

    private final RepositorioReservas repositorio;
    private final CatalogoPoliticas politicas;
    private final Videoconferencia videoconferencia;
    private final List<ReservaObserver> observadores = new ArrayList<>();

    /**
     * Configuracion minima: notificacion por el canal recibido, politicas por
     * defecto y sin proveedor de videoconferencia.
     */
    public ServicioReservas(RepositorioReservas repositorio, Notificador notificador) {
        this(repositorio, new CatalogoPoliticas(), null);
        agregarObservador(new ObservadorNotificaciones(notificador));
    }

    public ServicioReservas(RepositorioReservas repositorio, Notificador notificador,
                            CatalogoPoliticas politicas, Videoconferencia videoconferencia) {
        this(repositorio, politicas, videoconferencia);
        agregarObservador(new ObservadorNotificaciones(notificador));
    }

    public ServicioReservas(RepositorioReservas repositorio, CatalogoPoliticas politicas,
                            Videoconferencia videoconferencia) {
        this.repositorio = repositorio;
        this.politicas = politicas;
        this.videoconferencia = videoconferencia;
    }

    /**
     * Registra un observador que reaccionara a todas las reservas que administre
     * este servicio. Agregar uno nuevo no obliga a modificar ningun metodo.
     */
    public void agregarObservador(ReservaObserver observador) {
        observadores.add(observador);
    }

    /**
     * Crea una reserva presencial y de prioridad normal, que es el caso mas comun.
     */
    public Reserva crearReserva(Estudiante estudiante, HorarioTutoria horario, Asignatura asignatura) {
        return crearReserva(new ReservaBuilder()
                .estudiante(estudiante)
                .horario(horario)
                .asignatura(asignatura)
                .build());
    }

    /**
     * Registra una reserva ya construida con {@link ReservaBuilder}, lo que permite
     * indicar modalidad, prioridad y observaciones sin multiplicar sobrecargas.
     */
    public Reserva crearReserva(Reserva reserva) {
        HorarioTutoria horario = reserva.getHorario();
        if (!horario.verificarDisponibilidad()) {
            throw new IllegalStateException("El horario seleccionado no está disponible");
        }
        horario.reservar();
        reserva.getEstudiante().agregarReserva(reserva);
        if (reserva.esVirtual()) {
            reserva.asignarEnlaceSesion(crearSalaVirtual(reserva));
        }
        observadores.forEach(reserva::agregarObservador);
        repositorio.guardar(reserva);
        reserva.publicar(EventoReserva.CREADA);
        return reserva;
    }

    public void confirmarReserva(String reservaId) {
        Reserva reserva = obtenerReserva(reservaId);
        reserva.confirmar();
        repositorio.actualizar(reserva);
    }

    /**
     * Cancela una reserva si la politica correspondiente a su prioridad lo permite.
     * El servicio no conoce ningun plazo: solo sabe a quien preguntar.
     */
    public void cancelarReserva(String reservaId) {
        Reserva reserva = obtenerReserva(reservaId);
        PoliticaCancelacion politica = politicas.politicaPara(reserva.getPrioridad());
        if (!politica.puedeCancelar(reserva)) {
            throw new IllegalStateException(
                    "No se puede cancelar: " + politica.descripcion());
        }
        reserva.cancelar();
        reserva.getHorario().liberar();
        repositorio.actualizar(reserva);
    }

    public void reprogramarReserva(String reservaId, HorarioTutoria nuevoHorario) {
        Reserva reserva = obtenerReserva(reservaId);
        reserva.reprogramar(nuevoHorario);
        repositorio.actualizar(reserva);
    }

    public void marcarRealizada(String reservaId) {
        Reserva reserva = obtenerReserva(reservaId);
        reserva.marcarRealizada();
        repositorio.actualizar(reserva);
    }

    /**
     * Pide la sala al proveedor a traves del contrato Videoconferencia. El
     * servicio no sabe si detras esta Zoom o Teams.
     */
    private String crearSalaVirtual(Reserva reserva) {
        if (videoconferencia == null) {
            throw new IllegalStateException(
                    "No hay un proveedor de videoconferencia configurado para tutorías virtuales");
        }
        return videoconferencia.crearSala(
                "Tutoría de " + reserva.getAsignatura().getNombre(),
                reserva.getHorario().getDocente().getCorreo());
    }

    private Reserva obtenerReserva(String reservaId) {
        Reserva reserva = repositorio.buscar(reservaId);
        if (reserva == null) {
            throw new IllegalArgumentException("No existe una reserva con el id " + reservaId);
        }
        return reserva;
    }
}
