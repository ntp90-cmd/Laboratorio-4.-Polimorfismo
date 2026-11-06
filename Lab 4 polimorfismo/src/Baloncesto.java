public class Baloncesto extends DisciplinaPorEncuentros {

    public Baloncesto() {
        super("Baloncesto", 4, 6, 500);
    }

    @Override
    public ResultadoEvaluado evaluarResultado(
            Evento evento,
            DatosResultado datos
    ) {
        int[] marcador = marcador(evento, datos);

        int a = marcador[0];
        int b = marcador[1];

        Reglas.exigir(
                datos.penales().isEmpty() && a != b,
                "Baloncesto no admite penales ni empate."
        );

        return resultado(
                evento,
                datos,
                a > b ? 2 : 1,
                b > a ? 2 : 1,
                (long) a - b,
                a > b ? 0 : 1
        );
    }
}