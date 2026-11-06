import java.math.BigDecimal;
import java.util.Locale;

public class Reglas {

    static void exigir(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    static String texto(String valor) {
        exigir(
                valor != null && !valor.isBlank(),
                "El texto no puede estar vacio."
        );

        return valor.trim().replaceAll("\\s+", " ");
    }

    static String codigo(String valor) {
        return texto(valor).toUpperCase(Locale.ROOT);
    }

    static String dinero(BigDecimal monto) {
        return "Q" + monto.setScale(2).toPlainString();
    }
}