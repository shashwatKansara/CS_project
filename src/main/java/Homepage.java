import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class Homepage extends JFrame {
    private int userId;

    public Homepage(int userId) {
        this.userId = userId;

        setTitle("NeuroNote - User ID: " + userId);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(117, 112, 112));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(
                        new LineBorder(new Color(0, 0, 0), 2, true),
                        "NeuroNote.",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 18),
                        new Color(0, 0, 0)
                ),
                new EmptyBorder(20, 30, 20, 30)
        ));

        // Notes button leading to the note window, corresponding to current user = userId.
        JButton notesButton = new JButton("Notes");
        notesButton.setForeground(Color.BLACK);
        notesButton.setBackground(new Color(143, 147, 151));
        notesButton.setFocusPainted(false);
        notesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        notesButton.setMaximumSize(new Dimension(200, 40));
        notesButton.addActionListener(e -> {
            new Note(userId).setVisible(true);
            dispose();
        });

        // flashcards button
        JButton flashcardsButton = new JButton("Flashcards");
        flashcardsButton.setForeground(Color.BLACK);
        flashcardsButton.setBackground(new Color(143, 147, 151));
        flashcardsButton.setFocusPainted(false);
        flashcardsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        flashcardsButton.setMaximumSize(new Dimension(200, 40));
        flashcardsButton.addActionListener(e -> {
            new FlashCardUI(userId).setVisible(true);
            dispose();
        });

        // Revision Dashboard button similarly, for particular userId.
        JButton rBoardBtn = new JButton("Revision Dashboard");
        rBoardBtn.setForeground(Color.BLACK);
        rBoardBtn.setBackground(new Color(143, 147, 151, 255));
        rBoardBtn.setFocusPainted(false);
        rBoardBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        rBoardBtn.setMaximumSize(new Dimension(200, 40));
        rBoardBtn.addActionListener(e -> {
            new Dashboard(userId).setVisible(true);
            dispose();
        });

        // Adding buttons to panel
        panel.add(notesButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(flashcardsButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(rBoardBtn);

        add(panel);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Homepage(1).setVisible(true));
    }
}
