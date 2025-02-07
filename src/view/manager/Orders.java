package view.manager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.sql.*;
import utils.DBConnection;
import com.formdev.flatlaf.FlatLightLaf;
import java.text.DecimalFormat;

public class Orders extends JPanel {
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusComboBox;
    private DecimalFormat formatter = new DecimalFormat("#,###");
    
    public Orders() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top Panel - Search and Filter
        JPanel topPanel = createTopPanel();
        
        // Center Panel - Table
        JPanel tablePanel = createTablePanel();
        
        // Add panels to main panel
        add(topPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        
        // Load initial data
        loadOrderData();
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Tìm kiếm đơn hàng...");
        
        JButton searchButton = createStyledButton("Tìm kiếm", new Color(70, 130, 180));
        searchButton.addActionListener(e -> searchOrders());
        
        // Status filter
        statusComboBox = new JComboBox<>(new String[]{"Tất cả", "Chờ xử lý", "Đã giao", "Đã hủy"});
        statusComboBox.addActionListener(e -> loadOrderData());
        
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(new JLabel("Trạng thái:"));
        searchPanel.add(statusComboBox);
        
        panel.add(searchPanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table model
        String[] columns = {"ID", "Khách hàng", "Tổng tiền", "Trạng thái", "Ngày đặt", "Thao tác"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Chỉ cho phép edit cột thao tác
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 5 ? JButton.class : Object.class;
            }
        };
        
        orderTable = new JTable(tableModel);
        
        // Thêm button renderer và editor cho cột thao tác
        TableColumn actionColumn = orderTable.getColumnModel().getColumn(5);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor());
        
