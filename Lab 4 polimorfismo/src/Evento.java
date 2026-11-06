import java.util.List;
import java.util.Optional;

public interface Evento {

    String getCodigo();

    List<Participante> getParticipantes();

    DatosEvento getDatos();

    default EstadoEvento getEstado() {
        return getDatos().estado();
    }

    default Optional<Programacion> getProgramacion() {
        return getDatos().programacion();
    }

    default Optional<ResultadoEvaluado> getResultado() {
        return getDatos().resultado();
    }

    default void programar(Programacion nueva) {
        getDatos().programar(nueva);
    }

    default void registrarResultado(ResultadoEvaluado resultado) {
        getDatos().registrar(resultado);
    }

    default String resumen() {
        String nombres = String.join(
                " / ",
                getParticipantes().stream()
                        .map(Participante::nombre)
                        .toList()
        );

        String resultado = getResultado()
                .map(r -> " | Marcas: " + r.datos().marcas()
                        + " | Penales: " + r.datos().penales()
                        + " | Descalificados: "
                        + r.datos().descalificados())
                .orElse("");

        return getCodigo()
                + " | " + nombres
                + " | " + getEstado()
                + " | " + getProgramacion()
                        .map(Object::toString)
                        .orElse("Sin programar")
                + resultado;
    }
}