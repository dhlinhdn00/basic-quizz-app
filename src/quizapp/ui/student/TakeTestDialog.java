package quizapp.ui.student;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import javax.swing.JDialog;
import database.AdminRepository;

public class TakeTestDialog extends JDialog {

    private JComboBox<String> testComboBox;
    private JButton startTestButton;
    private AdminRepository adminRepo;
    private String studentId;

    public TakeTestDialog(JFrame parent, AdminRepository arepo, String studentId) {
        super(parent, "Choose the test", true);
        this.adminRepo = arepo;
        this.studentId = studentId;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(300, 200);
        setLocationRelativeTo(getParent());

        testComboBox = new JComboBox<>();
        loadTestNames();

        startTestButton = new JButton("Start Test");
        startTestButton.addActionListener(e -> startTest());

        add(testComboBox, BorderLayout.CENTER);
        add(startTestButton, BorderLayout.PAGE_END);
    }

    private void loadTestNames() {
        List<Map<String, String>> tests = adminRepo.getAllTests();
        for (Map<String, String> test : tests) {
            testComboBox.addItem(test.get("test_name") + " (" + test.get("test_id") + ")");
        }
    }

    private void startTest() {
        String selectedTest = (String) testComboBox.getSelectedItem();
        if (selectedTest != null && !selectedTest.isEmpty()) {
            try {
                String testId = selectedTest.substring(selectedTest.lastIndexOf("(") + 1, selectedTest.lastIndexOf(")"));

                // Lấy JFrame chứa TakeTestDialog
                JFrame parentFrame = (JFrame) this.getOwner();

                TestWindow testWindow = new TestWindow(parentFrame, adminRepo, testId, studentId);
                testWindow.setVisible(true);
                this.dispose();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error starting the test. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No test selected. Please select a test.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

}
