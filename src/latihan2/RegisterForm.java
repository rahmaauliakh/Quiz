package latihan2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterForm {

    private JFrame frame;
    private JTextField textUsername;
    private JPasswordField textPassword;
    private JPasswordField textConfirmPassword;

    public JFrame getFrame() {
        return frame;
    }

    public RegisterForm() {
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

        JLabel lblTitle = new JLabel("Form Register");
        lblTitle.setBounds(80, 80, 250, 30);
        lblTitle.setForeground(yellow);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 24));
        backgroundPanel.add(lblTitle);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(30, 160, 100, 25);
        lblUsername.setForeground(brown);
        backgroundPanel.add(lblUsername);

        textUsername = new JTextField();
        textUsername.setBounds(150, 160, 167, 25);
        backgroundPanel.add(textUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(30, 200, 100, 25);
        lblPassword.setForeground(brown);
        backgroundPanel.add(lblPassword);

        textPassword = new JPasswordField();
        textPassword.setBounds(150, 200, 167, 25);
        backgroundPanel.add(textPassword);

        JLabel lblConfirmPassword = new JLabel("Confirm:");
        lblConfirmPassword.setBounds(30, 240, 100, 25);
        lblConfirmPassword.setForeground(brown);
        backgroundPanel.add(lblConfirmPassword);

        textConfirmPassword = new JPasswordField();
        textConfirmPassword.setBounds(150, 240, 167, 25);
        backgroundPanel.add(textConfirmPassword);

        JButton btnRegister = new JButton("Register");
        btnRegister.setBounds(56, 320, 106, 36);
        styleButton(btnRegister, purple);
        backgroundPanel.add(btnRegister);

        JButton btnBack = new JButton("Login");
        btnBack.setBounds(174, 320, 106, 36);
        styleButton(btnBack, yellow);
        backgroundPanel.add(btnBack);

        JLabel lblSudahPunya = new JLabel("Sudah punya akun? Klik Login.");
        lblSudahPunya.setForeground(purple);
        lblSudahPunya.setBounds(67, 280, 250, 25);
        backgroundPanel.add(lblSudahPunya);

        // Aksi tombol
        btnRegister.addActionListener(e -> registerUser());
        btnBack.addActionListener(e -> {
            frame.dispose();
            new LoginForm().getFrame().setVisible(true);
        });
    }

    private void styleButton(JButton button, Color baseColor) {
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Tahoma", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createLineBorder(baseColor.darker()));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);

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

    private void registerUser() {
        String username = textUsername.getText().trim();
        String password = new String(textPassword.getPassword());
        String confirmPassword = new String(textConfirmPassword.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(frame, "Password tidak cocok!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement checkStmt = con.prepareStatement("SELECT * FROM users WHERE username = ?");
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(frame, "Username sudah digunakan!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            PreparedStatement pst = con.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            pst.setString(1, username);
            pst.setString(2, password);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(frame, "Registrasi berhasil!");
            frame.dispose();
            new LoginForm().getFrame().setVisible(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Gagal koneksi ke database!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                RegisterForm window = new RegisterForm();
                window.getFrame().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
