import java.sql.*;
import java.util.*;
import javax.swing.*;

public class WarehouseApp {
    private static Connection conn;
    private static WarehouseGUI gui;

    public static void main(String[] args) {
        // Init GUI
        SwingUtilities.invokeLater(() -> {
            try {
                conn = DBManager.getConnection();
                gui = new WarehouseGUI();
                setupEventHandlers();
                refreshProductTable();
                gui.setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
                System.exit(1);
            }
        });
    }

    private static void setupEventHandlers() {
        // Events
        gui.addButton.addActionListener(e -> addProduct());
        gui.deleteButton.addActionListener(e -> deleteProduct());
        gui.findNearestButton.addActionListener(e -> findNearestProduct());
        gui.refreshButton.addActionListener(e -> refreshProductTable());
    }

    private static void addProduct() {
        try {
            // Get input
            String name = gui.getNameField().getText().trim();
            String xText = gui.getXField().getText().trim();
            String yText = gui.getYField().getText().trim();

            if (name.isEmpty() || xText.isEmpty() || yText.isEmpty()) {
                gui.setStatus("Please fill all fields", true);
                return;
            }

            int x = Integer.parseInt(xText);
            int y = Integer.parseInt(yText);

            // Save
            String sql = "INSERT INTO products(name, x, y) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setInt(2, x);
                pstmt.setInt(3, y);
                pstmt.executeUpdate();
                gui.setStatus("Product added successfully", false);
                gui.clearInputFields();
                refreshProductTable();
            }
        } catch (NumberFormatException e) {
            gui.setStatus("Please enter valid coordinates", true);
        } catch (SQLException e) {
            gui.setStatus("Error adding product: " + e.getMessage(), true);
        }
    }

    private static void deleteProduct() {
        try {
            // Get selection
            int row = gui.getProductTable().getSelectedRow();
            if (row == -1) {
                gui.setStatus("Please select a product to delete", true);
                return;
            }

            // Delete
            int id = (int) gui.getTableModel().getValueAt(row, 0);
            String sql = "DELETE FROM products WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    gui.setStatus("Product deleted successfully", false);
                    refreshProductTable();
                }
            }
        } catch (SQLException e) {
            gui.setStatus("Error deleting product: " + e.getMessage(), true);
        }
    }

    private static void findNearestProduct() {
        try {
            // Get source
            int row = gui.getProductTable().getSelectedRow();
            if (row == -1) {
                gui.setStatus("Please select a source product", true);
                return;
            }

            String targetName = JOptionPane.showInputDialog(gui, "Enter target product name:");
            if (targetName == null || targetName.trim().isEmpty()) {
                return;
            }

            // Load data
            List<Product> products = new ArrayList<>();
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {
                while (rs.next()) {
                    products.add(new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("x"),
                            rs.getInt("y")));
                }
            }

            if (products.size() < 2) {
                gui.setStatus("At least two products required", true);
                return;
            }

            // Find nearest
            int sourceId = (int) gui.getTableModel().getValueAt(row, 0);
            int startIndex = -1;
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).id == sourceId) {
                    startIndex = i;
                    break;
                }
            }

            int nearestIdx = findNearestProduct(targetName, products, startIndex);
            if (nearestIdx != -1) {
                Product p = products.get(nearestIdx);
                String message = String.format("Nearest product '%s' found!\n\nLocation: (%d, %d)\nProduct ID: %d",
                        targetName, p.x, p.y, p.id);
                JOptionPane.showMessageDialog(gui,
                        message,
                        "Nearest Product Found",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(gui,
                        "No other product with that name found.",
                        "Product Not Found",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            gui.setStatus("Error finding nearest product: " + e.getMessage(), true);
        }
    }

    private static int findNearestProduct(String targetName, List<Product> products, int startIndex) {
        // Calculate
        int nearestIdx = -1;
        int minDistance = Integer.MAX_VALUE;
        Product source = products.get(startIndex);

        for (int i = 0; i < products.size(); i++) {
            if (i != startIndex && products.get(i).name.equalsIgnoreCase(targetName)) {
                Product target = products.get(i);
                int distance = (int) Math.sqrt(
                        Math.pow(source.x - target.x, 2) +
                                Math.pow(source.y - target.y, 2));
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestIdx = i;
                }
            }
        }
        return nearestIdx;
    }

    private static void refreshProductTable() {
        try {
            // Refresh
            gui.getTableModel().setRowCount(0);
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {
                while (rs.next()) {
                    gui.getTableModel().addRow(new Object[] {
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("x"),
                            rs.getInt("y")
                    });
                }
            }
        } catch (SQLException e) {
            gui.setStatus("Error refreshing table: " + e.getMessage(), true);
        }
    }
}
