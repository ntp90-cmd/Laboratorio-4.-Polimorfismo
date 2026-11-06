import java.util.List;
import java.util.Map;

public class FutbolSalaConPenales extends FutbolSala {

    public FutbolSalaConPenales() {
        super("Futbol sala con penales", 750);
    }

    @Override
    protected Map<String, Integer> leerPenales(
            EntradaConsola entrada,
            List<Participante> participantes,
            int a,
            int b
    ) {
        if (a != b) {
            return Map.of();
        }

        int penalesA = entrada.entero(
                "Penales de " + participantes.get(0).nombre() + ": "
        );

        int penalesB = entrada.entero(
                "Penales de " + participantes.get(1).nombre() + ": "
        );

        return Map.of(
                participantes.get(0).codigo(), penalesA,
                participantes.get(1).codigo(), penalesB
        );
    }

    @Override
    public ResultadoEvaluado evaluarResultado(
            Evento evento,
            DatosResultado datos
    ) {
        int[] marcador = marcador(evento, datos);

        if (marcador[0] != marcador[1]) {
            return super.evaluarResultado(evento, datos);
        }

        Reglas.exigir(
                datos.penales().keySet().equals(datos.marcas().keySet()),
                "Faltan los penales."
        );

        List<Participante> participantes = evento.getParticipantes();

        int a = datos.penales().get(participantes.get(0).codigo());
        int b = datos.penales().get(participantes.get(1).codigo());

        Reglas.exigir(
                a >= 0 && b >= 0 && a != b,
                "Los penales deben ser no negativos y distintos."
        );

        return resultado(
                evento,
                datos,
                a > b ? 2 : 1,
                b > a ? 2 : 1,
                0,
                a > b ? 0 : 1
        );
    }
}