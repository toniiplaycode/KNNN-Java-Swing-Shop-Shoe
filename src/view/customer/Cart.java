package view.customer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import utils.DBConnection;
import java.text.DecimalFormat;
import java.net.URL;

public class Cart extends JDialog {
    private int userId;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private double total = 0;
    private DecimalFormat formatter = new DecimalFormat("#,###");
    private JComboBox<String> paymentMethodComboBox;
    
    public Cart(JFrame parent, int userId) {
        super(parent, "Giỏ hàng", true);
        this.userId = userId;
        
        setSize(800, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Table Panel
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Bottom Panel
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Load cart data
        loadCartData();
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table model
        String[] columns = {"ID", "Hình ảnh", "Sản phẩm", "Size", "Số lượng", "Đơn giá", "Thành tiền", ""};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 7;
            }
        };
        
        cartTable = new JTable(tableModel);
        cartTable.setRowHeight(100); // Tăng chiều cao để hiển thị ảnh
        cartTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        cartTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Add ImageRenderer cho cột hình ảnh
        TableColumn imageColumn = cartTable.getColumnModel().getColumn(1);
        imageColumn.setCellRenderer(new ImageRenderer());
        imageColumn.setPreferredWidth(100);
        
        // Add delete button to last column
        TableColumn deleteColumn = cartTable.getColumnModel().getColumn(7);
        deleteColumn.setMinWidth(80);
        deleteColumn.setMaxWidth(80);
        deleteColumn.setCellRenderer(new ButtonRenderer());
        deleteColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
        
        // Add spinner for quantity column
        TableColumn quantityColumn = cartTable.getColumnModel().getColumn(4);
        quantityColumn.setCellEditor(new SpinnerEditor());
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Left Panel - Payment Method
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(new JLabel("Phương thức thanh toán: "));
        paymentMethodComboBox = new JComboBox<>(new String[]{
            "cash",           // Thay vì "Tiền mặt"
            "credit_card",    // Thay vì "Thẻ tín dụng" 
            "paypal",         // Thay vì "PayPal"
            "bank_transfer"   // Thay vì "Chuyển khoản"
        });
        
