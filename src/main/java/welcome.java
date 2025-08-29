import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class welcome extends JFrame{

        public welcome() {
            setTitle("NeuroNote.");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setResizable(true);


            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(new Color(240, 248, 255));
            panel.setBorder(BorderFactory.createCompoundBorder(
                    new TitledBorder(
                            new LineBorder(new Color(0, 0, 0), 2, true), // Cornflower Blue
                            "NeuroNote",
                            TitledBorder.CENTER,
                            TitledBorder.TOP,
                            new Font("Arial", Font.BOLD, 18),
                            new Color(105, 105, 108)
                    ),
                    new EmptyBorder(20, 30, 20, 30)
            ));
// Labeling the top
            JLabel title = new JLabel("Welcome to NeuroNote.");
            title.setFont(new Font("Arial", Font.BOLD, 20));
            title.setForeground(Color.DARK_GRAY);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);


// necessary buttons through JButton
            JButton loginButton = new JButton("Login");
            loginButton.setPreferredSize(new Dimension(200, 40));
            loginButton.setMaximumSize(new Dimension(200, 40));
            loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);


            JButton signUpButton = new JButton("Sign Up");
            signUpButton.setPreferredSize(new Dimension(200, 40));
            signUpButton.setMaximumSize(new Dimension(200, 40));
            signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);


            panel.add(Box.createVerticalStrut(20));
            panel.add(title);
            panel.add(Box.createVerticalStrut(25));
            panel.add(loginButton);
            panel.add(Box.createVerticalStrut(45));
            panel.add(signUpButton);

            add(panel);


            loginButton.addActionListener(e -> {
                dispose();
                new loginUI().setVisible(true);
            });

            signUpButton.addActionListener(e -> {
                dispose();
                new SignUpUI().setVisible(true);
            });
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> new welcome().setVisible(true));
        }
    }


