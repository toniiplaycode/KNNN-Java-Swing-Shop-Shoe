package view.customer;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import utils.DBConnection;
import java.text.DecimalFormat;
import java.net.URL;

public class DetailProduct extends JFrame {
    private int productId;
    private int userId;
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
    
    public DetailProduct(int productId, int userId) {
        this.productId = productId;
        this.userId = userId;
        
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
        
        addToCartButton.addActionListener(e -> addToCart());
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
            // Truy vấn thông tin sản phẩm và tổng số lượng tồn kho
            String query = "SELECT p.*, c.name as category_name, " +
                          "(SELECT COALESCE(SUM(pv.stock), 0) FROM product_variants pv WHERE pv.product_id = p.id) as total_stock " +
                          "FROM products p " +
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
                    rs.getInt("total_stock")  // Sử dụng tổng số lượng từ product_variants
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
            String query = "SELECT s.size, COALESCE(pv.stock, 0) as stock " +
                          "FROM sizes s " +
                          "LEFT JOIN product_variants pv ON s.id = pv.size_id AND pv.product_id = ? " +
                          "ORDER BY s.size";
                          
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            
            sizeComboBox.removeAllItems();
            while (rs.next()) {
                String size = rs.getString("size");
                int stock = rs.getInt("stock");
                String item = size + " (Còn " + stock + ")";
                if (stock > 0) {
                    sizeComboBox.addItem(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void addToCart() {
        if (sizeComboBox.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn size giày!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int quantity = (int) quantitySpinner.getValue();
        // Lấy size từ chuỗi "39 (Còn 10)"
        String selectedSize = sizeComboBox.getSelectedItem().toString().split(" ")[0];
        
        try (Connection conn = DBConnection.getConnection()) {
            // Lấy variant_id dựa trên product_id và size
            String variantQuery = "SELECT pv.id FROM product_variants pv " +
                                "JOIN sizes s ON pv.size_id = s.id " +
                                "WHERE pv.product_id = ? AND s.size = ?";
            PreparedStatement variantStmt = conn.prepareStatement(variantQuery);
            variantStmt.setInt(1, productId);
            variantStmt.setString(2, selectedSize);
            
            ResultSet variantRs = variantStmt.executeQuery();
            
            if (variantRs.next()) {
                int variantId = variantRs.getInt("id");
                
                // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
                String checkQuery = "SELECT id, quantity FROM cart WHERE user_id = ? AND variant_id = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, variantId);
                
                ResultSet checkRs = checkStmt.executeQuery();
                
                if (checkRs.next()) {
                    // Cập nhật số lượng nếu đã có trong giỏ
                    String updateQuery = "UPDATE cart SET quantity = quantity + ? WHERE id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setInt(1, quantity);
                    updateStmt.setInt(2, checkRs.getInt("id"));
                    updateStmt.executeUpdate();
                } else {
                    // Thêm mới vào giỏ hàng
                    String insertQuery = "INSERT INTO cart (user_id, variant_id, quantity) VALUES (?, ?, ?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, variantId);
                    insertStmt.setInt(3, quantity);
                    insertStmt.executeUpdate();
                }
                
                JOptionPane.showMessageDialog(this,
                    "Đã thêm vào giỏ hàng!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
                    
                dispose(); // Đóng cửa sổ chi tiết sau khi thêm vào giỏ
                
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không tìm thấy sản phẩm với size này!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi thêm vào giỏ hàng: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DetailProduct(1, 1).setVisible(true);
        });
    }
} 