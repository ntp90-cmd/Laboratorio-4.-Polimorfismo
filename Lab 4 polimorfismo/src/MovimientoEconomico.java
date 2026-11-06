import java.math.BigDecimal;

public record MovimientoEconomico(
        TipoMovimiento tipo,
        String torneo,
        String disciplina,
        String participante,
        BigDecimal monto
) {
}