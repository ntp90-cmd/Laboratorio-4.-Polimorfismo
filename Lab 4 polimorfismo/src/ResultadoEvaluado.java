import java.util.Map;

public record ResultadoEvaluado(
        DatosResultado datos,
        Map<String, AporteTabla> aportes
) {

    public ResultadoEvaluado {
        aportes = Map.copyOf(aportes);
    }
}