package view.manager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.sql.*;
import java.util.Calendar;
import utils.DBConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.Color;
import java.util.HashMap;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.Box;
import javax.swing.JOptionPane;

public class Statistical extends JPanel {
    private JTabbedPane tabbedPane;
    private DecimalFormat formatter = new DecimalFormat("#,###");
    
    public Statistical() {
        setLayout(new BorderLayout());
        
        // Tạo TabbedPane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Thêm các tab thống kê
        tabbedPane.addTab("Doanh thu", createRevenuePanel());
        tabbedPane.addTab("Đơn hàng", createOrdersPanel());
        tabbedPane.addTab("Sản phẩm bán chạy", createBestSellerPanel());
        tabbedPane.addTab("Khách hàng", createCustomersPanel());
        tabbedPane.addTab("Đánh giá", createReviewsPanel());
        tabbedPane.addTab("Thanh toán", createPaymentsPanel());
        
        add(tabbedPane);
    }
    
    // Panel Doanh thu
    private JPanel createRevenuePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header Panel với bộ lọc
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Thống kê doanh thu bán hàng");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Tạo combobox năm
        JComboBox<String> yearComboBox = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = currentYear; year >= currentYear - 5; year--) {
            yearComboBox.addItem(String.valueOf(year));
        }
        
        // Tạo combobox tháng
        JComboBox<String> monthComboBox = new JComboBox<>();
        monthComboBox.addItem("Tất cả các tháng");
        for (int month = 1; month <= 12; month++) {
            monthComboBox.addItem("Tháng " + month);
        }
        
        filterPanel.add(new JLabel("Năm: "));
        filterPanel.add(yearComboBox);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(new JLabel("Tháng: "));
        filterPanel.add(monthComboBox);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);
        
        // Chart Panel
        JPanel chartPanel = new JPanel(new BorderLayout());
        
        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Load initial data
        loadRevenueData(chartPanel, statsPanel, yearComboBox.getSelectedItem().toString(),
                monthComboBox.getSelectedItem().toString());
        
        // Add listeners
        yearComboBox.addActionListener(e -> loadRevenueData(chartPanel, statsPanel,
                yearComboBox.getSelectedItem().toString(),
                monthComboBox.getSelectedItem().toString()));
                
        monthComboBox.addActionListener(e -> loadRevenueData(chartPanel, statsPanel,
                yearComboBox.getSelectedItem().toString(),
                monthComboBox.getSelectedItem().toString()));
        
        // Add components
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.add(statsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadRevenueData(JPanel chartPanel, JPanel statsPanel, String selectedYear, String selectedMonth) {
        try (Connection conn = DBConnection.getConnection()) {
            // Chuẩn bị câu query dựa trên lựa chọn tháng/năm
            String dateFilter;
            if (selectedMonth.equals("Tất cả các tháng")) {
                dateFilter = "YEAR(o.order_date) = " + selectedYear;
            } else {
                int month = Integer.parseInt(selectedMonth.split(" ")[1]);
                dateFilter = "YEAR(o.order_date) = " + selectedYear + " AND MONTH(o.order_date) = " + month;
            }
            
            // Query để lấy doanh thu và số đơn hàng theo ngày
            String query = "SELECT DATE(o.order_date) as date, " +
                          "SUM(o.total_price) as revenue, " +
                          "COUNT(o.id) as order_count " +
                          "FROM orders o " +
                          "WHERE " + dateFilter + " " +
                          "GROUP BY DATE(o.order_date) " +
                          "ORDER BY date DESC " +
                          "LIMIT 10";
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            // Tạo dataset cho biểu đồ
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            double totalRevenue = 0;
            int totalOrders = 0;
            
            while (rs.next()) {
                String date = rs.getDate("date").toString();
                double revenue = rs.getDouble("revenue");
                int orderCount = rs.getInt("order_count");
                
                dataset.addValue(revenue, "Doanh thu", date);
                totalRevenue += revenue;
                totalOrders += orderCount;
            }
            
            // Tạo biểu đồ
            JFreeChart chart = ChartFactory.createBarChart(
                null,                      // chart title
                "Ngày",                    // domain axis label
                "Doanh thu (VNĐ)",        // range axis label
                dataset,                   // data
                PlotOrientation.VERTICAL,  // orientation
                true,                      // include legend
                true,                      // tooltips
                false                      // urls
            );
            
            // Cập nhật chart panel
            chartPanel.removeAll();
            ChartPanel newChartPanel = new ChartPanel(chart);
            chartPanel.add(newChartPanel);
            chartPanel.revalidate();
            
            // Cập nhật stats panel
            statsPanel.removeAll();
            statsPanel.add(createStatBox("Doanh thu", formatter.format(totalRevenue) + " đ", 
                new Color(240, 240, 255)));
            statsPanel.add(createStatBox("Đơn hàng", String.valueOf(totalOrders), 
                new Color(255, 240, 240)));
            statsPanel.revalidate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }
    
    // Panel Đơn hàng
    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        try (Connection conn = DBConnection.getConnection()) {
            // Thống kê trạng thái đơn hàng
            String query = "SELECT status, COUNT(*) as count FROM orders GROUP BY status";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            DefaultPieDataset dataset = new DefaultPieDataset();
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("count");
                dataset.setValue(getStatusText(status), count);
            }
            
            JFreeChart chart = ChartFactory.createPieChart(
                "Thống kê trạng thái đơn hàng",
                dataset,
                true,
                true,
                false
            );
            
            ChartPanel chartPanel = new ChartPanel(chart);
            panel.add(chartPanel, BorderLayout.CENTER);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return panel;
    }
    
    // Panel Sản phẩm bán chạy
    private JPanel createBestSellerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Tạo bảng sản phẩm bán chạy
        String[] columns = {"STT", "Tên sản phẩm", "Danh mục", "Số lượng đã bán", "Doanh thu"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT p.name, c.name as category, " +
                          "SUM(od.quantity) as total_quantity, " +
                          "SUM(od.quantity * od.unit_price) as revenue " +
                          "FROM products p " +
                          "JOIN categories c ON p.category_id = c.id " +
                          "JOIN order_details od ON p.id = od.variant_id " +
                          "GROUP BY p.id " +
                          "ORDER BY total_quantity DESC " +
                          "LIMIT 10";
                          
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            int stt = 1;
            while (rs.next()) {
                Object[] row = {
                    stt++,
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getInt("total_quantity"),
                    formatter.format(rs.getDouble("revenue")) + " đ"
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(new JLabel("Top 10 sản phẩm bán chạy nhất"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Panel Khách hàng
    private JPanel createCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Tạo bảng khách hàng tiềm năng
        String[] columns = {"STT", "Khách hàng", "Số đơn hàng", "Tổng chi tiêu"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT u.username, " +
                          "COUNT(o.id) as order_count, " +
                          "SUM(o.total_price) as total_spent " +
                          "FROM users u " +
                          "JOIN orders o ON u.id = o.user_id " +
                          "GROUP BY u.id " +
                          "ORDER BY total_spent DESC " +
                          "LIMIT 10";
                          
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            int stt = 1;
            while (rs.next()) {
                Object[] row = {
                    stt++,
                    rs.getString("username"),
                    rs.getInt("order_count"),
                    formatter.format(rs.getDouble("total_spent")) + " đ"
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(new JLabel("Top 10 khách hàng tiềm năng"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Panel Đánh giá
    private JPanel createReviewsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        try (Connection conn = DBConnection.getConnection()) {
            // Thống kê số sao đánh giá
            String query = "SELECT rating, COUNT(*) as count FROM reviews GROUP BY rating";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            while (rs.next()) {
                int rating = rs.getInt("rating");
                int count = rs.getInt("count");
                dataset.setValue(count, "Số lượng", rating + " sao");
            }
            
            JFreeChart chart = ChartFactory.createBarChart(
                "Thống kê đánh giá sản phẩm",
                "Số sao",
                "Số lượng",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
            );
            
            ChartPanel chartPanel = new ChartPanel(chart);
            panel.add(chartPanel, BorderLayout.CENTER);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return panel;
    }
    
    // Panel Thanh toán
    private JPanel createPaymentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        try (Connection conn = DBConnection.getConnection()) {
            // Thống kê phương thức thanh toán
            String query = "SELECT payment_method, COUNT(*) as count, SUM(o.total_price) as total " +
                          "FROM payments p " +
                          "JOIN orders o ON p.order_id = o.id " +
                          "GROUP BY payment_method";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            DefaultPieDataset dataset = new DefaultPieDataset();
            while (rs.next()) {
                String method = getPaymentMethodText(rs.getString("payment_method"));
                double total = rs.getDouble("total");
                dataset.setValue(method, total);
            }
            
            JFreeChart chart = ChartFactory.createPieChart(
                "Thống kê phương thức thanh toán",
                dataset,
                true,
                true,
                false
            );
            
            ChartPanel chartPanel = new ChartPanel(chart);
            panel.add(chartPanel, BorderLayout.CENTER);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return panel;
    }
    
    private String getStatusText(String status) {
        switch (status) {
            case "pending": return "Chờ xử lý";
            case "processing": return "Đang xử lý";
            case "completed": return "Hoàn thành";
            case "cancelled": return "Đã hủy";
            default: return status;
        }
    }
    
    private String getPaymentMethodText(String method) {
        switch (method) {
            case "cash": return "Tiền mặt";
            case "credit_card": return "Thẻ tín dụng";
            case "bank_transfer": return "Chuyển khoản";
            case "paypal": return "PayPal";
            default: return method;
        }
    }
    
    private JPanel createStatBox(String title, String value, Color bgColor) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(bgColor);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        panel.add(titleLabel);
        panel.add(valueLabel);
        
        return panel;
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Thống kê");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Statistical());
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
} 