public class Diagnostico extends OrdenServicio {
    private static final double TARIFA_METROPOLITANA = 150.00;
    private static final double TARIFA_PERIFERICA = 225.00;
    private static final double MAXIMO_HORAS = 1.5;
    private static final int DIAS_METROPOLITANA = 1;
    private static final int DIAS_PERIFERICA = 2;
    private static final int MAXIMO_VISITAS = 3;

    public Diagnostico(String codigo, String cliente, String direccion, double horasEstimadas, String zona) {
        super(codigo, cliente, direccion, validarHoras(horasEstimadas), zona);
    }

    private static double validarHoras(double horas) {
        if (!Double.isFinite(horas) || horas <= 0 || horas > MAXIMO_HORAS) {
            throw new IllegalArgumentException(
                    "Un diagnóstico debe tener horas estimadas "
                    + "mayores que cero y no superiores a 1.5.");
        }
        return horas;
    }

    @Override
    public double calcularCosto() {
        if (getZona().equals("Metropolitana")) {
            return TARIFA_METROPOLITANA;
        }
        return TARIFA_PERIFERICA;
    }

    @Override
    public int obtenerTiempoAtencion() {
        if (getZona().equals("Metropolitana")) {
            return DIAS_METROPOLITANA;
        }
        return DIAS_PERIFERICA;
    }

    @Override
    public int obtenerMaximoVisitas() {
        return MAXIMO_VISITAS;
    }
}
