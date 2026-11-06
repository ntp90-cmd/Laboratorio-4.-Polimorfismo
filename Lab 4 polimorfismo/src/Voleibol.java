public class Voleibol extends DisciplinaPorEncuentros {

    public Voleibol() {
        super("Voleibol", 4, 6, 450);
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
                datos.penales().isEmpty()
                        && Math.max(a, b) == 3
                        && Math.min(a, b) <= 2,
                "Resultado valido: 3-0, 3-1 o 3-2, en cualquier orden."
        );

        boolean cerrado = Math.min(a, b) == 2;

        int puntosGanador = cerrado ? 2 : 3;
        int puntosPerdedor = cerrado ? 1 : 0;

        return resultado(
                evento,
                datos,
                a > b ? puntosGanador : puntosPerdedor,
                b > a ? puntosGanador : puntosPerdedor,
                a - b,
                a > b ? 0 : 1
        );
    }
}