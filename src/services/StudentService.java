package services;

import java.util.List;
import java.util.Map;

public interface StudentService {

    public boolean getLogin(String username, String password);

    List<Map<String, String>> getStudentResults(String studentId);
    
}
