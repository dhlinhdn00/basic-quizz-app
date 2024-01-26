package services;

import java.util.List;
import java.util.Map;

public interface AdminService {
    boolean getLogin(String username, String password);

    boolean createStudent(String studentId, String fullName, String gender, String className, String phoneNumber, String password);

    boolean updateStudent(String studentId, String fullName, String gender, String className, String phoneNumber, String password);

    boolean deleteStudent(String studentId);

    List<Map<String, String>> getAllStudents();
    
    List<Map<String, String>> getAllTests();
    
    List<Map<String, String>> getTestDetails(String testId);
    
    boolean addQuestion(String testId, String questionText, String optionA, String optionB, String optionC, String optionD, String correctOption);
    
    boolean deleteQuestion(int questionId);
    
    boolean editQuestion(int questionId, String questionText, String optionA, String optionB, String optionC, String optionD, String correctOption);
    
    Map<String, String> getQuestionDetails(int questionId);
    
    boolean addTest(String testName, int testTime, boolean showResults, boolean showAnswers);
    
    boolean updateTest(String testId, String testName, String testTime, boolean showResults, boolean showAnswers);
    
    boolean deleteTest(String testId);
    
    List<Map<String, String>> getAllTestResults();
    
    boolean saveTestResult(String studentId, String testId, double score);
    
    int getTestTime(String testId);
}
