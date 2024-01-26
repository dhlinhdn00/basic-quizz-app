package quizapp.ui;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import quizapp.ui.admin.AdminDashboard;
import quizapp.ui.student.StudentDashboard;
import database.AdminRepository;
import database.StudentRepository;
import services.AdminService;
import services.StudentService;
/**
 *
 * @author RyOS
 */
public class Login extends javax.swing.JFrame {

    private final StudentService studentService;
    private final AdminService adminService;

    public Login() {
        initComponents();
        this.studentService = new StudentRepository();
        this.adminService = new AdminRepository();
        this.setTitle("QuizApp Login");
    }

    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        JPanel loginPanel = new JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        password = new javax.swing.JPasswordField();
        loginButton = new javax.swing.JButton();
        loginButton1 = new javax.swing.JButton();
        background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1024, 768));
        pack();

        jPanel1.setPreferredSize(new java.awt.Dimension(1024, 768));
        jPanel1.setLayout(null);

        // Setting up the white login panel
        loginPanel.setLayout(null);
        loginPanel.setBounds(312, 230, 400, 270); // Centered on background
        loginPanel.setBackground(new java.awt.Color(255, 255, 255)); // White background

        // Username label
        jLabel1.setText("Username:");
        jLabel1.setBounds(50, 50, 100, 30);
        loginPanel.add(jLabel1);

        // Password label
        jLabel2.setText("Password:");
        jLabel2.setBounds(50, 130, 100, 30);
        loginPanel.add(jLabel2);

        // Login label (title)
        jLabel3.setText("Login Form");
        jLabel3.setFont(new java.awt.Font("Arial", 1, 24)); // Larger font for the title
        jLabel3.setBounds(150, 10, 300, 40);
        loginPanel.add(jLabel3);

        // Username text field
        username.setBounds(50, 80, 300, 30);
        loginPanel.add(username);

        // Password field
        password.setBounds(50, 160, 300, 30);
        loginPanel.add(password);

        // Login button
        loginButton.setText("Login");
        loginButton.setBounds(50, 220, 100, 30);
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButton1ActionPerformed(evt);
            }
        });
        loginPanel.add(loginButton);

        // Exit button
        loginButton1.setText("Exit");
        loginButton1.setBounds(250, 220, 100, 30);
        loginButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });
        loginPanel.add(loginButton1);

        // Add login panel to the main panel
        jPanel1.add(loginPanel);

        // Background label setup
        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/quizapp/ui/login-page.jpg"))); // replace with your image path
        background.setBounds(0, 0, 1024, 768);
        jPanel1.add(background);

        getContentPane().add(jPanel1);
        validate();
    }

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0);
    }

    private void loginButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        String studentId = username.getText();
        String studentPassword = String.valueOf(password.getPassword());

        boolean studentLoginAttempt = studentService.getLogin(studentId, studentPassword);
        boolean adminLoginAttempt = adminService.getLogin(studentId, studentPassword);

        if (studentLoginAttempt) {
            this.dispose();
            new StudentDashboard(studentId).setVisible(true);
        } else if (adminLoginAttempt) {
            this.dispose();
            new AdminDashboard().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Login failed!");
        }
    }


    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration
    private javax.swing.JLabel background;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton loginButton;
    private javax.swing.JButton loginButton1;
    private javax.swing.JPasswordField password;
    private javax.swing.JTextField username;
    // End of variables declaration
}
