package view.manager;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.sql.*;
import java.io.*;
import java.nio.file.*;
import java.net.URL;
import utils.DBConnection;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.text.DecimalFormat;

public class Products extends JPanel {
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JTextField nameField;
    private JTextField idField;
    private JComboBox<String> categoryComboBox;
    private JTextArea descriptionArea;
    private JTextField priceField;
    private JTextField imageUrlTextField;
    private JComboBox<String> statusComboBox;
    private JLabel imagePreview;
    private String selectedImagePath;
    private final String IMAGE_UPLOAD_PATH = "src/resources/images/products/";
    private DecimalFormat formatter = new DecimalFormat("#,###");
    
    public Products() {
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
        loadCategories();
        loadProductData();
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
        
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Add button
        JButton addButton = createStyledButton("Thêm sản phẩm", new Color(46, 204, 113));
        addButton.addActionListener(e -> clearForm());
        
        panel.add(searchPanel, BorderLayout.WEST);
        panel.add(addButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        
        // Thêm cột "Thao tác" vào columns
        String[] columns = {"ID", "Tên sản phẩm", "Danh mục", "Mô tả", "Giá", "Hình ảnh", "Trạng thái", "Ngày tạo"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép edit trực tiếp trên bảng
            }
        };
        
        productTable = new JTable(tableModel);
        
        // Thêm ImageRenderer cho cột hình ảnh
        TableColumn imageColumn = productTable.getColumnModel().getColumn(5);
        imageColumn.setCellRenderer(new ImageRenderer());
        
        // Style table
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadProductToForm();
            }
        });
        
        // Điều chỉnh kích thước các cột
        productTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        productTable.setRowHeight(100); // Tăng chiều cao hàng để hiển thị ảnh
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(productTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createTitledBorder("Thông tin sản phẩm")
        ));
        
        // Set background color to white
        panel.setBackground(Color.WHITE);
        
        // Create form fields
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        idField = createTextField("ID");
        idField.setEnabled(false);
        nameField = createTextField("Tên sản phẩm");
        categoryComboBox = new JComboBox<>();
        descriptionArea = new JTextArea(1, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        
        priceField = createTextField("Giá");
        imageUrlTextField = createTextField("URL hình ảnh");
        statusComboBox = new JComboBox<>(new String[]{"0 - Ngưng bán", "1 - Đang bán"});
        
        // Thêm DocumentListener cho imageUrlTextField
        imageUrlTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePreview();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePreview();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePreview();
            }
        });
        
        // Add form fields to grid
        fieldsPanel.add(createFormRow("Tên sản phẩm:", nameField));
        fieldsPanel.add(createFormRow("Danh mục:", categoryComboBox));
        fieldsPanel.add(createFormRow("Giá:", priceField));
        fieldsPanel.add(createFormRow("Mô tả:", descScrollPane));
        fieldsPanel.add(createFormRow("URL hình ảnh:", imageUrlTextField));
        fieldsPanel.add(createFormRow("Trạng thái:", statusComboBox));
        
        // Image preview
        imagePreview = new JLabel();
        imagePreview.setPreferredSize(new Dimension(150, 150));
        imagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        imagePreview.setHorizontalAlignment(JLabel.CENTER);
        
        // Create a panel for the preview
        JPanel previewPanel = new JPanel();
        previewPanel.setLayout(new BorderLayout());
        previewPanel.add(new JLabel("Preview:"), BorderLayout.NORTH);
        previewPanel.add(imagePreview, BorderLayout.CENTER);
        
        // Add components to main panel
        panel.add(fieldsPanel, BorderLayout.CENTER);
        panel.add(previewPanel, BorderLayout.EAST);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveButton = createStyledButton("Lưu", new Color(46, 204, 113));
        JButton deleteButton = createStyledButton("Xóa", new Color(231, 76, 60));
        JButton clearButton = createStyledButton("Hủy", new Color(149, 165, 166));
        
        saveButton.addActionListener(e -> saveProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        clearButton.addActionListener(e -> clearForm());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedImagePath = selectedFile.getAbsolutePath();
            displayImage(selectedImagePath);
        }
    }
    
    private void displayImage(String path) {
        try {
            ImageIcon imageIcon = new ImageIcon(path);
            Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            imagePreview.setIcon(new ImageIcon(image));
        } catch (Exception e) {
            e.printStackTrace();
            imagePreview.setIcon(null);
            imagePreview.setText("Không thể tải ảnh");
        }
    }
    
    private void loadCategories() {
        categoryComboBox.removeAllItems();
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM categories ORDER BY id";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                categoryComboBox.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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
    
    private void loadProductData() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT p.*, c.name as category_name FROM products p " +
                           "JOIN categories c ON p.category_id = c.id ORDER BY p.id";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category_name"),
                    rs.getString("description"),
                    formatter.format(rs.getDouble("price")),
                    rs.getString("image_url"),
                    rs.getString("status").equals("1") ? "Đang bán" : "Ngưng bán",
                    rs.getTimestamp("created_at")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }
    
    private void loadProductToForm() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            
            // Set category
            String categoryName = tableModel.getValueAt(selectedRow, 2).toString();
            for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                if (categoryComboBox.getItemAt(i).contains(categoryName)) {
                    categoryComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            descriptionArea.setText(tableModel.getValueAt(selectedRow, 3).toString());
            priceField.setText(tableModel.getValueAt(selectedRow, 4).toString().replace(",", ""));
            imageUrlTextField.setText(tableModel.getValueAt(selectedRow, 5).toString());
            updatePreview();
            
            // Set status
            String status = tableModel.getValueAt(selectedRow, 6).toString();
            statusComboBox.setSelectedIndex(status.equals("Đang bán") ? 1 : 0);
        }
    }
    
    private void saveProduct() {
        try (Connection conn = DBConnection.getConnection()) {
            validateInput();
            
            int selectedRow = productTable.getSelectedRow();
            String query;
            if (selectedRow >= 0) {
                query = "UPDATE products SET name=?, category_id=?, description=?, price=?, image_url=?, status=? WHERE id=?";
            } else {
                query = "INSERT INTO products (name, category_id, description, price, image_url, status) VALUES (?, ?, ?, ?, ?, ?)";
            }
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nameField.getText());
            
            // Get category_id from selected item
            String selectedCategory = categoryComboBox.getSelectedItem().toString();
            int categoryId = Integer.parseInt(selectedCategory.split(" - ")[0]);
            stmt.setInt(2, categoryId);
            
            stmt.setString(3, descriptionArea.getText());
            stmt.setLong(4, Long.parseLong(priceField.getText().replace(",", "")));
            stmt.setString(5, imageUrlTextField.getText());
            
            // Set status (0 or 1)
            String selectedStatus = statusComboBox.getSelectedItem().toString();
            stmt.setInt(6, selectedStatus.startsWith("1") ? 1 : 0);
            
            if (selectedRow >= 0) {
                stmt.setInt(7, Integer.parseInt(idField.getText()));
            }
            
            stmt.executeUpdate();
            loadProductData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Lưu thành công!");
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu: " + e.getMessage());
        }
    }
    
    private void validateInput() throws Exception {
        if (nameField.getText().trim().isEmpty()) {
            throw new Exception("Vui lòng nhập tên sản phẩm");
        }
        
        if (imageUrlTextField.getText().trim().isEmpty()) {
            throw new Exception("Vui lòng nhập URL hình ảnh");
        }
        
        try {
            Long.parseLong(priceField.getText().replace(",", ""));
        } catch (NumberFormatException e) {
            throw new Exception("Giá không hợp lệ");
        }
    }
    
    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa sản phẩm này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    // Lấy thông tin hình ảnh trước khi xóa
                    String imageUrl = tableModel.getValueAt(selectedRow, 5).toString();
                    
                    // Xóa sản phẩm từ database
                    String query = "DELETE FROM products WHERE id=?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString()));
                    stmt.executeUpdate();
                    
                    // Xóa file hình ảnh nếu là file local
                    if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.startsWith("http")) {
                        try {
                            File imageFile = new File(IMAGE_UPLOAD_PATH + imageUrl);
                            if (imageFile.exists()) {
                                imageFile.delete();
                            }
                        } catch (Exception e) {
                            System.out.println("Không thể xóa file hình ảnh: " + e.getMessage());
                        }
                    }
                    
                    loadProductData();
                    clearForm();
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage());
                }
            }
        }
    }
    
    private void searchProducts() {
        String searchText = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT p.*, c.name as category_name FROM products p " +
                          "JOIN categories c ON p.category_id = c.id " +
                          "WHERE LOWER(p.name) LIKE ? OR LOWER(p.description) LIKE ? " +
                          "ORDER BY p.id";
            PreparedStatement stmt = conn.prepareStatement(query);
            String searchPattern = "%" + searchText + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category_name"),
                    rs.getString("description"),
                    formatter.format(rs.getDouble("price")),
                    rs.getString("image_url"),
                    rs.getString("status").equals("1") ? "Đang bán" : "Ngưng bán",
                    rs.getTimestamp("created_at")
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
        nameField.setText("");
        categoryComboBox.setSelectedIndex(0);
        descriptionArea.setText("");
        priceField.setText("");
        imageUrlTextField.setText("");
        statusComboBox.setSelectedIndex(0);
        productTable.clearSelection();
        imagePreview.setIcon(null);
        imagePreview.setText("No image");
    }
    
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        comboBox.setFont(new Font("Arial", Font.PLAIN, 12));
    }
    
    // Thêm class ImageRenderer
    private class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(JLabel.CENTER);
            
            if (value != null) {
                String imageUrl = value.toString();
                try {
                    ImageIcon originalIcon = loadImage(imageUrl);
                    if (originalIcon != null) {
                        // Scale image to fit in table cell
                        ImageIcon scaledIcon = scaleImage(originalIcon, 80, 80);
                        label.setIcon(scaledIcon);
                        label.setText(""); // Clear text when showing image
                    } else {
                        label.setIcon(null);
                        label.setText("No image");
                    }
                } catch (Exception e) {
                    label.setIcon(null);
                    label.setText("Error");
                }
            } else {
                label.setIcon(null);
                label.setText("No image");
            }
            
            return label;
        }
    }
    
    // Cập nhật phương thức loadImage để hỗ trợ cả local file và URL
    private ImageIcon loadImage(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) return null;
            
            if (imagePath.startsWith("http")) {
                // Load from URL
                return new ImageIcon(new URL(imagePath));
            } else {
                // Load from local file
                File file = new File(IMAGE_UPLOAD_PATH + imagePath);
                if (file.exists()) {
                    return new ImageIcon(file.getAbsolutePath());
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Cập nhật phương thức scaleImage để giữ tỷ lệ ảnh
    private ImageIcon scaleImage(ImageIcon icon, int targetWidth, int targetHeight) {
        Image img = icon.getImage();
        
        // Calculate dimensions preserving aspect ratio
        double ratio = Math.min(
            (double) targetWidth / icon.getIconWidth(),
            (double) targetHeight / icon.getIconHeight()
        );
        
        int width = (int) (icon.getIconWidth() * ratio);
        int height = (int) (icon.getIconHeight() * ratio);
        
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
    
    // Thêm phương thức updatePreview
    private void updatePreview() {
        String imageUrl = imageUrlTextField.getText().trim();
        if (!imageUrl.isEmpty()) {
            try {
                ImageIcon icon = loadImage(imageUrl);
                if (icon != null) {
                    imagePreview.setIcon(scaleImage(icon, 180, 180));
                    imagePreview.setText("");
                } else {
                    imagePreview.setIcon(null);
                    imagePreview.setText("Invalid URL");
                }
            } catch (Exception e) {
                e.printStackTrace();
                imagePreview.setIcon(null);
                imagePreview.setText("Error loading image");
            }
        } else {
            imagePreview.setIcon(null);
            imagePreview.setText("No image");
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            
            JFrame frame = new JFrame("Quản lý sản phẩm");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new Products());
            frame.setSize(1200, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 