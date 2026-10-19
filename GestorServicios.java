import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GestorServicios {
    private final List<OrdenServicio> ordenes;
    private final List<Cuadrilla> cuadrillas;
    private final List<Jornada> jornadas;
    private double ingresosRegistro;
    private double ingresosVisitasAdicionales;
    private static final double CARGO_VISITA_ADICIONAL = 40.0;
    private final Map<String, Double> ingresosPorCategoria;

    public GestorServicios() {
        ordenes = new ArrayList<>();
        cuadrillas = new ArrayList<>();
        jornadas = new ArrayList<>();

        ingresosRegistro = 0;
        ingresosVisitasAdicionales = 0;

        ingresosPorCategoria = new LinkedHashMap<>();
    }

    public double cotizarOrden(OrdenServicio orden) {
        if (orden == null) {
            throw new IllegalArgumentException(
                "La orden no puede ser nula."
            );
        }

        return orden.calcularCosto();
    }

    public void registrarOrden(OrdenServicio orden) {
        if (orden == null) {
            throw new IllegalArgumentException(
                "La orden no puede ser nula."
            );
        }

        if (buscarOrden(orden.getCodigo()) != null) {
            throw new IllegalArgumentException(
                "Ya existe una orden con ese código."
            );
        }

        if (!OrdenServicio.PENDIENTE.equals(orden.getEstado())
                || orden.getVisitas() != 0) {
            throw new IllegalArgumentException(
                "Solo se pueden registrar órdenes nuevas."
            );
        }

        double costo = orden.calcularCosto();
        String categoria = obtenerCategoria(orden);

        ordenes.add(orden);
        ingresosRegistro += costo;

        ingresosPorCategoria.put(
            categoria,
            ingresosPorCategoria.getOrDefault(categoria, 0.0) + costo
        );
    }

        public void registrarOrdenInicial(OrdenServicio orden) {
            if (orden == null) {
                throw new IllegalArgumentException(
                "La orden no puede ser nula."
                );
            }

            if (buscarOrden(orden.getCodigo()) != null) {
                throw new IllegalArgumentException(
                "Ya existe una orden con ese código."
                );
            }

            if (!OrdenServicio.PENDIENTE.equals(orden.getEstado()) || orden.getVisitas() != 0) {
                    throw new IllegalArgumentException("La orden inicial debe estar pendiente y sin visitas.");
                }
            ordenes.add(orden);
        }

    public OrdenServicio buscarOrden(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }

        for (OrdenServicio orden : ordenes) {
            if (orden.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return orden;
            }
        }

        return null;
    }

    public List<OrdenServicio> getOrdenes() {
        return Collections.unmodifiableList(ordenes);
    }

    // REGISTRO Y CONSULTA DE CUADRILLAS

    public void registrarCuadrilla(Cuadrilla cuadrilla) {
        if (cuadrilla == null) {
            throw new IllegalArgumentException(
                "La cuadrilla no puede ser nula."
            );
        }

        if (buscarCuadrilla(cuadrilla.getCodigo()) != null) {
            throw new IllegalArgumentException(
                "Ya existe una cuadrilla con ese código."
            );
        }

        cuadrillas.add(cuadrilla);
    }

    public Cuadrilla buscarCuadrilla(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }

        for (Cuadrilla cuadrilla : cuadrillas) {
            if (cuadrilla.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return cuadrilla;
            }
        }

        return null;
    }

    public List<Cuadrilla> getCuadrillas() {
        return Collections.unmodifiableList(cuadrillas);
    }

    // CREACIÓN Y ADMINISTRACIÓN DE JORNADAS

    public Jornada crearJornada(String codigoCuadrilla) {
        Cuadrilla cuadrilla = buscarCuadrilla(codigoCuadrilla);

        if (cuadrilla == null) {
            throw new IllegalArgumentException(
                "No existe una cuadrilla con ese código."
            );
        }

        if (tieneJornadaAbierta(cuadrilla)) {
            throw new IllegalArgumentException(
                "La cuadrilla ya tiene una jornada sin cerrar."
            );
        }

        Jornada jornada = new Jornada(cuadrilla);
        jornadas.add(jornada);

        return jornada;
    }

    public Jornada buscarJornadaAbierta(String codigoCuadrilla) {
        if (codigoCuadrilla == null || codigoCuadrilla.isBlank()) {
            return null;
        }

        for (Jornada jornada : jornadas) {
            if (jornada.getCuadrilla().getCodigo()
                    .equalsIgnoreCase(codigoCuadrilla.trim())
                    && !Jornada.CERRADA.equals(jornada.getEstado())) {
                return jornada;
            }
        }

        return null;
    }

    private boolean tieneJornadaAbierta(Cuadrilla cuadrilla) {
        return buscarJornadaAbierta(cuadrilla.getCodigo()) != null;
    }

    public void asignarOrden(String codigoCuadrilla, String codigoOrden) {
        Jornada jornada = buscarJornadaAbierta(codigoCuadrilla);
        OrdenServicio orden = buscarOrden(codigoOrden);

        if (jornada == null) {
            throw new IllegalArgumentException(
                "La cuadrilla no tiene una jornada abierta."
            );
        }

        if (orden == null) {
            throw new IllegalArgumentException(
                "No existe una orden con ese código."
            );
        }

        if (ordenEstaAsignada(orden)) {
            throw new IllegalArgumentException(
                "La orden ya está asignada a una jornada sin cerrar."
            );
        }

        jornada.agregarOrden(orden);
    }

    private boolean ordenEstaAsignada(OrdenServicio orden) {
        for (Jornada jornada : jornadas) {
            if (Jornada.CERRADA.equals(jornada.getEstado())) {
                continue;
            }

            for (OrdenServicio asignada : jornada.getOrdenes()) {
                if (asignada.equals(orden)) {
                    return true;
                }
            }
        }

        return false;
    }

    public OrdenServicio retirarOrden(
            String codigoCuadrilla, String codigoOrden) {

        Jornada jornada = buscarJornadaAbierta(codigoCuadrilla);

        if (jornada == null) {
            throw new IllegalArgumentException(
                "La cuadrilla no tiene una jornada abierta."
            );
        }

        return jornada.retirarOrden(codigoOrden);
    }

    public void iniciarJornada(String codigoCuadrilla) {
        Jornada jornada = buscarJornadaAbierta(codigoCuadrilla);

        if (jornada == null) {
            throw new IllegalArgumentException(
                "No existe una jornada abierta para esa cuadrilla."
            );
        }

        List<OrdenServicio> ordenesJornada =
            new ArrayList<>(jornada.getOrdenes());

        double cargo = jornada.iniciarJornada();

        ingresosVisitasAdicionales += cargo;

        for (OrdenServicio orden : ordenesJornada) {
            if (orden.getVisitas() >= 1) {
                String categoria = obtenerCategoria(orden);

                ingresosPorCategoria.put(
                    categoria,
                    ingresosPorCategoria.getOrDefault(categoria, 0.0)
                        + CARGO_VISITA_ADICIONAL
                );
            }
        }
    }

    public void registrarResultado(
            String codigoCuadrilla, String codigoOrden, boolean exitosa) {

        Jornada jornada = buscarJornadaAbierta(codigoCuadrilla);

        if (jornada == null) {
            throw new IllegalArgumentException(
                "No existe una jornada abierta para esa cuadrilla."
            );
        }

        jornada.registrarResultadoVisita(codigoOrden, exitosa);
    }

    public List<Jornada> getJornadas() {
        return Collections.unmodifiableList(jornadas);
    }

    // REPORTES

    private String obtenerCategoria(OrdenServicio orden) {
        // Se distingue la categoría mediante el nombre de la clase concreta.
        return orden.getClass().getSimpleName();
    }

    public Map<String, Integer> contarOrdenesPorCategoria() {
        Map<String, Integer> conteo = new LinkedHashMap<>();

        for (OrdenServicio orden : ordenes) {
            String categoria = obtenerCategoria(orden);
            conteo.put(categoria, conteo.getOrDefault(categoria, 0) + 1);
        }

        return conteo;
    }

    public Map<String, Integer> contarOrdenesPorEstado() {
        Map<String, Integer> conteo = new LinkedHashMap<>();

        for (OrdenServicio orden : ordenes) {
            String estado = orden.getEstado();
            conteo.put(estado, conteo.getOrDefault(estado, 0) + 1);
        }

        return conteo;
    }

    public double getIngresosRegistro() {
        return ingresosRegistro;
    }

    public double getIngresosVisitasAdicionales() {
        return ingresosVisitasAdicionales;
    }

    public double getIngresosTotales() {
        return ingresosRegistro + ingresosVisitasAdicionales;
    }

    public Map<String, Double> getIngresosPorCategoria() {
        return Collections.unmodifiableMap(ingresosPorCategoria);
    }

    public List<Jornada> getJornadasAbiertas() {
        List<Jornada> abiertas = new ArrayList<>();

        for (Jornada jornada : jornadas) {
            if (!Jornada.CERRADA.equals(jornada.getEstado())) {
                abiertas.add(jornada);
            }
        }

        return abiertas;
    }
}