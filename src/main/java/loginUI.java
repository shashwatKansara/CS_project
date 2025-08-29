import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class loginUI extends JFrame {
    private static final String dburl = "jdbc:mysql://127.0.0.1:3306/school_Manager";
    private static final String dbuser = "root";
    private static final String dbpassword = "kansara55";

    public loginUI() {
        setTitle("Login - NeuroNote");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));
        panel.setBackground(new Color(245, 250, 255));

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(33, 37, 41));

        JTextField emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        JTextField otpField = new JTextField();
        otpField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        otpField.setBorder(BorderFactory.createTitledBorder("Enter OTP"));

        JLabel errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginButton = new JButton("Login");
        loginButton.setForeground(Color.BLACK);
        loginButton.setBackground(new Color(200, 230, 250));
        loginButton.setFocusPainted(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(200, 40));

        JButton signUpButton = new JButton("Go to Sign Up");
        signUpButton.setForeground(Color.BLACK);
        signUpButton.setBackground(new Color(230, 230, 230));
        signUpButton.setFocusPainted(false);
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.setMaximumSize(new Dimension(200, 40));

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(otpField); // NEW OTP FIELD ADDED HERE
        panel.add(Box.createVerticalStrut(10));
        panel.add(errorLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(signUpButton);

        add(panel);

        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String enteredOTP = otpField.getText().trim();

            if (email.isEmpty() || password.isEmpty() || enteredOTP.isEmpty()) {
                errorLabel.setText("Please fill in all fields including OTP.");
                return;
            }

            try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword)) {
                String query = "SELECT id, password FROM user_data WHERE email = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, email);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String hashedPassword = rs.getString("password");
                        int userId = rs.getInt("id");

                        if (BCrypt.checkpw(password, hashedPassword)) {
                            // Placeholder for OTP verification
                            // You should replace this with actual OTP logic
                            String expectedOTP = "123456"; // Simulated expected OTP
                            if (enteredOTP.equals(expectedOTP)) {
                                JOptionPane.showMessageDialog(this, "Login Successful!");
                                dispose();
                                new Homepage(userId).setVisible(true);
                            } else {
                                errorLabel.setText("Incorrect OTP.");
                            }
                        } else {
                            errorLabel.setText("Invalid email or password.");
                        }
                    } else {
                        errorLabel.setText("No user found with this email.");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });

        signUpButton.addActionListener(e -> {
            dispose();
            new SignUpUI().setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new loginUI().setVisible(true));
    }
}
