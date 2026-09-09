package edu.uees.tutorias.videoconferencia;

/**
 * Segundo Adaptee: API del proveedor externo Microsoft Teams.
 *
 * Su operacion tiene otro nombre y otros parametros que la de Zoom, lo que
 * demuestra que la incompatibilidad de interfaces es real y no un supuesto.
 */
public class MicrosoftTeamsAPI {

    public String scheduleOnlineMeeting(String subject, String organizer) {
        return "https://teams.microsoft.com/l/meetup-join/"
                + Math.abs((subject + organizer).hashCode());
    }
}
