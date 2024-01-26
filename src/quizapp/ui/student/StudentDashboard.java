package quizapp.ui.student;

import javax.swing.*;
import java.awt.event.ActionEvent;
import quizapp.ui.Login;
import database.StudentRepository;
import database.AdminRepository;

public class StudentDashboard extends JFrame {

    private String studentId;
    private JButton takeTestButton, logoutButton;
    private JLabel background;
    private JLabel titleLabel;
    private JPanel mainPanel;

    public StudentDashboard(String studentId) {
        this.studentId = studentId;
        initComponents();
        this.setTitle("Student Dashboard - " + studentId);
    }

    private void initComponents() {
        mainPanel = new JPanel();
        titleLabel = new JLabel();
        takeTestButton = new JButton("Take the Test");
        logoutButton = new JButton("Logout");
        background = new JLabel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1024, 768));
        getContentPane().setLayout(null);

        mainPanel.setLayout(null);
        mainPanel.setBounds(0, 0, 1024, 768);
        getContentPane().add(mainPanel);

        titleLabel.setFont(new java.awt.Font("sansserif", 1, 22));
        titleLabel.setForeground(java.awt.Color.BLACK);
        titleLabel.setText("Hello, " + studentId + "!");
        titleLabel.setBounds(412, 100, 200, 40);
        mainPanel.add(titleLabel);

        configureButton(takeTestButton, 432, 175);
        configureButton(logoutButton, 432, 375);
        takeTestButton.addActionListener(this::takeTestActionPerformed);
        logoutButton.addActionListener(e -> backToLogin());

        background.setIcon(new ImageIcon(getClass().getResource("/quizapp/ui/student/studentDashboard-page.jpg"))); // Replace with your image path
        background.setBounds(0, 0, 1024, 768);
        mainPanel.add(background);

        pack();
    }

    private void configureButton(JButton button, int x, int y) {
        button.setBounds(x, y, 160, 50);
        mainPanel.add(button);
    }

    private void backToLogin() {
        this.dispose();
        new Login().setVisible(true);
    }

    private void takeTestActionPerformed(ActionEvent evt) {
        TakeTestDialog takeTestDialog = new TakeTestDialog(this, new AdminRepository(), studentId);
        takeTestDialog.setVisible(true);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}
