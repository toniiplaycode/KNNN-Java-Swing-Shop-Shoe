package view.manager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.sql.*;
import utils.DBConnection;
import com.formdev.flatlaf.FlatLightLaf;
import java.text.DecimalFormat;

public class Payments extends JPanel {
    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JTextField idField;
    private JComboBox<String> orderComboBox;
    private JComboBox<String> methodComboBox;
    private JComboBox<String> statusComboBox;
    private DecimalFormat formatter = new DecimalFormat("#,###");
    
    public Payments() {
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
        loadOrdersComboBox();
        loadPaymentData();
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Tìm kiếm thanh toán...");
        
        JButton searchButton = createStyledButton("Tìm kiếm", new Color(70, 130, 180));
        searchButton.addActionListener(e -> searchPayments());
        
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Add button
        JButton addButton = createStyledButton("Thêm thanh toán", new Color(46, 204, 113));
        addButton.addActionListener(e -> clearForm());
        
        panel.add(searchPanel, BorderLayout.WEST);
        panel.add(addButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table model
        String[] columns = {"ID", "Đơn hàng", "Phương thức", "Trạng thái", "Ngày thanh toán"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        paymentTable = new JTable(tableModel);
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paymentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadPaymentToForm();
            }
        });
        
        // Style table
        paymentTable.setRowHeight(30);
        paymentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        paymentTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(paymentTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createTitledBorder("Thông tin thanh toán")
        ));
        
        // Create form fields
        idField = createTextField("ID");
        idField.setEnabled(false);
        orderComboBox = new JComboBox<>();
        methodComboBox = new JComboBox<>(new String[]{
            "cash", "credit_card", "paypal", "bank_transfer"
        });
        statusComboBox = new JComboBox<>(new String[]{
            "pending", "paid", "failed", "refunded"
        });
        
        // Create grid panel for form fields
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        fieldsPanel.add(createFormRow("Đơn hàng:", orderComboBox));
        fieldsPanel.add(createFormRow("Phương thức:", methodComboBox));
        fieldsPanel.add(createFormRow("Trạng thái:", statusComboBox));
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        JButton saveButton = createStyledButton("Lưu", new Color(46, 204, 113));
        JButton deleteButton = createStyledButton("Xóa", new Color(231, 76, 60));
        JButton clearButton = createStyledButton("Hủy", new Color(149, 165, 166));
        
        saveButton.addActionListener(e -> savePayment());
        deleteButton.addActionListener(e -> deletePayment());
        clearButton.addActionListener(e -> clearForm());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        // Add components to main panel
        panel.add(fieldsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadOrdersComboBox() {
        orderComboBox.removeAllItems();
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT o.id, u.username, o.total_price FROM orders o " +
                          "JOIN users u ON o.user_id = u.id " +
                          "WHERE o.id NOT IN (SELECT order_id FROM payments) " +
                          "ORDER BY o.order_date DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String item = String.format("#%d - %s (%s đ)", 
                    rs.getInt("id"),
                    rs.getString("username"),
                    formatter.format(rs.getDouble("total_price"))
                );
                orderComboBox.addItem(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadPaymentData() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT p.*, o.total_price, u.username " +
                          "FROM payments p " +
                          "JOIN orders o ON p.order_id = o.id " +
                          "JOIN users u ON o.user_id = u.id " +
                          "ORDER BY p.payment_date DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    String.format("#%d - %s (%s đ)", 
                        rs.getInt("order_id"),
                        rs.getString("username"),
                        formatter.format(rs.getDouble("total_price"))),
                    getPaymentMethodText(rs.getString("payment_method")),
                    getStatusText(rs.getString("status")),
                    rs.getTimestamp("payment_date")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }
    
    private void loadPaymentToForm() {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow >= 0) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            String orderInfo = tableModel.getValueAt(selectedRow, 1).toString();
            orderComboBox.setSelectedItem(orderInfo);
            
            // Chuyển đổi text hiển thị về giá trị trong database
            String paymentMethod = tableModel.getValueAt(selectedRow, 2).toString();
            if (paymentMethod.equals("Tiền mặt")) methodComboBox.setSelectedItem("cash");
            else if (paymentMethod.equals("Thẻ tín dụng")) methodComboBox.setSelectedItem("credit_card");
            else if (paymentMethod.equals("PayPal")) methodComboBox.setSelectedItem("paypal");
            else if (paymentMethod.equals("Chuyển khoản")) methodComboBox.setSelectedItem("bank_transfer");
            
            statusComboBox.setSelectedItem(getStatusValue(tableModel.getValueAt(selectedRow, 3).toString()));
        }
    }
    
    private void savePayment() {
        try (Connection conn = DBConnection.getConnection()) {
            String orderId = orderComboBox.getSelectedItem().toString().split(" - ")[0].substring(1);
            String method = methodComboBox.getSelectedItem().toString();
            String status = statusComboBox.getSelectedItem().toString();
            
            String query;
            if (idField.getText().isEmpty()) {
                query = "INSERT INTO payments (order_id, payment_method, status, payment_date) VALUES (?, ?, ?, NOW())";
            } else {
                query = "UPDATE payments SET order_id=?, payment_method=?, status=? WHERE id=?";
            }
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(orderId));
            stmt.setString(2, method);
            stmt.setString(3, status);
            
            if (!idField.getText().isEmpty()) {
                stmt.setInt(4, Integer.parseInt(idField.getText()));
            }
            
            stmt.executeUpdate();
            loadPaymentData();
            loadOrdersComboBox();
            clearForm();
            JOptionPane.showMessageDialog(this, "Lưu thành công!");
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu: " + e.getMessage());
        }
    }
    
    private void deletePayment() {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa thanh toán này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    String query = "DELETE FROM payments WHERE id=?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, Integer.parseInt(idField.getText()));
                    stmt.executeUpdate();
                    
                    loadPaymentData();
                    loadOrdersComboBox();
                    clearForm();
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage());
                }
            }
        }
    }
    
    private void searchPayments() {
        String searchText = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT p.*, o.total_price, u.username " +
                          "FROM payments p " +
                          "JOIN orders o ON p.order_id = o.id " +
                          "JOIN users u ON o.user_id = u.id " +
                          "WHERE LOWER(u.username) LIKE ? OR " +
                          "LOWER(p.payment_method) LIKE ? OR " +
                          "LOWER(p.status) LIKE ? " +
                          "ORDER BY p.payment_date DESC";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            String pattern = "%" + searchText + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    String.format("#%d - %s (%s đ)", 
                        rs.getInt("order_id"),
                        rs.getString("username"),
                        formatter.format(rs.getDouble("total_price"))),
                    getPaymentMethodText(rs.getString("payment_method")),
                    getStatusText(rs.getString("status")),
                    rs.getTimestamp("payment_date")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }
    
    private void clearForm() {
        idField.setText("");
        if (orderComboBox.getItemCount() > 0) {
            orderComboBox.setSelectedIndex(0);
        }
        methodComboBox.setSelectedIndex(0);
        statusComboBox.setSelectedIndex(0);
        paymentTable.clearSelection();
    }
    
    private String getStatusText(String status) {
        switch (status) {
            case "pending": return "Chờ xử lý";
            case "paid": return "Đã thanh toán";
            case "failed": return "Thất bại";
            default: return status;
        }
    }
    
    private String getStatusValue(String displayText) {
        switch (displayText) {
            case "Chờ xử lý": return "pending";
            case "Đã thanh toán": return "paid";
            case "Thất bại": return "failed";
            default: return displayText;
        }
    }
    
    private String getPaymentMethodText(String method) {
        switch (method) {
            case "cash": return "Tiền mặt";
            case "credit_card": return "Thẻ tín dụng";
            case "paypal": return "PayPal";
            case "bank_transfer": return "Chuyển khoản";
            default: return method;
        }
    }
    
    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.putClientProperty("JTextField.placeholderText", placeholder);
        return field;
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
            
            JFrame frame = new JFrame("Quản lý thanh toán");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new Payments());
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 