
public abstract class OrdenServicio {
    public static final String PENDIENTE = "Pendiente";
    public static final String EN_ATENCION = "En atención";
    public static final String COMPLETADA = "Completada";
    public static final String CANCELADA = "Cancelada";

    private String codigo;
    private String cliente;
    private String direccion;
    private double horasEstimadas;
    private String zona;
    private String estado;
    private int visitas;

    public OrdenServicio(String codigo, String cliente, String direccion, double horasEstimadas, String zona) {

        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El código de la orden no puede estar vacío.");
        }

        if (cliente == null || cliente.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre del cliente no puede estar vacío.");
        }

        if (direccion == null || direccion.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La dirección no puede estar vacía.");
        }

        if (!Double.isFinite(horasEstimadas) || horasEstimadas <= 0) {
            throw new IllegalArgumentException(
                    "Las horas estimadas deben ser un número positivo y finito.");
        }

        if (zona == null
                || (!zona.equalsIgnoreCase("Metropolitana")
                && !zona.equalsIgnoreCase("Periferica")
                && !zona.equalsIgnoreCase("Periférica"))) {
            throw new IllegalArgumentException(
                    "La zona debe ser Metropolitana o Periférica.");
        }

        this.codigo = codigo.trim();
        this.cliente = cliente.trim();
        this.direccion = direccion.trim();
        this.horasEstimadas = horasEstimadas;

        if (zona.equalsIgnoreCase("Metropolitana")) {
            this.zona = "Metropolitana";
        } else {
            this.zona = "Periférica";
        }

        this.estado = PENDIENTE;
        this.visitas = 0;
    }

    public abstract double calcularCosto();

    public abstract int obtenerTiempoAtencion();

    public abstract int obtenerMaximoVisitas();

    public boolean requiereCertificacionRefrigeracion() {
        return false;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCliente() {
        return cliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public double getHorasEstimadas() {
        return horasEstimadas;
    }

    public String getZona() {
        return zona;
    }

    public String getEstado() {
        return estado;
    }

    public int getVisitas() {
        return visitas;
    }

    public void iniciarAtencion() {
        if (!estado.equals(PENDIENTE)) {
            throw new IllegalStateException(
                    "Solo se puede iniciar una orden pendiente.");
        }

        if (visitas >= obtenerMaximoVisitas()) {
            estado = CANCELADA;
            throw new IllegalStateException(
                    "La orden agotó su límite de visitas.");
        }
        estado = EN_ATENCION;
    }

    public void registrarResultadoVisita(boolean exitosa) {
        if (!estado.equals(EN_ATENCION)) {
            throw new IllegalStateException(
                    "La orden debe estar en atención para registrar el resultado.");
        }

        visitas++;

        if (exitosa) {
            estado = COMPLETADA;
        } else if (visitas >= obtenerMaximoVisitas()) {
            estado = CANCELADA;
        } else {
            estado = PENDIENTE;
        }
    }

    @Override
    public String toString() {
        return String.format(
                "Código: %s | Cliente: %s | Dirección: %s"
                + " | Horas estimadas: %.2f | Zona: %s"
                + " | Estado: %s | Visitas: %d/%d",
                codigo,
                cliente,
                direccion,
                horasEstimadas,
                zona,
                estado,
                visitas,
                obtenerMaximoVisitas());
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }

        if (!(objeto instanceof OrdenServicio)) {
            return false;
        }

        OrdenServicio otra = (OrdenServicio) objeto;
        return codigo.equalsIgnoreCase(otra.codigo);
    }

    @Override
    public int hashCode() {
        return codigo.toLowerCase(java.util.Locale.ROOT).hashCode();
    }
}
