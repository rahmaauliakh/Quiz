package latihan2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm {

    private JFrame frame;
    private JTextField textUsername;
    private JPasswordField textPassword;

    public JFrame getFrame() {
        return frame;
    }

    public LoginForm() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(50, 50, 360, 640);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // Panel background custom
        JPanel backgroundPanel = new JPanel() {
            Image background = new ImageIcon("images/wpp3.png").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };

        backgroundPanel.setLayout(null);
        frame.setContentPane(backgroundPanel);

        // Warna tema
        Color yellow = Color.decode("#ffc800");
        Color purple = Color.decode("#7a37e6");
        Color brown = Color.decode("#3a301e");

        JLabel lblTitle = new JLabel("Form Login");
        lblTitle.setBounds(100, 100, 180, 30);
        lblTitle.setForeground(yellow);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 24));
        backgroundPanel.add(lblTitle);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(30, 180, 100, 25);
        lblUsername.setForeground(brown);
        backgroundPanel.add(lblUsername);

        textUsername = new JTextField();
        textUsername.setBounds(150, 180, 167, 25);
        textUsername.setBackground(Color.WHITE);
        backgroundPanel.add(textUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(30, 220, 100, 25);
        lblPassword.setForeground(brown);
        backgroundPanel.add(lblPassword);

        textPassword = new JPasswordField();
        textPassword.setBounds(150, 220, 167, 25);
        textPassword.setBackground(Color.WHITE);
        backgroundPanel.add(textPassword);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(56, 332, 106, 36);
        styleButton(btnLogin, purple);
        backgroundPanel.add(btnLogin);

        JButton btnRegister = new JButton("Register");
        btnRegister.setBounds(174, 332, 106, 36);
        styleButton(btnRegister, yellow);
        backgroundPanel.add(btnRegister);

        JLabel lblBelumPunya = new JLabel("Belum punya akun? Klik Register.");
        lblBelumPunya.setForeground(purple);
        lblBelumPunya.setBounds(67, 279, 250, 25);
        backgroundPanel.add(lblBelumPunya);

        // Aksi tombol
        btnLogin.addActionListener(e -> loginUser());
        btnRegister.addActionListener(e -> {
            frame.dispose();
            new RegisterForm().getFrame().setVisible(true);
        });
    }

    private void styleButton(JButton button, Color baseColor) {
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Tahoma", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createLineBorder(baseColor.darker()));

        // Efek hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });
    }

    private void loginUser() {
        String username = textUsername.getText().trim();
        String password = new String(textPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Username dan Password harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(frame, "Login berhasil!");
                frame.dispose();
                Dashboard dashboard = new Dashboard(username);
                dashboard.getFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(frame, "Username atau Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Koneksi ke database gagal!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LoginForm window = new LoginForm();
                window.getFrame().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
