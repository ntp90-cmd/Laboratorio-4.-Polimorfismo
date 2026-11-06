import java.util.Objects;
import java.util.Optional;

public class DatosEvento {

    private EstadoEvento estado = EstadoEvento.SIN_PROGRAMAR;
    private Programacion programacion;
    private ResultadoEvaluado resultado;

    EstadoEvento estado() {
        return estado;
    }

    Optional<Programacion> programacion() {
        return Optional.ofNullable(programacion);
    }

    Optional<ResultadoEvaluado> resultado() {
        return Optional.ofNullable(resultado);
    }

    void programar(Programacion nueva) {
        Reglas.exigir(
                estado != EstadoEvento.FINALIZADO,
                "El evento ya finalizo."
        );

        Objects.requireNonNull(nueva);

        programacion = nueva;
        estado = EstadoEvento.PROGRAMADO;
    }

    void registrar(ResultadoEvaluado nuevo) {
        Reglas.exigir(
                estado == EstadoEvento.PROGRAMADO,
                "El evento no admite resultados."
        );

        Objects.requireNonNull(nuevo);

        resultado = nuevo;
        estado = EstadoEvento.FINALIZADO;
    }
}
