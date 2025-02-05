package view.customer;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import utils.DBConnection;
import java.text.DecimalFormat;
import java.net.URL;

public class DetailProduct extends JFrame {
    private int productId;
    private DecimalFormat formatter = new DecimalFormat("#,###");
    private JSpinner quantitySpinner;
    private JComboBox<String> sizeComboBox;
    
    // Thêm các biến tham chiếu
    private JLabel nameLabel;
    private JLabel stockLabel;
    private JTextArea descArea;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JLabel priceLabel;
    
    public DetailProduct(int productId) {
        this.productId = productId;
        
        setTitle("Chi tiết sản phẩm");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));
        
        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);
        
        // Top Panel chứa tên sản phẩm
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        nameLabel = new JLabel();
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(nameLabel);
        
        // Center Panel chứa ảnh và thông tin
        JPanel centerPanel = new JPanel(new BorderLayout(20, 0));
        centerPanel.setBackground(Color.WHITE);
        
        // Left Panel - Image
        leftPanel = createImagePanel();
        
        // Right Panel - Product Info
        rightPanel = createInfoPanel();
        
        centerPanel.add(leftPanel, BorderLayout.WEST);
        centerPanel.add(rightPanel, BorderLayout.CENTER);
        
        // Bottom Panel chứa mô tả
        JPanel bottomPanel = createDescriptionPanel();
        
        // Add các panel vào main panel
        mainPanel.add(topPanel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(centerPanel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(bottomPanel);
        
        add(mainPanel);
        
        loadProductData();
    }
    
    private JPanel createImagePanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(400, 500));
        panel.setBackground(Color.WHITE);
        return panel;
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        // Stock Info
        JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        stockPanel.setBackground(Color.WHITE);
        stockLabel = new JLabel("Số lượng: ");
        stockPanel.add(stockLabel);
        panel.add(stockPanel);
        panel.add(Box.createVerticalStrut(5));
        
        // Price Panel
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pricePanel.setBackground(Color.WHITE);
        priceLabel = new JLabel();
        priceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        priceLabel.setForeground(new Color(231, 76, 60));
        pricePanel.add(priceLabel);
        panel.add(pricePanel);
        panel.add(Box.createVerticalStrut(5));
        
        // Size Selection
        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        sizePanel.setBackground(Color.WHITE);
        sizePanel.add(new JLabel("Chọn size giày:  "));
        sizeComboBox = new JComboBox<>();
        sizeComboBox.setPreferredSize(new Dimension(80, 25));
        sizePanel.add(sizeComboBox);
        panel.add(sizePanel);
        panel.add(Box.createVerticalStrut(5));
        
        // Quantity Selection
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        quantityPanel.setBackground(Color.WHITE);
        quantityPanel.add(new JLabel("Số lượng:  "));
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 100, 1);
        quantitySpinner = new JSpinner(spinnerModel);
        quantitySpinner.setPreferredSize(new Dimension(80, 25));
        quantityPanel.add(quantitySpinner);
        panel.add(quantityPanel);
        panel.add(Box.createVerticalStrut(10));
        
        // Add to Cart Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setBackground(Color.WHITE);
        JButton addToCartButton = new JButton("Thêm vào giỏ hàng");
        addToCartButton.setPreferredSize(new Dimension(200, 35));
        addToCartButton.setBackground(new Color(46, 204, 113));
        addToCartButton.setForeground(Color.WHITE);
        addToCartButton.setFocusPainted(false);
        buttonPanel.add(addToCartButton);
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private JPanel createDescriptionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        JLabel descTitle = new JLabel("Mô tả sản phẩm:");
        descTitle.setFont(new Font("Arial", Font.BOLD, 16));
        descTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        descArea = new JTextArea();
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBackground(Color.WHITE);
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(descTitle);
        panel.add(Box.createVerticalStrut(5));
        panel.add(descArea);
        
        return panel;
    }
    
    private void loadProductData() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT p.*, c.name as category_name FROM products p " +
                          "JOIN categories c ON p.category_id = c.id " +
                          "WHERE p.id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, productId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Update UI with product data
                updateProductUI(
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("image_url"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                );
                
                // Load sizes
                loadSizes();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải thông tin sản phẩm: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateProductUI(String name, String description, String imageUrl, double price, int stock) {
        // Update product name
        nameLabel.setText(name);
        
        // Update stock info
        stockLabel.setText("Số lượng: " + stock);
        
        // Update price
        priceLabel.setText(formatter.format(price) + "đ");
        
        // Update image
        try {
            URL url = new URL(imageUrl);
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(img));
            leftPanel.removeAll();
            leftPanel.add(imageLabel);
        } catch (Exception e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Không thể tải ảnh");
            leftPanel.removeAll();
            leftPanel.add(errorLabel);
        }
        
        // Update description
        descArea.setText(description);
        
        // Refresh UI
        revalidate();
        repaint();
    }
    
    private void loadSizes() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT size FROM sizes ORDER BY size";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                sizeComboBox.addItem(rs.getString("size"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DetailProduct(1).setVisible(true);
        });
    }
} 