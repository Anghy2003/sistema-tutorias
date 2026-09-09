package edu.uees.tutorias.videoconferencia;

/**
 * Adaptee: API del proveedor externo Zoom.
 *
 * Simula la libreria del proveedor. Su operacion se llama generarMeeting y usa
 * los nombres topic y hostEmail, que no coinciden con el contrato del sistema.
 * No podemos modificarla porque pertenece al proveedor.
 */
public class ProveedorZoomAPI {

    public String generarMeeting(String topic, String hostEmail) {
        return "https://zoom.us/j/" + Math.abs((topic + hostEmail).hashCode());
    }
}
