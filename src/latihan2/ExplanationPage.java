package latihan2;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ExplanationPage extends JFrame {

    public ExplanationPage(List<String> summaries, String username, int score, int total) {
        setTitle("Penjelasan Jawaban");
        setSize(360, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Gunakan background yang sama dengan QuizPage
        BackgroundPanel background = new BackgroundPanel("images/wpp6.png");
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
        setContentPane(background);

        // Panel isi konten
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("📘 Rangkuman & Penjelasan");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(70, 30, 120));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(title);

        contentPanel.add(Box.createVerticalStrut(10));
        JLabel scoreLabel = new JLabel("👤 " + username + " | Skor: " + score + " / " + total);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        scoreLabel.setForeground(Color.DARK_GRAY);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(scoreLabel);

        contentPanel.add(Box.createVerticalStrut(15));

        // Panel isi jawaban
        JPanel answerPanel = new JPanel();
        answerPanel.setLayout(new BoxLayout(answerPanel, BoxLayout.Y_AXIS));
        answerPanel.setOpaque(false);

        for (String summary : summaries) {
            JTextArea textArea = new JTextArea(summary);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);
            textArea.setFont(new Font("Serif", Font.PLAIN, 13));
            textArea.setBackground(new Color(255, 250, 255));
            textArea.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 130, 255), 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            textArea.setAlignmentX(Component.CENTER_ALIGNMENT);
            answerPanel.add(textArea);
            answerPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(answerPanel);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        contentPanel.add(scrollPane);

        JButton closeButton = new JButton("Tutup");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setBackground(new Color(120, 0, 200));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        closeButton.addActionListener(e -> dispose());

        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(closeButton);

        background.add(Box.createVerticalGlue());
        background.add(contentPanel);
        background.add(Box.createVerticalGlue());

        
        setVisible(true);

        JLabel messageLabel = new JLabel();
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        messageLabel.setForeground(Color.WHITE); // warna putih
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Pesan berdasarkan skor
        double percentage = (score * 100.0) / total;
        if (percentage >= 70.0) {
            messageLabel.setText("🎉 Congratulations! You passed. Let's move to the next level!");
            SoundUtil.playOnce("voice/congrats.wav");
        } else {
            messageLabel.setText("💪 Try again! You can do better next time!");
            SoundUtil.playOnce("voice/tryagain.wav");
        }
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(messageLabel);
 
    }

    // Reuse BackgroundPanel dari QuizPage
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
}
