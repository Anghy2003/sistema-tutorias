package edu.uees.tutorias;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

import edu.uees.tutorias.domain.Asignatura;
import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.HorarioTutoria;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.NotificadorCorreo;
import edu.uees.tutorias.repository.RepositorioReservasMemoria;
import edu.uees.tutorias.service.ServicioReservas;

public class App {

    private static final Scanner teclado = new Scanner(System.in);
    private static final RepositorioReservasMemoria repositorio = new RepositorioReservasMemoria();
    private static final ServicioReservas servicio = new ServicioReservas(repositorio, new NotificadorCorreo());
    private static Docente docente;
    private static Estudiante estudiante;
    private static List<Asignatura> asignaturas;

    public static void main(String[] args) {
        cargarDatosIniciales();
        System.out.println("=============================================");
        System.out.println("   SISTEMA DE GESTIÓN DE TUTORÍAS - UEES");
        System.out.println("=============================================");
        System.out.println("Estudiante: " + estudiante.getNombre() + " (" + estudiante.getCorreo() + ")");
        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            int opcion = leerNumero("Seleccione una opción: ");
            switch (opcion) {
                case 1 -> verHorariosDisponibles();
                case 2 -> solicitarTutoria();
                case 3 -> confirmarReserva();
                case 4 -> cancelarReserva();
                case 5 -> reprogramarReserva();
                case 6 -> marcarRealizada();
                case 7 -> verMisTutorias();
                case 0 -> salir = true;
                default -> System.out.println("Esa opción no existe, intente de nuevo.");
            }
        }
        System.out.println("Gracias por usar el sistema. ¡Hasta pronto!");
    }

    private static void cargarDatosIniciales() {
        docente = new Docente("D01", "María Torres", "mtorres@uees.edu.ec");
        LocalDate manana = LocalDate.now().plusDays(1);
        docente.publicarHorario(new HorarioTutoria("H01", docente, manana, LocalTime.of(10, 0), LocalTime.of(11, 0)));
        docente.publicarHorario(new HorarioTutoria("H02", docente, manana, LocalTime.of(15, 0), LocalTime.of(16, 0)));
        docente.publicarHorario(new HorarioTutoria("H03", docente, manana.plusDays(1), LocalTime.of(9, 0), LocalTime.of(10, 0)));
        docente.publicarHorario(new HorarioTutoria("H04", docente, manana.plusDays(2), LocalTime.of(11, 0), LocalTime.of(12, 0)));
        estudiante = new Estudiante("E01", "Carlos Vera", "cvera@uees.edu.ec");
        asignaturas = List.of(
                new Asignatura("A01", "Diseño de Software"),
                new Asignatura("A02", "Base de Datos"),
                new Asignatura("A03", "Programación Orientada a Objetos"));
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("--------- MENÚ PRINCIPAL ---------");
        System.out.println("1. Ver horarios disponibles");
        System.out.println("2. Solicitar una tutoría");
        System.out.println("3. Confirmar una reserva");
        System.out.println("4. Cancelar una reserva");
        System.out.println("5. Reprogramar una reserva");
        System.out.println("6. Marcar tutoría como realizada");
        System.out.println("7. Ver mis tutorías");
        System.out.println("0. Salir");
    }

    private static void verHorariosDisponibles() {
        List<HorarioTutoria> disponibles = docente.consultarHorariosDisponibles();
        if (disponibles.isEmpty()) {
            System.out.println("No hay horarios disponibles por el momento.");
            return;
        }
        System.out.println("Horarios disponibles del docente " + docente.getNombre() + ":");
        for (int i = 0; i < disponibles.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + describirHorario(disponibles.get(i)));
        }
    }

    private static void solicitarTutoria() {
        List<HorarioTutoria> disponibles = docente.consultarHorariosDisponibles();
        if (disponibles.isEmpty()) {
            System.out.println("No hay horarios disponibles para reservar.");
            return;
        }
        HorarioTutoria horario = elegirHorario(disponibles);
        if (horario == null) {
            return;
        }
        System.out.println("Asignaturas:");
        for (int i = 0; i < asignaturas.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + asignaturas.get(i).getNombre());
        }
        int posicion = leerNumero("Elija la asignatura: ");
        if (posicion < 1 || posicion > asignaturas.size()) {
            System.out.println("Asignatura no válida.");
            return;
        }
        try {
            Reserva reserva = servicio.crearReserva(estudiante, horario, asignaturas.get(posicion - 1));
            System.out.println("Reserva registrada en estado " + reserva.getEstado() + ".");
        } catch (Exception e) {
            System.out.println("No se pudo crear la reserva: " + e.getMessage());
        }
    }

    private static void confirmarReserva() {
        Reserva reserva = elegirReserva();
        if (reserva == null) {
            return;
        }
        try {
            servicio.confirmarReserva(reserva.getId());
            System.out.println("La reserva quedó en estado " + reserva.getEstado() + ".");
        } catch (Exception e) {
            System.out.println("No se pudo confirmar: " + e.getMessage());
        }
    }

    private static void cancelarReserva() {
        Reserva reserva = elegirReserva();
        if (reserva == null) {
            return;
        }
        try {
            servicio.cancelarReserva(reserva.getId());
            System.out.println("La reserva quedó en estado " + reserva.getEstado() + " y el horario se liberó.");
        } catch (Exception e) {
            System.out.println("No se pudo cancelar: " + e.getMessage());
        }
    }

    private static void reprogramarReserva() {
        Reserva reserva = elegirReserva();
        if (reserva == null) {
            return;
        }
        List<HorarioTutoria> disponibles = docente.consultarHorariosDisponibles();
        if (disponibles.isEmpty()) {
            System.out.println("No hay otros horarios disponibles para reprogramar.");
            return;
        }
        System.out.println("Horarios disponibles:");
        HorarioTutoria nuevoHorario = elegirHorario(disponibles);
        if (nuevoHorario == null) {
            return;
        }
        try {
            reserva.reprogramar(nuevoHorario);
            repositorio.actualizar(reserva);
            System.out.println("Reserva reprogramada para el " + describirHorario(nuevoHorario) + ". Queda pendiente de confirmación.");
        } catch (Exception e) {
            System.out.println("No se pudo reprogramar: " + e.getMessage());
        }
    }

    private static void marcarRealizada() {
        Reserva reserva = elegirReserva();
        if (reserva == null) {
            return;
        }
        try {
            reserva.marcarRealizada();
            repositorio.actualizar(reserva);
            System.out.println("La tutoría quedó registrada como " + reserva.getEstado() + ".");
        } catch (Exception e) {
            System.out.println("No se pudo marcar como realizada: " + e.getMessage());
        }
    }

    private static void verMisTutorias() {
        List<Reserva> reservas = estudiante.consultarTutorias();
        if (reservas.isEmpty()) {
            System.out.println("Todavía no ha solicitado ninguna tutoría.");
            return;
        }
        System.out.println("Tutorías de " + estudiante.getNombre() + ":");
        for (int i = 0; i < reservas.size(); i++) {
            Reserva reserva = reservas.get(i);
            System.out.println("  " + (i + 1) + ") " + reserva.getAsignatura().getNombre()
                    + " | " + describirHorario(reserva.getHorario())
                    + " | Estado: " + reserva.getEstado());
        }
    }

    private static HorarioTutoria elegirHorario(List<HorarioTutoria> disponibles) {
        for (int i = 0; i < disponibles.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + describirHorario(disponibles.get(i)));
        }
        int posicion = leerNumero("Elija el horario: ");
        if (posicion < 1 || posicion > disponibles.size()) {
            System.out.println("Horario no válido.");
            return null;
        }
        return disponibles.get(posicion - 1);
    }

    private static Reserva elegirReserva() {
        List<Reserva> reservas = estudiante.consultarTutorias();
        if (reservas.isEmpty()) {
            System.out.println("Todavía no tiene reservas registradas.");
            return null;
        }
        System.out.println("Sus reservas:");
        for (int i = 0; i < reservas.size(); i++) {
            Reserva reserva = reservas.get(i);
            System.out.println("  " + (i + 1) + ") " + reserva.getAsignatura().getNombre()
                    + " | " + describirHorario(reserva.getHorario())
                    + " | Estado: " + reserva.getEstado());
        }
        int posicion = leerNumero("Elija la reserva: ");
        if (posicion < 1 || posicion > reservas.size()) {
            System.out.println("Reserva no válida.");
            return null;
        }
        return reservas.get(posicion - 1);
    }

    private static String describirHorario(HorarioTutoria horario) {
        return horario.getFecha() + " de " + horario.getHoraInicio() + " a " + horario.getHoraFin()
                + " con " + horario.getDocente().getNombre();
    }

    private static int leerNumero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = teclado.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        }
    }
}
