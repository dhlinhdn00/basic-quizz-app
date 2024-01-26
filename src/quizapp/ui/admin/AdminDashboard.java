package quizapp.ui.admin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import database.AdminRepository;
import services.AdminService;
import quizapp.ui.Login;

public class AdminDashboard extends JFrame {

    private JButton manageStudentButton;
    private JButton manageTestButton;
    private JButton viewResultButton;
    private JButton backButton;
    private JLabel background;
    private JLabel titleLabel;
    private JPanel mainPanel;

    public AdminDashboard() {
        initComponents();
        this.setTitle("Admin Dashboard");
    }

    private void initComponents() {
        mainPanel = new JPanel();
        titleLabel = new JLabel();
        manageStudentButton = new JButton();
        manageTestButton = new JButton();
        viewResultButton = new JButton();
        backButton = new JButton();
        background = new JLabel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1024, 768));
        getContentPane().setLayout(null);

        mainPanel.setLayout(null);
        mainPanel.setBounds(0, 0, 1024, 768);
        getContentPane().add(mainPanel);

        // Title label
        titleLabel.setFont(new java.awt.Font("sansserif", 1, 22));
        titleLabel.setForeground(java.awt.Color.BLACK);
        titleLabel.setText("Admin Dashboard");
        titleLabel.setBounds(412, 100, 200, 40);
        mainPanel.add(titleLabel);

        // Configure buttons
        configureButton(manageStudentButton, "Manage Student", 432, 175);
        manageStudentButton.addActionListener(this::manageStudentActionPerformed);

        configureButton(manageTestButton, "Manage Test", 432, 275);
        manageTestButton.addActionListener(this::manageTestActionPerformed);

        configureButton(viewResultButton, "View Result", 432, 375);
        viewResultButton.addActionListener(this::viewResultActionPerformed);

        configureButton(backButton, "Back", 432, 475);
        backButton.addActionListener(e -> backToLogin());

        // Background
        background.setIcon(new ImageIcon(getClass().getResource("/quizapp/ui/admin/adminDashboard-page.jpg"))); // Replace with your image path
        background.setBounds(0, 0, 1024, 768);
        mainPanel.add(background);

        pack();
    }

    private void configureButton(JButton button, String text, int x, int y) {
        button.setText(text);
        button.setBounds(x, y, 160, 50);
        mainPanel.add(button);
    }

    private void manageStudentActionPerformed(ActionEvent evt) {
        AdminService adminService = new AdminRepository();
        ManageStudent manageStudentWindow = new ManageStudent(adminService);
        manageStudentWindow.setVisible(true);
        this.dispose(); // Đóng cửa sổ hiện tại
    }


    private void manageTestActionPerformed(ActionEvent evt) {
        AdminService adminService = new AdminRepository();
        ManageTest manageTestWindow = new ManageTest(adminService);
        manageTestWindow.setVisible(true);
        this.dispose(); // Đóng cửa sổ hiện tại
    }

    private void viewResultActionPerformed(ActionEvent evt) {
        AdminService adminService = new AdminRepository();
        ViewResult ViewResultWindow = new ViewResult(adminService);
        ViewResultWindow.setVisible(true);
        this.dispose(); // Đóng cửa sổ hiện tại
    }
    
    private void backToLogin() {
        this.dispose();

        Login loginWindow = new Login();
        loginWindow.setVisible(true);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new AdminDashboard().setVisible(true));
    }
}
