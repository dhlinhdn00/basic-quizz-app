package database;

import services.StudentService;
import java.sql.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class StudentRepository implements StudentService {
    
    private Connection connection;

    public StudentRepository() {
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/QuizApp";
            String user = "root";
            String password = "hoailinh@123";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean getLogin(String studentId, String password) {
        try {
            String query = "SELECT * FROM Students WHERE student_id = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, studentId);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Các phương thức khác cho việc thêm, cập nhật, và xóa thông tin sinh viên...
    // Bạn có thể sử dụng mô hình tương tự như trong AdminRepository

    @Override
    public List<Map<String, String>> getStudentResults(String studentId) {
        List<Map<String, String>> studentResults = new ArrayList<>();
        try {
            String query = "SELECT * FROM TestResults WHERE student_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, studentId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Map<String, String> result = new HashMap<>();
                result.put("result_id", Integer.toString(resultSet.getInt("result_id")));
                result.put("student_id", resultSet.getString("student_id"));
                result.put("test_id", Integer.toString(resultSet.getInt("test_id")));
                result.put("attempt_date", resultSet.getTimestamp("attempt_date").toString());
                result.put("score", resultSet.getBigDecimal("score").toString());
                studentResults.add(result);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return studentResults;
    }
}
