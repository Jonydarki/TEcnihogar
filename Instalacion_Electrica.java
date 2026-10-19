public class Instalacion_Electrica extends Instalacion {
    private static final double COSTO_CERTIFICADO = 120.00;
    private static final double PORCENTAJE_MATERIALES = 0.05;
    private static final int DIAS_ADICIONALES = 2;
    private static final int MAXIMO_VISITAS = 2;
    private double valorMateriales;

    public Instalacion_Electrica(String codigo, String cliente, String direccion, double horasEstimadas, String zona, int puntos, double valorMateriales) {
        super(codigo, cliente, direccion, horasEstimadas, zona, puntos);
        this.valorMateriales = validarValorMateriales(valorMateriales);
    }

    private static double validarValorMateriales(double valor) {
        if (!Double.isFinite(valor) || valor < 0) {
            throw new IllegalArgumentException(
                    "El valor de los materiales no puede ser negativo "
                    + "ni un número no finito.");
        }
        return valor;
    }

    @Override
    public double calcularCosto() {
        double costoInstalacion = calcularCostoInstalacion();
        double costoMateriales = valorMateriales * PORCENTAJE_MATERIALES;
        return costoInstalacion
                + COSTO_CERTIFICADO
                + costoMateriales;
    }

    @Override
    public int obtenerTiempoAtencion() {
        return obtenerDiasInstalacion() + DIAS_ADICIONALES;
    }

    @Override
    public int obtenerMaximoVisitas() {
        return MAXIMO_VISITAS;
    }

    public double getValorMateriales() {
        return valorMateriales;
    }

    @Override
    public String toString() {
        return super.toString()
                + String.format(
                        " | Valor de materiales: Q%.2f"
                        + " | Certificado: Q%.2f",
                        valorMateriales,
                        COSTO_CERTIFICADO);
    }
}
