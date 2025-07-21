package latihan2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Dashboard {

    private JFrame frame;

    public JFrame getFrame() {
        return frame;
    }

    public Dashboard(String username) {
        initialize(username);
    }

    private void initialize(String username) {
        frame = new JFrame("Dashboard");
        frame.setBounds(50, 50, 360, 640);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // Panel background
        JPanel backgroundPanel = new JPanel() {
            Image background = new ImageIcon("images/wpp6.png").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background != null) {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        
        SoundUtil.playOnce("voice/audio2.wav");
        backgroundPanel.setLayout(null);
        frame.setContentPane(backgroundPanel);
        SoundUtil.playOnce("voice/audio1.wav");

        // Warna tema
        Color purple = Color.decode("#7a37e6");
        Color yellow = Color.decode("#ffc800");
        Color brown = Color.decode("#3a301e");

        // Label Welcome
        JLabel lblWelcome = new JLabel("Welcome, " + username + "!");
        lblWelcome.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblWelcome.setBounds(40, 145, 300, 30);
        lblWelcome.setForeground(yellow);
        backgroundPanel.add(lblWelcome);

        JLabel lblInfo = new JLabel("Yuk belajar sambil seru-seruan bareng!");
        lblInfo.setBounds(62, 186, 250, 20);
        lblInfo.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblInfo.setForeground(brown);
        backgroundPanel.add(lblInfo);

     // Tombol Slang
        JButton btnSlang = new JButton("Slang");
        btnSlang.setBounds(80, 230, 180, 40);
        styleButton(btnSlang, purple);
        btnSlang.addActionListener(e -> {
            SoundUtil.stopOnce(); // <<< Hentikan suara dulu
            new LevelSelectionPage(username, "slang").show();
            frame.dispose();
        });
        backgroundPanel.add(btnSlang);

        // Tombol Vocabulary
        JButton btnVocabulary = new JButton("Vocabulary");
        btnVocabulary.setBounds(80, 281, 180, 40);
        styleButton(btnVocabulary, purple);
        btnVocabulary.addActionListener(e -> {
            SoundUtil.stopOnce();
            new LevelSelectionPage(username, "vocabulary").show();
            frame.dispose();
        });
        backgroundPanel.add(btnVocabulary);

        // Tombol Verb
        JButton btnVerb = new JButton("Verb 1/2/3");
        btnVerb.setBounds(80, 332, 180, 40);
        styleButton(btnVerb, purple);
        btnVerb.addActionListener(e -> {
            SoundUtil.stopOnce();
            new LevelSelectionPage(username, "verb").show();
            frame.dispose();
        });
        backgroundPanel.add(btnVerb);

        // Tombol Phrases
        JButton btnPhrases = new JButton("Phrases");
        btnPhrases.setBounds(80, 383, 180, 40);
        styleButton(btnPhrases, purple);
        btnPhrases.addActionListener(e -> {
            SoundUtil.stopOnce();
            new LevelSelectionPage(username, "phrases").show();
            frame.dispose();
        });
        backgroundPanel.add(btnPhrases);

        // Tombol Logout
        JButton btnLogout = new JButton("Log out");
        btnLogout.setBounds(234, 558, 89, 23);
        styleButton(btnLogout, yellow);
        btnLogout.setFont(new Font("Tahoma", Font.PLAIN, 12));
        btnLogout.addActionListener(e -> {
            SoundUtil.stopOnce();
            frame.dispose();
            new LoginForm().getFrame().setVisible(true);
        });
        backgroundPanel.add(btnLogout);


        frame.setVisible(true);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Tahoma", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(bgColor.darker()));
        button.setOpaque(true);
        button.setContentAreaFilled(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Dashboard dashboard = new Dashboard("TestUser"); // bisa ganti jadi nama user apa aja
            dashboard.getFrame().setVisible(true);
        });
    }
}
