package view.customer;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import utils.DBConnection;
import java.text.DecimalFormat;
import javax.swing.table.*;
import java.net.URL;
import javax.swing.border.TitledBorder;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.BaseFont;
import java.awt.Image;
import javax.swing.ImageIcon;

public class OrderConfirmation extends JDialog {
    private int orderId;
    private DecimalFormat formatter = new DecimalFormat("#,###");
    
    public OrderConfirmation(JDialog parent, int orderId) {
        super(parent, "Thông tin hóa đơn đơn hàng", true);
        this.orderId = orderId;
        
        setSize(600, 800);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Shop Info Panel
        mainPanel.add(createShopInfoPanel());
        mainPanel.add(Box.createVerticalStrut(20));
        
        // User Info Panel
        mainPanel.add(createUserInfoPanel());
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Order Info Panel
        mainPanel.add(createOrderInfoPanel());
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Products Table Panel
        JPanel productsPanel = createProductsPanel();
        JTable table = (JTable) ((JScrollPane) productsPanel.getComponent(0)).getViewport().getView();
        table.setRowHeight(120);
        mainPanel.add(productsPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Payment Info Panel
        mainPanel.add(createPaymentInfoPanel());
        
        // Add to scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
        
        // Close Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        // Thêm nút in hóa đơn
        JButton printButton = new JButton("In hóa đơn");
        printButton.setPreferredSize(new Dimension(100, 35));
        printButton.addActionListener(e -> exportToPDF());
        
        JButton closeButton = new JButton("Đóng");
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        loadOrderData();
    }
    
    private JPanel createShopInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEmptyBorder(),
            "SHOE SHOP",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new java.awt.Font("Arial", java.awt.Font.BOLD, 18)
        ));
        
        java.awt.Font infoFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 12);
        
        JLabel addressLabel = new JLabel("Địa chỉ: 168 Nguyễn Văn Cừ (nối dài), Phường An Bình, Q. Ninh Kiều, TP. Cần Thơ", SwingConstants.CENTER);
        JLabel phoneLabel = new JLabel("Hotline: 1900 1234", SwingConstants.CENTER);
        JLabel emailLabel = new JLabel("Email: contact@shoeshop.com", SwingConstants.CENTER);
        
        addressLabel.setFont(infoFont);
        phoneLabel.setFont(infoFont);
        emailLabel.setFont(infoFont);
        
        panel.add(addressLabel);
        panel.add(phoneLabel);
        panel.add(emailLabel);
        
        return panel;
    }
    
    private JPanel createUserInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEmptyBorder(), 
            "Thông tin khách hàng",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new java.awt.Font("Arial", java.awt.Font.BOLD, 14)
        ));
        panel.setPreferredSize(new Dimension(500, 150));
        
        java.awt.Font labelFont = new java.awt.Font("Arial", java.awt.Font.BOLD, 12);
        java.awt.Font valueFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 12);
        
        JLabel[] labels = {
            new JLabel("Họ tên:"),
            new JLabel("Email:"),
            new JLabel("Số điện thoại:"),
            new JLabel("Địa chỉ:")
        };
        
        for (JLabel label : labels) {
            label.setFont(labelFont);
            panel.add(label);
            JLabel valueLabel = new JLabel();
            valueLabel.setFont(valueFont);
            panel.add(valueLabel);
        }
        
        return panel;
    }
    
    private JPanel createOrderInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEmptyBorder(),
            "Thông tin đơn hàng",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new java.awt.Font("Arial", java.awt.Font.BOLD, 14)
        ));
        panel.setPreferredSize(new Dimension(500, 80));
        
        java.awt.Font labelFont = new java.awt.Font("Arial", java.awt.Font.BOLD, 12);
        java.awt.Font valueFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 12);
        
        JLabel orderIdLabel = new JLabel("Mã đơn hàng:");
        orderIdLabel.setFont(labelFont);
        JLabel orderIdValue = new JLabel("#" + orderId);
        orderIdValue.setFont(valueFont);
        
        JLabel dateLabel = new JLabel("Ngày đặt:");
        dateLabel.setFont(labelFont);
        JLabel dateValue = new JLabel();
        dateValue.setFont(valueFont);
        
        panel.add(orderIdLabel);
        panel.add(orderIdValue);
        panel.add(dateLabel);
        panel.add(dateValue);
        
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
        totalLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
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
                JPanel mainPanel = (JPanel) ((JScrollPane) getContentPane().getComponent(0)).getViewport().getView();
                // User info panel là component thứ 2 (index 2) sau shop info và vertical strut
                JPanel userInfoPanel = (JPanel) mainPanel.getComponent(2);
                ((JLabel) userInfoPanel.getComponent(1)).setText(userRs.getString("username"));
                ((JLabel) userInfoPanel.getComponent(3)).setText(userRs.getString("email"));
                ((JLabel) userInfoPanel.getComponent(5)).setText(userRs.getString("phone"));
                ((JLabel) userInfoPanel.getComponent(7)).setText(userRs.getString("address"));
            }
            
            // Load order info
            String orderQuery = "SELECT * FROM orders WHERE id = ?";
            PreparedStatement orderStmt = conn.prepareStatement(orderQuery);
            orderStmt.setInt(1, orderId);
            ResultSet orderRs = orderStmt.executeQuery();
            
            if (orderRs.next()) {
                JPanel mainPanel = (JPanel) ((JScrollPane) getContentPane().getComponent(0)).getViewport().getView();
                // Order info panel là component thứ 4 (index 4)
                JPanel orderPanel = (JPanel) mainPanel.getComponent(4);
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
            
            JPanel mainPanel = (JPanel) ((JScrollPane) getContentPane().getComponent(0)).getViewport().getView();
            // Products panel là component thứ 6 (index 6)
            JPanel productsPanel = (JPanel) mainPanel.getComponent(6);
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
                // Payment info panel là component thứ 8 (index 8)
                JPanel paymentPanel = (JPanel) mainPanel.getComponent(8);
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
                "Lỗi khi tải thông tin hóa đơn đơn hàng: " + e.getMessage(),
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
                    java.awt.Image img = icon.getImage().getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH);
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
    
    // Thêm phương thức xuất PDF
    private void exportToPDF() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu hóa đơn");
            fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
            fileChooser.setSelectedFile(new File("hoadon_" + orderId + ".pdf"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }
                
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();
                
                // Font cho PDF
                BaseFont bf = BaseFont.createFont("src/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(bf, 12);
                com.itextpdf.text.Font boldFont = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(bf, 18, com.itextpdf.text.Font.BOLD);
                
                // Lấy thông tin từ các panel
                JPanel mainPanel = (JPanel) ((JScrollPane) getContentPane().getComponent(0)).getViewport().getView();
                
                // 1. Thông tin cửa hàng
                document.add(new Paragraph("SHOE SHOP", titleFont));
                JPanel shopInfoPanel = (JPanel) mainPanel.getComponent(0);
                document.add(new Paragraph(((JLabel) shopInfoPanel.getComponent(0)).getText(), normalFont));
                document.add(new Paragraph(((JLabel) shopInfoPanel.getComponent(1)).getText(), normalFont));
                document.add(new Paragraph(((JLabel) shopInfoPanel.getComponent(2)).getText(), normalFont));
                document.add(new Paragraph("\n"));
                
                // 2. Thông tin khách hàng
                document.add(new Paragraph("THÔNG TIN KHÁCH HÀNG", boldFont));
                JPanel userInfoPanel = (JPanel) mainPanel.getComponent(2);
                document.add(new Paragraph("Họ tên: " + ((JLabel) userInfoPanel.getComponent(1)).getText(), normalFont));
                document.add(new Paragraph("Email: " + ((JLabel) userInfoPanel.getComponent(3)).getText(), normalFont));
                document.add(new Paragraph("Số điện thoại: " + ((JLabel) userInfoPanel.getComponent(5)).getText(), normalFont));
                document.add(new Paragraph("Địa chỉ: " + ((JLabel) userInfoPanel.getComponent(7)).getText(), normalFont));
                document.add(new Paragraph("\n"));
                
                // 3. Thông tin đơn hàng
                document.add(new Paragraph("THÔNG TIN ĐƠN HÀNG", boldFont));
                JPanel orderInfoPanel = (JPanel) mainPanel.getComponent(4);
                document.add(new Paragraph("Mã đơn hàng: " + ((JLabel) orderInfoPanel.getComponent(1)).getText(), normalFont));
                document.add(new Paragraph("Ngày đặt: " + ((JLabel) orderInfoPanel.getComponent(3)).getText(), normalFont));
                document.add(new Paragraph("\n"));
                
                // 4. Bảng sản phẩm
                document.add(new Paragraph("DANH SÁCH SẢN PHẨM", boldFont));
                JPanel productsPanel = (JPanel) mainPanel.getComponent(6);
                JTable table = (JTable) ((JScrollPane) productsPanel.getComponent(0)).getViewport().getView();
                
                PdfPTable pdfTable = new PdfPTable(5); // 5 cột (bỏ cột hình ảnh)
                pdfTable.setWidthPercentage(100);
                float[] columnWidths = {40f, 10f, 15f, 15f, 20f};
                pdfTable.setWidths(columnWidths);
                
                // Header
                String[] headers = {"Sản phẩm", "Size", "Số lượng", "Đơn giá", "Thành tiền"};
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, boldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    pdfTable.addCell(cell);
                }
                
                // Data
                for (int i = 0; i < table.getRowCount(); i++) {
                    pdfTable.addCell(new Phrase(table.getValueAt(i, 1).toString(), normalFont)); // Tên sản phẩm
                    pdfTable.addCell(new Phrase(table.getValueAt(i, 2).toString(), normalFont)); // Size
                    pdfTable.addCell(new Phrase(table.getValueAt(i, 3).toString(), normalFont)); // Số lượng
                    pdfTable.addCell(new Phrase(table.getValueAt(i, 4).toString(), normalFont)); // Đơn giá
                    pdfTable.addCell(new Phrase(table.getValueAt(i, 5).toString(), normalFont)); // Thành tiền
                }
                document.add(pdfTable);
                document.add(new Paragraph("\n"));
                
                // 5. Tổng tiền
                JPanel totalPanel = (JPanel) productsPanel.getComponent(1);
                document.add(new Paragraph("Tổng tiền: " + ((JLabel) totalPanel.getComponent(1)).getText(), boldFont));
                document.add(new Paragraph("\n"));
                
                // 6. Thông tin thanh toán
                document.add(new Paragraph("THÔNG TIN THANH TOÁN", boldFont));
                JPanel paymentPanel = (JPanel) mainPanel.getComponent(8);
                document.add(new Paragraph("Phương thức: " + ((JLabel) paymentPanel.getComponent(1)).getText(), normalFont));
                document.add(new Paragraph("Trạng thái: " + ((JLabel) paymentPanel.getComponent(3)).getText(), normalFont));
                
                document.close();
                
                JOptionPane.showMessageDialog(this, 
                    "Đã xuất hóa đơn thành công!", 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi xuất hóa đơn: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 