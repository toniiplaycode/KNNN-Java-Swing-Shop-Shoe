package view;

import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import view.manager.ManagerView;

public class Main extends JFrame {

    private int userId;

    public Main(int userId) {
        this.userId = userId;
    }                       

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            
            // Tạo khung chính
            JFrame frame = new JFrame("Hệ Thống Quản Lý Cửa Hàng Giày");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null); // Căn giữa màn hình
            
            // Panel chính với BoxLayout
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
            mainPanel.setBackground(new Color(245, 245, 245));
            // Tiêu đề
            JLabel titleLabel = new JLabel("CHỌN CHỨC NĂNG");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setForeground(new Color(51, 51, 51));

            // Panel chứa các nút
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(2, 1, 0, 20));
            buttonPanel.setMaximumSize(new Dimension(300, 150));
            buttonPanel.setBackground(new Color(245, 245, 245));

            // Tạo nút cho khách hàng
            JButton customerButton = createStyledButton("Khách Hàng", new Color(70, 130, 180));
            customerButton.addActionListener(e -> {
                // Mở giao diện khách hàng
                frame.dispose(); // Đóng cửa sổ hiện tại
                // TODO: Thêm code mở giao diện khách hàng
            });

            // Tạo nút cho quản lý
            JButton adminButton = createStyledButton("Quản Lý", new Color(60, 179, 113));
            adminButton.addActionListener(e -> {
                frame.dispose(); // Đóng cửa sổ hiện tại
                new ManagerView().setVisible(true);
            });

            // Thêm các components vào panel
            buttonPanel.add(customerButton);
            buttonPanel.add(adminButton);

            mainPanel.add(Box.createVerticalGlue());
            mainPanel.add(titleLabel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
            mainPanel.add(buttonPanel);
            mainPanel.add(Box.createVerticalGlue());

            frame.add(mainPanel);
            frame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hiệu ứng hover
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
}
