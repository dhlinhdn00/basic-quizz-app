package quizapp.ui.admin;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import services.AdminService;
import java.util.List;
import java.util.Map;
import database.AdminRepository;
import services.AdminService;
import java.sql.Timestamp;
import java.util.Date;


public class ViewResult extends JFrame {
    private JTable tableResults;
    private JButton backButton;
    private JScrollPane scrollPane;
    private final AdminService adminService;

    public ViewResult(AdminService adminService) {
        this.adminService = adminService;
        initComponents();
        loadResults();
    }

    private void initComponents() {
        setTitle("View Results");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                backToDashboard();
            }
        });

        tableResults = new JTable();
        tableResults.setFont(new Font("SansSerif", Font.PLAIN, 18));
        tableResults.setRowHeight(30);
        scrollPane = new JScrollPane(tableResults);

        add(scrollPane, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
    }

    private void loadResults() {
        List<Map<String, String>> resultsList = adminService.getAllTestResults();

        String[] columnNames = {"Result ID", "Student ID", "Test ID", "Attempt Date", "Score"};

        Object[][] data = new Object[resultsList.size()][columnNames.length];
        for (int i = 0; i < resultsList.size(); i++) {
            Map<String, String> result = resultsList.get(i);
            data[i][0] = result.get("result_id");
            data[i][1] = result.get("student_id");
            data[i][2] = result.get("test_id");
            data[i][3] = Timestamp.valueOf(result.get("attempt_date")); // Ensure this is a Timestamp
            data[i][4] = result.get("score");
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 3: // Assuming 'Attempt Date' is the 4th column (index 3)
                        return Date.class; // Or Timestamp.class
                    default:
                        return String.class;
                }
            }
        };

        tableResults.setModel(model);
    }


    private void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 50; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 1, width);
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    private void backToDashboard() {
        AdminDashboard adminDashboard = new AdminDashboard();
        adminDashboard.setVisible(true);
        this.dispose(); // Close current window
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AdminService adminService = new AdminRepository();
                new ViewResult(adminService).setVisible(true);
            }
        });
    }
}

            