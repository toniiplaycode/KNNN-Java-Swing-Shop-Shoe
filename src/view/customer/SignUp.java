package view.customer;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import utils.DBConnection;

public class SignUp extends JFrame {
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField phoneField;
    private JTextField addressField;
    
    private Color primaryColor = new Color(41, 128, 185);
    private Color successColor = new Color(46, 204, 113);
    private Color dangerColor = new Color(231, 76, 60);
    
    public SignUp() {
        setTitle("Đăng ký");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));  // Căn giữa title
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);  // Căn trái panel
        JLabel titleLabel = new JLabel("ĐĂNG KÝ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);  // Căn trái form panel
        
        // Email
        JLabel emailLabel = createStyledLabel("Email:");
        emailField = createStyledTextField();
        
        // Username
        JLabel usernameLabel = createStyledLabel("Tên đăng nhập:");
        usernameField = createStyledTextField();
        
        // Password
        JLabel passwordLabel = createStyledLabel("Mật khẩu:");
        passwordField = createStyledPasswordField();
        
        // Confirm Password
        JLabel confirmPasswordLabel = createStyledLabel("Xác nhận mật khẩu:");
        confirmPasswordField = createStyledPasswordField();
        
        // Phone
        JLabel phoneLabel = createStyledLabel("Số điện thoại:");
        phoneField = createStyledTextField();
        
        // Address
        JLabel addressLabel = createStyledLabel("Địa chỉ:");
        addressField = createStyledTextField();
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));  // Căn trái, bỏ gap
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton registerButton = new JButton("Đăng ký");
        styleButton(registerButton, successColor, 130, 40);
        registerButton.addActionListener(e -> register());
        
        JButton cancelButton = new JButton("Hủy");
        styleButton(cancelButton, dangerColor, 130, 40);
        cancelButton.addActionListener(e -> dispose());
        
        // Login panel
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));  // Căn trái, bỏ gap
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel loginLabel = new JLabel("Đã có tài khoản? ");
        JButton loginButton = new JButton("Đăng nhập ngay");
        styleLinkButton(loginButton, primaryColor);
        loginButton.addActionListener(e -> {
            new SignIn().setVisible(true);
            dispose();
        });
        
        // Add components
        buttonsPanel.add(registerButton);
        buttonsPanel.add(Box.createHorizontalStrut(20));  // Tăng khoảng cách giữa các nút
        buttonsPanel.add(cancelButton);
        
        loginPanel.add(loginLabel);
        loginPanel.add(Box.createHorizontalStrut(5));  // Thêm khoảng cách nhỏ
        loginPanel.add(loginButton);
        
        formPanel.add(emailLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(usernameLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(confirmPasswordLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(confirmPasswordField);
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(phoneLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(phoneField);
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(addressLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(addressField);
        formPanel.add(Box.createVerticalStrut(20));
        
        formPanel.add(buttonsPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(loginPanel);
        
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(formPanel);
        
        add(mainPanel);
    }
    
    private void register() {
        // Validate input
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String phone = phoneField.getText();
        String address = addressField.getText();
        
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || 
            confirmPassword.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng điền đầy đủ thông tin!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Mật khẩu xác nhận không khớp!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            // Check if email already exists
            String checkQuery = "SELECT * FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            
            if (checkStmt.executeQuery().next()) {
                JOptionPane.showMessageDialog(this,
                    "Email đã được sử dụng!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Insert new user
            String insertQuery = "INSERT INTO users (email, username, password, phone, address, role, status) VALUES (?, ?, ?, ?, ?, 'customer', 1)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, email);
            insertStmt.setString(2, username);
            insertStmt.setString(3, password);
            insertStmt.setString(4, phone);
            insertStmt.setString(5, address);
            
            insertStmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this,
                "Đăng ký thành công!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
                
            // Open login form
            new SignIn().setVisible(true);
            dispose();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi kết nối cơ sở dữ liệu!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));  // Cho phép mở rộng tối đa theo chiều ngang
        field.setPreferredSize(new Dimension(300, 35));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setPreferredSize(new Dimension(300, 35));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private void styleButton(JButton button, Color color, int width, int height) {
        button.setPreferredSize(new Dimension(width, height));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }
    
    private void styleLinkButton(JButton button, Color color) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(color);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(color);
            }
        });
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new SignUp().setVisible(true);
        });
    }
} 