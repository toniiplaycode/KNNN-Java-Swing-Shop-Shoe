package view.manager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.sql.*;
import utils.DBConnection;
import com.formdev.flatlaf.FlatLightLaf;

public class Users extends JPanel {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JComboBox<String> roleComboBox;
    private JComboBox<String> statusComboBox;
    
    public Users() {
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
        loadUserData();
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Tìm kiếm người dùng...");
        
        JButton searchButton = createStyledButton("Tìm kiếm", new Color(70, 130, 180));
        searchButton.addActionListener(e -> searchUsers());
        
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Add button
        JButton addButton = createStyledButton("Thêm người dùng", new Color(46, 204, 113));
        addButton.addActionListener(e -> clearForm());
        
        panel.add(searchPanel, BorderLayout.WEST);
        panel.add(addButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        
        // Create table model
        String[] columns = {"ID", "Tên đăng nhập", "Email", "Số điện thoại", "Địa chỉ", "Vai trò", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadUserToForm();
            }
        });
        
        // Style table
        userTable.setRowHeight(30);
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        userTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createTitledBorder("Thông tin người dùng")
        ));
                
        // Create form fields
        usernameField = createTextField("Tên đăng nhập");
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        emailField = createTextField("Email");
        phoneField = createTextField("Số điện thoại");
        addressField = createTextField("Địa chỉ");
        
        roleComboBox = new JComboBox<>(new String[]{"customer - Khách hàng", "employee - Nhân viên"});
        statusComboBox = new JComboBox<>(new String[]{"0 - Không hoạt động", "1 - Hoạt động"});
        
        styleComboBox(roleComboBox);
        styleComboBox(statusComboBox);
        
        // Create grid panel for form fields
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add form fields to grid
        fieldsPanel.add(createFormRow("Tên đăng nhập:", usernameField));
        fieldsPanel.add(createFormRow("Mật khẩu:", passwordField));
        fieldsPanel.add(createFormRow("Email:", emailField));
        fieldsPanel.add(createFormRow("Số điện thoại:", phoneField));
        fieldsPanel.add(createFormRow("Địa chỉ:", addressField));
        fieldsPanel.add(createFormRow("Vai trò:", roleComboBox));
        fieldsPanel.add(createFormRow("Trạng thái:", statusComboBox));
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        JButton saveButton = createStyledButton("Lưu", new Color(46, 204, 113));
        JButton deleteButton = createStyledButton("Xóa", new Color(231, 76, 60));
        JButton clearButton = createStyledButton("Hủy", new Color(149, 165, 166));
        
        saveButton.addActionListener(e -> saveUser());
        deleteButton.addActionListener(e -> deleteUser());
        clearButton.addActionListener(e -> clearForm());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        // Add components to main panel
        panel.add(fieldsPanel);
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return field;
    }
    
    private JPanel createFormRow(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(5, 2));
        panel.setBackground(Color.WHITE);
        
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        panel.add(jLabel, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        comboBox.setFont(new Font("Arial", Font.PLAIN, 12));
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void loadUserData() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE role != 'admin'";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    // Convert role for display
                    rs.getString("role").equals("customer") ? "Khách hàng" : "Nhân viên",
                    rs.getString("status").equals("1") ? "Hoạt động" : "Không hoạt động" // Convert status for display
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }
    
    private void loadUserToForm() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            usernameField.setText(tableModel.getValueAt(selectedRow, 1) != null ? tableModel.getValueAt(selectedRow, 1).toString() : "");
            emailField.setText(tableModel.getValueAt(selectedRow, 2) != null ? tableModel.getValueAt(selectedRow, 2).toString() : "");
            phoneField.setText(tableModel.getValueAt(selectedRow, 3) != null ? tableModel.getValueAt(selectedRow, 3).toString() : "");
            addressField.setText(tableModel.getValueAt(selectedRow, 4) != null ? tableModel.getValueAt(selectedRow, 4).toString() : "");
            
            // Set role combobox
            String role = tableModel.getValueAt(selectedRow, 5).toString();
            if (role.equals("Khách hàng")) {
                roleComboBox.setSelectedItem("customer - Khách hàng");
            } else if (role.equals("Nhân viên")) {
                roleComboBox.setSelectedItem("employee - Nhân viên");
            }
            
            // Set status combobox
            String status = tableModel.getValueAt(selectedRow, 6).toString();
            if (status.equals("Hoạt động")) {
                statusComboBox.setSelectedItem("1 - Hoạt động");
            } else {
                statusComboBox.setSelectedItem("0 - Không hoạt động");
            }
            
            passwordField.setText(""); // Clear password field for security
        }
    }
    
    private void saveUser() {
        try (Connection conn = DBConnection.getConnection()) {
            int selectedRow = userTable.getSelectedRow();
            String query;
            if (selectedRow >= 0) {
                // Update existing user
                query = "UPDATE users SET username=?, password=?, email=?, phone=?, address=?, role=?, status=? WHERE id=?";
            } else {
                // Insert new user
                query = "INSERT INTO users (username, password, email, phone, address, role, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            }
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, usernameField.getText());
            stmt.setString(2, new String(passwordField.getPassword()));
            stmt.setString(3, emailField.getText());
            stmt.setString(4, phoneField.getText());
            stmt.setString(5, addressField.getText());
            
            // Chỉ lưu các giá trị hợp lệ cho role
            String selectedRole = roleComboBox.getSelectedItem().toString().split(" - ")[0]; // Lấy giá trị trước dấu "-"
            stmt.setString(6, selectedRole);
            
            // Set status based on selected item
            String selectedStatus = statusComboBox.getSelectedItem().toString();
            stmt.setString(7, selectedStatus.startsWith("1") ? "1" : "0"); // Set status as "1" or "0"
            
            if (selectedRow >= 0) {
                stmt.setInt(8, Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString()));
            }
            
            stmt.executeUpdate();
            loadUserData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Lưu thành công!");
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu: " + e.getMessage());
        }
    }
    
    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa người dùng này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    String query = "DELETE FROM users WHERE id=?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString()));
                    stmt.executeUpdate();
                    
                    loadUserData();
                    clearForm();
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage());
                }
            }
        }
    }
    
    private void searchUsers() {
        String searchText = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE LOWER(username) LIKE ? OR LOWER(email) LIKE ? OR phone LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            String searchPattern = "%" + searchText + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("role"),
                    rs.getString("status")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }
    
    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        roleComboBox.setSelectedIndex(0);
        statusComboBox.setSelectedIndex(0);
        userTable.clearSelection();
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            
            JFrame frame = new JFrame("Quản lý người dùng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new Users());
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 