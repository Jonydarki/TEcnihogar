
public class Cuadrilla {
    private String codigo;
    private double capacidadMaximaHoras;
    private boolean certificadaRefrigeracion;

    public Cuadrilla(String codigo, double capacidadMaximaHoras, boolean certificadaRefrigeracion) {

        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El código de la cuadrilla no puede estar vacío.");
        }

        if (!Double.isFinite(capacidadMaximaHoras)
                || capacidadMaximaHoras <= 0) {
            throw new IllegalArgumentException(
                    "La capacidad máxima debe ser un número positivo y finito.");
        }

        this.codigo = codigo.trim();
        this.capacidadMaximaHoras = capacidadMaximaHoras;
        this.certificadaRefrigeracion = certificadaRefrigeracion;
    }

    public boolean puedeAtender(OrdenServicio orden) {
        if (orden == null) {
            return false;
        }

        return !orden.requiereCertificacionRefrigeracion()
                || certificadaRefrigeracion;
    }

    public boolean tieneCapacidad(
            double horasAsignadas,
            double nuevasHoras) {

        if (!Double.isFinite(horasAsignadas)
                || !Double.isFinite(nuevasHoras)
                || horasAsignadas < 0
                || nuevasHoras <= 0) {
            return false;
        }

        return horasAsignadas + nuevasHoras
                <= capacidadMaximaHoras;
    }

    public String getCodigo() {
        return codigo;
    }

    public double getCapacidadMaximaHoras() {
        return capacidadMaximaHoras;
    }

    public boolean isCertificadaRefrigeracion() {
        return certificadaRefrigeracion;
    }

    @Override
    public String toString() {
        return String.format(
                "Código: %s | Capacidad máxima: %.2f horas"
                + " | Certificada en refrigeración: %s",
                codigo,
                capacidadMaximaHoras,
                certificadaRefrigeracion ? "Sí" : "No");
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }

        if (!(objeto instanceof Cuadrilla)) {
            return false;
        }

        Cuadrilla otra = (Cuadrilla) objeto;

        return codigo.equalsIgnoreCase(otra.codigo);
    }

    @Override
    public int hashCode() {
        return codigo.toLowerCase(
                java.util.Locale.ROOT).hashCode();
    }
}