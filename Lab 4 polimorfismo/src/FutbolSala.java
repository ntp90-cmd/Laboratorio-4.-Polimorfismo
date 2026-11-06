public class FutbolSala extends DisciplinaPorEncuentros {

    public FutbolSala() {
        this("Futbol sala", 600);
    }

    protected FutbolSala(String nombre, int cuota) {
        super(nombre, 4, 8, cuota);
    }

    @Override
    public ResultadoEvaluado evaluarResultado(
            Evento evento,
            DatosResultado datos
    ) {
        int[] marcador = marcador(evento, datos);

        Reglas.exigir(
                datos.penales().isEmpty(),
                "Este resultado no admite penales."
        );

        int a = marcador[0];
        int b = marcador[1];

        int puntosA = a == b ? 1 : a > b ? 3 : 0;
        int puntosB = a == b ? 1 : b > a ? 3 : 0;
        int ganador = a == b ? -1 : a > b ? 0 : 1;

        return resultado(
                evento,
                datos,
                puntosA,
                puntosB,
                (long) a - b,
                ganador
        );
    }
}