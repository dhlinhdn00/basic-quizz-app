package quizapp.ui.admin;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import services.AdminService;
import java.util.List;
import java.util.Map;
import database.AdminRepository;

public class ManageStudent extends JFrame {
    private JTable tableStudents;
    private JButton addButton, adjustButton, removeButton, backButton;
    private JScrollPane scrollPane;
    private final AdminService adminService;

    public ManageStudent(AdminService adminService) {
        this.adminService = adminService;
        initComponents();
        loadStudents();
    }

    private void initComponents() {
        setTitle("Manage Students");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        addButton = new JButton("Add");
        adjustButton = new JButton("Adjust");
        removeButton = new JButton("Remove");
        backButton = new JButton("Back");

        // Set bounds for buttons
        addButton.setBounds(172, 680, 150, 40);
        adjustButton.setBounds(342, 680, 150, 40); 
        removeButton.setBounds(512, 680, 150, 40); 
        backButton.setBounds(682, 680, 150, 40); 

        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });

        adjustButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                adjustStudent();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeStudent();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                backToDashboard();
            }
        });

        // Initialize JTable and JScrollPane
        tableStudents = new JTable();
        tableStudents.setFont(new Font("SansSerif", Font.PLAIN, 18));
        tableStudents.setRowHeight(30);
        scrollPane = new JScrollPane(tableStudents);
        scrollPane.setBounds(20, 20, 980, 650);

        // Add components to JFrame
        add(addButton);
        add(adjustButton);
        add(removeButton);
        add(backButton);
        add(scrollPane);
    }

    private void loadStudents() {
        // Lấy dữ liệu sinh viên từ AdminService
        List<Map<String, String>> studentsList = adminService.getAllStudents();

        String[] columnNames = {"Student ID", "Full Name", "Gender", "Class", "Phone Number", "Password"};

        // Tạo một mảng đối tượng để đựng dữ liệu của bảng
        Object[][] data = new Object[studentsList.size()][columnNames.length];

        // Chuyển dữ liệu từ List sang mảng đối tượng
        int i = 0;
        for (Map<String, String> student : studentsList) {
            data[i][0] = student.get("student_id");
            data[i][1] = student.get("full_name");
            data[i][2] = student.get("gender");
            data[i][3] = student.get("class");
            data[i][4] = student.get("phone_number");
            data[i][5] = student.get("password");
            i++;
        }

        // Tạo một mô hình bảng mới và đặt nó cho JTable
        DefaultTableModel model = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableStudents.setModel(model);
    }

    private void addStudent() {
        // Tạo JDialog để nhập thông tin sinh viên mới
        JDialog addDialog = new JDialog(this, "Add Student", true);
        addDialog.setLayout(new FlowLayout());
        addDialog.setSize(300, 400);

        // Các trường nhập liệu
        JTextField studentIdField = new JTextField(25);
        JTextField fullNameField = new JTextField(25);
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderComboBox.setPreferredSize(new Dimension(fullNameField.getPreferredSize().width, fullNameField.getPreferredSize().height));
        // Tạo ListCellRenderer tùy chỉnh để căn giữa văn bản trong combobox
        DefaultListCellRenderer centerRenderer = new DefaultListCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER); // Căn giữa chữ
        genderComboBox.setRenderer(centerRenderer);
        JTextField classField = new JTextField(25);
        JTextField phoneNumberField = new JTextField(25);
        JPasswordField passwordField = new JPasswordField(25);

        // Nút để thêm sinh viên
        JButton addButtonpopup = new JButton("Add");
        addButtonpopup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String studentId = studentIdField.getText();
                String fullName = fullNameField.getText();
                String gender = (String) genderComboBox.getSelectedItem();
                String classname = classField.getText();
                String phoneNumber = phoneNumberField.getText();
                String password = new String(passwordField.getPassword());
                
            if (studentId.trim().isEmpty() || fullName.trim().isEmpty() || gender == null || classname.trim().isEmpty() || phoneNumber.trim().isEmpty() || password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(addDialog, "All fields must be filled out.");
                return;
            }
            if (studentId.length() < 10 || phoneNumber.length() < 10) {
                JOptionPane.showMessageDialog(addDialog, "Student ID and Phone Number must be 10 digits.");
                return;
            }

            // Kiểm tra độ dài của password
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(addDialog, "Password must be at least 6 characters.");
                return;
            }

                // Gọi AdminService để thêm sinh viên
                boolean isAdded = adminService.createStudent(studentId, fullName, gender, classname, phoneNumber, password);
                if (isAdded) {
                    JOptionPane.showMessageDialog(addDialog, "Student added successfully!");
                    loadStudents(); // Tải lại danh sách sinh viên
                } else {
                    JOptionPane.showMessageDialog(addDialog, "Failed to add student.");
                }
                addDialog.dispose();
            }
        });

        // Thêm các trường vào dialog
        addDialog.add(new JLabel("Student ID:"));
        addDialog.add(studentIdField);
        addDialog.add(new JLabel("Full Name:"));
        addDialog.add(fullNameField);
        addDialog.add(new JLabel("Gender:"));
        addDialog.add(genderComboBox);
        addDialog.add(new JLabel("Class:"));
        addDialog.add(classField);
        addDialog.add(new JLabel("Phone Number:"));
        addDialog.add(phoneNumberField);
        addDialog.add(new JLabel("Password:"));
        addDialog.add(passwordField);
        addDialog.add(addButtonpopup);

        addDialog.setVisible(true);
    }


    private void adjustStudent() {
        int selectedRow = tableStudents.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student first.");
            return;
        }

        // Lấy thông tin sinh viên từ bảng
        String studentId = tableStudents.getValueAt(selectedRow, 0).toString();
        String fullName = tableStudents.getValueAt(selectedRow, 1).toString();
        String gender = tableStudents.getValueAt(selectedRow, 2).toString();
        String classname = tableStudents.getValueAt(selectedRow, 3).toString();
        String phoneNumber = tableStudents.getValueAt(selectedRow, 4).toString();
        String password = tableStudents.getValueAt(selectedRow, 5).toString();

        // Tạo và cấu hình JDialog
        JDialog adjustDialog = new JDialog(this, "Adjust Student", true);
        adjustDialog.setLayout(new FlowLayout());
        adjustDialog.setSize(300, 400);

        // Tạo các trường nhập liệu và đặt giá trị ban đầu
        JTextField fullNameField = new JTextField(fullName, 25);
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderComboBox.setSelectedItem(gender);
        genderComboBox.setPreferredSize(new Dimension(fullNameField.getPreferredSize().width, fullNameField.getPreferredSize().height));
        // Tạo ListCellRenderer tùy chỉnh để căn giữa văn bản trong combobox
        DefaultListCellRenderer centerRenderer = new DefaultListCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER); // Căn giữa chữ
        genderComboBox.setRenderer(centerRenderer);
        JTextField classField = new JTextField(classname, 25);
        JTextField phoneNumberField = new JTextField(phoneNumber, 25);
        JTextField passwordField = new JTextField(password, 25);

        // Nút điều chỉnh
        JButton adjustButtonpopup = new JButton("Adjust");
        adjustButtonpopup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Lấy giá trị cập nhật từ các trường nhập liệu
                String newFullName = fullNameField.getText();
                String newGender = (String) genderComboBox.getSelectedItem();
                String newClass = classField.getText();
                String newPhoneNumber = phoneNumberField.getText();
                String newPassword = passwordField.getText();

                // Gọi AdminService để cập nhật sinh viên
                boolean isUpdated = adminService.updateStudent(studentId, newFullName, newGender, newClass, newPhoneNumber, newPassword);
                if (isUpdated) {
                    JOptionPane.showMessageDialog(adjustDialog, "Student updated successfully!");
                    loadStudents();
                } else {
                    JOptionPane.showMessageDialog(adjustDialog, "Failed to update student.");
                }
                adjustDialog.dispose();
            }
        });

        // Thêm các thành phần vào JDialog
        adjustDialog.add(new JLabel("Full Name:"));
        adjustDialog.add(fullNameField);
        adjustDialog.add(new JLabel("Gender:"));
        adjustDialog.add(genderComboBox);
        adjustDialog.add(new JLabel("Class:"));
        adjustDialog.add(classField);
        adjustDialog.add(new JLabel("Phone Number:"));
        adjustDialog.add(phoneNumberField);
        adjustDialog.add(new JLabel("Password:"));
        adjustDialog.add(passwordField);
        adjustDialog.add(adjustButtonpopup);

        adjustDialog.setVisible(true);
    }



    private void removeStudent() {
        int selectedRow = tableStudents.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String studentId = tableStudents.getValueAt(selectedRow, 0).toString();
            boolean isDeleted = adminService.deleteStudent(studentId);
            if (isDeleted) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully!");
                loadStudents(); // Reload student list
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete student.");
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
                new ManageStudent(adminService).setVisible(true);
            }
        });
    }
}
