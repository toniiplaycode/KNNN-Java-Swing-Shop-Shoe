package view.customer;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import utils.DBConnection;
import java.text.DecimalFormat;
import javax.swing.table.*;
import java.net.URL;

public class OrderConfirmation extends JDialog {
    private int orderId;
    private DecimalFormat formatter = new DecimalFormat("#,###");
    
    public OrderConfirmation(JDialog parent, int orderId) {
        super(parent, "Thông tin đơn hàng", true);
        this.orderId = orderId;
        
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // User Info Panel
        mainPanel.add(createUserInfoPanel());
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Order Info Panel
        mainPanel.add(createOrderInfoPanel());
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Products Table Panel
        mainPanel.add(createProductsPanel());
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Payment Info Panel
        mainPanel.add(createPaymentInfoPanel());
        
        // Add to scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        // Close Button
        JButton closeButton = new JButton("Đóng");
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        loadOrderData();
    }
    
    private JPanel createUserInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
        
        panel.add(new JLabel("Họ tên:"));
        panel.add(new JLabel());
        panel.add(new JLabel("Email:"));
        panel.add(new JLabel());
        panel.add(new JLabel("Số điện thoại:"));
        panel.add(new JLabel());
        panel.add(new JLabel("Địa chỉ:"));
        panel.add(new JLabel());
        
        return panel;
    }
    
    private JPanel createOrderInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin đơn hàng"));
        
        panel.add(new JLabel("Mã đơn hàng:"));
        panel.add(new JLabel("#" + orderId));
        panel.add(new JLabel("Ngày đặt:"));
        panel.add(new JLabel());
        
        return panel;
    }
    
    private JPanel createProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Sản phẩm đã mua"));
        
        String[] columns = {"Hình ảnh", "Sản phẩm", "Size", "Số lượng", "Đơn giá", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setRowHeight(100);
        
        // Add ImageRenderer cho cột hình ảnh
        TableColumn imageColumn = table.getColumnModel().getColumn(0);
        imageColumn.setCellRenderer(new ImageRenderer());
        imageColumn.setPreferredWidth(100);
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Total Panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(new JLabel("Tổng tiền: "));
        JLabel totalLabel = new JLabel();
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalPanel.add(totalLabel);
        panel.add(totalPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createPaymentInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin thanh toán"));
        
        panel.add(new JLabel("Phương thức:"));
        panel.add(new JLabel());
        panel.add(new JLabel("Trạng thái:"));
        panel.add(new JLabel());
        
        return panel;
    }
    
    private void loadOrderData() {
        try (Connection conn = DBConnection.getConnection()) {
            // Load user info
            String userQuery = "SELECT u.* FROM users u " +
                             "JOIN orders o ON u.id = o.user_id " +
                             "WHERE o.id = ?";
            PreparedStatement userStmt = conn.prepareStatement(userQuery);
            userStmt.setInt(1, orderId);
            ResultSet userRs = userStmt.executeQuery();
            
            if (userRs.next()) {
                JPanel userPanel = (JPanel) ((JScrollPane) getContentPane().getComponent(0)).getViewport().getView();
                JPanel infoPanel = (JPanel) userPanel.getComponent(0);
                ((JLabel) infoPanel.getComponent(1)).setText(userRs.getString("username"));
                ((JLabel) infoPanel.getComponent(3)).setText(userRs.getString("email"));
                ((JLabel) infoPanel.getComponent(5)).setText(userRs.getString("phone"));
                ((JLabel) infoPanel.getComponent(7)).setText(userRs.getString("address"));
            }
            
            // Load order info
            String orderQuery = "SELECT * FROM orders WHERE id = ?";
            PreparedStatement orderStmt = conn.prepareStatement(orderQuery);
            orderStmt.setInt(1, orderId);
            ResultSet orderRs = orderStmt.executeQuery();
            
            if (orderRs.next()) {
                JPanel userPanel = (JPanel) ((JScrollPane) getContentPane().getComponent(0)).getViewport().getView();
                JPanel orderPanel = (JPanel) userPanel.getComponent(2);
                ((JLabel) orderPanel.getComponent(3)).setText(orderRs.getTimestamp("order_date").toString());
            }
            
            // Load products
            String productsQuery = "SELECT p.name, p.image_url, s.size, od.quantity, od.unit_price, " +
                                 "(od.quantity * od.unit_price) as subtotal " +
                                 "FROM order_details od " +
                                 "JOIN product_variants pv ON od.variant_id = pv.id " +
                                 "JOIN products p ON pv.product_id = p.id " +
                                 "JOIN sizes s ON pv.size_id = s.id " +
                                 "WHERE od.order_id = ?";
            PreparedStatement productsStmt = conn.prepareStatement(productsQuery);
            productsStmt.setInt(1, orderId);
            ResultSet productsRs = productsStmt.executeQuery();
            
            JPanel userPanel = (JPanel) ((JScrollPane) getContentPane().getComponent(0)).getViewport().getView();
            JPanel productsPanel = (JPanel) userPanel.getComponent(4);
            JTable table = (JTable) ((JScrollPane) productsPanel.getComponent(0)).getViewport().getView();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            
            double total = 0;
            while (productsRs.next()) {
                Object[] row = {
                    productsRs.getString("image_url"),
                    productsRs.getString("name"),
                    productsRs.getString("size"),
                    productsRs.getInt("quantity"),
                    formatter.format(productsRs.getDouble("unit_price")) + "đ",
                    formatter.format(productsRs.getDouble("subtotal")) + "đ"
                };
                model.addRow(row);
                total += productsRs.getDouble("subtotal");
            }
            
            JPanel totalPanel = (JPanel) productsPanel.getComponent(1);
            ((JLabel) totalPanel.getComponent(1)).setText(formatter.format(total) + "đ");
            
            // Load payment info
            String paymentQuery = "SELECT * FROM payments WHERE order_id = ?";
            PreparedStatement paymentStmt = conn.prepareStatement(paymentQuery);
            paymentStmt.setInt(1, orderId);
            ResultSet paymentRs = paymentStmt.executeQuery();
            
            if (paymentRs.next()) {
                JPanel paymentPanel = (JPanel) userPanel.getComponent(6);
                String paymentMethod = switch(paymentRs.getString("payment_method")) {
                    case "cash" -> "Tiền mặt";
                    case "credit_card" -> "Thẻ tín dụng";
                    case "paypal" -> "PayPal";
                    case "bank_transfer" -> "Chuyển khoản";
                    default -> paymentRs.getString("payment_method");
                };
                ((JLabel) paymentPanel.getComponent(1)).setText(paymentMethod);
                ((JLabel) paymentPanel.getComponent(3)).setText(paymentRs.getString("status"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải thông tin đơn hàng: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
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