import java.util.List;
import java.util.NoSuchElementException;

public class Principal {

    public static void main(String[] args) {
        ArenaGT sistema = new ArenaGT();
        sistema.cargarDatosIniciales();

        EntradaConsola entrada = new EntradaConsola();

        while (true) {
            System.out.println("""
                    
                    ArenaGT
                    1. Registrar participante
                    2. Crear torneo
                    3. Cotizar inscripciones
                    4. Inscribir participante
                    5. Cerrar inscripciones
                    6. Programar o reprogramar evento
                    7. Registrar resultado
                    8. Consultar torneos y eventos
                    9. Consultar participantes
                    10. Ver tabla de posiciones
                    11. Ver calendario
                    12. Ver ingresos y premios
                    13. Ver ranking de empresas
                    0. Salir
                    """);

            try {
                int opcion = entrada.entero("Opcion: ");

                if (opcion == 0) {
                    break;
                }

                switch (opcion) {
                    case 1 -> sistema.registrarParticipante(
                            new Participante(
                                    entrada.texto("Codigo: "),
                                    entrada.texto("Nombre: "),
                                    entrada.texto("Empresa: ")
                            )
                    );

                    case 2 -> sistema.crearTorneo(
                            entrada.texto("Codigo: "),
                            entrada.texto("Nombre: "),
                            elegirDisciplina(sistema, entrada)
                    );

                    case 3 -> {
                        Disciplina disciplina = elegirDisciplina(
                                sistema,
                                entrada
                        );

                        int cantidad = entrada.entero(
                                "Cantidad de participantes: "
                        );

                        System.out.println(
                                "Cotizacion: "
                                        + Reglas.dinero(
                                                disciplina.cotizar(cantidad)
                                        )
                        );
                    }

                    case 4 -> sistema.inscribir(
                            entrada.texto("Torneo: "),
                            entrada.texto("Participante: ")
                    );

                    case 5 -> sistema
                            .torneo(entrada.texto("Torneo: "))
                            .cerrarInscripciones();

                    case 6 -> {
                        Torneo torneo = sistema.torneo(
                                entrada.texto("Torneo: ")
                        );

                        Evento evento = torneo.evento(
                                entrada.texto("Codigo del evento: ")
                        );

                        Programacion programacion = new Programacion(
                                entrada.fecha(),
                                entrada.entero("Hora (0 a 23): "),
                                entrada.texto("Sede: ")
                        );

                        sistema.programar(evento, programacion);
                    }

                    case 7 -> {
                        Torneo torneo = sistema.torneo(
                                entrada.texto("Torneo: ")
                        );

                        Evento evento = torneo.evento(
                                entrada.texto("Codigo del evento: ")
                        );

                        Reglas.exigir(
                                evento.getEstado() == EstadoEvento.PROGRAMADO,
                                "El evento debe estar programado y sin resultado."
                        );

                        DatosResultado datos = torneo
                                .getDisciplina()
                                .leerDatos(entrada, evento);

                        sistema.registrarResultado(
                                torneo,
                                evento,
                                datos
                        );
                    }

                    case 8 -> sistema.mostrarTorneos();

                    case 9 -> sistema.participantes()
                            .forEach(System.out::println);

                    case 10 -> mostrarTabla(
                            sistema.torneo(
                                    entrada.texto("Torneo: ")
                            )
                    );

                    case 11 -> {
                        int tipo = entrada.entero(
                                "1. General  2. Torneo  3. Participante: "
                        );

                        Calendario calendario = switch (tipo) {
                            case 1 -> sistema.calendario();

                            case 2 -> new Calendario(
                                    sistema.torneo(
                                            entrada.texto("Torneo: ")
                                    ).getEventos()
                            );

                            case 3 -> sistema.calendarioParticipante(
                                    entrada.texto("Participante: ")
                            );

                            default -> throw new IllegalArgumentException(
                                    "Opcion invalida."
                            );
                        };

                        for (Evento evento : calendario) {
                            System.out.println(evento);
                        }
                    }

                    case 12 -> sistema.mostrarFinanzas();

                    case 13 -> sistema.ranking().forEach(
                            empresa -> System.out.println(
                                    empresa.empresa()
                                            + ": " + empresa.puntos()
                                            + " puntos"
                            )
                    );

                    default -> throw new IllegalArgumentException(
                            "Opcion invalida."
                    );
                }

            } catch (IllegalArgumentException | IllegalStateException error) {
                System.out.println(
                        "No se pudo realizar la operacion: "
                                + error.getMessage()
                );

            } catch (NoSuchElementException fin) {
                break;
            }
        }

        System.out.println("Programa finalizado.");
    }

    private static Disciplina elegirDisciplina(
            ArenaGT sistema,
            EntradaConsola entrada
    ) {
        List<Disciplina> lista = sistema.disciplinas();

        for (int i = 0; i < lista.size(); i++) {
            System.out.println(
                    (i + 1) + ". " + lista.get(i).getNombre()
            );
        }

        int opcion = entrada.entero("Disciplina: ");

        Reglas.exigir(
                opcion >= 1 && opcion <= lista.size(),
                "Disciplina invalida."
        );

        return lista.get(opcion - 1);
    }

    static void mostrarTabla(Torneo torneo) {
        System.out.println("\n" + torneo);
        System.out.println("Participante | Puntos | Diferencia | Victorias");

        for (Posicion posicion : torneo.tabla()) {
            System.out.printf(
                    "%s | %d | %d | %d%n",
                    posicion.getParticipante().nombre(),
                    posicion.getPuntos(),
                    posicion.getDiferencia(),
                    posicion.getVictorias()
            );
        }

        torneo.campeon().ifPresent(
                participante -> System.out.println(
                        "Campeon: " + participante.nombre()
                                + " | Premio del torneo: "
                                + Reglas.dinero(torneo.premio())
                )
        );
    }
}