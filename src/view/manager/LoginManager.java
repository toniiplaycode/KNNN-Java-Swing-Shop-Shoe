package view.manager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import utils.DBConnection;
import java.util.prefs.Preferences;

public class LoginManager extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox rememberCheckBox;
    private Preferences prefs;
    
    public LoginManager() {
        setTitle("Đăng nhập quản lý");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Khởi tạo Preferences để lưu thông tin đăng nhập
        prefs = Preferences.userNodeForPackage(LoginManager.class);
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Logo/Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("ĐĂNG NHẬP QUẢN LÝ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        
        // Email field
        JPanel emailPanel = new JPanel(new BorderLayout(5, 5));
        emailField = new JTextField();
        emailField.putClientProperty("JTextField.placeholderText", "Nhập email của bạn");
        emailPanel.add(new JLabel("Email:"), BorderLayout.NORTH);
        emailPanel.add(emailField, BorderLayout.CENTER);
        
        // Password field
        JPanel passwordPanel = new JPanel(new BorderLayout(5, 5));
        passwordField = new JPasswordField();
        passwordField.putClientProperty("JTextField.placeholderText", "Nhập mật khẩu");
        passwordPanel.add(new JLabel("Mật khẩu:"), BorderLayout.NORTH);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        
        // Remember checkbox
        JPanel rememberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rememberCheckBox = new JCheckBox("Ghi nhớ đăng nhập");
        rememberPanel.add(rememberCheckBox);
        
        formPanel.add(emailPanel);
        formPanel.add(passwordPanel);
        formPanel.add(rememberPanel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        JButton loginButton = new JButton("Đăng nhập");
        loginButton.setPreferredSize(new Dimension(120, 35));
        loginButton.setBackground(new Color(52, 152, 219));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        
        // Add components to main panel
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(buttonPanel);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Load saved credentials
        loadSavedCredentials();
        
        // Add action listeners
        loginButton.addActionListener(e -> login());
        cancelButton.addActionListener(e -> dispose());
        
        // Add enter key listener
        getRootPane().setDefaultButton(loginButton);
    }
    
    private void login() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập đầy đủ email và mật khẩu!",
                "Lỗi đăng nhập",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE email = ? AND password = ? AND role = 'admin'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                if (rememberCheckBox.isSelected()) {
                    saveCredentials(email, password);
                }
                new ManagerView(rs.getInt("id")).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Email hoặc mật khẩu không chính xác!",
                    "Lỗi đăng nhập",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi kết nối: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveCredentials(String email, String password) {
        try {
            // Lưu thông tin vào Preferences
            Preferences prefs = Preferences.userNodeForPackage(LoginManager.class);
            prefs.put("savedEmail", email);
            prefs.put("savedPassword", password);
            prefs.putBoolean("rememberLogin", true);
            prefs.flush(); // Đảm bảo dữ liệu được lưu ngay lập tức
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadSavedCredentials() {
        try {
            // Đọc thông tin từ Preferences
            Preferences prefs = Preferences.userNodeForPackage(LoginManager.class);
            boolean remember = prefs.getBoolean("rememberLogin", false);
            
            if (remember) {
                String savedEmail = prefs.get("savedEmail", "");
                String savedPassword = prefs.get("savedPassword", "");
                
                emailField.setText(savedEmail);
                passwordField.setText(savedPassword);
                rememberCheckBox.setSelected(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void clearSavedCredentials() {
        try {
            // Xóa thông tin đã lưu
            Preferences prefs = Preferences.userNodeForPackage(LoginManager.class);
            prefs.remove("savedEmail");
            prefs.remove("savedPassword");
            prefs.remove("rememberLogin");
            prefs.flush();
            
            // Xóa text trên form
            emailField.setText("");
            passwordField.setText("");
            rememberCheckBox.setSelected(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new LoginManager().setVisible(true);
        });
    }
} 