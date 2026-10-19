
public class Instalacion extends OrdenServicio {
    private static final double TARIFA_METROPOLITANA = 100.00;
    private static final double TARIFA_PERIFERICA = 175.00;
    private static final double COSTO_POR_HORA = 90.00;
    private static final double HORAS_POR_PUNTO = 0.75;
    private static final int DIAS_METROPOLITANA = 3;
    private static final int DIAS_PERIFERICA = 5;
    private static final int MAXIMO_VISITAS = 3;
    private int puntos;

    public Instalacion(String codigo, String cliente, String direccion, double horasEstimadas, String zona, int puntos) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        this.puntos = validarPuntos(puntos);
    }

    private static int validarPuntos(int puntos) {
        if (puntos <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de puntos debe ser un entero positivo.");
        }
        return puntos;
    }

    protected int calcularHorasFacturables() {
        double horasPorPuntos = puntos * HORAS_POR_PUNTO;

        double horasFacturables = Math.max(
                getHorasEstimadas(),
                horasPorPuntos);
        return (int) Math.ceil(horasFacturables);
    }

    protected double calcularCostoInstalacion() {
        double tarifaBase;
        if (getZona().equals("Metropolitana")) {
            tarifaBase = TARIFA_METROPOLITANA;
        } else {
            tarifaBase = TARIFA_PERIFERICA;
        }
        return tarifaBase + calcularHorasFacturables() * COSTO_POR_HORA;
    }

    protected int obtenerDiasInstalacion() {
        if (getZona().equals("Metropolitana")) {
            return DIAS_METROPOLITANA;
        }
        return DIAS_PERIFERICA;
    }

    @Override
    public double calcularCosto() {
        return calcularCostoInstalacion();
    }

    @Override
    public int obtenerTiempoAtencion() {
        return obtenerDiasInstalacion();
    }

    @Override
    public int obtenerMaximoVisitas() {
        return MAXIMO_VISITAS;
    }

    public int getPuntos() {
        return puntos;
    }

    @Override
    public String toString() {
        return super.toString()
                + String.format(" | Puntos: %d"
                + " | Horas facturables: %d",
                puntos,
                calcularHorasFacturables());
    }
}
