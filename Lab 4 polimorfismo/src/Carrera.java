import java.util.List;

public class Carrera implements Evento {

    private final String codigo;
    private final List<Participante> participantes;
    private final DatosEvento datos = new DatosEvento();

    public Carrera(String codigo, List<Participante> participantes) {
        Reglas.exigir(
                participantes.size() >= 3 && participantes.size() <= 8,
                "La carrera requiere de 3 a 8 participantes."
        );

        long distintos = participantes.stream()
                .map(Participante::codigo)
                .distinct()
                .count();

        Reglas.exigir(
                distintos == participantes.size(),
                "Participantes repetidos."
        );

        this.codigo = Reglas.codigo(codigo);
        this.participantes = List.copyOf(participantes);
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    @Override
    public List<Participante> getParticipantes() {
        return participantes;
    }

    @Override
    public DatosEvento getDatos() {
        return datos;
    }

    @Override
    public String toString() {
        return resumen();
    }
}