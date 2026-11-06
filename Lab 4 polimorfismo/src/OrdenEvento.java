import java.util.Comparator;

public class OrdenEvento implements Comparator<Evento> {

    @Override
    public int compare(Evento a, Evento b) {
        Programacion primera = a.getProgramacion().orElseThrow();
        Programacion segunda = b.getProgramacion().orElseThrow();

        int orden = primera.fecha().compareTo(segunda.fecha());

        if (orden == 0) {
            orden = Integer.compare(
                    primera.hora(),
                    segunda.hora()
            );
        }

        if (orden == 0) {
            orden = primera.sede().compareToIgnoreCase(
                    segunda.sede()
            );
        }

        return orden;
    }
}