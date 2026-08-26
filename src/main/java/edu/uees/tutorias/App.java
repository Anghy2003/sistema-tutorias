package edu.uees.tutorias;

import java.time.LocalDate;
import java.time.LocalTime;

import edu.uees.tutorias.domain.Asignatura;
import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.HorarioTutoria;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.NotificadorCorreo;
import edu.uees.tutorias.repository.RepositorioReservasMemoria;
import edu.uees.tutorias.service.ServicioReservas;

public class App {

    public static void main(String[] args) {
        Docente docente = new Docente("D01", "María Torres", "mtorres@uees.edu.ec");
        HorarioTutoria horario = new HorarioTutoria("H01", docente,
                LocalDate.of(2026, 8, 28), LocalTime.of(10, 0), LocalTime.of(11, 0));
        docente.publicarHorario(horario);

        Estudiante estudiante = new Estudiante("E01", "Carlos Vera", "cvera@uees.edu.ec");
        Asignatura asignatura = new Asignatura("A01", "Diseño de Software");

        ServicioReservas servicio = new ServicioReservas(new RepositorioReservasMemoria(), new NotificadorCorreo());

        Reserva reserva = servicio.crearReserva(estudiante, horario, asignatura);
        System.out.println("Estado inicial: " + reserva.getEstado());
        System.out.println("Horario disponible: " + horario.verificarDisponibilidad());

        servicio.confirmarReserva(reserva.getId());
        System.out.println("Estado luego de confirmar: " + reserva.getEstado());

        servicio.cancelarReserva(reserva.getId());
        System.out.println("Estado luego de cancelar: " + reserva.getEstado());
        System.out.println("Horario disponible: " + horario.verificarDisponibilidad());

        System.out.println("Tutorías del estudiante: " + estudiante.consultarTutorias().size());
        System.out.println("Horarios disponibles del docente: " + docente.consultarHorariosDisponibles().size());
    }
}
