package view.customer;

import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.FlatLightLaf;
import view.Main;
import java.sql.*;
import utils.DBConnection;

public class CustomerView extends JFrame {
    private int userId;
    private String username;
    private JLabel userLabel;
    private JPanel contentPanel;
    
    public CustomerView(int userId) {
        this.userId = userId;
        loadUserInfo();
        
        setTitle("Shoe Shop");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content Panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(new Products(userId), BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 100));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Left Panel - Logo & Shop Info
        JPanel leftPanel = new JPanel(new BorderLayout(10, 5));
        leftPanel.setOpaque(false);
        
        // Logo
        JLabel logoLabel = new JLabel("SHOE SHOP");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        
        // Shop Info
        JPanel shopInfoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        shopInfoPanel.setOpaque(false);
        
        JLabel addressLabel = new JLabel("Địa chỉ: 168 Nguyễn Văn Cừ (nối dài), Phường An Bình, Q. Ninh Kiều, TP. Cần Thơ.");
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        addressLabel.setForeground(Color.WHITE);
        
        JLabel contactLabel = new JLabel("Hotline: 1900 1234 | Email: contact@shoeshop.com");
        contactLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        contactLabel.setForeground(Color.WHITE);
        
        shopInfoPanel.add(addressLabel);
        shopInfoPanel.add(contactLabel);
        
        leftPanel.add(logoLabel, BorderLayout.NORTH);
        leftPanel.add(shopInfoPanel, BorderLayout.CENTER);
        
        // User Info & Buttons Panel
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        // User info
        userLabel = new JLabel("Xin chào, " + username);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        
        // Cart button
        JButton cartButton = createHeaderButton("Giỏ hàng", "src/icons/cart1.png");
        cartButton.addActionListener(e -> {
            new Cart(this, userId).setVisible(true);
        });
        
        // Logout button
        JButton logoutButton = createHeaderButton("Đăng xuất", "src/icons/signout.png");
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new SignIn().setVisible(true);
            }
        });
        
        rightPanel.add(userLabel);
        rightPanel.add(cartButton);
        rightPanel.add(logoutButton);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JButton createHeaderButton(String text, String iconPath) {
        JButton button = new JButton(text);
        try {
            ImageIcon icon = new ImageIcon(iconPath);
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }
        });
        
        return button;
    }
    
    private void loadUserInfo() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT username FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                username = rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new CustomerView(1).setVisible(true);
        });
    }
} 