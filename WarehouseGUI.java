import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class WarehouseGUI extends JFrame {
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, xField, yField;
    private JLabel statusLabel;
    public JButton addButton, deleteButton, findNearestButton, refreshButton;
    // Colors
    private Color backgroundColor = new Color(240, 248, 255);
    private Color buttonColor = new Color(70, 130, 180);
    private Color addButtonColor = new Color(46, 139, 87);
    private Color deleteButtonColor = new Color(178, 34, 34);
    private Color headerColor = new Color(25, 25, 112);

    public WarehouseGUI() {
        setTitle("Warehouse Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Init layout
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(backgroundColor);

        // Components
        createTable();
        createInputPanel();
        createButtonPanel();

        // Status
        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(backgroundColor);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void createTable() {
        // Table setup
        tableModel = new DefaultTableModel(new String[] { "ID", "Name", "X", "Y" }, 0);
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setBackground(new Color(255, 255, 255));
        productTable.setGridColor(new Color(220, 220, 220));
        productTable.getTableHeader().setBackground(headerColor);
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.setRowHeight(25);

        // Scroll
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.getViewport().setBackground(backgroundColor);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createInputPanel() {
        // Input grid
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBackground(backgroundColor);

        nameField = createStyledTextField();
        xField = createStyledTextField();
        yField = createStyledTextField();

        // Labels
        JLabel[] labels = {
                new JLabel("Product Name:"),
                new JLabel("X Coordinate:"),
                new JLabel("Y Coordinate:")
        };

        for (JLabel label : labels) {
            label.setForeground(headerColor);
            label.setFont(new Font("Arial", Font.BOLD, 12));
        }

        inputPanel.add(labels[0]);
        inputPanel.add(nameField);
        inputPanel.add(labels[1]);
        inputPanel.add(xField);
        inputPanel.add(labels[2]);
        inputPanel.add(yField);

        add(inputPanel, BorderLayout.NORTH);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(15);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return field;
    }

    private void createButtonPanel() {
        // Button grid
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        buttonPanel.setBackground(backgroundColor);

        addButton = createStyledButton("Add Product", addButtonColor);
        deleteButton = createStyledButton("Delete Product", deleteButtonColor);
        findNearestButton = createStyledButton("Find Nearest", buttonColor);
        refreshButton = createStyledButton("Refresh", buttonColor);

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(findNearestButton);
        buttonPanel.add(refreshButton);

        // Container
        JPanel eastPanel = new JPanel(new FlowLayout());
        eastPanel.setBackground(backgroundColor);
        eastPanel.add(buttonPanel);
        add(eastPanel, BorderLayout.EAST);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(130, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    // Getters
    public JTable getProductTable() {
        return productTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getNameField() {
        return nameField;
    }

    public JTextField getXField() {
        return xField;
    }

    public JTextField getYField() {
        return yField;
    }

    public void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? new Color(178, 34, 34) : new Color(46, 139, 87));
    }

    public void clearInputFields() {
        nameField.setText("");
        xField.setText("");
        yField.setText("");
    }
}