package edu.uees.tutorias.videoconferencia;

/**
 * Segundo Adapter. Se agrega sin modificar la interfaz Videoconferencia, el
 * servicio de reservas ni el dominio: solo cambia que implementacion se inyecta.
 */
public class TeamsAdapter implements Videoconferencia {

    private final MicrosoftTeamsAPI teams;

    public TeamsAdapter(MicrosoftTeamsAPI teams) {
        this.teams = teams;
    }

    @Override
    public String crearSala(String titulo, String correoDocente) {
        return teams.scheduleOnlineMeeting(titulo, correoDocente);
    }
}