        // Style table
        orderTable.setRowHeight(30);
        orderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        orderTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(orderTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadOrderData() {
        tableModel.setRowCount(0);
        String selectedStatus = statusComboBox.getSelectedItem().toString();
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT o.*, u.username FROM orders o " +
                          "JOIN users u ON o.user_id = u.id " +
                          (selectedStatus.equals("Tất cả") ? "" : 
                           "WHERE o.status = '" + getStatusValue(selectedStatus) + "' ") +
                          "ORDER BY o.order_date DESC";
                          
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("username"),
                    formatter.format(rs.getDouble("total_price")) + " đ",
                    getStatusText(rs.getString("status")),
                    rs.getTimestamp("order_date"),
                    "Chi tiết đơn hàng" // Thêm text cho nút
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }
    
    private void showOrderDetails() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow >= 0) {
            int orderId = (int) tableModel.getValueAt(selectedRow, 0);
            JDialog dialog = new OrderDetailsDialog(orderId);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng để xem chi tiết.");
        }
    }
    
    private void searchOrders() {
        String searchText = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT o.*, u.username FROM orders o " +
                          "JOIN users u ON o.user_id = u.id " +
                          "WHERE LOWER(u.username) LIKE ? " +
                          "ORDER BY o.order_date DESC";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + searchText + "%");
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("username"),
                    formatter.format(rs.getDouble("total_price")) + " đ",
                    getStatusText(rs.getString("status")),
                    rs.getTimestamp("order_date"),
                    "Chi tiết đơn hàng" // Thêm text cho nút
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }
    
    private String getStatusText(String status) {
        switch (status) {
            case "pending": return "Chờ xử lý";
            case "shipped": return "Đã giao";
            case "cancelled": return "Đã hủy";
            default: return status;
        }
    }
    
    private String getStatusValue(String displayText) {
        switch (displayText) {
            case "Chờ xử lý": return "pending";
            case "Đã giao": return "shipped";
            case "Đã hủy": return "cancelled";
            default: return displayText;
        }
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    // Dialog hiển thị chi tiết đơn hàng
    private class OrderDetailsDialog extends JDialog {
        public OrderDetailsDialog(int orderId) {
            setTitle("Chi tiết đơn hàng #" + orderId);
            setModal(true);
            setSize(800, 500);
            setLocationRelativeTo(null);
            
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Order info panel
            JPanel infoPanel = createOrderInfoPanel(orderId);
            
            // Order items table
            JPanel itemsPanel = createOrderItemsPanel(orderId);
            
            // Status update panel
            JPanel statusPanel = createStatusUpdatePanel(orderId);
            
            mainPanel.add(infoPanel, BorderLayout.NORTH);
            mainPanel.add(itemsPanel, BorderLayout.CENTER);
            mainPanel.add(statusPanel, BorderLayout.SOUTH);
            
            add(mainPanel);
        }
        
        private JPanel createOrderInfoPanel(int orderId) {
            JPanel panel = new JPanel(new GridLayout(0, 2, 10, 5));
            panel.setBorder(BorderFactory.createTitledBorder("Thông tin đơn hàng"));
            
            try (Connection conn = DBConnection.getConnection()) {
                String query = "SELECT o.*, u.username, u.phone, u.address, " +
                              "p.payment_method, p.status as payment_status, p.payment_date " +
                              "FROM orders o " +
                              "JOIN users u ON o.user_id = u.id " +
                              "LEFT JOIN payments p ON o.id = p.order_id " +
                              "WHERE o.id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, orderId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    panel.add(new JLabel("Mã đơn hàng:"));
                    panel.add(new JLabel("#" + orderId));
                    panel.add(new JLabel("Khách hàng:"));
                    panel.add(new JLabel(rs.getString("username")));
                    panel.add(new JLabel("Số điện thoại:"));
                    panel.add(new JLabel(rs.getString("phone")));
                    panel.add(new JLabel("Địa chỉ:"));
                    panel.add(new JLabel(rs.getString("address")));
                    panel.add(new JLabel("Ngày đặt:"));
                    panel.add(new JLabel(rs.getTimestamp("order_date").toString()));
                    panel.add(new JLabel("Phương thức thanh toán:"));
                    panel.add(new JLabel(rs.getString("payment_method") != null ? 
                            getPaymentMethodText(rs.getString("payment_method")) : "Chưa thanh toán"));
                    panel.add(new JLabel("Trạng thái thanh toán:"));
                    panel.add(new JLabel(rs.getString("payment_status") != null ? 
                            getPaymentStatusText(rs.getString("payment_status")) : "Chưa thanh toán"));
                    panel.add(new JLabel("Trạng thái đơn hàng:"));
                    panel.add(new JLabel(getStatusText(rs.getString("status"))));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            return panel;
        }
        
        private JPanel createOrderItemsPanel(int orderId) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createTitledBorder("Chi tiết sản phẩm"));
            
            String[] columns = {"Sản phẩm", "Màu sắc", "Kích thước", "Giá", "Số lượng", "Thành tiền"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            JTable itemsTable = new JTable(model);
            
            try (Connection conn = DBConnection.getConnection()) {
                String query = "SELECT p.name, c.color_name, s.size, od.unit_price, od.quantity " +
                              "FROM order_details od " +
                              "JOIN product_variants pv ON od.variant_id = pv.id " +
                              "JOIN products p ON pv.product_id = p.id " +
                              "JOIN colors c ON pv.color_id = c.id " +
                              "JOIN sizes s ON pv.size_id = s.id " +
                              "WHERE od.order_id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, orderId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    double price = rs.getDouble("unit_price");
                    int quantity = rs.getInt("quantity");
                    Object[] row = {
                        rs.getString("name"),
                        rs.getString("color_name"),
                        rs.getString("size"),
                        formatter.format(price) + " đ",
                        quantity,
                        formatter.format(price * quantity) + " đ"
                    };
                    model.addRow(row);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Lỗi khi tải chi tiết đơn hàng: " + e.getMessage());
            }
            
            // Style table
            itemsTable.setRowHeight(25);
            itemsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            itemsTable.setFont(new Font("Arial", Font.PLAIN, 12));
            
            panel.add(new JScrollPane(itemsTable), BorderLayout.CENTER);
            return panel;
        }
        
        private JPanel createStatusUpdatePanel(int orderId) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            
            JComboBox<String> statusUpdate = new JComboBox<>(new String[]{
                "Chờ xử lý", "Đã giao", "Đã hủy"
            });
            
            JButton updateButton = createStyledButton("Cập nhật trạng thái", new Color(46, 204, 113));
            updateButton.addActionListener(e -> {
                try (Connection conn = DBConnection.getConnection()) {
                    String query = "UPDATE orders SET status = ? WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, getStatusValue(statusUpdate.getSelectedItem().toString()));
                    stmt.setInt(2, orderId);
                    stmt.executeUpdate();
                    
                    loadOrderData();
                    JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thành công!");
                    dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage());
                }
            });
            
            panel.add(new JLabel("Trạng thái:"));
            panel.add(statusUpdate);
            panel.add(updateButton);
            
            return panel;
        }
        
        private String getPaymentStatusText(String status) {
            switch (status) {
                case "pending": return "Chờ thanh toán";
                case "paid": return "Đã thanh toán";
                case "failed": return "Thanh toán thất bại";
                case "refunded": return "Đã hoàn tiền";
                default: return status;
            }
        }
        
        private String getPaymentMethodText(String method) {
            switch (method) {
                case "cash": return "Tiền mặt";
                case "credit_card": return "Thẻ tín dụng";
                case "paypal": return "PayPal";
                case "bank_transfer": return "Chuyển khoản";
                default: return method;
            }
        }
    }
    
    // Thêm class ButtonRenderer
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(52, 152, 219));
            setForeground(Color.WHITE);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Chi tiết đơn hàng");
            return this;
        }
    }
    
    // Thêm class ButtonEditor
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        
        public ButtonEditor() {
            super(new JCheckBox());
            button = new JButton("Chi tiết đơn hàng");
            button.setBackground(new Color(52, 152, 219));
            button.setForeground(Color.WHITE);
            
            button.addActionListener(e -> {
                fireEditingStopped();
                showOrderDetails();
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "Chi tiết đơn hàng";
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            
            JFrame frame = new JFrame("Quản lý đơn hàng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new Orders());
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 