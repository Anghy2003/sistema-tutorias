package edu.uees.tutorias.videoconferencia;

/**
 * Target del patron Adapter: contrato de videoconferencia que usa el sistema.
 *
 * Esta escrito en el lenguaje del dominio de tutorias (sala, titulo, docente) y
 * es el que debe permanecer estable aunque cambie el proveedor.
 */
public interface Videoconferencia {

    String crearSala(String titulo, String correoDocente);
}
