package view.manager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.sql.*;
import utils.DBConnection;
import com.formdev.flatlaf.FlatLightLaf;

public class Stock extends JPanel {
    private JTable stockTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> productComboBox;
    private JComboBox<String> colorComboBox;
    private JComboBox<String> sizeComboBox;
    private JSpinner quantitySpinner;
    
    public Stock() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top Panel - Search and Add Button
        JPanel topPanel = createTopPanel();
        
        // Center Panel - Contains Table and Form
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        
        // Table Panel
        JPanel tablePanel = createTablePanel();
        
        // Form Panel
        JPanel formPanel = createFormPanel();
        
        // Add table and form to center panel
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        centerPanel.add(formPanel, BorderLayout.SOUTH);
        
        // Add panels to main panel
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        
        // Load initial data
        loadProducts();
        loadColors();
        loadSizes();
        loadStockData();
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Tìm kiếm kho hàng...");
        
        JButton searchButton = createStyledButton("Tìm kiếm", new Color(70, 130, 180));
        searchButton.addActionListener(e -> searchStock());
        
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Add button
        JButton addButton = createStyledButton("Thêm kho hàng", new Color(46, 204, 113));
        addButton.addActionListener(e -> clearForm());
        
        panel.add(searchPanel, BorderLayout.WEST);
        panel.add(addButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        
        // Create table model
        String[] columns = {"ID", "Sản phẩm", "Màu sắc", "Kích thước", "Số lượng"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép edit trực tiếp trên bảng
            }
        };
        
