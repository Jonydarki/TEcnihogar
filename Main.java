import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Scanner entrada = new Scanner(System.in);
    private static final GestorServicios gestor = new GestorServicios();

    public static void main(String[] args) {
        cargarDatosIniciales();

        int opcion;

        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opción: ");

            try {
                switch (opcion) {
                    case 1:
                        cotizarYRegistrarOrden();
                        break;
                    case 2:
                        registrarCuadrilla();
                        break;
                    case 3:
                        crearJornada();
                        break;
                    case 4:
                        asignarOrden();
                        break;
                    case 5:
                        retirarOrden();
                        break;
                    case 6:
                        iniciarJornada();
                        break;
                    case 7:
                        registrarResultado();
                        break;
                    case 8:
                        consultarDatos();
                        break;
                    case 9:
                        mostrarReportes();
                        break;
                    case 0:
                        System.out.println("Saliendo del sistema...");
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
            } catch (IllegalArgumentException
                    | IllegalStateException e) {
                System.out.println("Operación rechazada: " + e.getMessage());
            } catch (Exception e) {
                System.out.println(
                    "Ocurrió un error: " + e.getMessage()
                );
            }

            System.out.println();

        } while (opcion != 0);

        entrada.close();
    }

    // MENÚ PRINCIPAL

    private static void mostrarMenu() {
        System.out.println("==================================");
        System.out.println("       SISTEMA TECNIHOGAR");
        System.out.println("==================================");
        System.out.println("1. Cotizar y registrar orden");
        System.out.println("2. Registrar cuadrilla");
        System.out.println("3. Crear jornada");
        System.out.println("4. Asignar orden a jornada");
        System.out.println("5. Retirar orden de jornada");
        System.out.println("6. Iniciar jornada");
        System.out.println("7. Registrar resultado de visita");
        System.out.println("8. Consultar órdenes y cuadrillas");
        System.out.println("9. Mostrar reportes");
        System.out.println("0. Salir");
        System.out.println("==================================");
    }

    // DATOS INICIALES

    private static void cargarDatosIniciales() {
        gestor.registrarCuadrilla(
            new Cuadrilla("C01", 8.0, true)
        );
        gestor.registrarCuadrilla(
            new Cuadrilla("C02", 6.0, false)
        );
        gestor.registrarCuadrilla(
            new Cuadrilla("C03", 10.0, true)
        );

        // Diagnósticos
        gestor.registrarOrdenInicial(
            new Diagnostico(
                "D01", "Ana López", "Zona 10",
                1.0, "Metropolitana"
            )
        );
        gestor.registrarOrdenInicial(
            new Diagnostico(
                "D02", "Luis Pérez", "Mixco",
                1.5, "Periférica"
            )
        );

        // Instalaciones
        gestor.registrarOrdenInicial(
            new Instalacion(
                "I01", "Marta Ruiz", "Zona 1",
                2.5, "Metropolitana", 5
            )
        );
        gestor.registrarOrdenInicial(
            new Instalacion(
                "I02", "Carlos Díaz", "Villa Nueva",
                2.0, "Periférica", 2
            )
        );

        // Instalaciones eléctricas certificadas
        gestor.registrarOrdenInicial(
            new Instalacion_Electrica(
                "E01", "Sofía Morales", "Zona 15",
                2.5, "Metropolitana", 5, 2000
            )
        );
        gestor.registrarOrdenInicial(
            new Instalacion_Electrica(
                "E02", "Pedro Gómez", "Zona 7",
                3.0, "Periférica", 4, 1500
            )
        );
        gestor.registrarOrdenInicial(
            new Mantenimiento_Acondicionado(
                "A01", "Elena Castro", "Zona 14",
                2.0, "Metropolitana", 12000
            )
        );
        gestor.registrarOrdenInicial(
            new Mantenimiento_Acondicionado(
                "A02", "Diego Flores", "Zona 4",
                1.5, "Metropolitana", 24000
            )
        );
    }


    private static void cotizarYRegistrarOrden() {
        System.out.println("\nCategorías de servicio:");
        System.out.println("1. Diagnóstico");
        System.out.println("2. Instalación");
        System.out.println("3. Instalación eléctrica certificada");
        System.out.println("4. Mantenimiento de aire acondicionado");

        int tipo = leerEntero("Seleccione la categoría: ");

        String codigo = leerTexto("Código: ");
        String cliente = leerTexto("Nombre del cliente: ");
        String direccion = leerTexto("Dirección: ");
        double horas = leerDouble("Horas estimadas: ");

        System.out.println("Zonas disponibles:");
        System.out.println("1. Metropolitana");
        System.out.println("2. Periférica");

        int zonaSeleccionada = leerEntero("Seleccione la zona: ");

        String zona;

        if (zonaSeleccionada == 1) {
            zona = "Metropolitana";
        } else if (zonaSeleccionada == 2) {
            zona = "Periférica";
        } else {
            throw new IllegalArgumentException(
                "La zona seleccionada no existe."
            );
        }

        OrdenServicio orden;

        switch (tipo) {
            case 1:
                orden = new Diagnostico(
                    codigo, cliente, direccion, horas, zona
                );
                break;

            case 2:
                int puntos = leerEntero("Cantidad de puntos: ");

                orden = new Instalacion(
                    codigo, cliente, direccion, horas, zona, puntos
                );
                break;

            case 3:
                int puntosElectricos =
                    leerEntero("Cantidad de puntos: ");

                double materiales =
                    leerDouble("Valor declarado de materiales: Q");

                orden = new Instalacion_Electrica(
                    codigo, cliente, direccion, horas, zona,
                    puntosElectricos, materiales
                );
                break;

            case 4:
                int capacidad = leerEntero(
                    "Capacidad del aire acondicionado en BTU: "
                );

                orden = new Mantenimiento_Acondicionado(
                    codigo, cliente, direccion, horas, zona, capacidad
                );
                break;

            default:
                throw new IllegalArgumentException(
                    "La categoría seleccionada no existe."
                );
        }

        double costo = gestor.cotizarOrden(orden);

        System.out.println("\nCotización:");
        System.out.printf(
            Locale.US, "Costo: Q%.2f%n", costo
        );
        System.out.println(
            "Tiempo estimado: " + orden.obtenerTiempoAtencion()
                + " día(s)"
        );
        System.out.println(
            "Máximo de visitas: " + orden.obtenerMaximoVisitas()
        );

        String confirmar = leerTexto(
            "¿Desea registrar la orden? (S/N): "
        );

        if (confirmar.equalsIgnoreCase("S")) {
            gestor.registrarOrden(orden);
            System.out.println("Orden registrada correctamente.");
        } else {
            System.out.println(
                "Cotización realizada. No se registró la orden."
            );
        }
    }

    private static void registrarCuadrilla() {
        String codigo = leerTexto("Código de la cuadrilla: ");
        double capacidad = leerDouble(
            "Capacidad máxima de horas por jornada: "
        );

        String respuesta = leerTexto(
            "¿Está certificada en refrigeración? (S/N): "
        );

        boolean certificada = respuesta.equalsIgnoreCase("S");

        Cuadrilla cuadrilla = new Cuadrilla(
            codigo, capacidad, certificada
        );

        gestor.registrarCuadrilla(cuadrilla);

        System.out.println("Cuadrilla registrada correctamente.");
    }
    
    private static void crearJornada() {
        mostrarCuadrillas();

        String codigo = leerTexto(
            "Código de la cuadrilla para la jornada: "
        );

        gestor.crearJornada(codigo);

        System.out.println("Jornada creada en preparación.");
    }

    private static void asignarOrden() {
        mostrarCuadrillas();
        mostrarOrdenes();

        String codigoCuadrilla = leerTexto("Código de cuadrilla: ");
        String codigoOrden = leerTexto("Código de orden: ");

        gestor.asignarOrden(codigoCuadrilla, codigoOrden);

        System.out.println("Orden asignada correctamente.");
    }

    private static void retirarOrden() {
        String codigoCuadrilla = leerTexto("Código de cuadrilla: ");
        String codigoOrden = leerTexto("Código de orden: ");

        OrdenServicio retirada = gestor.retirarOrden(
            codigoCuadrilla, codigoOrden
        );

        System.out.println(
            "Orden " + retirada.getCodigo()
                + " retirada. Continúa pendiente."
        );
    }

    private static void iniciarJornada() {
        String codigo = leerTexto("Código de cuadrilla: ");

        gestor.iniciarJornada(codigo);

        System.out.println("Jornada iniciada correctamente.");
    }

    private static void registrarResultado() {
        String codigoCuadrilla = leerTexto("Código de cuadrilla: ");
        String codigoOrden = leerTexto("Código de orden: ");

        String respuesta = leerTexto(
            "¿La visita fue exitosa? (S/N): "
        );

        boolean exitosa = respuesta.equalsIgnoreCase("S");

        gestor.registrarResultado(
            codigoCuadrilla, codigoOrden, exitosa
        );

        System.out.println("Resultado registrado correctamente.");
    }

    // CONSULTAS

    private static void consultarDatos() {
        System.out.println("\n1. Consultar órdenes");
        System.out.println("2. Consultar cuadrillas");
        System.out.println("3. Consultar jornadas abiertas");

        int opcion = leerEntero("Seleccione una opción: ");

        switch (opcion) {
            case 1:
                mostrarOrdenes();
                break;
            case 2:
                mostrarCuadrillas();
                break;
            case 3:
                for (Jornada jornada : gestor.getJornadasAbiertas()) {
                    System.out.println(jornada);
                }
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    private static void mostrarOrdenes() {
        System.out.println("\n--- ÓRDENES ---");

        for (OrdenServicio orden : gestor.getOrdenes()) {
            System.out.println(orden);
            System.out.printf(
                Locale.US, "Costo: Q%.2f%n",
                orden.calcularCosto()
            );
        }
    }

    private static void mostrarCuadrillas() {
        System.out.println("\n--- CUADRILLAS ---");

        for (Cuadrilla cuadrilla : gestor.getCuadrillas()) {
            System.out.println(cuadrilla);
        }
    }

    // REPORTES

    private static void mostrarReportes() {
        System.out.println("\n========== REPORTES ==========");

        System.out.println("\nÓrdenes por categoría y estado:");

        for (Map.Entry<String, Integer> categoria :
                gestor.contarOrdenesPorCategoria().entrySet()) {

            System.out.println("\n" + categoria.getKey() + ":");

            for (String estado : new String[] {
                    OrdenServicio.PENDIENTE,
                    OrdenServicio.EN_ATENCION,
                    OrdenServicio.COMPLETADA,
                    OrdenServicio.CANCELADA
            }) {
                int cantidad = 0;

                for (OrdenServicio orden : gestor.getOrdenes()) {
                    if (orden.getClass().getSimpleName()
                            .equals(categoria.getKey())
                            && orden.getEstado().equals(estado)) {
                        cantidad++;
                    }
                }

                System.out.println("  " + estado + ": " + cantidad);
            }
        }

        System.out.println("\nIngresos:");

        System.out.printf(
            Locale.US, "Por registro: Q%.2f%n",
            gestor.getIngresosRegistro()
        );

        System.out.printf(
            Locale.US, "Por visitas adicionales: Q%.2f%n",
            gestor.getIngresosVisitasAdicionales()
        );

        System.out.printf(
            Locale.US, "Total: Q%.2f%n",
            gestor.getIngresosTotales()
        );

        System.out.println("\nIngresos por categoría:");

        for (Map.Entry<String, Double> ingreso :
                gestor.getIngresosPorCategoria().entrySet()) {

            System.out.printf(
                Locale.US, "%s: Q%.2f%n",
                ingreso.getKey(), ingreso.getValue()
            );
        }

        System.out.println("\nJornadas sin cerrar:");

        for (Jornada jornada : gestor.getJornadasAbiertas()) {
            System.out.println(jornada);
            System.out.printf(
                Locale.US, "Horas asignadas: %.2f%n",
                jornada.getHorasAsignadas()
            );
            System.out.printf(
                Locale.US, "Capacidad restante: %.2f%n",
                jornada.getCapacidadRestante()
            );
        }

        System.out.println("===============================");
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        String texto = entrada.nextLine().trim();

        if (texto.isEmpty()) {
            throw new IllegalArgumentException(
                "El campo no puede estar vacío."
            );
        }

        return texto;
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = entrada.nextLine().trim();

            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println(
                    "Entrada inválida. Ingrese un número entero."
                );
            }
        }
    }

    private static double leerDouble(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = entrada.nextLine().trim().replace(',', '.');

            try {
                double valor = Double.parseDouble(texto);

                if (!Double.isFinite(valor)) {
                    System.out.println(
                        "Ingrese un número finito."
                    );
                    continue;
                }

                return valor;

            } catch (NumberFormatException e) {
                System.out.println(
                    "Entrada inválida. Ingrese un número válido."
                );
            }
        }
    }
}