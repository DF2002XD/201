import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseOperations {
    // MySQL connection details
    private static final String MYSQL_URL = "jdbc:mysql://localhost/productos";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "abc123";

    // PostgreSQL connection details
    private static final String POSTGRES_URL = "jdbc:postgresql://localhost/postgres";
    private static final String POSTGRES_USER = "postgres";
    private static final String POSTGRES_PASSWORD = "abc123";

    public static void main(String[] args) {
        // Example usage of the implemented functions
        try {
            // 1. Create a new category
            crearCategoria("Smartphones");

            // 2. Create a new provider
            crearNuevoProveedor("TechSupplier", "B12345678", 912345678, "contact@techsupplier.com");

            // 3. Create a new user
            crearUsuario("Elena López", "elena.lopez@email.com", 1995);

            // 4. Create a new product
            crearProducto("Smartphone X1", 599.99, 100, "Smartphones", "B12345678");

            // 5. List products with low stock
            listarProductosBajoStock(50);

            // 6. Get total orders by users
            obtenerTotalPedidosUsuarios();

            // 7. Get product quantity in each warehouse
            obtenerCantidadProductosEnCadaAlmacen();

            // 8. List all products with their categories and providers
            listarTodosProductosConCategoriaYProveedor();

            // 9. Get users who bought products from a specific category
            // Assuming the category ID for "Smartphones" is 1
            obtenerUsuariosCompraronProductosCategoria(1);

            // 10. Delete a product (uncomment to test)
            // eliminarProductoPorNombre("Smartphone X1");

            // 11. Delete a user (uncomment to test)
            // eliminarUsuario(6); // Assuming Elena López got ID 6

            // 12. Delete a provider (uncomment to test)
            // eliminarProveedor(6); // Assuming TechSupplier got ID 6

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1. Crear una nueva categoría (PostgreSQL)
    public static void crearCategoria(String nombreCategoria) {
        try (Connection conn = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD);
                PreparedStatement checkstmt = conn
                        .prepareStatement("SELECT COUNT(*) FROM categorias WHERE nombre_categoria = ?");
                PreparedStatement pstmt = conn
                        .prepareStatement("INSERT INTO categorias (nombre_categoria) VALUES (?)")) {
            checkstmt.setString(1, "nombreCategoria");
            ResultSet rs = checkstmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count == 0) {
                pstmt.setString(1, nombreCategoria);
                pstmt.executeUpdate();
                System.out.println("Categoría creada exitosamente.");
            } else {
                System.out.println("La categoría ya existe. No se ha creado una nueva.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2. Crear un nuevo proveedor (PostgreSQL)
    public static void crearNuevoProveedor(String nombreProveedor, String nif, int telefono, String email) {
        try (Connection conn = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD);
                PreparedStatement checkstmt = conn
                        .prepareStatement("SELECT COUNT(*) FROM proveedores WHERE (contacto).nif = ?");
                PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO proveedores (nombre_proveedor, contacto) VALUES (?, ROW(?, ?, ?, ?))")) {
            checkstmt.setString(1, nif);
            ResultSet rs = checkstmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            if (count == 0) {
                pstmt.setString(1, nombreProveedor);
                pstmt.setString(2, nombreProveedor); // nombre_contacto
                pstmt.setString(3, nif);
                pstmt.setString(4, String.valueOf(telefono));
                pstmt.setString(5, email);
                pstmt.executeUpdate();
                System.out.println("Proveedor creado exitosamente.");
            } else {
                System.out.println("Ya existe un proveedor con el mismo NIF. No se ha creado uno nuevo.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3. Eliminar un nuevo proveedor (PostgreSQL)
    public static void eliminarProveedor(int id) {
        try (Connection conn = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM proveedores WHERE id_proveedor = ?")) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Proveedor eliminado exitosamente.");
            } else {
                System.out.println("No se encontró el proveedor con el ID especificado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 4. Crear un nuevo usuario (MySQL)
    public static void crearUsuario(String nombre, String email, int anho_nacimiento) {
        try (Connection conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
                PreparedStatement checkstmt = conn.prepareStatement("SELECT COUNT(*) FROM usuarios WHERE email = ?");
                PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO usuarios (nombre, email, ano_nacimiento) VALUES (?, ?, ?)")) {
            checkstmt.setString(1, email);
            ResultSet rs = checkstmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count == 0) {
                pstmt.setString(1, nombre);
                pstmt.setString(2, email);
                pstmt.setInt(3, anho_nacimiento);
                pstmt.executeUpdate();
                System.out.println("Usuario creado exitosamente.");
            } else {
                System.out.println("Ya existe un usuario con el mismo email. No se ha creado uno nuevo.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 5. Eliminar un usuario (MySQL)
    public static void eliminarUsuario(int id) {
        try (Connection conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM usuarios WHERE id_usuario = ?")) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Usuario eliminado exitosamente.");
            } else {
                System.out.println("No se encontró el usuario con el ID especificado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 6. Crear nuevo producto (MySQL + PostgreSQL)
    public static void crearProducto(String nombre, Double precio, int stock, String nombre_categoria, String nif) {
        Connection connMySQL = null;
        Connection connPostgres = null;
        try {
            connMySQL = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
            connPostgres = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD);

            connMySQL.setAutoCommit(false);
            connPostgres.setAutoCommit(false);
            try (PreparedStatement checkstmt = connMySQL
                    .prepareStatement("SELECT COUNT(*) FROM productos WHERE nombre_producto = ?")) {
                checkstmt.setString(1, nombre);
                ResultSet rs = checkstmt.executeQuery();
                rs.next();
                int count = rs.getInt(1);
                if (count > 0) {
                    System.out.println("Ya existe un producto con el mismo nombre. No se ha creado uno nuevo.");
                    return;
                }
            }
            // Insert into MySQL
            try (PreparedStatement pstmtMySQL = connMySQL.prepareStatement(
                    "INSERT INTO productos (nombre_producto, precio, stock) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                pstmtMySQL.setString(1, nombre);
                pstmtMySQL.setDouble(2, precio);
                pstmtMySQL.setInt(3, stock);
                pstmtMySQL.executeUpdate();

                try (ResultSet generatedKeys = pstmtMySQL.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id_producto = generatedKeys.getInt(1);

                        // Get category and provider IDs from PostgreSQL
                        int id_categoria = getCategoriaId(connPostgres, nombre_categoria);
                        int id_proveedor = getProveedorId(connPostgres, nif);

                        // Insert into PostgreSQL
                        try (PreparedStatement pstmtPostgres = connPostgres.prepareStatement(
                                "INSERT INTO productos (id_producto, id_proveedor, id_categoria) VALUES (?, ?, ?)")) {
                            pstmtPostgres.setInt(1, id_producto);
                            pstmtPostgres.setInt(2, id_proveedor);
                            pstmtPostgres.setInt(3, id_categoria);
                            pstmtPostgres.executeUpdate();
                        }

                        connMySQL.commit();
                        connPostgres.commit();
                        System.out.println("Producto creado exitosamente en ambas bases de datos.");
                    } else {
                        throw new SQLException("Creating product failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            try {
                if (connMySQL != null)
                    connMySQL.rollback();
                if (connPostgres != null)
                    connPostgres.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (connMySQL != null)
                    connMySQL.close();
                if (connPostgres != null)
                    connPostgres.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Helper method to get categoria ID
    private static int getCategoriaId(Connection conn, String nombre_categoria) throws SQLException {
        try (PreparedStatement pstmt = conn
                .prepareStatement("SELECT id_categoria FROM categorias WHERE nombre_categoria = ?")) {
            pstmt.setString(1, nombre_categoria);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_categoria");
                } else {
                    throw new SQLException("Categoria not found: " + nombre_categoria);
                }
            }
        }
    }

    // Helper method to get proveedor ID
    private static int getProveedorId(Connection conn, String nif) throws SQLException {
        try (PreparedStatement pstmt = conn
                .prepareStatement("SELECT id_proveedor FROM proveedores WHERE (contacto).nif = ?")) {
            pstmt.setString(1, nif);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_proveedor");
                } else {
                    throw new SQLException("Proveedor not found with NIF: " + nif);
                }
            }
        }
    }

    // 7. Eliminar un producto por su nombre (MySQL + PostgreSQL)
    public static void eliminarProductoPorNombre(String nombre) {
        Connection connMySQL = null;
        Connection connPostgres = null;
        try {
            connMySQL = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
            connPostgres = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD);

            connMySQL.setAutoCommit(false);
            connPostgres.setAutoCommit(false);

            int id_producto;
            // Get product ID from MySQL
            try (PreparedStatement pstmt = connMySQL
                    .prepareStatement("SELECT id_producto FROM productos WHERE nombre_producto = ?")) {
                pstmt.setString(1, nombre);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        id_producto = rs.getInt("id_producto");
                    } else {
                        throw new SQLException("Product not found: " + nombre);
                    }
                }
            }

            // Delete from MySQL
            try (PreparedStatement pstmt = connMySQL.prepareStatement("DELETE FROM productos WHERE id_producto = ?")) {
                pstmt.setInt(1, id_producto);
                pstmt.executeUpdate();
            }

            // Delete from PostgreSQL
            try (PreparedStatement pstmt = connPostgres
                    .prepareStatement("DELETE FROM productos WHERE id_producto = ?")) {
                pstmt.setInt(1, id_producto);
                pstmt.executeUpdate();
            }

            connMySQL.commit();
            connPostgres.commit();
            System.out.println("Producto eliminado exitosamente de ambas bases de datos.");
        } catch (SQLException e) {
            try {
                if (connMySQL != null)
                    connMySQL.rollback();
                if (connPostgres != null)
                    connPostgres.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (connMySQL != null)
                    connMySQL.close();
                if (connPostgres != null)
                    connPostgres.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 8. Listar los productos con bajo stock (MySQL)
    public static void listarProductosBajoStock(int stock) {
        try (Connection conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
                PreparedStatement pstmt = conn
                        .prepareStatement("SELECT nombre_producto, stock FROM productos WHERE stock < ?")) {
            pstmt.setInt(1, stock);
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("Productos con bajo stock:");
                while (rs.next()) {
                    System.out.printf("Nombre: %s, Stock: %d%n", rs.getString("nombre_producto"), rs.getInt("stock"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 9. Obtener el total de pedidos realizados por cada usuario (MySQL)
    public static void obtenerTotalPedidosUsuarios() {
        try (Connection conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT u.nombre, COUNT(p.id_pedido) as total_pedidos " +
                                "FROM usuarios u LEFT JOIN pedidos p ON u.id_usuario = p.id_usuario " +
                                "GROUP BY u.id_usuario, u.nombre")) {
            System.out.println("Total de pedidos por usuario:");
            while (rs.next()) {
                System.out.printf("Usuario: %s, Total pedidos: %d%n", rs.getString("nombre"),
                        rs.getInt("total_pedidos"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 10. Obtener la cantidad de productos almacenados por cada almacén
    // (PostgreSQL)
    public static void obtenerCantidadProductosEnCadaAlmacen() {
        try (Connection conn = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT a.nombre_almacen, COALESCE(SUM(ap.cantidad), 0) as total_productos " +
                                "FROM almacenes a LEFT JOIN almacenes_productos ap ON a.id_almacen = ap.id_almacen " +
                                "GROUP BY a.id_almacen, a.nombre_almacen")) {
            System.out.println("Cantidad de productos por almacén:");
            while (rs.next()) {
                System.out.printf("Almacén: %s, Total productos: %d%n", rs.getString("nombre_almacen"),
                        rs.getInt("total_productos"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 11. Listar todos los productos con sus respectivas categorías y proveedores
    public static void listarTodosProductosConCategoriaYProveedor() {
        try (Connection connPostgres = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD);
                Connection connMySQL = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
                PreparedStatement pstmtPostgres = connPostgres.prepareStatement(
                        "SELECT p.id_producto, c.nombre_categoria, pr.nombre_proveedor, " +
                                "(pr.contacto).nif, (pr.contacto).telefono, (pr.contacto).email " +
                                "FROM productos p " +
                                "JOIN categorias c ON p.id_categoria = c.id_categoria " +
                                "JOIN proveedores pr ON p.id_proveedor = pr.id_proveedor");
                ResultSet rsPostgres = pstmtPostgres.executeQuery()) {

            System.out.println("Listado de productos con categoría y proveedor:");
            while (rsPostgres.next()) {
                int idProducto = rsPostgres.getInt("id_producto");
                String categoria = rsPostgres.getString("nombre_categoria");
                String proveedor = rsPostgres.getString("nombre_proveedor");
                String nif = rsPostgres.getString("nif");
                String telefono = rsPostgres.getString("telefono");
                String email = rsPostgres.getString("email");

                // Get product details from MySQL
                try (PreparedStatement pstmtMySQL = connMySQL.prepareStatement(
                        "SELECT nombre_producto, precio, stock FROM productos WHERE id_producto = ?")) {
                    pstmtMySQL.setInt(1, idProducto);
                    try (ResultSet rsMySQL = pstmtMySQL.executeQuery()) {
                        if (rsMySQL.next()) {
                            String nombre = rsMySQL.getString("nombre_producto");
                            double precio = rsMySQL.getDouble("precio");
                            int stock = rsMySQL.getInt("stock");

                            System.out.printf("Producto: %s, Precio: %.2f, Stock: %d, Categoría: %s, " +
                                    "Proveedor: %s, NIF: %s, Teléfono: %s, Email: %s%n",
                                    nombre, precio, stock, categoria, proveedor, nif, telefono, email);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 12. Obtener todos los usuarios que han comprado algún producto de una
    // categoria dada
    public static void obtenerUsuariosCompraronProductosCategoria(int idCategoria) {
        try (Connection connPostgres = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD);
                Connection connMySQL = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD)) {

            // Get product IDs from PostgreSQL
            List<Integer> productIds = new ArrayList<>();
            try (PreparedStatement pstmtPostgres = connPostgres.prepareStatement(
                    "SELECT id_producto FROM productos WHERE id_categoria = ?")) {
                pstmtPostgres.setInt(1, idCategoria);
                try (ResultSet rsPostgres = pstmtPostgres.executeQuery()) {
                    while (rsPostgres.next()) {
                        productIds.add(rsPostgres.getInt("id_producto"));
                    }
                }
            }

            if (productIds.isEmpty()) {
                System.out.println("No se encontraron productos para la categoría especificada.");
                return;
            }

            // Get users who bought these products from MySQL
            String sql = "SELECT DISTINCT u.nombre FROM usuarios u " +
                    "JOIN pedidos p ON u.id_usuario = p.id_usuario " +
                    "JOIN pedidos_productos pp ON p.id_pedido = pp.id_pedido " +
                    "WHERE pp.id_producto IN (" + String.join(",", Collections.nCopies(productIds.size(), "?")) + ")";

            try (PreparedStatement pstmtMySQL = connMySQL.prepareStatement(sql)) {
                for (int i = 0; i < productIds.size(); i++) {
                    pstmtMySQL.setInt(i + 1, productIds.get(i));
                }
                try (ResultSet rsMySQL = pstmtMySQL.executeQuery()) {
                    System.out.println("Usuarios que han comprado productos de la categoría especificada:");
                    while (rsMySQL.next()) {
                        System.out.println(rsMySQL.getString("nombre"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
