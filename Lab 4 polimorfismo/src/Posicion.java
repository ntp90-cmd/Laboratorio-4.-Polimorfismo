public class Posicion implements Comparable<Posicion> {

    private final Participante participante;

    private int puntos;
    private long diferencia;
    private int victorias;

    public Posicion(Participante participante) {
        this.participante = participante;
    }

    public Participante getParticipante() {
        return participante;
    }

    public int getPuntos() {
        return puntos;
    }

    public long getDiferencia() {
        return diferencia;
    }

    public int getVictorias() {
        return victorias;
    }

    void acumular(AporteTabla aporte) {
        puntos += aporte.puntos();
        diferencia += aporte.diferencia();
        victorias += aporte.victorias();
    }

    @Override
    public int compareTo(Posicion otra) {
        int orden = Integer.compare(otra.puntos, puntos);

        if (orden == 0) {
            orden = Long.compare(otra.diferencia, diferencia);
        }

        if (orden == 0) {
            orden = Integer.compare(otra.victorias, victorias);
        }

        if (orden == 0) {
            orden = participante.nombre().compareToIgnoreCase(
                    otra.participante.nombre()
            );
        }

        return orden;
    }
}