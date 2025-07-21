package latihan2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LeaderboardPage extends JFrame {

    private String username;

    public LeaderboardPage(String username) {
        this.username = username;

        setTitle("Leaderboard");
        setSize(360, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Gunakan background image
        BackgroundPanel background = new BackgroundPanel("images/wpp6.png");
        background.setLayout(new BorderLayout());
        setContentPane(background);

        JLabel title = new JLabel("🏆 LEADERBOARD", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(new Color(120, 0, 200));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        background.add(title, BorderLayout.NORTH);

        JPanel leaderboardPanel = new JPanel();
        leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.Y_AXIS));
        leaderboardPanel.setOpaque(false);
        leaderboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(leaderboardPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        background.add(scrollPane, BorderLayout.CENTER);

        loadLeaderboard(leaderboardPanel);

        JButton backButton = new JButton("Kembali");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(120, 0, 200));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            dispose();
            new LevelSelectionPage(username, getCurrentCategory()).show();
        });


        leaderboardPanel.add(Box.createVerticalStrut(20));
        leaderboardPanel.add(backButton);
    }
    
    private String getCurrentCategory() {
        // Ambil 1 kategori terbaru dari leaderboard si user ini
        String latestCategory = "slang"; // default
        String url = "jdbc:mysql://localhost:3306/questions";
        String user = "root";
        String password = "root";
        String sql = "SELECT category FROM leaderboard WHERE username = ? ORDER BY date DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                latestCategory = rs.getString("category");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return latestCategory;
    }

    private void loadLeaderboard(JPanel leaderboardPanel) {
        String url = "jdbc:mysql://localhost:3306/questions";
        String user = "root";
        String password = "root";
        String sql = "SELECT * FROM leaderboard ORDER BY score DESC, date ASC";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int rank = 1;

            while (rs.next()) {
                String uname = rs.getString("username");
                String category = rs.getString("category");
                int level = rs.getInt("level");
                int score = rs.getInt("score");

                JPanel item = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(getBackground());
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };

                item.setOpaque(false);
                item.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
                item.setBackground(getRankColor(rank));

                JLabel rankLabel = new JLabel("#" + rank, SwingConstants.CENTER);
                rankLabel.setFont(new Font("Arial", Font.BOLD, 20));
                rankLabel.setPreferredSize(new Dimension(60, 60));
                rankLabel.setForeground(Color.WHITE);

                JLabel info = new JLabel("<html><b>" + uname + "</b> | " + category + " - Level " + level +
                        "<br/>Score: " + score + "</html>");
                info.setFont(new Font("Arial", Font.PLAIN, 16));
                info.setForeground(Color.WHITE);

                item.add(rankLabel, BorderLayout.WEST);
                item.add(info, BorderLayout.CENTER);

                leaderboardPanel.add(Box.createVerticalStrut(10));
                leaderboardPanel.add(item);
                rank++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat leaderboard dari database.");
        }
    }

    private Color getRankColor(int rank) {
        switch (rank) {
            case 1: return new Color(163, 106, 255); // Ungu
            case 2: return new Color(255, 217, 61);  // Kuning
            case 3: return new Color(184, 107, 68);  // Coklat
            default: return new Color(120, 120, 120); // Abu gelap
        }
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
                System.out.println("Gagal memuat gambar latar: " + imagePath);
            }
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null)
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LeaderboardPage("guest").setVisible(true));
    }
}