        // Tạo một renderer để hiển thị tên đẹp hơn
        paymentMethodComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                String displayText = switch(value.toString()) {
                    case "cash" -> "Tiền mặt";
                    case "credit_card" -> "Thẻ tín dụng";
                    case "paypal" -> "PayPal";
                    case "bank_transfer" -> "Chuyển khoản";
                    default -> value.toString();
                };
                return super.getListCellRendererComponent(list, displayText, index, isSelected, cellHasFocus);
            }
        });
        
        paymentMethodComboBox.setPreferredSize(new Dimension(150, 30));
        leftPanel.add(paymentMethodComboBox);
        
        // Center Panel - Total
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Tổng tiền: 0đ");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalPanel.add(totalLabel);
        
        // Right Panel - Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        JButton checkoutButton = new JButton("Thanh toán");
        styleButton(checkoutButton, new Color(46, 204, 113));
        checkoutButton.addActionListener(e -> checkout());
        
        JButton closeButton = new JButton("Đóng");
        styleButton(closeButton, new Color(231, 76, 60));
        closeButton.addActionListener(e -> dispose());
        
        buttonsPanel.add(checkoutButton);
        buttonsPanel.add(closeButton);
        
        // Add panels
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(totalPanel, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void loadCartData() {
        tableModel.setRowCount(0);
        total = 0;
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT c.id, p.name, p.image_url, s.size, c.quantity, p.price, (c.quantity * p.price) as subtotal " +
                          "FROM cart c " +
                          "JOIN product_variants pv ON c.variant_id = pv.id " +
                          "JOIN products p ON pv.product_id = p.id " +
                          "JOIN sizes s ON pv.size_id = s.id " +
                          "WHERE c.user_id = ? " +
                          "ORDER BY c.id DESC";
                          
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("image_url"),
                    rs.getString("name"),
                    rs.getString("size"),
                    rs.getInt("quantity"),
                    formatter.format(rs.getDouble("price")) + "đ",
                    formatter.format(rs.getDouble("subtotal")) + "đ",
                    "Xóa"
                };
                tableModel.addRow(row);
                total += rs.getDouble("subtotal");
            }
            
            totalLabel.setText("Tổng tiền: " + formatter.format(total) + "đ");
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }
    
    private void updateQuantity(int cartId, int quantity) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE cart SET quantity = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, quantity);
            stmt.setInt(2, cartId);
            stmt.executeUpdate();
            
            loadCartData(); // Reload cart data
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi cập nhật số lượng: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteItem(int cartId) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "DELETE FROM cart WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, cartId);
                stmt.executeUpdate();
                
                loadCartData(); // Reload cart data
                JOptionPane.showMessageDialog(this, "Đã xóa sản phẩm khỏi giỏ hàng!");
                
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi xóa sản phẩm: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void checkout() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "Giỏ hàng trống!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Tạo đơn hàng mới với status là 'pending'
                String orderQuery = "INSERT INTO orders (user_id, total_price, status, order_date) VALUES (?, ?, 'pending', CURRENT_TIMESTAMP)";
                PreparedStatement orderStmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
                orderStmt.setInt(1, userId);
                orderStmt.setDouble(2, total);
                orderStmt.executeUpdate();
                
                // Lấy order_id vừa tạo
                ResultSet rs = orderStmt.getGeneratedKeys();
                if (rs.next()) {
                    int orderId = rs.getInt(1);
                    
                    // Thêm chi tiết đơn hàng
                    String detailQuery = "INSERT INTO order_details (order_id, variant_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
                    PreparedStatement detailStmt = conn.prepareStatement(detailQuery);
                    
                    // Thêm từng sản phẩm trong giỏ hàng vào chi tiết đơn hàng
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        int cartId = (int) tableModel.getValueAt(i, 0);
                        int quantity = (int) tableModel.getValueAt(i, 4);
                        double price = Double.parseDouble(tableModel.getValueAt(i, 5).toString().replace("đ", "").replace(",", ""));
                        
                        // Lấy variant_id từ cart
                        String variantQuery = "SELECT variant_id FROM cart WHERE id = ?";
                        PreparedStatement variantStmt = conn.prepareStatement(variantQuery);
                        variantStmt.setInt(1, cartId);
                        ResultSet variantRs = variantStmt.executeQuery();
                        
                        if (variantRs.next()) {
                            int variantId = variantRs.getInt("variant_id");
                            detailStmt.setInt(1, orderId);
                            detailStmt.setInt(2, variantId);
                            detailStmt.setInt(3, quantity);
                            detailStmt.setDouble(4, price);
                            detailStmt.executeUpdate();
                        }
                    }
                    
                    // Thêm thông tin thanh toán với status là 'paid'
                    String paymentQuery = "INSERT INTO payments (order_id, payment_method, status, payment_date) VALUES (?, ?, 'paid', CURRENT_TIMESTAMP)";
                    PreparedStatement paymentStmt = conn.prepareStatement(paymentQuery);
                    paymentStmt.setInt(1, orderId);
                    paymentStmt.setString(2, paymentMethodComboBox.getSelectedItem().toString());
                    paymentStmt.executeUpdate();
                    
                    // Xóa giỏ hàng
                    String clearCartQuery = "DELETE FROM cart WHERE user_id = ?";
                    PreparedStatement clearCartStmt = conn.prepareStatement(clearCartQuery);
                    clearCartStmt.setInt(1, userId);
                    clearCartStmt.executeUpdate();
                    
                    conn.commit();
                    JOptionPane.showMessageDialog(this,
                        "Đặt hàng thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Hiển thị thông tin đơn hàng
                    new OrderConfirmation(this, orderId).setVisible(true);
                    dispose();
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi đặt hàng: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void styleButton(JButton button, Color color) {
        button.setPreferredSize(new Dimension(100, 35));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    // Custom Button Renderer
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(231, 76, 60));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Xóa");
            return this;
        }
    }
    
    // Custom Button Editor
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private int cartId;
        private boolean isPushed;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(new Color(231, 76, 60));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            
            button.addActionListener(e -> fireEditingStopped());
        }
        
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            cartId = (int)table.getValueAt(row, 0);
            button.setText("Xóa");
            isPushed = true;
            return button;
        }
        
        public Object getCellEditorValue() {
            if (isPushed) {
                deleteItem(cartId);
            }
            isPushed = false;
            return "Xóa";
        }
    }
    
    // Custom Spinner Editor
    class SpinnerEditor extends DefaultCellEditor {
        private JSpinner spinner;
        private int cartId;
        
        public SpinnerEditor() {
            super(new JTextField());
            spinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
            spinner.setFont(new Font("Arial", Font.PLAIN, 12));
            
            // Add change listener
            spinner.addChangeListener(e -> {
                int quantity = (int)spinner.getValue();
                updateQuantity(cartId, quantity);
            });
        }
        
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            cartId = (int)table.getValueAt(row, 0);
            spinner.setValue(Integer.parseInt(value.toString()));
            return spinner;
        }
        
        public Object getCellEditorValue() {
            return spinner.getValue();
        }
    }
    
    // Custom Image Renderer
    class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            if (value != null) {
                try {
                    URL url = new URL(value.toString());
                    ImageIcon icon = new ImageIcon(url);
                    Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                    JLabel label = new JLabel(new ImageIcon(img));
                    label.setHorizontalAlignment(JLabel.CENTER);
                    return label;
                } catch (Exception e) {
                    e.printStackTrace();
                    JLabel errorLabel = new JLabel("No image");
                    errorLabel.setHorizontalAlignment(JLabel.CENTER);
                    return errorLabel;
                }
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
} 