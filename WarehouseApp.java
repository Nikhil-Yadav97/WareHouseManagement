import java.sql.*;
import java.util.*;

public class WarehouseApp {

    public static void main(String[] args) throws Exception {
        try (Connection conn = DBManager.getConnection()) {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\n--- Warehouse Management Menu ---");
                System.out.println("1. Add Product");
                System.out.println("2. Show All Products");
                System.out.println("3. Find Nearest Product");
                System.out.println("4. Exit");
                System.out.print("Enter choice: ");
                int choice = scanner.nextInt();

                if (choice == 1) {
                    scanner.nextLine(); // clear buffer
                    System.out.print("Enter product name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter x coordinate: ");
                    int x = scanner.nextInt();
                    System.out.print("Enter y coordinate: ");
                    int y = scanner.nextInt();

                    String sql = "INSERT INTO products(name, x, y) VALUES (?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, name);
                        pstmt.setInt(2, x);
                        pstmt.setInt(3, y);
                        pstmt.executeUpdate();
                        System.out.println("Product added.");
                    }

                } else if (choice == 2) {
                    String sql = "SELECT * FROM products";
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(sql)) {
                        System.out.println("ID | Name | X | Y");
                        while (rs.next()) {
                            System.out.printf("%d | %s | %d | %d\n",
                                    rs.getInt("id"),
                                    rs.getString("name"),
                                    rs.getInt("x"),
                                    rs.getInt("y"));
                        }
                    }

                } else if (choice == 3) {
                    List<Product> products = new ArrayList<>();
                    String sql = "SELECT * FROM products";
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(sql)) {
                        while (rs.next()) {
                            products.add(new Product(
                                    rs.getInt("id"),
                                    rs.getString("name"),
                                    rs.getInt("x"),
                                    rs.getInt("y")
                            ));
                        }
                    }

                    if (products.size() < 2) {
                        System.out.println("At least two products required.");
                        continue;
                    }

                    System.out.print("Enter source product ID: ");
                    int sourceId = scanner.nextInt();

                    int startIndex = -1;
                    for (int i = 0; i < products.size(); i++) {
                        if (products.get(i).id == sourceId) {
                            startIndex = i;
                            break;
                        }
                    }

                    if (startIndex == -1) {
                        System.out.println("Invalid ID.");
                        continue;
                    }

                    scanner.nextLine(); // clear buffer
                    System.out.print("Enter target product name: ");
                    String targetName = scanner.nextLine();

                    int n = products.size();
                    int[][] graph = new int[n][n];

                    for (int i = 0; i < n; i++) {
                        for (int j = 0; j < n; j++) {
                            if (i != j) {
                                int dx = products.get(i).x - products.get(j).x;
                                int dy = products.get(i).y - products.get(j).y;
                                graph[i][j] = (int) Math.sqrt(dx * dx + dy * dy);
                            }
                        }
                    }

                    int nearestIdx = Dijkstra.findNearestProduct(targetName, products, graph, startIndex);
                    if (nearestIdx != -1) {
                        Product p = products.get(nearestIdx);
                        System.out.println("Nearest product '" + targetName + "' is at (" + p.x + "," + p.y + "), ID: " + p.id);
                    } else {
                        System.out.println("No other product with that name found.");
                    }

                } else if (choice == 4) {
                    System.out.println("Exiting...");
                    break;
                } else {
                    System.out.println("Invalid choice!");
                }
            }

            scanner.close();
        }
    }
}
