package latihan2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class LevelSelectionPage {

    private JFrame frame;
    private Set<Integer> unlockedLevels = new HashSet<>();
    private String username;
    private String category;
    LevelSelectionPage self = this;

    public LevelSelectionPage(String username, String category) {
        this.username = username;
        this.category = category;
        loadUnlockedLevelsFromDB();
        initialize();
    }

    public void show() {
        frame.setVisible(true);
    }

    private void loadUnlockedLevelsFromDB() {
        String url = "jdbc:mysql://localhost:3306/questions";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT level FROM level_progress WHERE username = ? AND category = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                unlockedLevels.add(rs.getInt("level"));
            }

            unlockedLevels.add(1); // Level 1 selalu terbuka

        } catch (SQLException e) {
            e.printStackTrace();
            unlockedLevels.clear();
            unlockedLevels.add(1);
        }
    }

    private void initialize() {
        frame = new JFrame("Level " + capitalize(category));
        frame.setBounds(50, 50, 360, 640);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        // Background panel
        JPanel backgroundPanel = new JPanel() {
            Image background = new ImageIcon("images/wpp7.png").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background != null) {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setLayout(null);
        frame.setContentPane(backgroundPanel);
        
        Color yellow = Color.decode("#ffc800");
        Color brown = Color.decode("#3a301e");

        JLabel lblTitle = new JLabel("Pilih Level " + capitalize(category));
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitle.setForeground(brown);
        lblTitle.setBounds(100, 20, 200, 30);
        backgroundPanel.add(lblTitle);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 4, 10, 10));
        panel.setBounds(30, 70, 280, 400);
        panel.setOpaque(false);
        backgroundPanel.add(panel);

        for (int i = 1; i <= 20; i++) {
            JButton btn = new JButton(String.valueOf(i));
            btn.setFont(new Font("Tahoma", Font.BOLD, 14));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setForeground(Color.BLACK);
            btn.setPreferredSize(new Dimension(60, 60));
            btn.setContentAreaFilled(false);

            boolean isUnlocked = unlockedLevels.contains(i);
            Color bgColor = isUnlocked ? yellow : Color.LIGHT_GRAY;

            btn.setUI(new RoundedButtonUI(bgColor));

            if (isUnlocked) {
                int level = i;
                btn.addActionListener(e -> {
                    frame.setVisible(false);
                    new QuizPage(username, category, level, frame).setVisible(true);
                });
            } else {
                btn.setEnabled(false);
            }

            panel.add(btn);
        }
     // Tombol Kembali
        JButton btnBack = new JButton("Kembali");
        btnBack.setFont(new Font("Tahoma", Font.PLAIN, 12));
        btnBack.setBounds(120, 500, 100, 30);
        btnBack.setBackground(yellow);
        btnBack.setFocusPainted(false);
        btnBack.setBorder(BorderFactory.createLineBorder(brown));
        btnBack.setForeground(brown);
        backgroundPanel.add(btnBack);

        btnBack.addActionListener(e -> {
            frame.dispose(); // Tutup LevelSelectionPage
            new Dashboard(username); // Tampilkan Dashboard
        });

    }

    // Custom UI tombol bulat
    static class RoundedButtonUI extends javax.swing.plaf.basic.BasicButtonUI {
        private final Color bgColor;

        public RoundedButtonUI(Color bgColor) {
            this.bgColor = bgColor;
        }

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            c.setOpaque(false);
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g;
            AbstractButton b = (AbstractButton) c;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(c.getWidth(), c.getHeight());
            g2.setColor(bgColor);
            g2.fillOval(0, 0, size, size);

            g2.setColor(Color.BLACK);
            FontMetrics fm = g2.getFontMetrics();
            String text = b.getText();
            int x = (size - fm.stringWidth(text)) / 2;
            int y = (size + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(text, x, y);
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(60, 60);
        }
    }

    private String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LevelSelectionPage("testuser", "slang").show();
        });
    }
}
