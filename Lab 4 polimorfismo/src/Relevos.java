import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Relevos extends Disciplina {

    public Relevos() {
        super("Relevos", 3, 8, 300);
    }

    @Override
    public List<Evento> generarEventos(
            String torneo,
            List<Participante> participantes
    ) {
        return List.of(
                new Carrera(torneo + "-E1", participantes)
        );
    }

    @Override
    public DatosResultado leerDatos(
            EntradaConsola entrada,
            Evento evento
    ) {
        Map<String, Double> tiempos = new LinkedHashMap<>();
        Set<String> descalificados = new HashSet<>();

        for (Participante participante : evento.getParticipantes()) {
            while (true) {
                String texto = entrada.texto(
                        "Segundos de " + participante.nombre()
                                + " o D si fue descalificado: "
                );

                if (texto.equalsIgnoreCase("D")) {
                    descalificados.add(participante.codigo());
                    break;
                }

                try {
                    tiempos.put(
                            participante.codigo(),
                            Double.parseDouble(texto)
                    );

                    break;

                } catch (NumberFormatException error) {
                    System.out.println("Ingrese un numero o D.");
                }
            }
        }

        return new DatosResultado(
                tiempos,
                Map.of(),
                descalificados
        );
    }

    @Override
    public ResultadoEvaluado evaluarResultado(
            Evento evento,
            DatosResultado datos
    ) {
        Set<String> inscritos = new HashSet<>();

        for (Participante participante : evento.getParticipantes()) {
            inscritos.add(participante.codigo());
        }

        Set<String> recibidos = new HashSet<>(
                datos.marcas().keySet()
        );

        recibidos.addAll(datos.descalificados());

        Reglas.exigir(
                datos.penales().isEmpty()
                        && recibidos.equals(inscritos)
                        && Collections.disjoint(
                                datos.marcas().keySet(),
                                datos.descalificados()
                        ),
                "Cada inscrito debe tener un tiempo o una descalificacion, "
                        + "sin datos adicionales."
        );

        Set<Double> tiempos = new HashSet<>();

        for (double tiempo : datos.marcas().values()) {
            Reglas.exigir(
                    Double.isFinite(tiempo)
                            && tiempo > 0
                            && tiempos.add(tiempo),
                    "Los tiempos deben ser positivos, finitos y distintos."
            );
        }

        List<String> orden = new ArrayList<>(
                datos.marcas().keySet()
        );

        orden.sort(
                Comparator.comparingDouble(datos.marcas()::get)
        );

        int[] puntos = {10, 8, 6, 5, 4, 3, 2, 1};

        Map<String, AporteTabla> aportes = new HashMap<>();

        for (int i = 0; i < orden.size(); i++) {
            aportes.put(
                    orden.get(i),
                    new AporteTabla(
                            puntos[i],
                            0,
                            i == 0 ? 1 : 0
                    )
            );
        }

        for (String codigo : datos.descalificados()) {
            aportes.put(
                    codigo,
                    new AporteTabla(0, 0, 0)
            );
        }

        return new ResultadoEvaluado(datos, aportes);
    }
}