package view.customer;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import utils.DBConnection;
import java.text.DecimalFormat;
import javax.swing.border.EmptyBorder;
import java.net.URL;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Products extends JPanel {
    private JPanel productsPanel;
    private JTextField searchField;
    private JComboBox<String> categoryComboBox;
    private DecimalFormat formatter = new DecimalFormat("#,###");
    private int userId;
    
    public Products(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top Panel - Search and Filter
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Products Panel with GridLayout
        productsPanel = new JPanel(new GridLayout(0, 4, 15, 15)); // Giảm khoảng cách giữa các card
        productsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Scroll Pane for Products
        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
        
        // Load initial data
        loadCategories();
        loadProducts();
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Tìm kiếm sản phẩm...");
        
        JButton searchButton = createStyledButton("Tìm kiếm", new Color(70, 130, 180));
        searchButton.addActionListener(e -> searchProducts());
        
        // Category filter
        categoryComboBox = new JComboBox<>();
        categoryComboBox.addItem("Tất cả danh mục");
        categoryComboBox.addActionListener(e -> loadProducts());
        
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(new JLabel("   Danh mục: "));
        searchPanel.add(categoryComboBox);
        
        panel.add(searchPanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private void loadCategories() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT name FROM categories ORDER BY name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                categoryComboBox.addItem(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadProducts() {
        productsPanel.removeAll();
        
        try (Connection conn = DBConnection.getConnection()) {
            String category = (String) categoryComboBox.getSelectedItem();
            String query;
            PreparedStatement stmt;
            
            if (category.equals("Tất cả danh mục")) {
                query = "SELECT p.*, c.name as category_name FROM products p " +
                        "JOIN categories c ON p.category_id = c.id " +
                        "WHERE p.status = '1' ORDER BY p.id DESC";
                stmt = conn.prepareStatement(query);
            } else {
                query = "SELECT p.*, c.name as category_name FROM products p " +
                        "JOIN categories c ON p.category_id = c.id " +
                        "WHERE p.status = '1' AND c.name = ? ORDER BY p.id DESC";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, category);
            }
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                productsPanel.add(createProductCard(
                    rs.getInt("id"),
                    rs.getString("image_url"),
                    rs.getString("name"),
                    rs.getDouble("price")
                ));
            }
            
            productsPanel.revalidate();
            productsPanel.repaint();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private JPanel createProductCard(int id, String imageUrl, String name, double price) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(250, 350));
        
        // Thêm MouseListener cho card
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // Single click
                    new DetailProduct(id, userId).setVisible(true);
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(new Color(46, 204, 113), 2));
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        // Image Panel để chứa ảnh
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setPreferredSize(new Dimension(200, 200));
        imagePanel.setMaximumSize(new Dimension(200, 200));
        
        // Image
        try {
            URL url = new URL(imageUrl);
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(img));
            imagePanel.add(imageLabel);
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Không thể tải ảnh");
            imagePanel.add(errorLabel);
        }
        
        // Name Panel
        JPanel namePanel = new JPanel();
        namePanel.setBackground(Color.WHITE);
        namePanel.setPreferredSize(new Dimension(230, 40));
        namePanel.setMaximumSize(new Dimension(230, 40));
        
        JLabel nameLabel = new JLabel("<html><div style='text-align: center; width: 200px;'>" + name + "</div></html>");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        namePanel.add(nameLabel);
        
        // Price Panel
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pricePanel.setBackground(Color.WHITE);
        pricePanel.setPreferredSize(new Dimension(230, 30));
        pricePanel.setMaximumSize(new Dimension(230, 30));
        
        JLabel priceLabel = new JLabel(formatter.format(price) + "đ");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(new Color(231, 76, 60));
        pricePanel.add(priceLabel);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        
        JButton addToCartButton = new JButton("Thêm vào giỏ");
        addToCartButton.setBackground(new Color(46, 204, 113));
        addToCartButton.setForeground(Color.WHITE);
        addToCartButton.setFocusPainted(false);
        addToCartButton.setBorderPainted(false);
        addToCartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        addToCartButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                addToCartButton.setBackground(new Color(46, 204, 113).brighter());
            }
            
            public void mouseExited(MouseEvent e) {
                addToCartButton.setBackground(new Color(46, 204, 113));
            }
        });
        
        // Add to cart action
        addToCartButton.addActionListener(e -> {
            new DetailProduct(id, userId).setVisible(true);
        });
        
        buttonPanel.add(addToCartButton);
        
        // Add components với khoảng cách cố định
        card.add(Box.createVerticalStrut(10));
        card.add(imagePanel);
        card.add(Box.createVerticalStrut(5));
        card.add(namePanel);
        card.add(Box.createVerticalStrut(5));
        card.add(pricePanel);
        card.add(Box.createVerticalStrut(5));
        card.add(buttonPanel);
        card.add(Box.createVerticalStrut(10));
        
        return card;
    }
    
    private void searchProducts() {
        String searchText = searchField.getText().toLowerCase();
        productsPanel.removeAll();
        
        try (Connection conn = DBConnection.getConnection()) {
            String category = (String) categoryComboBox.getSelectedItem();
            String query;
            PreparedStatement stmt;
            
            if (category.equals("Tất cả danh mục")) {
                query = "SELECT p.*, c.name as category_name FROM products p " +
                        "JOIN categories c ON p.category_id = c.id " +
                        "WHERE p.status = '1' AND LOWER(p.name) LIKE ? ORDER BY p.id DESC";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, "%" + searchText + "%");
            } else {
                query = "SELECT p.*, c.name as category_name FROM products p " +
                        "JOIN categories c ON p.category_id = c.id " +
                        "WHERE p.status = '1' AND c.name = ? AND LOWER(p.name) LIKE ? ORDER BY p.id DESC";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, category);
                stmt.setString(2, "%" + searchText + "%");
            }
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                productsPanel.add(createProductCard(
                    rs.getInt("id"),
                    rs.getString("image_url"),
                    rs.getString("name"),
                    rs.getDouble("price")
                ));
            }
            
            productsPanel.revalidate();
            productsPanel.repaint();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Sản phẩm");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Products(0));
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
} 