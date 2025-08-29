import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class SignUpUI extends JFrame {
    private static final String dburl = "jdbc:mysql://127.0.0.1:3306/school_Manager";
    private static final String dbuser = "root";
    private static final String dbpassword = "kansara55";

    // Inserts user and returns generated user_id
    private int insertIntoDatabase(String email, String username, int age, String password) {
        String query = "INSERT INTO user_data (email, username, age, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword);
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, email);
            stmt.setString(2, username);
            stmt.setInt(3, age);
            stmt.setString(4, password);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // return the generated user_id
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
        return -1;
    }

    public SignUpUI() {
        setTitle("Sign Up - NeuroNote");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));
        panel.setBackground(new Color(245, 250, 255));
        panel.setPreferredSize(new Dimension(350, 600));

        JLabel titleLabel = new JLabel("Create a New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(33, 37, 41));

        JTextField emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));

        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));

        JTextField ageField = new JTextField();
        ageField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        ageField.setBorder(BorderFactory.createTitledBorder("Age"));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        confirmPasswordField.setBorder(BorderFactory.createTitledBorder("Confirm Password"));

        JLabel emailErrorLabel = new JLabel();
        emailErrorLabel.setForeground(Color.RED);
        emailErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel generalErrorLabel = new JLabel();
        generalErrorLabel.setForeground(Color.RED);
        generalErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setForeground(Color.BLACK);
        signUpButton.setBackground(new Color(200, 230, 250));
        signUpButton.setFocusPainted(false);
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.setMaximumSize(new Dimension(200, 40));

        JButton backButton = new JButton("Back to Welcome Window");
        backButton.setForeground(Color.BLACK);
        backButton.setBackground(new Color(230, 230, 230));
        backButton.setFocusPainted(false);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setMaximumSize(new Dimension(200, 40));

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(emailField);
        panel.add(emailErrorLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(ageField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(confirmPasswordField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(generalErrorLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(signUpButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(backButton);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane);

        signUpButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String ageText = ageField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

            if (email.isEmpty() || username.isEmpty() || ageText.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                generalErrorLabel.setText("Please fill all fields.");
                return;
            } else {
                generalErrorLabel.setText("");
            }

            if (!email.endsWith("@gmail.com")) {
                emailErrorLabel.setText("Wrong Email format");
                return;
            } else {
                emailErrorLabel.setText("");
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.");
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for age.");
                return;
            }

            try {
                String hashedPassword = hashPassword(password);
                int userId = insertIntoDatabase(email, username, age, hashedPassword);

                if (userId != -1) {
                    JOptionPane.showMessageDialog(this, "Sign Up Successful!");
                    dispose();
                    new Homepage(userId).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Error: Could not insert data into database.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> {
            dispose();
            new welcome().setVisible(true);
        });
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUpUI().setVisible(true));
    }
}
