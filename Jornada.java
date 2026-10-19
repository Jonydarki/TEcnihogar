
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Jornada {
    public static final String EN_PREPARACION = "En preparación";
    public static final String EN_CURSO = "En curso";
    public static final String CERRADA = "Cerrada";
    private static final double CARGO_VISITA_ADICIONAL = 40.00;
    private Cuadrilla cuadrilla;
    private List<OrdenServicio> ordenes;
    private String estado;
    private Map<String, Boolean> resultados;
    private double ingresosVisitasAdicionales;

    public Jornada(Cuadrilla cuadrilla) {
        if (cuadrilla == null) {
            throw new IllegalArgumentException(
                    "La cuadrilla no puede ser nula.");
        }

        this.cuadrilla = cuadrilla;
        this.ordenes = new ArrayList<>();
        this.resultados = new HashMap<>();
        this.estado = EN_PREPARACION;
        this.ingresosVisitasAdicionales = 0.0;
    }

    public void agregarOrden(OrdenServicio orden) {
        verificarPreparacion();

        if (orden == null) {
            throw new IllegalArgumentException(
                    "La orden no puede ser nula.");
        }

        if (!orden.getEstado().equals(OrdenServicio.PENDIENTE)) {
            throw new IllegalStateException(
                    "Solo se pueden asignar órdenes pendientes.");
        }

        if (ordenes.contains(orden)) {
            throw new IllegalArgumentException(
                    "La orden ya está asignada a esta jornada.");
        }

        if (!cuadrilla.puedeAtender(orden)) {
            throw new IllegalArgumentException(
                    "La cuadrilla no cumple los requisitos "
                    + "de certificación de esta orden.");
        }

        if (!cuadrilla.tieneCapacidad(
                getHorasAsignadas(),
                orden.getHorasEstimadas())) {
            throw new IllegalArgumentException(
                    "La asignación superaría la capacidad "
                    + "máxima de la cuadrilla.");
        }

        ordenes.add(orden);
    }

    public OrdenServicio retirarOrden(String codigo) {
        verificarPreparacion();

        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El código de la orden no puede estar vacío.");
        }

        for (int i = 0; i < ordenes.size(); i++) {
            OrdenServicio orden = ordenes.get(i);

            if (orden.getCodigo().equalsIgnoreCase(codigo.trim())) {
                ordenes.remove(i);
                return orden;
            }
        }

        throw new IllegalArgumentException(
                "La orden no está asignada a esta jornada.");
    }

    public double iniciarJornada() {
        verificarPreparacion();

        if (ordenes.isEmpty()) {
            throw new IllegalStateException(
                    "No se puede iniciar una jornada vacía.");
        }

        // Validar todas las órdenes antes de cambiar estados.
        for (OrdenServicio orden : ordenes) {
            if (!orden.getEstado().equals(OrdenServicio.PENDIENTE)) {
                throw new IllegalStateException(
                        "Todas las órdenes deben estar pendientes.");
            }

            if (orden.getVisitas() >= orden.obtenerMaximoVisitas()) {
                throw new IllegalStateException(
                        "Una orden agotó su límite de visitas.");
            }

            if (!cuadrilla.puedeAtender(orden)) {
                throw new IllegalStateException(
                        "La cuadrilla no cumple los requisitos "
                        + "de certificación de una orden.");
            }
        }
        double ingresosNuevos = 0.0;

        for (OrdenServicio orden : ordenes) {
            if (orden.getVisitas() >= 1) {
                ingresosNuevos += CARGO_VISITA_ADICIONAL;
            }
        }

        for (OrdenServicio orden : ordenes) {
            orden.iniciarAtencion();
        }

        ingresosVisitasAdicionales += ingresosNuevos;
        estado = EN_CURSO;

        return ingresosNuevos;
    }

    public void registrarResultadoVisita(
            String codigo,
            boolean exitosa) {

        if (!estado.equals(EN_CURSO)) {
            throw new IllegalStateException(
                    "La jornada debe estar en curso.");
        }

        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El código de la orden no puede estar vacío.");
        }

        OrdenServicio ordenEncontrada = buscarOrden(codigo);

        if (ordenEncontrada == null) {
            throw new IllegalArgumentException(
                    "La orden no pertenece a esta jornada.");
        }

        String clave = normalizarCodigo(ordenEncontrada.getCodigo());

        if (resultados.containsKey(clave)) {
            throw new IllegalStateException(
                    "El resultado de esta orden ya fue registrado.");
        }
        ordenEncontrada.registrarResultadoVisita(exitosa);
        resultados.put(clave, exitosa);
        if (resultados.size() == ordenes.size()) {
            estado = CERRADA;
        }
    }

    public void cerrarJornada() {
        if (!estado.equals(EN_CURSO)) {
            throw new IllegalStateException(
                    "Solo se puede cerrar una jornada en curso.");
        }

        if (resultados.size() != ordenes.size()) {
            throw new IllegalStateException(
                    "No se puede cerrar la jornada: "
                    + "faltan resultados de visitas.");
        }

        estado = CERRADA;
    }

    private OrdenServicio buscarOrden(String codigo) {
        for (OrdenServicio orden : ordenes) {
            if (orden.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return orden;
            }
        }

        return null;
    }

    private String normalizarCodigo(String codigo) {
        return codigo.toLowerCase(java.util.Locale.ROOT);
    }

    private void verificarPreparacion() {
        if (!estado.equals(EN_PREPARACION)) {
            throw new IllegalStateException(
                    "La operación solo se permite "
                    + "durante la preparación de la jornada.");
        }
    }

    public double getHorasAsignadas() {
        double total = 0.0;

        for (OrdenServicio orden : ordenes) {
            total += orden.getHorasEstimadas();
        }

        return total;
    }

    public double getCapacidadRestante() {
        return cuadrilla.getCapacidadMaximaHoras()
                - getHorasAsignadas();
    }

    public Cuadrilla getCuadrilla() {
        return cuadrilla;
    }

    public String getEstado() {
        return estado;
    }

    public List<OrdenServicio> getOrdenes() {
        return Collections.unmodifiableList(ordenes);
    }

    public double getIngresosVisitasAdicionales() {
        return ingresosVisitasAdicionales;
    }

    public int getCantidadResultadosRegistrados() {
        return resultados.size();
    }

    @Override
    public String toString() {
        return String.format(
                "Cuadrilla: %s | Estado: %s"
                + " | Órdenes: %d | Horas asignadas: %.2f"
                + " | Capacidad restante: %.2f"
                + " | Ingresos por visitas adicionales: Q%.2f",
                cuadrilla.getCodigo(),
                estado,
                ordenes.size(),
                getHorasAsignadas(),
                getCapacidadRestante(),
                ingresosVisitasAdicionales);
    }
}
