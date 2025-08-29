import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class FlashCardUI extends JFrame {

    private int userId;
    private int userAge;
    private String question;
    private String answer;
    private String topic;
    private String difficultyLevel;
    private LocalDateTime creationDate;
    private Boolean isBookmarked;
    private String hint;
    private int attempts;

    public FlashCardUI(int userId) {
        this.userId = userId;

        setTitle("Flashcards - User ID: " + userId);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel for layout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Flashcard Interface Coming Soon!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton HWindow = new JButton("Go back to Home Window");
        HWindow.setPreferredSize(new Dimension(200, 40));
        HWindow.setMaximumSize(new Dimension(200, 40));
        HWindow.setAlignmentX(Component.CENTER_ALIGNMENT);

        HWindow.addActionListener(e -> {
            new Homepage(userId).setVisible(true);
            dispose();
        });

        panel.add(Box.createVerticalGlue());
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(HWindow);
        panel.add(Box.createVerticalGlue());

        add(panel);
    }

    // Optional main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FlashCardUI(1).setVisible(true));
    }
}
