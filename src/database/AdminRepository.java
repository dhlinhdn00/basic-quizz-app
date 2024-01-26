package database;

import services.AdminService;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AdminRepository implements AdminService {
    
    private Connection connection;

    public AdminRepository() {
        // Thiết lập kết nối với cơ sở dữ liệu MySQL
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/QuizApp";
            String user = "root";
            String password = "hoailinh@123";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Xử lý lỗi kết nối
        }
    }
    @Override
    public boolean getLogin(String username, String password) {
        return username.equals("admin") && password.equals("admin");
    }
    
    @Override
    public boolean createStudent(String studentId, String fullName, String gender, String classname, String phoneNumber, String password) {
        try {
            String query = "INSERT INTO Students (student_id, full_name, gender, class, phone_number, password) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, studentId);
            preparedStatement.setString(2, fullName);
            preparedStatement.setString(3, gender);
            preparedStatement.setString(4, classname);
            preparedStatement.setString(5, phoneNumber);
            preparedStatement.setString(6, password);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateStudent(String studentId, String fullName, String gender, String classname, String phoneNumber, String password) {
        try {
            String query = "UPDATE Students SET full_name = ?, gender = ?, class = ?, phone_number = ?, password = ? WHERE student_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, fullName);
            preparedStatement.setString(2, gender);
            preparedStatement.setString(3, classname);
            preparedStatement.setString(4, phoneNumber);
            preparedStatement.setString(5, password);
            preparedStatement.setString(6, studentId);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteStudent(String studentId) {
        try {
            String query = "DELETE FROM Students WHERE student_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, studentId);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Map<String, String>> getAllStudents() {
        try {
            String query = "SELECT student_id, full_name, gender, class, phone_number, password FROM Students";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            List<Map<String, String>> students = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, String> student = new HashMap<>();
                student.put("student_id", resultSet.getString("student_id"));
                student.put("full_name", resultSet.getString("full_name"));
                student.put("gender", resultSet.getString("gender"));
                student.put("class", resultSet.getString("class"));
                student.put("phone_number", resultSet.getString("phone_number"));
                student.put("password", resultSet.getString("password"));
                students.add(student);
            }
            return students;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Map<String, String>> getAllTests() {
        try {
            String query = "SELECT test_id, test_name, test_time, showresults, showanswers FROM Tests";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Map<String, String>> tests = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, String> test = new HashMap<>();
                test.put("test_id", resultSet.getString("test_id"));
                test.put("test_name", resultSet.getString("test_name"));
                test.put("number_of_questions", countQuestionsForTest(resultSet.getInt("test_id")));
                test.put("test_time", resultSet.getString("test_time"));
                test.put("showresults", String.valueOf(resultSet.getBoolean("showresults"))); // Chuyển đổi Boolean thành String
                test.put("showanswers", String.valueOf(resultSet.getBoolean("showanswers"))); // Chuyển đổi Boolean thành String
                tests.add(test);
            }
            return tests;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }




    // Phương thức phụ trợ để đếm số câu hỏi cho mỗi bài kiểm tra
    private String countQuestionsForTest(int testId) {
        try {
            String countQuery = "SELECT COUNT(*) AS question_count FROM TestQuestions WHERE test_id = ?";
            PreparedStatement countStmt = connection.prepareStatement(countQuery);
            countStmt.setInt(1, testId);
            ResultSet countResult = countStmt.executeQuery();
            if (countResult.next()) {
                return countResult.getString("question_count");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "0";
    }
    
    @Override
    public List<Map<String, String>> getTestDetails(String testId) {
        List<Map<String, String>> testDetails = new ArrayList<>();
        try {
            // Thêm 'q.question_id' vào truy vấn để lấy ID của mỗi câu hỏi
            String query = "SELECT q.question_id, q.question_text, q.option_a, q.option_b, q.option_c, q.option_d, q.correct_option " +
                           "FROM Questions q INNER JOIN TestQuestions tq ON q.question_id = tq.question_id " +
                           "WHERE tq.test_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, testId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Map<String, String> question = new HashMap<>();
                question.put("question_id", Integer.toString(resultSet.getInt("question_id"))); // Lấy và lưu 'question_id'
                question.put("question_text", resultSet.getString("question_text"));
                question.put("option_a", resultSet.getString("option_a"));
                question.put("option_b", resultSet.getString("option_b"));
                question.put("option_c", resultSet.getString("option_c"));
                question.put("option_d", resultSet.getString("option_d"));
                question.put("correct_option", resultSet.getString("correct_option"));
                testDetails.add(question);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return testDetails;
    }
    
    @Override
    public boolean addQuestion(String testId, String questionText, String optionA, String optionB, String optionC, String optionD, String correctOption) {
        try {
            // Thêm câu hỏi vào bảng Questions
            String questionQuery = "INSERT INTO Questions (question_text, option_a, option_b, option_c, option_d, correct_option) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(questionQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, questionText);
            preparedStatement.setString(2, optionA);
            preparedStatement.setString(3, optionB);
            preparedStatement.setString(4, optionC);
            preparedStatement.setString(5, optionD);
            preparedStatement.setString(6, correctOption);
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            // Lấy ID của câu hỏi vừa thêm
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int questionId = generatedKeys.getInt(1);

                // Thêm mối quan hệ giữa Test và Question
                String testQuestionQuery = "INSERT INTO TestQuestions (test_id, question_id) VALUES (?, ?)";
                PreparedStatement testQuestionStmt = connection.prepareStatement(testQuestionQuery);
                testQuestionStmt.setString(1, testId);
                testQuestionStmt.setInt(2, questionId);
                testQuestionStmt.executeUpdate();
            }

            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean deleteQuestion(int questionId) {
        try {
            // Xóa mối quan hệ giữa câu hỏi và bài kiểm tra từ bảng TestQuestions
            String deleteTestQuestionQuery = "DELETE FROM TestQuestions WHERE question_id = ?";
            PreparedStatement testQuestionStmt = connection.prepareStatement(deleteTestQuestionQuery);
            testQuestionStmt.setInt(1, questionId);
            testQuestionStmt.executeUpdate();

            // Xóa câu hỏi từ bảng Questions
            String deleteQuestionQuery = "DELETE FROM Questions WHERE question_id = ?";
            PreparedStatement questionStmt = connection.prepareStatement(deleteQuestionQuery);
            questionStmt.setInt(1, questionId);
            int affectedRows = questionStmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean editQuestion(int questionId, String questionText, String optionA, String optionB, String optionC, String optionD, String correctOption) {
        try {
            // Cập nhật thông tin câu hỏi trong bảng Questions
            String updateQuestionQuery = "UPDATE Questions SET question_text = ?, option_a = ?, option_b = ?, option_c = ?, option_d = ?, correct_option = ? WHERE question_id = ?";
            PreparedStatement questionStmt = connection.prepareStatement(updateQuestionQuery);
            questionStmt.setString(1, questionText);
            questionStmt.setString(2, optionA);
            questionStmt.setString(3, optionB);
            questionStmt.setString(4, optionC);
            questionStmt.setString(5, optionD);
            questionStmt.setString(6, correctOption);
            questionStmt.setInt(7, questionId);

            int affectedRows = questionStmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    @Override
    public Map<String, String> getQuestionDetails(int questionId) {
        Map<String, String> questionDetails = new HashMap<>();
        try {
            String query = "SELECT * FROM Questions WHERE question_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, questionId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                questionDetails.put("question_id", Integer.toString(resultSet.getInt("question_id")));
                questionDetails.put("question_text", resultSet.getString("question_text"));
                questionDetails.put("option_a", resultSet.getString("option_a"));
                questionDetails.put("option_b", resultSet.getString("option_b"));
                questionDetails.put("option_c", resultSet.getString("option_c"));
                questionDetails.put("option_d", resultSet.getString("option_d"));
                questionDetails.put("correct_option", resultSet.getString("correct_option"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Xử lý lỗi
        }
        return questionDetails;
    }
    
    @Override
    public boolean addTest(String testName, int testTime, boolean showResults, boolean showAnswers) {
        try {
            String query = "INSERT INTO Tests (test_name, test_time, showresults, showanswers) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, testName);
            preparedStatement.setInt(2, testTime);
            preparedStatement.setBoolean(3, showResults);
            preparedStatement.setBoolean(4, showAnswers);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateTest(String testId, String testName, String testTime, boolean showResults, boolean showAnswers) {
        try {
            String query = "UPDATE Tests SET test_name = ?, test_time = ?, showresults = ?, showanswers = ? WHERE test_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, testName);
            preparedStatement.setString(2, testTime);
            preparedStatement.setBoolean(3, showResults);
            preparedStatement.setBoolean(4, showAnswers);
            preparedStatement.setString(5, testId);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteTest(String testId) {
       boolean success = false;
       try {
           // Bắt đầu một transaction
           connection.setAutoCommit(false);

           // Xóa các kết quả liên quan đến bài kiểm tra
           String deleteResultsQuery = "DELETE FROM TestResults WHERE test_id = ?";
           try (PreparedStatement deleteResultsStmt = connection.prepareStatement(deleteResultsQuery)) {
               deleteResultsStmt.setString(1, testId);
               deleteResultsStmt.executeUpdate();
           }

           // Xóa các mối quan hệ liên quan đến bài kiểm tra trong bảng TestQuestions
           String deleteTestQuestionsQuery = "DELETE FROM TestQuestions WHERE test_id = ?";
           try (PreparedStatement deleteTestQuestionsStmt = connection.prepareStatement(deleteTestQuestionsQuery)) {
               deleteTestQuestionsStmt.setString(1, testId);
               deleteTestQuestionsStmt.executeUpdate();
           }

           // Xóa bài kiểm tra từ bảng Tests
           String deleteTestQuery = "DELETE FROM Tests WHERE test_id = ?";
           try (PreparedStatement deleteTestStmt = connection.prepareStatement(deleteTestQuery)) {
               deleteTestStmt.setString(1, testId);
               deleteTestStmt.executeUpdate();
           }

           // Nếu không có lỗi, commit transaction
           connection.commit();
           success = true;
       } catch (SQLException e) {
           e.printStackTrace();
           try {
               // Nếu có lỗi, rollback transaction
               connection.rollback();
           } catch (SQLException ex) {
               ex.printStackTrace();
           }
       } finally {
           try {
               // Trả lại trạng thái auto commit
               connection.setAutoCommit(true);
           } catch (SQLException e) {
               e.printStackTrace();
           }
       }
       return success;
    }
    @Override
    public List<Map<String, String>> getAllTestResults() {
        List<Map<String, String>> testResults = new ArrayList<>();
        try {
            String query = "SELECT * FROM TestResults";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Map<String, String> result = new HashMap<>();
                result.put("result_id", Integer.toString(resultSet.getInt("result_id")));
                result.put("student_id", resultSet.getString("student_id"));
                result.put("test_id", Integer.toString(resultSet.getInt("test_id")));
                result.put("attempt_date", resultSet.getTimestamp("attempt_date").toString());
                result.put("score", resultSet.getBigDecimal("score").toString());
                testResults.add(result);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return testResults;
    }
    
    @Override
    public boolean saveTestResult(String studentId, String testId, double score) {
        try {
            String query = "INSERT INTO TestResults (student_id, test_id, score, attempt_date) VALUES (?, ?, ?, NOW())";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, studentId);
            preparedStatement.setString(2, testId);
            preparedStatement.setDouble(3, score);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    @Override
    public int getTestTime(String testId) {
        try {
            String query = "SELECT test_time FROM Tests WHERE test_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, testId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("test_time");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0; // Trả về 0 nếu không tìm thấy thời gian
    }





}
