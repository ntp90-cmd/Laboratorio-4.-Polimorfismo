import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class EntradaConsola {

    private final Scanner scanner = new Scanner(System.in);

    String texto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    int entero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(texto(mensaje));

            } catch (NumberFormatException error) {
                System.out.println("Ingrese un numero entero valido.");
            }
        }
    }

    LocalDate fecha() {
        while (true) {
            try {
                return LocalDate.parse(
                        texto("Fecha (AAAA-MM-DD): ")
                );

            } catch (DateTimeParseException error) {
                System.out.println("Ingrese una fecha valida.");
            }
        }
    }
}