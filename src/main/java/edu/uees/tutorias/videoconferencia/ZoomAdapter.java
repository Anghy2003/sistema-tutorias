package edu.uees.tutorias.videoconferencia;

/**
 * Adapter: traduce el contrato Videoconferencia a la API de Zoom.
 *
 * Es la unica clase del sistema que conoce el metodo generarMeeting. Si manana
 * la universidad cambia de proveedor, se escribe otro adaptador y ni el
 * servicio ni el dominio se enteran.
 */
public class ZoomAdapter implements Videoconferencia {

    private final ProveedorZoomAPI zoom;

    public ZoomAdapter(ProveedorZoomAPI zoom) {
        this.zoom = zoom;
    }

    @Override
    public String crearSala(String titulo, String correoDocente) {
        return zoom.generarMeeting(titulo, correoDocente);
    }
}
