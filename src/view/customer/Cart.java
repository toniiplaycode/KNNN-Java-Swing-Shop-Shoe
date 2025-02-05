package view.customer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import utils.DBConnection;
import java.text.DecimalFormat;

public class Cart extends JDialog {
    private int userId;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private double total = 0;
    private DecimalFormat formatter = new DecimalFormat("#,###");
    
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
        String[] columns = {"ID", "Sản phẩm", "Size", "Số lượng", "Đơn giá", "Thành tiền", ""};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Chỉ cho phép sửa cột số lượng
            }
        };
        
        cartTable = new JTable(tableModel);
        cartTable.setRowHeight(35);
        cartTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        cartTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Add delete button to last column
        TableColumn deleteColumn = cartTable.getColumnModel().getColumn(6);
        deleteColumn.setMinWidth(80);
        deleteColumn.setMaxWidth(80);
        deleteColumn.setCellRenderer(new ButtonRenderer());
        deleteColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
        
        // Add spinner for quantity column
        TableColumn quantityColumn = cartTable.getColumnModel().getColumn(3);
        quantityColumn.setCellEditor(new SpinnerEditor());
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Total Panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Tổng tiền: 0đ");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalPanel.add(totalLabel);
        
        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        JButton checkoutButton = new JButton("Thanh toán");
        styleButton(checkoutButton, new Color(46, 204, 113));
        checkoutButton.addActionListener(e -> checkout());
        
        JButton closeButton = new JButton("Đóng");
        styleButton(closeButton, new Color(231, 76, 60));
        closeButton.addActionListener(e -> dispose());
        
        buttonsPanel.add(checkoutButton);
        buttonsPanel.add(closeButton);
        
        panel.add(totalPanel, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void loadCartData() {
        tableModel.setRowCount(0);
        total = 0;
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT c.id, p.name, c.size, c.quantity, p.price " +
                          "FROM cart c " +
                          "JOIN products p ON c.product_id = p.id " +
                          "WHERE c.user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String size = rs.getString("size");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                double subtotal = price * quantity;
                
                total += subtotal;
                
                Object[] row = {
                    id,
                    name,
                    size,
                    quantity,
                    formatter.format(price) + "đ",
                    formatter.format(subtotal) + "đ",
                    "Xóa"
                };
                tableModel.addRow(row);
            }
            
            totalLabel.setText("Tổng tiền: " + formatter.format(total) + "đ");
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải giỏ hàng: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
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
        try (Connection conn = DBConnection.getConnection()) {
            String query = "DELETE FROM cart WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, cartId);
            stmt.executeUpdate();
            
            loadCartData(); // Reload cart data
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi xóa sản phẩm: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
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
        
        // TODO: Implement checkout logic
        JOptionPane.showMessageDialog(this,
            "Tính năng thanh toán đang được phát triển!",
            "Thông báo",
            JOptionPane.INFORMATION_MESSAGE);
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
} 