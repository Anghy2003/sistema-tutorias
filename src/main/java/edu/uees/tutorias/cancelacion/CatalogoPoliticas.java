package edu.uees.tutorias.cancelacion;

import java.util.EnumMap;
import java.util.Map;

import edu.uees.tutorias.domain.Prioridad;

/**
 * Selecciona la politica que corresponde a cada prioridad.
 *
 * Se resolvio con un mapa y no con una cadena de condicionales para que agregar
 * una prioridad nueva sea registrar una entrada mas y no modificar una decision
 * existente. El catalogo es el unico lugar del sistema que asocia una prioridad
 * con una regla concreta.
 */
public class CatalogoPoliticas {

    private final Map<Prioridad, PoliticaCancelacion> politicas = new EnumMap<>(Prioridad.class);

    public CatalogoPoliticas() {
        registrar(Prioridad.NORMAL, new CancelacionNormal());
        registrar(Prioridad.PRIORITARIA, new CancelacionPrioritaria());
        registrar(Prioridad.GRUPAL, new CancelacionGrupal());
    }

    public final void registrar(Prioridad prioridad, PoliticaCancelacion politica) {
        politicas.put(prioridad, politica);
    }

    public PoliticaCancelacion politicaPara(Prioridad prioridad) {
        PoliticaCancelacion politica = politicas.get(prioridad);
        if (politica == null) {
            throw new IllegalStateException("No hay una política registrada para la prioridad " + prioridad);
        }
        return politica;
    }
}
