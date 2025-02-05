package view.manager;

import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.FlatLightLaf;
import view.Main;
import view.customer.CustomerView;

public class ManagerView extends JFrame {
    private JTabbedPane tabbedPane;
    private int userId;
    
    public ManagerView(int userId) {
        this.userId = userId;
        setTitle("Hệ thống quản lý");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        // Tạo TabbedPane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Thêm các tab quản lý với icon
        tabbedPane.addTab("Sản phẩm", resizeIcon(new ImageIcon("src/icons/product.png"), 20, 20), new Products());
        tabbedPane.addTab("Danh mục", resizeIcon(new ImageIcon("src/icons/category.png"), 20, 20), new Categories());
        tabbedPane.addTab("Biến thể", resizeIcon(new ImageIcon("src/icons/variant.png"), 20, 20), new Variants());
        tabbedPane.addTab("Đơn hàng", resizeIcon(new ImageIcon("src/icons/order.png"), 20, 20), new Orders());
        tabbedPane.addTab("Thanh toán", resizeIcon(new ImageIcon("src/icons/payment.png"), 20, 20), new Payments());
        tabbedPane.addTab("Người dùng", resizeIcon(new ImageIcon("src/icons/user.png"), 20, 20), new Users());
        tabbedPane.addTab("Thống kê", resizeIcon(new ImageIcon("src/icons/statistical.png"), 20, 20), new Statistical());
        
        // Style cho TabbedPane
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(new Color(51, 51, 51));
        
        // Thêm TabbedPane vào frame
        add(tabbedPane);
        
        // Thêm nút đăng xuất và chuyển đổi
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        // Nút chuyển sang khách hàng
        JButton switchButton = new JButton("Chế độ khách hàng");
        switchButton.setBackground(new Color(52, 152, 219)); // Màu xanh dương
        switchButton.setForeground(Color.WHITE);
        switchButton.setFocusPainted(false);
        switchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        switchButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có muốn chuyển sang giao diện khách hàng?",
                "Xác nhận chuyển đổi",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                dispose(); // Đóng cửa sổ hiện tại
                new CustomerView(userId).setVisible(true);
            }
        });
        
        // Nút đăng xuất (giữ nguyên code cũ)
        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                dispose(); // Đóng cửa sổ hiện tại
                new Main(0).setVisible(true); // Mở lại màn hình đăng nhập
            }
        });
        
        buttonsPanel.add(switchButton);
        buttonsPanel.add(logoutButton);
        add(buttonsPanel, BorderLayout.NORTH);
    }
    
    // Phương thức để thay đổi kích thước icon
    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
} 