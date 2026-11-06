public record ResumenEmpresa(
        String empresa,
        int puntos
) implements Comparable<ResumenEmpresa> {

    @Override
    public int compareTo(ResumenEmpresa otra) {
        int orden = Integer.compare(otra.puntos, puntos);

        if (orden == 0) {
            orden = empresa.compareToIgnoreCase(otra.empresa);
        }

        return orden;
    }
}