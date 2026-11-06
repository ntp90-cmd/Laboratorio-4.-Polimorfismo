import java.time.LocalDate;

public record Programacion(
        LocalDate fecha,
        int hora,
        String sede
) {

    public Programacion {
        Reglas.exigir(fecha != null, "Fecha obligatoria.");
        Reglas.exigir(hora >= 0 && hora <= 23, "Hora fuera de rango.");

        sede = Reglas.texto(sede);
    }

    @Override
    public String toString() {
        return String.format("%s %02d:00 - %s", fecha, hora, sede);
    }
}