import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Torneo {

    private final String codigo;
    private final String nombre;
    private final Disciplina disciplina;

    private EstadoTorneo estado = EstadoTorneo.INSCRIPCION;

    private final Map<String, Posicion> posiciones = new LinkedHashMap<>();
    private List<Evento> eventos = List.of();

    public Torneo(
            String codigo,
            String nombre,
            Disciplina disciplina
    ) {
        this.codigo = Reglas.codigo(codigo);
        this.nombre = Reglas.texto(nombre);
        this.disciplina = Objects.requireNonNull(disciplina);
    }

    public String getCodigo() {
        return codigo;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public EstadoTorneo getEstado() {
        return estado;
    }

    public List<Evento> getEventos() {
        return List.copyOf(eventos);
    }

    public List<Participante> participantes() {
        return posiciones.values()
                .stream()
                .map(Posicion::getParticipante)
                .toList();
    }

    void inscribir(Participante participante) {
        Reglas.exigir(
                estado == EstadoTorneo.INSCRIPCION,
                "Las inscripciones estan cerradas."
        );

        Reglas.exigir(
                !posiciones.containsKey(participante.codigo()),
                "El participante ya esta inscrito."
        );

        long mismaEmpresa = participantes().stream()
                .filter(p -> p.empresa().equalsIgnoreCase(
                        participante.empresa()
                ))
                .count();

        Reglas.exigir(
                mismaEmpresa < 2,
                "La empresa ya tiene dos participantes en este torneo."
        );

        disciplina.validarInscripcion(
                participante,
                participantes()
        );

        posiciones.put(
                participante.codigo(),
                new Posicion(participante)
        );
    }

    void cerrarInscripciones() {
        Reglas.exigir(
                estado == EstadoTorneo.INSCRIPCION,
                "Las inscripciones ya estan cerradas."
        );

        Reglas.exigir(
                posiciones.size() >= disciplina.getMinimo(),
                "No se alcanzo el minimo de inscritos."
        );

        List<Evento> nuevos = disciplina.generarEventos(
                codigo,
                participantes()
        );

        eventos = List.copyOf(nuevos);
        estado = EstadoTorneo.EN_JUEGO;
    }

    public Evento evento(String codigoEvento) {
        String clave = Reglas.codigo(codigoEvento);

        return eventos.stream()
                .filter(evento -> evento.getCodigo().equals(clave))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Evento inexistente en este torneo."
                ));
    }

    void registrarResultado(
            Evento evento,
            DatosResultado datos
    ) {
        Reglas.exigir(
                estado == EstadoTorneo.EN_JUEGO
                        && eventos.contains(evento),
                "Evento o torneo invalido."
        );

        Reglas.exigir(
                evento.getEstado() == EstadoEvento.PROGRAMADO,
                "El evento no admite resultados."
        );

        // Primero se valida y calcula el resultado completo.
        ResultadoEvaluado resultado = disciplina.evaluarResultado(
                evento,
                datos
        );

        Set<String> codigos = new HashSet<>();

        for (Participante participante : evento.getParticipantes()) {
            codigos.add(participante.codigo());
        }

        Reglas.exigir(
                resultado.aportes().keySet().equals(codigos),
                "Aportes incompletos."
        );

        // La tabla cambia solamente despues de validar.
        evento.registrarResultado(resultado);

        resultado.aportes().forEach(
                (codigoParticipante, aporte) ->
                        posiciones.get(codigoParticipante).acumular(aporte)
        );

        boolean todosFinalizados = eventos.stream()
                .allMatch(e -> e.getEstado() == EstadoEvento.FINALIZADO);

        if (todosFinalizados) {
            estado = EstadoTorneo.FINALIZADO;
        }
    }

    public List<Posicion> tabla() {
        return posiciones.values()
                .stream()
                .sorted()
                .toList();
    }

    public Optional<Participante> campeon() {
        if (estado == EstadoTorneo.FINALIZADO) {
            return Optional.of(
                    tabla().get(0).getParticipante()
            );
        }

        return Optional.empty();
    }

    public BigDecimal premio() {
        return disciplina.cotizar(posiciones.size())
                .multiply(new BigDecimal("0.50"));
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre
                + " | " + disciplina.getNombre()
                + " | " + estado;
    }
}