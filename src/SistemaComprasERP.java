import java.util.*;

// ================= INTERFACES Y ENUM =================
interface Calculable {
    double calcularCostoTotal();
}

enum EstadoSolicitud {
    SOLICITADA,
    EN_REVISION,
    APROBADA,
    RECHAZADA
}

// ================= CLASE ABSTRACTA =================
abstract class Documento {
    protected int numero;
    protected Date fecha;

    public Documento(int numero) {
        this.numero = numero;
        this.fecha = new Date();
    }

    public abstract void mostrarResumen();
    public int getNumero() { return numero; }
}

// ================= CLASES BASE =================
class Persona {
    protected String id;
    protected String nombre;

    public Persona(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}

class Proveedor extends Persona {
    private String empresa;
    private List<Producto> productos;

    public Proveedor(String id, String nombre, String empresa) {
        super(id, nombre);
        this.empresa = empresa;
        this.productos = new ArrayList<>();
    }

    public void agregarProducto(Producto p) {
        productos.add(p);
    }

    public List<Producto> getProductos() { return productos; }
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmpresa() { return empresa; }
}

class Producto implements Calculable {
    private String nombre;
    private double precioUnitario;
    private String unidadMedida;
    private Proveedor proveedor;

    public Producto(String nombre, double precioUnitario, String unidadMedida, Proveedor proveedor) {
        this.nombre = nombre;
        this.precioUnitario = precioUnitario;
        this.unidadMedida = unidadMedida;
        this.proveedor = proveedor;
    }

    public double calcularCostoTotal() {
        return precioUnitario;
    }

    public String getNombre() { return nombre; }
    public double getPrecioUnitario() { return precioUnitario; }
    public Proveedor getProveedor() { return proveedor; }
}

class ItemSolicitud {
    private Producto producto;
    private int cantidad;

