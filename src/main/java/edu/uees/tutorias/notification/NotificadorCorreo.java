package edu.uees.tutorias.notification;

public class NotificadorCorreo implements Notificador {

    @Override
    public void enviarNotificacion(String destinatario, String mensaje) {
        System.out.println("Correo enviado a " + destinatario + ": " + mensaje);
    }
}
