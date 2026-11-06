public record Participante(
        String codigo,
        String nombre,
        String empresa
) {

    public Participante {
        codigo = Reglas.codigo(codigo);
        nombre = Reglas.texto(nombre);
        empresa = Reglas.texto(empresa);
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (" + empresa + ")";
    }
}