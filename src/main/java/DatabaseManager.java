import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/school_Manager";
    private static final String USER = "root";
    private static final String PASSWORD = "kansara55"; // your actual root password

    private static Connection conn;

    // Static initializer block to set up the connection once
    static {
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Static method for other classes to get the shared connection
    public static Connection getConnection() {
        return conn;
    }

    // Insert user data
    public boolean insertUser(String email, String username, int age, String password) {
        String sql = "INSERT INTO user_data (username, password, email, age) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, username);
            stmt.setInt(3, age);
            stmt.setString(4, password);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            return false;
        }
    }

    // inserting user notes
    public boolean insertNote(int userId, String title, String content) {
        String sql = "INSERT INTO notes (user_id, title, content) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, title);
            stmt.setString(3, content);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            return false;
        }
    }

    // insert user-entered flashcard into the flashcard table (database)
    public boolean insertFlashcard(int userId, String question, String answer,
                                   String topic, String difficulty, boolean isBookmarked,
                                   String hint, int attempts) {
        String sql = "INSERT INTO flashcards (user_id, question, answer, topic, difficulty, is_bookmarked, hint, attempts) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, question);
            stmt.setString(3, answer);
            stmt.setString(4, topic);
            stmt.setString(5, difficulty);
            stmt.setBoolean(6, isBookmarked);
            stmt.setString(7, hint);
            stmt.setInt(8, attempts);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            return false;
        }
    }

    // Close the shared connection
    public static void closeConnection() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
