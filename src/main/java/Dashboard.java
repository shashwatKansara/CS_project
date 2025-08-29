import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {
    private int userId;
    public Dashboard(int userId) {
        this.userId = userId;

        setTitle("Revision Dashboard - User ID: " + userId);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Welcome to your Revision Dashboard, User ID: " + userId, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(label, BorderLayout.CENTER);

        add(panel);
    }

    // Optional for testing standalone
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard(1).setVisible(true));
    }
}