        stockTable = new JTable(tableModel);
        stockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stockTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadStockToForm();
            }
        });
        
        // Style table
        stockTable.setRowHeight(30);
        stockTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        stockTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(stockTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createTitledBorder("Thông tin kho hàng")
        ));
        
        // Create form fields
        productComboBox = new JComboBox<>();
        colorComboBox = new JComboBox<>();
        sizeComboBox = new JComboBox<>();
        quantitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
        
        // Create grid panel for form fields
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        fieldsPanel.add(createFormRow("Sản phẩm:", productComboBox));
        fieldsPanel.add(createFormRow("Màu sắc:", colorComboBox));
        fieldsPanel.add(createFormRow("Kích thước:", sizeComboBox));
        fieldsPanel.add(createFormRow("Số lượng:", quantitySpinner));
        
        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        JButton saveButton = createStyledButton("Lưu", new Color(46, 204, 113));
        JButton deleteButton = createStyledButton("Xóa", new Color(231, 76, 60));
        JButton clearButton = createStyledButton("Làm mới", new Color(149, 165, 166));
        
        saveButton.addActionListener(e -> saveStock());
        deleteButton.addActionListener(e -> deleteStock());
        clearButton.addActionListener(e -> clearForm());
        
        buttonsPanel.add(saveButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(clearButton);
        
        panel.add(fieldsPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(buttonsPanel);
        
        return panel;
    }
    
    private void loadProducts() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT id, name FROM products WHERE status = '1' ORDER BY name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            productComboBox.removeAllItems();
            while (rs.next()) {
                productComboBox.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadColors() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT id, color_name FROM colors ORDER BY color_name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            colorComboBox.removeAllItems();
            while (rs.next()) {
                colorComboBox.addItem(rs.getInt("id") + " - " + rs.getString("color_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadSizes() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT id, size FROM sizes ORDER BY size";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            sizeComboBox.removeAllItems();
            while (rs.next()) {
                sizeComboBox.addItem(rs.getInt("id") + " - " + rs.getString("size"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadStockData() {
        tableModel.setRowCount(0);
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT pv.*, p.name as product_name, c.color_name, s.size " +
                          "FROM product_variants pv " +
                          "JOIN products p ON pv.product_id = p.id " +
                          "JOIN colors c ON pv.color_id = c.id " +
                          "JOIN sizes s ON pv.size_id = s.id " +
                          "ORDER BY p.name, c.color_name, s.size";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("product_name"),
                    rs.getString("color_name"),
                    rs.getString("size"),
                    rs.getInt("stock")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }
    
    private void loadStockToForm() {
        int selectedRow = stockTable.getSelectedRow();
        if (selectedRow >= 0) {
            String productName = tableModel.getValueAt(selectedRow, 1).toString();
            String colorName = tableModel.getValueAt(selectedRow, 2).toString();
            String size = tableModel.getValueAt(selectedRow, 3).toString();
            int quantity = Integer.parseInt(tableModel.getValueAt(selectedRow, 4).toString());
            
            // Set selected items in comboboxes
            for (int i = 0; i < productComboBox.getItemCount(); i++) {
                if (productComboBox.getItemAt(i).contains(productName)) {
                    productComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            for (int i = 0; i < colorComboBox.getItemCount(); i++) {
                if (colorComboBox.getItemAt(i).contains(colorName)) {
                    colorComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            for (int i = 0; i < sizeComboBox.getItemCount(); i++) {
                if (sizeComboBox.getItemAt(i).contains(size)) {
                    sizeComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            quantitySpinner.setValue(quantity);
        }
    }
    
    private void saveStock() {
        if (productComboBox.getSelectedIndex() == -1 ||
            colorComboBox.getSelectedIndex() == -1 ||
            sizeComboBox.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ thông tin!");
            return;
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            int productId = Integer.parseInt(productComboBox.getSelectedItem().toString().split(" - ")[0]);
            int colorId = Integer.parseInt(colorComboBox.getSelectedItem().toString().split(" - ")[0]);
            int sizeId = Integer.parseInt(sizeComboBox.getSelectedItem().toString().split(" - ")[0]);
            int quantity = (int) quantitySpinner.getValue();
            
            // Check if variant exists
            String checkQuery = "SELECT id FROM product_variants " +
                              "WHERE product_id = ? AND color_id = ? AND size_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, productId);
            checkStmt.setInt(2, colorId);
            checkStmt.setInt(3, sizeId);
            
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Update existing variant
                String updateQuery = "UPDATE product_variants SET stock = ? " +
                                   "WHERE product_id = ? AND color_id = ? AND size_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, quantity);
                updateStmt.setInt(2, productId);
                updateStmt.setInt(3, colorId);
                updateStmt.setInt(4, sizeId);
                updateStmt.executeUpdate();
            } else {
                // Insert new variant
                String insertQuery = "INSERT INTO product_variants (product_id, color_id, size_id, stock) " +
                                   "VALUES (?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setInt(1, productId);
                insertStmt.setInt(2, colorId);
                insertStmt.setInt(3, sizeId);
                insertStmt.setInt(4, quantity);
                insertStmt.executeUpdate();
            }
            
            loadStockData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Lưu thành công!");
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu: " + e.getMessage());
        }
    }
    
    private void deleteStock() {
        int selectedRow = stockTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa kho hàng này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    String query = "DELETE FROM product_variants WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString()));
                    stmt.executeUpdate();
                    
                    loadStockData();
                    clearForm();
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage());
                }
            }
        }
    }
    
    private void searchStock() {
        String searchText = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT pv.*, p.name as product_name, c.color_name, s.size " +
                          "FROM product_variants pv " +
                          "JOIN products p ON pv.product_id = p.id " +
                          "JOIN colors c ON pv.color_id = c.id " +
                          "JOIN sizes s ON pv.size_id = s.id " +
                          "WHERE LOWER(p.name) LIKE ? OR " +
                          "LOWER(c.color_name) LIKE ? OR " +
                          "LOWER(s.size) LIKE ? " +
                          "ORDER BY p.name, c.color_name, s.size";
                          
            PreparedStatement stmt = conn.prepareStatement(query);
            String pattern = "%" + searchText + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("product_name"),
                    rs.getString("color_name"),
                    rs.getString("size"),
                    rs.getInt("stock")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }
    
    private void clearForm() {
        if (productComboBox.getItemCount() > 0) productComboBox.setSelectedIndex(0);
        if (colorComboBox.getItemCount() > 0) colorComboBox.setSelectedIndex(0);
        if (sizeComboBox.getItemCount() > 0) sizeComboBox.setSelectedIndex(0);
        quantitySpinner.setValue(0);
        stockTable.clearSelection();
    }
    
    private JPanel createFormRow(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(5, 2));
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            
            JFrame frame = new JFrame("Quản lý kho hàng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new Stock());
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 