    public ItemSolicitud(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public double subtotal() {
        return producto.getPrecioUnitario() * cantidad;
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
}

class SolicitudCompra extends Documento implements Calculable {
    private String departamento;
    private List<ItemSolicitud> items;
    private EstadoSolicitud estado;

    public SolicitudCompra(int numero, String departamento) {
        super(numero);
        this.departamento = departamento;
        this.items = new ArrayList<>();
        this.estado = EstadoSolicitud.SOLICITADA;
    }

    public void agregarItem(ItemSolicitud item) {
        items.add(item);
    }

    public double calcularCostoTotal() {
        double total = 0;
        for (ItemSolicitud item : items) {
            total += item.subtotal();
        }
        return total;
    }

    public void aprobar() { estado = EstadoSolicitud.APROBADA; }
    public void rechazar() { estado = EstadoSolicitud.RECHAZADA; }

    public EstadoSolicitud getEstado() { return estado; }

    public void mostrarResumen() {
        System.out.println("Solicitud #: " + numero + ", Departamento: " + departamento);
        System.out.println("Estado: " + estado);
        System.out.println("Items:");
        for (ItemSolicitud item : items) {
            System.out.println("- " + item.getProducto().getNombre() + ", Cant: " + item.getCantidad());
        }
        System.out.println("Total: $" + calcularCostoTotal());
    }
}

// ================= PROGRAMA PRINCIPAL =================
public class Main {
    static Scanner scanner = new Scanner(System.in);
    static List<Proveedor> proveedores = new ArrayList<>();
    static List<Producto> productos = new ArrayList<>();
    static List<SolicitudCompra> solicitudes = new ArrayList<>();
    static int contadorSolicitudes = 1;

    public static void main(String[] args) {
        while (true) {
            System.out.println("===== SISTEMA DE GESTIÓN DE COMPRAS ERP =====");
            System.out.println("1. Registrar proveedor");
            System.out.println("2. Registrar producto");
            System.out.println("3. Registrar solicitud de compra");
            System.out.println("4. Listar proveedores");
            System.out.println("5. Listar productos");
            System.out.println("6. Listar solicitudes de compra");
            System.out.println("7. Buscar proveedor por ID");
            System.out.println("8. Buscar producto por nombre");
            System.out.println("9. Buscar solicitud por número");
            System.out.println("10. Aprobar solicitud");
            System.out.println("11. Rechazar solicitud");
            System.out.println("12. Calcular total de una solicitud");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            int op = Integer.parseInt(scanner.nextLine());
            switch (op) {
                case 1 -> registrarProveedor();
                case 2 -> registrarProducto();
                case 3 -> registrarSolicitud();
                case 4 -> proveedores.forEach(p -> System.out.println(p.getNombre()));
                case 5 -> productos.forEach(p -> System.out.println(p.getNombre()));
                case 6 -> solicitudes.forEach(SolicitudCompra::mostrarResumen);
                case 7 -> buscarProveedor();
                case 8 -> buscarProducto();
                case 9 -> buscarSolicitud();
                case 10 -> cambiarEstado(true);
                case 11 -> cambiarEstado(false);
                case 12 -> calcularTotal();
                case 0 -> System.exit(0);
            }
        }
    }

    static void registrarProveedor() {
        System.out.print("ID: "); String id = scanner.nextLine();
        System.out.print("Nombre: "); String nombre = scanner.nextLine();
        System.out.print("Empresa: "); String empresa = scanner.nextLine();
        proveedores.add(new Proveedor(id, nombre, empresa));
    }

    static void registrarProducto() {
        System.out.print("Nombre del producto: "); String nombre = scanner.nextLine();
        System.out.print("Precio unitario: "); double precio = Double.parseDouble(scanner.nextLine());
        System.out.print("Unidad de medida: "); String unidad = scanner.nextLine();
        System.out.print("ID del proveedor: "); String idProv = scanner.nextLine();
        Proveedor prov = proveedores.stream().filter(p -> p.getId().equals(idProv)).findFirst().orElse(null);
        if (prov != null) {
            Producto prod = new Producto(nombre, precio, unidad, prov);
            productos.add(prod);
            prov.agregarProducto(prod);
        } else System.out.println("Proveedor no encontrado.");
    }

    static void registrarSolicitud() {
        System.out.print("Departamento: ");
        String dep = scanner.nextLine();
        SolicitudCompra s = new SolicitudCompra(contadorSolicitudes++, dep);
        while (true) {
            System.out.print("Nombre del producto (o 'fin'): ");
            String nombre = scanner.nextLine();
            if (nombre.equals("fin")) break;
            Producto p = productos.stream().filter(pr -> pr.getNombre().equalsIgnoreCase(nombre)).findFirst().orElse(null);
            if (p != null) {
                System.out.print("Cantidad: ");
                int cant = Integer.parseInt(scanner.nextLine());
                s.agregarItem(new ItemSolicitud(p, cant));
            } else System.out.println("Producto no encontrado.");
        }
        solicitudes.add(s);
    }

    static void buscarProveedor() {
        System.out.print("ID del proveedor: ");
        String id = scanner.nextLine();
        proveedores.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .ifPresentOrElse(p -> {
                System.out.println("Nombre: " + p.getNombre());
                System.out.println("Empresa: " + p.getEmpresa());
            }, () -> System.out.println("No encontrado"));
    }

    static void buscarProducto() {
        System.out.print("Nombre del producto: ");
        String nombre = scanner.nextLine();
        productos.stream()
            .filter(p -> p.getNombre().equalsIgnoreCase(nombre))
            .findFirst()
            .ifPresentOrElse(p -> System.out.println("Precio: " + p.getPrecioUnitario()),
                () -> System.out.println("No encontrado"));
    }

    static void buscarSolicitud() {
        System.out.print("Número de solicitud: ");
        int num = Integer.parseInt(scanner.nextLine());
        solicitudes.stream()
            .filter(s -> s.getNumero() == num)
            .findFirst()
            .ifPresentOrElse(SolicitudCompra::mostrarResumen, () -> System.out.println("No encontrada"));
    }

    static void cambiarEstado(boolean aprobar) {
        System.out.print("Número de solicitud: ");
        int num = Integer.parseInt(scanner.nextLine());
        solicitudes.stream()
            .filter(s -> s.getNumero() == num)
            .findFirst()
            .ifPresent(s -> {
                if (aprobar) s.aprobar();
                else s.rechazar();
            });
    }

    static void calcularTotal() {
        System.out.print("Número de solicitud: ");
        int num = Integer.parseInt(scanner.nextLine());
        solicitudes.stream()
            .filter(s -> s.getNumero() == num)
            .findFirst()
            .ifPresent(s -> System.out.println("Total: $" + s.calcularCostoTotal()));
    }
}
