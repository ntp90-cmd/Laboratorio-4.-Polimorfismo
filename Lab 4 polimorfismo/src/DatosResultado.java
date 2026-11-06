import java.util.Map;
import java.util.Set;

public record DatosResultado(
        Map<String, Double> marcas,
        Map<String, Integer> penales,
        Set<String> descalificados
) {

    public DatosResultado {
        marcas = Map.copyOf(marcas);
        penales = Map.copyOf(penales);
        descalificados = Set.copyOf(descalificados);
    }
}