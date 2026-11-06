import java.util.List;

public class Encuentro implements Evento {

    private final String codigo;
    private final List<Participante> participantes;
    private final DatosEvento datos = new DatosEvento();

    public Encuentro(
            String codigo,
            Participante primero,
            Participante segundo
    ) {
        Reglas.exigir(
                !primero.codigo().equals(segundo.codigo()),
                "Participantes repetidos."
        );

        this.codigo = Reglas.codigo(codigo);
        participantes = List.of(primero, segundo);
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