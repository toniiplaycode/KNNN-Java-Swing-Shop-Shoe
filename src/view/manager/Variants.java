package view.manager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.sql.*;
import utils.DBConnection;
import com.formdev.flatlaf.FlatLightLaf;

public class Variants extends JPanel {
    private JTabbedPane tabbedPane;
    
    // Components for Sizes
    private JTable sizeTable;
    private DefaultTableModel sizeModel;
    private JTextField sizeIdField;
    private JTextField sizeField;
    
    // Components for Colors
    private JTable colorTable;
    private DefaultTableModel colorModel;
    private JTextField colorIdField;
    private JTextField colorNameField;
    private JTextField colorCodeField;
    
    public Variants() {
        setLayout(new BorderLayout());
        
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Kích thước", createSizePanel());
        tabbedPane.addTab("Màu sắc", createColorPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Load initial data
        loadSizeData();
        loadColorData();
    }
    
    private JPanel createSizePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        String[] sizeColumns = {"ID", "Kích thước"};
        sizeModel = new DefaultTableModel(sizeColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        sizeTable = new JTable(sizeModel);
        sizeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sizeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSizeToForm();
            }
        });
        
        JScrollPane sizeScrollPane = new JScrollPane(sizeTable);
        tablePanel.add(sizeScrollPane, BorderLayout.CENTER);
        
        // Form Panel
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin kích thước"));
        
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        sizeIdField = createTextField("ID");
        sizeIdField.setEnabled(false);
        sizeField = createTextField("Kích thước");
        
        fieldsPanel.add(createFormRow("Kích thước:", sizeField));
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton addSizeButton = createStyledButton("Thêm", new Color(46, 204, 113));
        JButton updateSizeButton = createStyledButton("Cập nhật", new Color(52, 152, 219));
        JButton deleteSizeButton = createStyledButton("Xóa", new Color(231, 76, 60));
        JButton clearSizeButton = createStyledButton("Làm mới", new Color(149, 165, 166));
        
        addSizeButton.addActionListener(e -> saveSize(true));
        updateSizeButton.addActionListener(e -> saveSize(false));
        deleteSizeButton.addActionListener(e -> deleteSize());
        clearSizeButton.addActionListener(e -> clearSizeForm());
        
        buttonPanel.add(addSizeButton);
        buttonPanel.add(updateSizeButton);
        buttonPanel.add(deleteSizeButton);
        buttonPanel.add(clearSizeButton);
        
        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(tablePanel, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createColorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        String[] colorColumns = {"ID", "Tên màu", "Mã màu"};
        colorModel = new DefaultTableModel(colorColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        colorTable = new JTable(colorModel);
        colorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        colorTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadColorToForm();
            }
        });
        
        JScrollPane colorScrollPane = new JScrollPane(colorTable);
        tablePanel.add(colorScrollPane, BorderLayout.CENTER);
        
        // Form Panel
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin màu sắc"));
        
        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        colorIdField = createTextField("ID");
        colorIdField.setEnabled(false);
        colorNameField = createTextField("Tên màu");
        colorCodeField = createTextField("Mã màu (VD: #FF0000)");
        
        fieldsPanel.add(createFormRow("Tên màu:", colorNameField));
        fieldsPanel.add(createFormRow("Mã màu:", colorCodeField));
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton addColorButton = createStyledButton("Thêm", new Color(46, 204, 113));
        JButton updateColorButton = createStyledButton("Cập nhật", new Color(52, 152, 219));
        JButton deleteColorButton = createStyledButton("Xóa", new Color(231, 76, 60));
        JButton clearColorButton = createStyledButton("Làm mới", new Color(149, 165, 166));
        
        addColorButton.addActionListener(e -> saveColor(true));
        updateColorButton.addActionListener(e -> saveColor(false));
        deleteColorButton.addActionListener(e -> deleteColor());
        clearColorButton.addActionListener(e -> clearColorForm());
        
        buttonPanel.add(addColorButton);
        buttonPanel.add(updateColorButton);
        buttonPanel.add(deleteColorButton);
        buttonPanel.add(clearColorButton);
        
        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(tablePanel, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Size Methods
    private void loadSizeData() {
        sizeModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM sizes ORDER BY id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("size")
                };
                sizeModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu kích thước: " + e.getMessage());
        }
    }
    
    private void loadSizeToForm() {
        int selectedRow = sizeTable.getSelectedRow();
        if (selectedRow >= 0) {
            sizeIdField.setText(sizeModel.getValueAt(selectedRow, 0).toString());
            sizeField.setText(sizeModel.getValueAt(selectedRow, 1).toString());
        }
    }
    
    private void saveSize(boolean isNew) {
        try {
            if (sizeField.getText().trim().isEmpty()) {
                throw new Exception("Vui lòng nhập kích thước");
            }
            
            Connection conn = DBConnection.getConnection();
            String query;
            if (isNew) {
                query = "INSERT INTO sizes (size) VALUES (?)";
            } else {
                query = "UPDATE sizes SET size = ? WHERE id = ?";
            }
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, sizeField.getText().trim());
            if (!isNew) {
                stmt.setInt(2, Integer.parseInt(sizeIdField.getText()));
            }
            
            stmt.executeUpdate();
            loadSizeData();
            clearSizeForm();
            JOptionPane.showMessageDialog(this, "Lưu thành công!");
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }
    
    private void deleteSize() {
        if (sizeIdField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn kích thước cần xóa");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa kích thước này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "DELETE FROM sizes WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, Integer.parseInt(sizeIdField.getText()));
                stmt.executeUpdate();
                
                loadSizeData();
                clearSizeForm();
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage());
            }
        }
    }
    
    private void clearSizeForm() {
        sizeIdField.setText("");
        sizeField.setText("");
        sizeTable.clearSelection();
    }
    
    // Color Methods
    private void loadColorData() {
        colorModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM colors ORDER BY id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("color_name"),
                    rs.getString("color_code")
                };
                colorModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu màu sắc: " + e.getMessage());
        }
    }
    
    private void loadColorToForm() {
        int selectedRow = colorTable.getSelectedRow();
        if (selectedRow >= 0) {
            colorIdField.setText(colorModel.getValueAt(selectedRow, 0).toString());
            colorNameField.setText(colorModel.getValueAt(selectedRow, 1).toString());
            colorCodeField.setText(colorModel.getValueAt(selectedRow, 2).toString());
        }
    }
    
    private void saveColor(boolean isNew) {
        try {
            if (colorNameField.getText().trim().isEmpty()) {
                throw new Exception("Vui lòng nhập tên màu");
            }
            if (colorCodeField.getText().trim().isEmpty()) {
                throw new Exception("Vui lòng nhập mã màu");
            }
            
            Connection conn = DBConnection.getConnection();
            String query;
            if (isNew) {
                query = "INSERT INTO colors (color_name, color_code) VALUES (?, ?)";
            } else {
                query = "UPDATE colors SET color_name = ?, color_code = ? WHERE id = ?";
            }
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, colorNameField.getText().trim());
            stmt.setString(2, colorCodeField.getText().trim());
            if (!isNew) {
                stmt.setInt(3, Integer.parseInt(colorIdField.getText()));
            }
            
            stmt.executeUpdate();
            loadColorData();
            clearColorForm();
            JOptionPane.showMessageDialog(this, "Lưu thành công!");
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }
    
    private void deleteColor() {
        if (colorIdField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn màu sắc cần xóa");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa màu sắc này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "DELETE FROM colors WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, Integer.parseInt(colorIdField.getText()));
                stmt.executeUpdate();
                
                loadColorData();
                clearColorForm();
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage());
            }
        }
    }
    
    private void clearColorForm() {
        colorIdField.setText("");
        colorNameField.setText("");
        colorCodeField.setText("");
        colorTable.clearSelection();
    }
    
    // Utility Methods
    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.putClientProperty("JTextField.placeholderText", placeholder);
        return field;
    }
    
    private JPanel createFormRow(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(5, 2));
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        return panel;
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
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            
            JFrame frame = new JFrame("Quản lý biến thể sản phẩm");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new Variants());
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 