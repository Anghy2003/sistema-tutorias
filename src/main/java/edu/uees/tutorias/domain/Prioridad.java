package edu.uees.tutorias.domain;

/**
 * Prioridad de la reserva.
 *
 * Determina que politica de cancelacion se aplica, porque el plazo minimo de
 * anticipacion no es el mismo para una tutoria comun, una prioritaria o una
 * grupal.
 */
public enum Prioridad {
    NORMAL,
    PRIORITARIA,
    GRUPAL
}
