package quizapp.ui.student;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import database.AdminRepository;

public class TestWindow extends JFrame {
    private String testId;
    private String studentId;
    private AdminRepository adminRepo;
    private JPanel questionsPanel; // Panel chứa các câu hỏi
    private List<String> correctAnswers; // Lưu các đáp án đúng
    private Timer testTimer; // Timer cho thời gian làm bài
    private JLabel timerLabel; // Hiển thị thời gian còn lại
    private int remainingTime; // Thời gian còn lại (tính bằng giây)

    public TestWindow(JFrame parent, AdminRepository arepo, String testId, String studentId) {
        super("Take the Test");
        this.adminRepo = arepo;
        this.testId = testId;
        this.studentId = studentId;
        this.questionsPanel = new JPanel();
        this.correctAnswers = new ArrayList<>();
        this.timerLabel = new JLabel("Time left: ", JLabel.CENTER);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(null);

        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));

        List<Map<String, String>> questions = adminRepo.getTestDetails(testId);
        for (Map<String, String> question : questions) {
            JPanel questionPanel = new JPanel();
            questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));

            JLabel questionLabel = new JLabel(question.get("question_text"));
            questionPanel.add(questionLabel);

            ButtonGroup optionsGroup = new ButtonGroup();
            addOption(questionPanel, optionsGroup, question.get("option_a"), "a");
            addOption(questionPanel, optionsGroup, question.get("option_b"), "b");
            addOption(questionPanel, optionsGroup, question.get("option_c"), "c");
            addOption(questionPanel, optionsGroup, question.get("option_d"), "d");

            correctAnswers.add(question.get("correct_option"));
            questionsPanel.add(questionPanel);
        }

        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        add(scrollPane, BorderLayout.CENTER);

        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(timerLabel, BorderLayout.NORTH);

        JButton submitButton = new JButton("Submit Test");
        submitButton.addActionListener(e -> evaluateTest());
        add(submitButton, BorderLayout.PAGE_END);

        setupTimer();
        validate();
    }

    private void addOption(JPanel questionPanel, ButtonGroup group, String optionText, String optionValue) {
        JRadioButton optionButton = new JRadioButton(optionText);
        optionButton.setActionCommand(optionValue);
        group.add(optionButton);
        questionPanel.add(optionButton);
    }

    private void setupTimer() {
        int testDuration = adminRepo.getTestTime(testId); // Phút
        remainingTime = testDuration * 60; // Chuyển đổi sang giây

        testTimer = new Timer(1000, e -> {
            remainingTime--;
            updateTimerLabel();

            if(remainingTime <= 0) {
                testTimer.stop();
                evaluateTest();
            }
        });
        testTimer.start();
    }

    private void updateTimerLabel() {
        int minutes = remainingTime / 60;
        int seconds = remainingTime % 60;
        timerLabel.setText("Time left: " + minutes + ":" + String.format("%02d", seconds));
    }

    private void evaluateTest() {
        if (testTimer.isRunning()) {
            testTimer.stop();
        }

        int score = 0;
        int questionIndex = 0;

        for (Component comp : questionsPanel.getComponents()) {
            if (comp instanceof JPanel) {
                ButtonGroup group = findButtonGroup((JPanel) comp);
                if (group != null && group.getSelection() != null) {
                    String selectedAnswer = group.getSelection().getActionCommand();
                    if (selectedAnswer.equals(correctAnswers.get(questionIndex))) {
                        score++;
                    }
                }
                questionIndex++;
            }
        }

        double percentage = (double) score / correctAnswers.size() * 100;
        adminRepo.saveTestResult(studentId, testId, percentage);

        JOptionPane.showMessageDialog(this, "Your Score: " + String.format("%.2f%%", percentage));
        dispose(); // Đóng cửa sổ sau khi hiển thị kết quả
    }

    private ButtonGroup findButtonGroup(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JRadioButton) {
                return ((JRadioButton) comp).getModel().getGroup();
            }
        }
        return null;
    }
}

        
