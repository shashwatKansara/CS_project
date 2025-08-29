import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.*;

public class Note extends JFrame {
    private static final Logger logger = Logger.getLogger(Note.class.getName());
    private JTextArea noteArea;
    private JPanel notesListPanel;
    private int userId;
    private Connection conn;
    private Integer currentNoteId = null;
    private final HashMap<Integer, JPanel> notePanels = new HashMap<>();
    private Timer autoSaveTimer;

    public Note(int userId) {
        this.userId = userId;
        setupLogger();
        conn = DatabaseManager.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed.");
            System.exit(1);
        }

        setTitle("Note Taking Panel");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        noteArea = new JTextArea();
        noteArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(noteArea);
        add(scrollPane, BorderLayout.CENTER);

        startAutoSaveTimer();

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(220, getHeight()));
        leftPanel.setBackground(new Color(112, 114, 117));

        JPanel topButtonsPanel = new JPanel();
        topButtonsPanel.setLayout(new BoxLayout(topButtonsPanel, BoxLayout.Y_AXIS));
        topButtonsPanel.setBackground(new Color(105, 105, 108));

        JButton newNoteButton = new JButton("New Note");
        JButton rBoard = new JButton("Revision Dashboard");
        JButton createFlashcardButton = new JButton("Create Flashcard");
        JButton hw2Button = new JButton("Back to Home");



        for (JButton btn : new JButton[]{newNoteButton,rBoard,createFlashcardButton, hw2Button}) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(180, 30));
            topButtonsPanel.add(Box.createVerticalStrut(10));
            topButtonsPanel.add(btn);
        }

        topButtonsPanel.add(Box.createVerticalStrut(10));

        notesListPanel = new JPanel();
        notesListPanel.setLayout(new BoxLayout(notesListPanel, BoxLayout.Y_AXIS));
        notesListPanel.setBackground(new Color(104, 105, 108));
        JScrollPane notesScrollPane = new JScrollPane(notesListPanel);
        notesScrollPane.setPreferredSize(new Dimension(200, 300));
        notesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        leftPanel.add(topButtonsPanel, BorderLayout.NORTH);
        leftPanel.add(notesScrollPane, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);

        createFlashcardButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Flashcard UI not implemented."));
        hw2Button.addActionListener(e -> {
            dispose();
            new Homepage(userId).setVisible(true);
        });
        newNoteButton.addActionListener(e -> createNewNote());

        loadUserNotes();
    }

    private void createNewNote() {
        String noteTitle = JOptionPane.showInputDialog(this, "Enter note title:");
        if (noteTitle == null || noteTitle.trim().isEmpty()) return;

        noteArea.setText("");
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO school_Manager.notes (id, title, content) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            stmt.setInt(1, userId);
            stmt.setString(2, noteTitle);
            stmt.setString(3, "");
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int noteId = rs.getInt(1);
                addNotePanel(noteTitle, noteId);
                currentNoteId = noteId;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error creating note", ex);
        }
    }

    private void addNotePanel(String title, int noteId) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(200, 30));

        JButton noteBtn = new JButton(title);
        noteBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        noteBtn.addActionListener(e -> loadNoteContent(noteId));

        JButton renameBtn = new JButton("✎");
        renameBtn.setPreferredSize(new Dimension(30, 30));
        renameBtn.setFont(new Font("SansSerif", Font.PLAIN, 10));
        renameBtn.setMargin(new Insets(1, 4, 1, 4));
        renameBtn.addActionListener(e -> renameNote(noteId));

        JButton deleteBtn = new JButton("✕");
        deleteBtn.setPreferredSize(new Dimension(30, 30));
        deleteBtn.setFont(new Font("SansSerif", Font.PLAIN, 10));
        deleteBtn.setMargin(new Insets(1, 4, 1, 4));
        deleteBtn.addActionListener(e -> deleteNote(noteId, panel));

        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        buttonGroup.add(renameBtn);
        buttonGroup.add(deleteBtn);

        panel.add(noteBtn, BorderLayout.CENTER);
        panel.add(buttonGroup, BorderLayout.EAST);

        notePanels.put(noteId, panel);
        notesListPanel.add(panel);
        notesListPanel.revalidate();
        notesListPanel.repaint();
    }

    private void loadNoteContent(int noteId) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT content FROM school_Manager.notes WHERE note_id = ?");
            stmt.setInt(1, noteId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currentNoteId = noteId;
                noteArea.setText(rs.getString("content"));
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to load note content", ex);
        }
    }

    private void saveContent() {
        if (currentNoteId == null) return;
        try {
            PreparedStatement stmt = conn.prepareStatement("UPDATE school_Manager.notes SET content = ? WHERE note_id = ?");
            stmt.setString(1, noteArea.getText());
            stmt.setInt(2, currentNoteId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to save note content", ex);
        }
    }

    private void deleteNote(int noteId, JPanel panelToRemove) {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this note?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM school_Manager.notes WHERE note_id = ?");
            stmt.setInt(1, noteId);
            stmt.executeUpdate();

            if (noteId == currentNoteId) {
                noteArea.setText("");
                currentNoteId = null;
            }

            notesListPanel.remove(panelToRemove);
            notesListPanel.revalidate();
            notesListPanel.repaint();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to delete note", ex);
        }
    }

    private void renameNote(int noteId) {
        String newTitle = JOptionPane.showInputDialog(this, "Enter new note title:");
        if (newTitle == null || newTitle.trim().isEmpty()) return;

        try {
            PreparedStatement stmt = conn.prepareStatement("UPDATE school_Manager.notes SET title = ? WHERE note_id = ?");
            stmt.setString(1, newTitle);
            stmt.setInt(2, noteId);
            stmt.executeUpdate();

            JPanel panel = notePanels.get(noteId);
            if (panel != null) {
                JButton titleButton = (JButton) panel.getComponent(0);
                titleButton.setText(newTitle);
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to rename note", ex);
        }
    }

    private void loadUserNotes() {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT note_id, title FROM school_Manager.notes WHERE id = ?");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int noteId = rs.getInt("note_id");
                String title = rs.getString("title");
                addNotePanel(title, noteId);
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to load user notes", ex);
        }
    }

    private void startAutoSaveTimer() {
        autoSaveTimer = new Timer();
        autoSaveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                saveContent();
            }
        }, 10000, 10000);
    }

    private static void setupLogger() {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(consoleHandler);
        logger.setLevel(Level.INFO);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Note(1).setVisible(true));
    }
}
