package quizapp.ui.admin;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import services.AdminService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import database.AdminRepository;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ManageTest extends JFrame {
    private JTable tableTests;
    private JButton showButton, backButton, addButton, adjustButton, removeButton;
    private JScrollPane scrollPane;
    private final AdminService adminService;
    private JDialog testDetailDialog; 

    public ManageTest(AdminService adminService) {
        this.adminService = adminService;
        initComponents();
        loadTests();
    }

    private void initComponents() {
        setTitle("Manage Tests");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        showButton = new JButton("Show");
        addButton = new JButton("Add");
        adjustButton = new JButton("Adjust");
        removeButton = new JButton("Remove");
        backButton = new JButton("Back");

        showButton.setBounds(87, 680, 150, 40); 
        addButton.setBounds(257, 680, 150, 40);
        adjustButton.setBounds(427, 680, 150, 40); 
        removeButton.setBounds(597, 680, 150, 40);
        backButton.setBounds(767, 680, 150, 40); 

        showButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showTestDetails();
            }
        });
        
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addTests();
            }
        });
        
        adjustButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                adjustTests();
            }
        });
        
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeTests();
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                backToDashboard();
            }
        });

        tableTests = new JTable();
        tableTests.setFont(new Font("SansSerif", Font.PLAIN, 18));
        tableTests.setRowHeight(30);
        scrollPane = new JScrollPane(tableTests);
        scrollPane.setBounds(20, 20, 980, 650);

        add(showButton);
        add(addButton);
        add(adjustButton);
        add(removeButton);
        add(backButton);
        add(scrollPane);
    }

    private void loadTests() {
        // Lấy dữ liệu bài kiểm tra từ AdminService
        List<Map<String, String>> testsList = adminService.getAllTests();

        String[] columnNames = {"Test ID", "Test Name", "Number of Questions", "Test Time (Minutes)", "Show Results", "Show Answers"};

        Object[][] data = new Object[testsList.size()][columnNames.length];
        for (int i = 0; i < testsList.size(); i++) {
            Map<String, String> test = testsList.get(i);
            data[i][0] = test.get("test_id");
            data[i][1] = test.get("test_name");
            data[i][2] = test.get("number_of_questions");
            data[i][3] = test.get("test_time");
            data[i][4] = Boolean.parseBoolean(test.get("showresults"));
            data[i][5] = Boolean.parseBoolean(test.get("showanswers"));

        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 4:
                    case 5:
                        return Boolean.class; 
                    default:
                        return String.class;
                }
            }
        };

        tableTests.setModel(model);

        // Điều chỉnh độ rộng cột
        TableColumnModel columnModel = tableTests.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100); // Test ID
        columnModel.getColumn(1).setPreferredWidth(300); // Test Name
        columnModel.getColumn(2).setPreferredWidth(150); // Number of Questions
        columnModel.getColumn(3).setPreferredWidth(100); // Test Time
        columnModel.getColumn(4).setPreferredWidth(100); // Show Results
        columnModel.getColumn(5).setPreferredWidth(100); // Show Answers
    }


    
    private HashMap<String, String> questionIdMap = new HashMap<>();
    
    private void showTestDetails() {
        int selectedRow = tableTests.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a test first.");
            return;
        }

        String testId = tableTests.getValueAt(selectedRow, 0).toString();
        List<Map<String, String>> testDetails = adminService.getTestDetails(testId);

        // Tạo và hiển thị JDialog
        testDetailDialog = new JDialog(this, "Test Details", true);
        testDetailDialog.setLayout(new BorderLayout());
        testDetailDialog.setSize(800, 600);

        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        int questionNumber = 1;
        
        for (Map<String, String> question : testDetails) {
            String questionId = question.get("question_id");
            String questionText = questionNumber + "." + question.get("question_text");
            String optionA = "A: " + question.get("option_a");
            String optionB = "B: " + question.get("option_b");
            String optionC = "C: " + question.get("option_c");
            String optionD = "D: " + question.get("option_d");
            String correctOption = question.get("correct_option");

            // Tạo JLabel cho câu hỏi và đặt màu đỏ
            JLabel questionLabel = new JLabel("<html><span style='color:red;'>" + questionText + "</span></html>");
            questionLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

            // Tạo JLabels cho các lựa chọn
            JLabel optionALabel = new JLabel(optionA);
            JLabel optionBLabel = new JLabel(optionB);
            JLabel optionCLabel = new JLabel(optionC);
            JLabel optionDLabel = new JLabel(optionD);

            // Đánh dấu đáp án đúng bằng cách in đậm
            if ("a".equals(correctOption)) optionALabel.setText("<html><b>" + optionA + "</b></html>");
            if ("b".equals(correctOption)) optionBLabel.setText("<html><b>" + optionB + "</b></html>");
            if ("c".equals(correctOption)) optionCLabel.setText("<html><b>" + optionC + "</b></html>");
            if ("d".equals(correctOption)) optionDLabel.setText("<html><b>" + optionD + "</b></html>");

            // Thiết lập font cho các lựa chọn (chỉ làm nếu cần thay đổi font chung)
            optionALabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            optionBLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            optionCLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            optionDLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            
            // Lưu trữ ánh xạ từ nội dung câu hỏi đến question_id
            questionIdMap.put(questionText, questionId);
        
            // Thêm vào panel
            questionPanel.add(questionLabel);
            questionPanel.add(optionALabel);
            questionPanel.add(optionBLabel);
            questionPanel.add(optionCLabel);
            questionPanel.add(optionDLabel);

            questionNumber++;
        }

        JScrollPane scrollPane = new JScrollPane(questionPanel);
        testDetailDialog.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Question");
        JButton deleteButton = new JButton("Delete Question");
        JButton editButton = new JButton("Edit Question");

        addButton.addActionListener(e -> addQuestion(testId));
        deleteButton.addActionListener(e -> deleteQuestion(testId));
        editButton.addActionListener(e -> editQuestion(testId));

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);

        testDetailDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        testDetailDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                loadTests(); // Tải lại dữ liệu cho bảng bài kiểm tra
            }
        });
        
        testDetailDialog.setVisible(true);
    }
    
    private void addQuestion(String testId) {
        JDialog addDialog = new JDialog(this, "Add New Question", true);
        addDialog.setLayout(new FlowLayout());
        addDialog.setSize(400, 300);

        // Các trường nhập liệu
        JTextField questionField = new JTextField(30);
        JTextField optionAField = new JTextField(30);
        JTextField optionBField = new JTextField(30);
        JTextField optionCField = new JTextField(30);
        JTextField optionDField = new JTextField(30);
        JComboBox<String> correctOptionComboBox = new JComboBox<>(new String[]{"a", "b", "c", "d"});

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            String questionText = questionField.getText();
            String optionA = optionAField.getText();
            String optionB = optionBField.getText();
            String optionC = optionCField.getText();
            String optionD = optionDField.getText();
            String correctOption = (String) correctOptionComboBox.getSelectedItem();

            // Gọi adminService để thêm câu hỏi
            boolean success = adminService.addQuestion(testId, questionText, optionA, optionB, optionC, optionD, correctOption);
            if (success) {
                JOptionPane.showMessageDialog(addDialog, "Question added successfully.");
                addDialog.dispose();
                closeTestDetailsDialog();
                showTestDetails(); // Tải lại thông tin chi tiết của bài kiểm tra
            } else {
                JOptionPane.showMessageDialog(addDialog, "Failed to add question.");
            }
        });

        // Thêm các trường và nút vào Dialog
        addDialog.add(new JLabel("Question:"));
        addDialog.add(questionField);
        addDialog.add(new JLabel("Option A:"));
        addDialog.add(optionAField);
        addDialog.add(new JLabel("Option B:"));
        addDialog.add(optionBField);
        addDialog.add(new JLabel("Option C:"));
        addDialog.add(optionCField);
        addDialog.add(new JLabel("Option D:"));
        addDialog.add(optionDField);
        addDialog.add(new JLabel("Correct Option:"));
        addDialog.add(correctOptionComboBox);
        addDialog.add(submitButton);

        addDialog.setVisible(true);
    }
    
    private void closeTestDetailsDialog() {
        if (testDetailDialog != null) {
            testDetailDialog.dispose(); // Đóng cửa sổ nếu nó đang mở
        }
    }
    Comparator<String> questionComparator = (q1, q2) -> {
        int num1 = Integer.parseInt(q1.split("\\.")[0]);
        int num2 = Integer.parseInt(q2.split("\\.")[0]);
        return Integer.compare(num1, num2);
    };

    private void deleteQuestion(String testId) {
        JDialog deleteDialog = new JDialog(this, "Delete Question", true);
        deleteDialog.setLayout(new FlowLayout());
        deleteDialog.setSize(500, 150);

        List<String> sortedQuestions = new ArrayList<>(questionIdMap.keySet());
        sortedQuestions.sort(questionComparator);

        JComboBox<String> questionComboBox = new JComboBox<>(sortedQuestions.toArray(new String[0]));
        JButton submitButton = new JButton("Delete");
        submitButton.addActionListener(e -> {
            String selectedQuestionText = (String) questionComboBox.getSelectedItem();
            String questionId = questionIdMap.get(selectedQuestionText);
            boolean success = adminService.deleteQuestion(Integer.parseInt(questionId));
            if (success) {
                JOptionPane.showMessageDialog(deleteDialog, "Question deleted successfully.");
                deleteDialog.dispose();
                closeTestDetailsDialog();
                showTestDetails(); 
            } else {
                JOptionPane.showMessageDialog(deleteDialog, "Failed to delete question.");
            }
        });

        deleteDialog.add(new JLabel("Select Question:"));
        deleteDialog.add(questionComboBox);
        deleteDialog.add(submitButton);

        deleteDialog.setVisible(true);
    }



    private void editQuestion(String testId) {
        // Cửa sổ chọn câu hỏi để chỉnh sửa
        JDialog selectDialog = new JDialog(this, "Select Question to Edit", true);
        selectDialog.setLayout(new FlowLayout());
        selectDialog.setSize(500, 150);

       List<String> sortedQuestions = new ArrayList<>(questionIdMap.keySet());
        sortedQuestions.sort(questionComparator);

        JComboBox<String> questionComboBox = new JComboBox<>(sortedQuestions.toArray(new String[0]));
        JButton selectButton = new JButton("Select");

        selectButton.addActionListener(e -> {
            String selectedQuestionText = (String) questionComboBox.getSelectedItem();
            String questionId = questionIdMap.get(selectedQuestionText);
            selectDialog.dispose();
            openEditQuestionDialog(questionId, testId);
        });

        selectDialog.add(new JLabel("Select Question:"));
        selectDialog.add(questionComboBox);
        selectDialog.add(selectButton);

        selectDialog.setVisible(true);
    }

    private void openEditQuestionDialog(String questionId, String testId) {
        // Lấy thông tin chi tiết của câu hỏi
        Map<String, String> questionDetails = adminService.getQuestionDetails(Integer.parseInt(questionId));

        JDialog editDialog = new JDialog(this, "Edit Question", true);
        editDialog.setLayout(new FlowLayout());
        editDialog.setSize(400, 300);

        // Tạo các trường nhập liệu và điền thông tin hiện tại của câu hỏi
        JTextField questionField = new JTextField(questionDetails.get("question_text"), 30);
        JTextField optionAField = new JTextField(questionDetails.get("option_a"), 30);
        JTextField optionBField = new JTextField(questionDetails.get("option_b"), 30);
        JTextField optionCField = new JTextField(questionDetails.get("option_c"), 30);
        JTextField optionDField = new JTextField(questionDetails.get("option_d"), 30);

        // Tạo và điền đáp án đúng vào JComboBox
        JComboBox<String> correctOptionComboBox = new JComboBox<>(new String[]{"a", "b", "c", "d"});
        correctOptionComboBox.setSelectedItem(questionDetails.get("correct_option"));

        JButton submitButton = new JButton("Update");
        submitButton.addActionListener(e -> {
            String questionText = questionField.getText();
            String optionA = optionAField.getText();
            String optionB = optionBField.getText();
            String optionC = optionCField.getText();
            String optionD = optionDField.getText();
            String correctOption = (String) correctOptionComboBox.getSelectedItem();

            boolean success = adminService.editQuestion(Integer.parseInt(questionId), questionText, optionA, optionB, optionC, optionD, correctOption);
            if (success) {
                JOptionPane.showMessageDialog(editDialog, "Question updated successfully.");
                editDialog.dispose();
                closeTestDetailsDialog();
                showTestDetails(); // Tải lại thông tin chi tiết của bài kiểm tra
            } else {
                JOptionPane.showMessageDialog(editDialog, "Failed to update question.");
            }
        });

        // Thêm các thành phần vào dialog
        editDialog.add(new JLabel("Question:"));
        editDialog.add(questionField);
        editDialog.add(new JLabel("Option A:"));
        editDialog.add(optionAField);
        editDialog.add(new JLabel("Option B:"));
        editDialog.add(optionBField);
        editDialog.add(new JLabel("Option C:"));
        editDialog.add(optionCField);
        editDialog.add(new JLabel("Option D:"));
        editDialog.add(optionDField);
        editDialog.add(new JLabel("Correct Option:"));
        editDialog.add(correctOptionComboBox);
        editDialog.add(submitButton);

        editDialog.setVisible(true);
    }

    
    
    private void addTests() {
        JDialog addDialog = new JDialog(this, "Add New Test", true);
        addDialog.setLayout(new BorderLayout(10, 10)); // Use BorderLayout with gaps

        // Input panel with GridLayout
        JPanel inputPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input fields
        JTextField testNameField = new JTextField(20);
        JTextField testTimeField = new JTextField(20);

        // Checkboxes
        JCheckBox showResultsCheckbox = new JCheckBox("Show Results");
        JCheckBox showAnswersCheckbox = new JCheckBox("Show Answers");

        // Adding components to the input panel
        inputPanel.add(new JLabel("Test Name:"));
        inputPanel.add(testNameField);
        inputPanel.add(new JLabel("Test Time (minutes):"));
        inputPanel.add(testTimeField);

        // Submit button
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            // Get the input data
            String testName = testNameField.getText();
            String testTime = testTimeField.getText();
            boolean showResults = showResultsCheckbox.isSelected();
            boolean showAnswers = showAnswersCheckbox.isSelected();

            // Validate and add the test
            if (!testName.isEmpty() && !testTime.isEmpty()) {
                try {
                    int time = Integer.parseInt(testTime);
                    // Call the adminService to add the test
                    boolean success = adminService.addTest(testName, time, showResults, showAnswers);
                    if (success) {
                        JOptionPane.showMessageDialog(addDialog, "Test added successfully.");
                        addDialog.dispose();
                        loadTests(); // Refresh the list of tests
                    } else {
                        JOptionPane.showMessageDialog(addDialog, "Failed to add test.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addDialog, "Please enter a valid number for test time.");
                }
            } else {
                JOptionPane.showMessageDialog(addDialog, "Please fill all the fields.");
            }
        });

        // Bottom panel for submit button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(submitButton);

        // Add components to main dialog
        addDialog.add(inputPanel, BorderLayout.NORTH);
        addDialog.add(showResultsCheckbox, BorderLayout.WEST);
        addDialog.add(showAnswersCheckbox, BorderLayout.CENTER);
        addDialog.add(bottomPanel, BorderLayout.SOUTH);

        // Finalize dialog
        addDialog.pack();
        addDialog.setLocationRelativeTo(null);
        addDialog.setVisible(true);
    }



    private void adjustTests() {
        int selectedRow = tableTests.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a test first.");
            return;
        }

        String testId = tableTests.getValueAt(selectedRow, 0).toString();
        String testName = tableTests.getValueAt(selectedRow, 1).toString();
        String testTime = tableTests.getValueAt(selectedRow, 3).toString();
        Boolean showResults = (Boolean) tableTests.getValueAt(selectedRow, 4);
        Boolean showAnswers = (Boolean) tableTests.getValueAt(selectedRow, 5);

        JDialog adjustDialog = new JDialog(this, "Adjust Test", true);
        adjustDialog.setLayout(new BorderLayout(10, 10)); // Use BorderLayout for main dialog layout
        adjustDialog.setSize(400, 300);

        // Panel for the input fields
        JPanel inputPanel = new JPanel(new GridLayout(0, 1, 5, 5)); // Use GridLayout to align vertically
        JTextField testNameField = new JTextField(testName, 30);
        JTextField testTimeField = new JTextField(testTime, 30); // Make sure to set the same width as testNameField
        JCheckBox showResultsCheckbox = new JCheckBox("Show Results", showResults);
        JCheckBox showAnswersCheckbox = new JCheckBox("Show Answers", showAnswers);

        // Adding components to the input panel
        inputPanel.add(new JLabel("Test Name:"));
        inputPanel.add(testNameField);
        inputPanel.add(new JLabel("Test Time (Minutes):"));
        inputPanel.add(testTimeField);
        inputPanel.add(showResultsCheckbox);
        inputPanel.add(showAnswersCheckbox);

        // Panel for the submit button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Use FlowLayout to center the button
        JButton submitButton = new JButton("Update");
        buttonPanel.add(submitButton); // Add the submit button to the button panel

        // Add the input panel and button panel to the dialog
        adjustDialog.add(inputPanel, BorderLayout.CENTER);
        adjustDialog.add(buttonPanel, BorderLayout.PAGE_END); // Place the button panel at the bottom
        submitButton.addActionListener(e -> {
            String newTestName = testNameField.getText();
            String newTestTime = testTimeField.getText();
            boolean newShowResults = showResultsCheckbox.isSelected();
            boolean newShowAnswers = showAnswersCheckbox.isSelected();

            // Validate input before updating
            if (newTestName.isEmpty() || newTestTime.isEmpty()) {
                JOptionPane.showMessageDialog(adjustDialog, "Please fill in all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int time = Integer.parseInt(newTestTime); // Convert test time to integer
                // Call the method in the admin service to update the test details
                boolean success = adminService.updateTest(testId, newTestName, newTestTime, newShowResults, newShowAnswers);
                if (success) {
                    JOptionPane.showMessageDialog(adjustDialog, "Test updated successfully.");
                    adjustDialog.dispose();
                    loadTests(); // Refresh the list of tests
                } else {
                    JOptionPane.showMessageDialog(adjustDialog, "Failed to update test.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(adjustDialog, "Please enter a valid number for test time.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        adjustDialog.setVisible(true);
    }




    private void removeTests() {
        int selectedRow = tableTests.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a test first.");
            return;
        }

        String testId = tableTests.getValueAt(selectedRow, 0).toString(); // Đảm bảo testId là một String

        // Tiếp tục với việc xác nhận và xóa
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this test?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = adminService.deleteTest(testId); // Sử dụng testId như một String
            if (success) {
                JOptionPane.showMessageDialog(this, "Test deleted successfully.");
                loadTests(); // Reload the test list
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete test.");
            }
        }
    }



    private void backToDashboard() {
        AdminDashboard adminDashboard = new AdminDashboard();
        adminDashboard.setVisible(true);
        this.dispose(); // Đóng cửa sổ hiện tại
    }
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AdminService adminService = new AdminRepository();
                new ManageTest(adminService).setVisible(true);
            }
        });
    }
}
