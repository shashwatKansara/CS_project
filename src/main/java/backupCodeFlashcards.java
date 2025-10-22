import javax.swing.*;
import java.awt.*;
import java.sql.*;

// AES imports
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class FlashCardUI extends JFrame {
    private static final String dburl = "jdbc:mysql://127.0.0.1:3306/school_Manager";
    private static final String dbuser = "root";
    private static final String dbpassword = "kansara55";

    private int userId;
    private JPanel flashcardsPanel;

    //  AES Utility for encryption/decryption ✅
    public static class AESUtil {
        private static final String ALGORITHM = "AES";
        private static final String SECRET_KEY = "12345678901234567890123456789012"; // 32-byte key

        public static String encrypt(String data) throws Exception {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes("UTF-8")));
        }

        public static String decrypt(String encryptedData) throws Exception {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            return new String(cipher.doFinal(decodedBytes), "UTF-8");
        }
    }

    public FlashCardUI(int userId) {
        this.userId = userId;

        setTitle("Flashcards - NeuroNote");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        setLayout(new BorderLayout());

        JLabel header = new JLabel("Flashcards", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(header, BorderLayout.NORTH);

        flashcardsPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        flashcardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(flashcardsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        JButton createButton = new JButton("Create Flashcard");
        JButton backButton = new JButton("Back to Home");

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(createButton);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadFlashcards();

        createButton.addActionListener(e -> new FlashcardCreator(userId, this).setVisible(true));
        backButton.addActionListener(e -> {
            dispose();
            new Homepage(userId).setVisible(true);
        });
    }

    // ✅ Load flashcards with decryption
    public void loadFlashcards() {
        flashcardsPanel.removeAll();

        try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword)) {
            String query = "SELECT card_id, title, front_text, back_text, flashcardsol FROM flashcards WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("card_id");

                    // Decrypting each field before displaying
                    String title = AESUtil.decrypt(rs.getString("title"));
                    String front = AESUtil.decrypt(rs.getString("front_text"));
                    String back = AESUtil.decrypt(rs.getString("back_text"));
                    String solution = AESUtil.decrypt(rs.getString("flashcardsol"));

                    // ✅ Outer wrapper for title + flashcard content
                    JPanel wrapperPanel = new JPanel(new BorderLayout());
                    wrapperPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    wrapperPanel.setBackground(Color.WHITE);
                    wrapperPanel.setPreferredSize(new Dimension(250, 180));

                    // title label visible on the top.
                    JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
                    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                    titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
                    wrapperPanel.add(titleLabel, BorderLayout.NORTH);

                    //  flipping content
                    JPanel cardPanel = new JPanel(new CardLayout());

                    JLabel frontLabel = new JLabel("<html><center>" + front + "</center></html>", SwingConstants.CENTER);
                    frontLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
                    JPanel frontCard = new JPanel(new BorderLayout());
                    frontCard.add(frontLabel, BorderLayout.CENTER);

                    JLabel backLabel = new JLabel("<html><center>" + back + "</center></html>", SwingConstants.CENTER);
                    backLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
                    JPanel backCard = new JPanel(new BorderLayout());
                    backCard.add(backLabel, BorderLayout.CENTER);

                    JLabel solLabel = new JLabel("<html><center>" + solution + "</center></html>", SwingConstants.CENTER);
                    solLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    JPanel solutionCard = new JPanel(new BorderLayout());
                    solutionCard.add(solLabel, BorderLayout.CENTER);

                    cardPanel.add(frontCard, "Front");
                    cardPanel.add(backCard, "Back");
                    cardPanel.add(solutionCard, "Solution");

                    cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                        int state = 0;
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            CardLayout cl = (CardLayout) cardPanel.getLayout();
                            state = (state + 1) % 3;
                            if (state == 0) cl.show(cardPanel, "Front");
                            else if (state == 1) cl.show(cardPanel, "Back");
                            else cl.show(cardPanel, "Solution");
                        }
                    });

                    JButton menuBtn = new JButton("⋮");
                    menuBtn.setFocusPainted(false);
                    menuBtn.setBorderPainted(false);
                    menuBtn.setContentAreaFilled(false);
                    menuBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
                    menuBtn.setMargin(new Insets(2, 2, 2, 2));

                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem editItem = new JMenuItem("Edit");
                    JMenuItem deleteItem = new JMenuItem("Delete");
                    menu.add(editItem);
                    menu.add(deleteItem);

                    menuBtn.addActionListener(e -> menu.show(menuBtn, 0, menuBtn.getHeight()));
                    editItem.addActionListener(e -> editFlashcard(id, front));
                    deleteItem.addActionListener(e -> deleteFlashcard(id));

                    JPanel topBar = new JPanel(new BorderLayout());
                    topBar.setOpaque(false);
                    topBar.add(menuBtn, BorderLayout.EAST);

                    wrapperPanel.add(topBar, BorderLayout.SOUTH);
                    wrapperPanel.add(cardPanel, BorderLayout.CENTER);

                    flashcardsPanel.add(wrapperPanel);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading flashcards: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        flashcardsPanel.revalidate();
        flashcardsPanel.repaint();
    }

    // ✅ Edit flashcard (encrypt new text)
    private void editFlashcard(int cardId, String oldFront) {
        String newFront = JOptionPane.showInputDialog(this, "Edit Front Text:", oldFront);
        if (newFront == null || newFront.trim().isEmpty()) return;

        try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword)) {
            String query = "UPDATE flashcards SET front_text = ? WHERE card_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, AESUtil.encrypt(newFront.trim()));
                stmt.setInt(2, cardId);
                stmt.executeUpdate();
                loadFlashcards();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error editing flashcard: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // delete flashcard
    private void deleteFlashcard(int cardId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this flashcard?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword)) {
            String query = "DELETE FROM flashcards WHERE card_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, cardId);
                stmt.executeUpdate();
                loadFlashcards();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting flashcard: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= Flashcard Creator =================
    static class FlashcardCreator extends JFrame {
        private int userId;
        private JTextField titleField, frontField, backField, solutionField;
        private FlashCardUI parent;

        public FlashcardCreator(int userId, FlashCardUI parent) {
            this.userId = userId;
            this.parent = parent;

            setTitle("Create Flashcard - NeuroNote");
            setSize(400, 300);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

            titleField = new JTextField();
            titleField.setBorder(BorderFactory.createTitledBorder("Flashcard Title"));
            frontField = new JTextField();
            frontField.setBorder(BorderFactory.createTitledBorder("Front Text"));
            backField = new JTextField();
            backField.setBorder(BorderFactory.createTitledBorder("Back Text"));
            solutionField = new JTextField();
            solutionField.setBorder(BorderFactory.createTitledBorder("Solution"));

            formPanel.add(titleField);
            formPanel.add(frontField);
            formPanel.add(backField);
            formPanel.add(solutionField);

            JPanel buttonPanel = new JPanel();
            JButton discardButton = new JButton("Discard");
            JButton saveButton = new JButton("Save");
            buttonPanel.add(discardButton);
            buttonPanel.add(saveButton);

            add(formPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);

            discardButton.addActionListener(e -> dispose());
            saveButton.addActionListener(e -> saveFlashcard());
        }

        // ✅ Save flashcard with encryption
        private void saveFlashcard() {
            String title = titleField.getText().trim();
            String front = frontField.getText().trim();
            String back = backField.getText().trim();
            String solution = solutionField.getText().trim();

            if (title.isEmpty() || front.isEmpty() || back.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword)) {
                String query = "INSERT INTO flashcards (user_id, title, front_text, back_text, flashcardsol, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, NOW())";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, userId);
                    stmt.setString(2, AESUtil.encrypt(title));
                    stmt.setString(3, AESUtil.encrypt(front));
                    stmt.setString(4, AESUtil.encrypt(back));
                    stmt.setString(5, AESUtil.encrypt(solution));

                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Flashcard saved successfully!");
                    dispose();
                    parent.loadFlashcards();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving flashcard: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FlashCardUI(1).setVisible(true));
    }
}
