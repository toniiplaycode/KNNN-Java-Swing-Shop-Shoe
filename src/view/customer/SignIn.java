package view.customer;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import utils.DBConnection;
import java.util.prefs.Preferences;

public class SignIn extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox rememberCheckBox;
    private Preferences prefs;
    private Color primaryColor = new Color(41, 128, 185);
    private Color successColor = new Color(46, 204, 113);
    private Color dangerColor = new Color(231, 76, 60);
    
    public SignIn() {
        setTitle("Đăng nhập");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Khởi tạo Preferences để lưu thông tin đăng nhập
        prefs = Preferences.userNodeForPackage(SignIn.class);
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel titleLabel = new JLabel("ĐĂNG NHẬP");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        
        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setMaximumSize(new Dimension(300, 35));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Password field
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField = new JPasswordField(20);
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(300, 35));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Remember checkbox
        rememberCheckBox = new JCheckBox("Ghi nhớ đăng nhập");
        rememberCheckBox.setFont(new Font("Arial", Font.PLAIN, 13));
        rememberCheckBox.setBackground(Color.WHITE);
        rememberCheckBox.setForeground(new Color(100, 100, 100));
        rememberCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton loginButton = new JButton("Đăng nhập");
        styleButton(loginButton, successColor, 130, 40);
        loginButton.addActionListener(e -> login());
        
        JButton cancelButton = new JButton("Hủy");
        styleButton(cancelButton, dangerColor, 130, 40);
        cancelButton.addActionListener(e -> dispose());
        
        // Register link
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        registerPanel.setBackground(Color.WHITE);
        registerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel registerLabel = new JLabel("Chưa có tài khoản? ");
        registerLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        JButton registerButton = new JButton("Đăng ký ngay");
        styleLinkButton(registerButton, primaryColor);
        registerButton.addActionListener(e -> {
            new SignUp().setVisible(true);
            dispose();
        });
        
        // Add components
        buttonsPanel.add(loginButton);
        buttonsPanel.add(Box.createHorizontalStrut(10));
        buttonsPanel.add(cancelButton);
        
        registerPanel.add(registerLabel);
        registerPanel.add(registerButton);
        
        formPanel.add(emailLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(rememberCheckBox);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(buttonsPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(registerPanel);
        
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(formPanel);
        
        add(mainPanel);
        
        // Load saved credentials
        loadSavedCredentials();
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
            String query = "SELECT * FROM users WHERE email = ? AND password = ? AND role = 'customer'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Lưu thông tin đăng nhập nếu checkbox được chọn
                if (rememberCheckBox.isSelected()) {
                    saveCredentials(email, password);
                } else {
                    clearSavedCredentials();
                }
                
                // Mở CustomerView với userId
                new CustomerView(rs.getInt("id")).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Email hoặc mật khẩu không đúng!",
                    "Lỗi đăng nhập",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi kết nối cơ sở dữ liệu!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveCredentials(String email, String password) {
        prefs.put("savedEmail", email);
        prefs.put("savedPassword", password);
        prefs.putBoolean("rememberLogin", true);
    }
    
    private void loadSavedCredentials() {
        if (prefs.getBoolean("rememberLogin", false)) {
            String savedEmail = prefs.get("savedEmail", "");
            String savedPassword = prefs.get("savedPassword", "");
            
            emailField.setText(savedEmail);
            passwordField.setText(savedPassword);
            rememberCheckBox.setSelected(true);
        }
    }
    
    private void clearSavedCredentials() {
        prefs.remove("savedEmail");
        prefs.remove("savedPassword");
        prefs.remove("rememberLogin");
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
            new SignIn().setVisible(true);
        });
    }
} 