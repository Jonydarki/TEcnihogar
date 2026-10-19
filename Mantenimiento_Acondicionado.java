public class Mantenimiento_Acondicionado extends OrdenServicio {
    private static final double TARIFA_BASE = 125.00;
    private static final double COSTO_POR_HORA = 110.00;
    private static final double COSTO_REFRIGERANTE = 80.00;
    private static final int BTU_MINIMOS = 9000;
    private static final int BTU_MAXIMOS = 60000;
    private static final int DIAS_ATENCION = 2;
    private static final int MAXIMO_VISITAS = 1;
    private int capacidadBTU;

    public Mantenimiento_Acondicionado(String codigo, String cliente, String direccion, double horasEstimadas, String zona, int capacidadBTU) {
        super(codigo, cliente, direccion, horasEstimadas, validarZona(zona));
        this.capacidadBTU = validarCapacidad(capacidadBTU);
    }

    private static String validarZona(String zona) {
        if (zona == null
                || !zona.equalsIgnoreCase("Metropolitana")) {
            throw new IllegalArgumentException(
                    "El mantenimiento de aire acondicionado "
                    + "solo está disponible en zona metropolitana.");
        }

        return "Metropolitana";
    }

    private static int validarCapacidad(int capacidadBTU) {
        if (capacidadBTU < BTU_MINIMOS
                || capacidadBTU > BTU_MAXIMOS) {
            throw new IllegalArgumentException(
                    "La capacidad debe estar entre 9,000 y 60,000 BTU.");
        }

        return capacidadBTU;
    }

    @Override
    public double calcularCosto() {
        return TARIFA_BASE
                + (COSTO_POR_HORA * getHorasEstimadas())
                + COSTO_REFRIGERANTE;
    }

    @Override
    public int obtenerTiempoAtencion() {
        return DIAS_ATENCION;
    }

    @Override
    public int obtenerMaximoVisitas() {
        return MAXIMO_VISITAS;
    }

    @Override
    public boolean requiereCertificacionRefrigeracion() {
        return true;
    }

    public int getCapacidadBTU() {
        return capacidadBTU;
    }

    @Override
    public String toString() {
        return super.toString()
                + String.format(
                        " | Capacidad: %,d BTU",
                        capacidadBTU);
    }
}
