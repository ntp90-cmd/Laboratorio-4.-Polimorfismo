import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public abstract class Disciplina {

    private final String nombre;
    private final int minimo;
    private final int maximo;
    private final BigDecimal cuota;

    protected Disciplina(
            String nombre,
            int minimo,
            int maximo,
            int cuota
    ) {
        this.nombre = nombre;
        this.minimo = minimo;
        this.maximo = maximo;
        this.cuota = BigDecimal.valueOf(cuota);
    }

    public String getNombre() {
        return nombre;
    }

    public int getMinimo() {
        return minimo;
    }

    public BigDecimal getCuota() {
        return cuota;
    }

    public BigDecimal cotizar(int cantidad) {
        Reglas.exigir(
                cantidad > 0,
                "La cantidad debe ser positiva."
        );

        return cuota.multiply(BigDecimal.valueOf(cantidad));
    }

    public void validarInscripcion(
            Participante participante,
            Collection<Participante> inscritos
    ) {
        Reglas.exigir(
                inscritos.size() < maximo,
                "El torneo ya alcanzo su cupo."
        );
    }

    public abstract List<Evento> generarEventos(
            String torneo,
            List<Participante> participantes
    );

    public abstract DatosResultado leerDatos(
            EntradaConsola entrada,
            Evento evento
    );

    public abstract ResultadoEvaluado evaluarResultado(
            Evento evento,
            DatosResultado datos
    );
}