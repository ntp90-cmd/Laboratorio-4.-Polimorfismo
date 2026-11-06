import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Calendario implements Iterable<Evento> {

    private final List<Evento> eventos;

    public Calendario(Collection<Evento> eventos) {
        this.eventos = eventos.stream()
                .filter(evento -> evento.getProgramacion().isPresent())
                .sorted(new OrdenEvento())
                .toList();
    }

    @Override
    public Iterator<Evento> iterator() {
        return eventos.iterator();
    }
}