import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ArenaGT {

    private final Map<String, Participante> participantes =
            new LinkedHashMap<>();

    private final Map<String, Torneo> torneos =
            new LinkedHashMap<>();

    private final List<MovimientoEconomico> movimientos =
            new ArrayList<>();

    private final List<Disciplina> disciplinas = List.of(
            new FutbolSala(),
            new FutbolSalaConPenales(),
            new Baloncesto(),
            new Voleibol(),
            new Relevos()
    );

    public List<Disciplina> disciplinas() {
        return disciplinas;
    }

    public List<Participante> participantes() {
        return List.copyOf(participantes.values());
    }

    public List<MovimientoEconomico> movimientos() {
        return List.copyOf(movimientos);
    }

    public Torneo torneo(String codigo) {
        Torneo torneo = torneos.get(
                Reglas.codigo(codigo)
        );

        Reglas.exigir(
                torneo != null,
                "Torneo inexistente."
        );

        return torneo;
    }

    public void registrarParticipante(Participante participante) {
        Reglas.exigir(
                !participantes.containsKey(participante.codigo()),
                "Codigo de participante repetido."
        );

        participantes.put(
                participante.codigo(),
                participante
        );
    }

    public void crearTorneo(
            String codigo,
            String nombre,
            Disciplina disciplina
    ) {
        Torneo nuevo = new Torneo(
                codigo,
                nombre,
                disciplina
        );

        Reglas.exigir(
                !torneos.containsKey(nuevo.getCodigo()),
                "Codigo de torneo repetido."
        );

        torneos.put(nuevo.getCodigo(), nuevo);
    }

    public void inscribir(
            String codigoTorneo,
            String codigoParticipante
    ) {
        Torneo torneo = torneo(codigoTorneo);

        Participante participante = participantes.get(
                Reglas.codigo(codigoParticipante)
        );

        Reglas.exigir(
                participante != null,
                "Participante inexistente."
        );

        // Si la inscripcion es rechazada, no se registra el cobro.
        torneo.inscribir(participante);

        movimientos.add(
                new MovimientoEconomico(
                        TipoMovimiento.INSCRIPCION,
                        torneo.getCodigo(),
                        torneo.getDisciplina().getNombre(),
                        participante.codigo(),
                        torneo.getDisciplina().getCuota()
                )
        );
    }

    private List<Evento> eventos() {
        return torneos.values()
                .stream()
                .flatMap(torneo -> torneo.getEventos().stream())
                .toList();
    }

    public void programar(Evento evento, Programacion nueva) {
        Reglas.exigir(
                eventos().contains(evento),
                "Evento no registrado."
        );

        Reglas.exigir(
                evento.getEstado() != EstadoEvento.FINALIZADO,
                "El evento ya finalizo."
        );

        Set<String> codigos = new HashSet<>();

        for (Participante participante : evento.getParticipantes()) {
            codigos.add(participante.codigo());
        }

        for (Evento otro : eventos()) {
            if (otro == evento || otro.getProgramacion().isEmpty()) {
                continue;
            }

            Programacion anterior = otro.getProgramacion().orElseThrow();

            boolean mismaFecha = anterior.fecha().equals(nueva.fecha());
            boolean mismaHora = anterior.hora() == nueva.hora();

            if (!mismaFecha || !mismaHora) {
                continue;
            }

            Reglas.exigir(
                    !anterior.sede().equalsIgnoreCase(nueva.sede()),
                    "La sede ya esta ocupada."
            );

            boolean participanteOcupado = otro.getParticipantes()
                    .stream()
                    .anyMatch(p -> codigos.contains(p.codigo()));

            Reglas.exigir(
                    !participanteOcupado,
                    "Un participante ya tiene otro evento en esa fecha y hora."
            );
        }

        evento.programar(nueva);
    }

    public void registrarResultado(
            Torneo torneo,
            Evento evento,
            DatosResultado datos
    ) {
        Reglas.exigir(
                torneos.get(torneo.getCodigo()) == torneo,
                "Torneo no registrado."
        );

        torneo.registrarResultado(evento, datos);

        if (torneo.getEstado() == EstadoTorneo.FINALIZADO) {
            movimientos.add(
                    new MovimientoEconomico(
                            TipoMovimiento.PREMIO,
                            torneo.getCodigo(),
                            torneo.getDisciplina().getNombre(),
                            torneo.campeon().orElseThrow().codigo(),
                            torneo.premio()
                    )
            );
        }
    }

    public Calendario calendario() {
        return new Calendario(eventos());
    }

    public Calendario calendarioParticipante(String codigo) {
        String clave = Reglas.codigo(codigo);

        Reglas.exigir(
                participantes.containsKey(clave),
                "Participante inexistente."
        );

        List<Evento> encontrados = eventos().stream()
                .filter(evento -> evento.getParticipantes()
                        .stream()
                        .anyMatch(p -> p.codigo().equals(clave)))
                .toList();

        return new Calendario(encontrados);
    }

    public BigDecimal ingresos() {
        return movimientos.stream()
                .filter(m -> m.tipo() == TipoMovimiento.INSCRIPCION)
                .map(MovimientoEconomico::monto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<ResumenEmpresa> ranking() {
        Map<String, Integer> puntos = new TreeMap<>(
                String.CASE_INSENSITIVE_ORDER
        );

        for (Participante participante : participantes.values()) {
            puntos.putIfAbsent(participante.empresa(), 0);
        }

        for (Torneo torneo : torneos.values()) {
            for (Posicion posicion : torneo.tabla()) {
                puntos.merge(
                        posicion.getParticipante().empresa(),
                        posicion.getPuntos(),
                        Integer::sum
                );
            }
        }

        return puntos.entrySet()
                .stream()
                .map(e -> new ResumenEmpresa(e.getKey(), e.getValue()))
                .sorted()
                .toList();
    }

    public void mostrarTorneos() {
        for (Torneo torneo : torneos.values()) {
            System.out.println("\n" + torneo);

            for (Participante participante : torneo.participantes()) {
                System.out.println("  Inscrito: " + participante);
            }

            for (Evento evento : new Calendario(torneo.getEventos())) {
                System.out.println(evento);
            }

            // Los eventos sin horario se muestran al final.
            torneo.getEventos()
                    .stream()
                    .filter(evento -> evento.getProgramacion().isEmpty())
                    .forEach(System.out::println);
        }
    }

    public void mostrarFinanzas() {
        System.out.println(
                "Ingresos totales: " + Reglas.dinero(ingresos())
        );

        for (Disciplina disciplina : disciplinas) {
            BigDecimal total = movimientos.stream()
                    .filter(m -> m.tipo() == TipoMovimiento.INSCRIPCION
                            && m.disciplina().equals(disciplina.getNombre()))
                    .map(MovimientoEconomico::monto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            System.out.println(
                    disciplina.getNombre() + ": " + Reglas.dinero(total)
            );
        }

        BigDecimal premios = BigDecimal.ZERO;

        for (MovimientoEconomico movimiento : movimientos) {
            if (movimiento.tipo() == TipoMovimiento.PREMIO) {
                System.out.println(
                        "Premio " + movimiento.torneo()
                                + " a " + movimiento.participante()
                                + ": " + Reglas.dinero(movimiento.monto())
                );

                premios = premios.add(movimiento.monto());
            }
        }

        System.out.println(
                "Total de premios entregados: " + Reglas.dinero(premios)
        );
    }

    void cargarDatosIniciales() {
        Reglas.exigir(
                participantes.isEmpty() && torneos.isEmpty(),
                "Los datos iniciales ya se cargaron."
        );

        String[] nombres = {
                "Aguilas", "Toros", "Halcones", "Leones",
                "Lobos", "Tigres", "Pumas", "Jaguares"
        };

        String[] empresas = {
                "Norte", "Norte", "Centro", "Centro",
                "Sur", "Sur", "Este", "Este"
        };

        for (int i = 0; i < nombres.length; i++) {
            registrarParticipante(
                    new Participante(
                            "P" + (i + 1),
                            nombres[i],
                            empresas[i]
                    )
            );
        }

        int turno = 0;

        for (int i = 0; i < 6; i++) {
            String codigo = "T" + (i + 1);

            crearTorneo(
                    codigo,
                    "Copa " + (i + 1),
                    disciplinas.get(Math.min(i, 4))
            );

            Torneo torneo = torneo(codigo);

            int cantidad = i == 0 ? 6 : i == 4 ? 8 : 4;

            // Carga directa: no genera ingresos.
            for (int j = 1; j <= cantidad; j++) {
                torneo.inscribir(participantes.get("P" + j));
            }

            torneo.cerrarInscripciones();

            for (Evento evento : torneo.getEventos()) {
                LocalDate fecha = LocalDate.of(2026, 11, 6)
                        .plusDays(turno / 10);

                programar(
                        evento,
                        new Programacion(
                                fecha,
                                8 + turno % 10,
                                "Arena central"
                        )
                );

                turno++;
            }
        }

        // Desempates por diferencia y por victorias.
        ejemplo("T1", "P1", "P2", 1, 0, Map.of());
        ejemplo("T1", "P3", "P4", 3, 0, Map.of());
        ejemplo("T1", "P5", "P6", 1, 1, Map.of());
        ejemplo("T1", "P5", "P2", 1, 1, Map.of());
        ejemplo("T1", "P5", "P4", 1, 1, Map.of());
        ejemplo("T1", "P1", "P6", 0, 1, Map.of());

        // Futbol con penales.
        ejemplo(
                "T2", "P1", "P2", 2, 2,
                Map.of("P1", 4, "P2", 3)
        );

        ejemplo("T2", "P3", "P4", 3, 1, Map.of());

        // Baloncesto.
        ejemplo("T3", "P1", "P2", 60, 50, Map.of());

        // Voleibol.
        ejemplo("T4", "P1", "P2", 3, 0, Map.of());
        ejemplo("T4", "P1", "P3", 3, 1, Map.of());
        ejemplo("T4", "P1", "P4", 3, 2, Map.of());

        // Relevos con ocho lugares.
        Map<String, Double> tiempos = new HashMap<>();

        for (int i = 1; i <= 8; i++) {
            tiempos.put("P" + i, 49.0 + i);
        }

        Torneo relevos = torneo("T5");

        relevos.registrarResultado(
                relevos.getEventos().get(0),
                new DatosResultado(tiempos, Map.of(), Set.of())
        );

        // Otra carrera para mostrar una descalificacion.
        relevos = torneo("T6");

        relevos.registrarResultado(
                relevos.getEventos().get(0),
                new DatosResultado(
                        Map.of(
                                "P1", 50.1,
                                "P2", 51.9,
                                "P3", 52.4
                        ),
                        Map.of(),
                        Set.of("P4")
                )
        );
    }

    private void ejemplo(
            String codigo,
            String a,
            String b,
            int x,
            int y,
            Map<String, Integer> penales
    ) {
        Torneo torneo = torneo(codigo);

        Evento evento = torneo.getEventos()
                .stream()
                .filter(e -> e.getParticipantes().size() == 2
                        && e.getParticipantes()
                                .stream()
                                .allMatch(p -> p.codigo().equals(a)
                                        || p.codigo().equals(b)))
                .findFirst()
                .orElseThrow();

        // Se carga sin registrar premios.
        torneo.registrarResultado(
                evento,
                new DatosResultado(
                        Map.of(a, (double) x, b, (double) y),
                        penales,
                        Set.of()
                )
        );
    }
}