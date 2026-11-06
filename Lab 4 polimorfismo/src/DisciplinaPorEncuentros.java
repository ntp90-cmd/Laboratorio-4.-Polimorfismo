import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class DisciplinaPorEncuentros extends Disciplina {

    protected DisciplinaPorEncuentros(
            String nombre,
            int minimo,
            int maximo,
            int cuota
    ) {
        super(nombre, minimo, maximo, cuota);
    }

    @Override
    public List<Evento> generarEventos(
            String torneo,
            List<Participante> participantes
    ) {
        List<Evento> eventos = new ArrayList<>();

        for (int i = 0; i < participantes.size(); i++) {
            for (int j = i + 1; j < participantes.size(); j++) {
                eventos.add(
                        new Encuentro(
                                torneo + "-E" + (eventos.size() + 1),
                                participantes.get(i),
                                participantes.get(j)
                        )
                );
            }
        }

        return eventos;
    }

    @Override
    public DatosResultado leerDatos(
            EntradaConsola entrada,
            Evento evento
    ) {
        List<Participante> participantes = evento.getParticipantes();

        int a = entrada.entero(
                "Goles/puntos/sets de "
                        + participantes.get(0).nombre() + ": "
        );

        int b = entrada.entero(
                "Goles/puntos/sets de "
                        + participantes.get(1).nombre() + ": "
        );

        Map<String, Double> marcas = Map.of(
                participantes.get(0).codigo(), (double) a,
                participantes.get(1).codigo(), (double) b
        );

        return new DatosResultado(
                marcas,
                leerPenales(entrada, participantes, a, b),
                Set.of()
        );
    }

    protected Map<String, Integer> leerPenales(
            EntradaConsola entrada,
            List<Participante> participantes,
            int a,
            int b
    ) {
        return Map.of();
    }

    protected int[] marcador(
            Evento evento,
            DatosResultado datos
    ) {
        List<Participante> participantes = evento.getParticipantes();

        Reglas.exigir(
                participantes.size() == 2
                        && datos.descalificados().isEmpty(),
                "Datos de encuentro invalidos."
        );

        Set<String> codigos = Set.of(
                participantes.get(0).codigo(),
                participantes.get(1).codigo()
        );

        Reglas.exigir(
                datos.marcas().keySet().equals(codigos),
                "Debe ingresar el marcador de ambos participantes, "
                        + "sin participantes adicionales."
        );

        int[] valores = new int[2];

        for (int i = 0; i < 2; i++) {
            double valor = datos.marcas().get(
                    participantes.get(i).codigo()
            );

            Reglas.exigir(
                    Double.isFinite(valor)
                            && valor >= 0
                            && valor <= Integer.MAX_VALUE
                            && valor == Math.rint(valor),
                    "El marcador debe ser un entero no negativo."
            );

            valores[i] = (int) valor;
        }

        return valores;
    }

    protected ResultadoEvaluado resultado(
            Evento evento,
            DatosResultado datos,
            int puntosA,
            int puntosB,
            long diferencia,
            int ganador
    ) {
        List<Participante> participantes = evento.getParticipantes();

        Map<String, AporteTabla> aportes = Map.of(
                participantes.get(0).codigo(),
                new AporteTabla(
                        puntosA,
                        diferencia,
                        ganador == 0 ? 1 : 0
                ),
                participantes.get(1).codigo(),
                new AporteTabla(
                        puntosB,
                        -diferencia,
                        ganador == 1 ? 1 : 0
                )
        );

        return new ResultadoEvaluado(datos, aportes);
    }
}