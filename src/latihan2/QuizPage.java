package latihan2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizPage extends JFrame {
    private List<Question> questions = new ArrayList<>();
    private List<String> answerSummaries = new ArrayList<>();
    private int currentIndex = 0;
    private int score = 0;
    private Timer timer;
    private int timeLeft = 0;
    private JLabel imageLabel = new JLabel();
    private JLabel questionLabel = new JLabel("Question");
    private JRadioButton optionA = new JRadioButton();
    private JRadioButton optionB = new JRadioButton();
    private JRadioButton optionC = new JRadioButton();
    private JRadioButton optionD = new JRadioButton();
    private ButtonGroup optionsGroup = new ButtonGroup();
    private JLabel timerLabel = new JLabel("Time: 0");
    private JLabel scoreLabel = new JLabel("Skor: 0");
    private JButton submitButton = new JButton("Submit");
    private String username;
    private String category;
    private int currentLevel;
    private JFrame parentFrame;

    /**
     * @wbp.parser.constructor
     */
    public QuizPage(String username, String category, int level, JFrame parentFrame) {
        this.username = username;
        this.category = category;
        this.currentLevel = level;
        this.parentFrame = parentFrame;
        
        loadQuestionsFromDB(category, level);


        setTitle("Quiz Page");
        setBounds(50, 50, 360, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        BackgroundPanel background = new BackgroundPanel("images/wpp6.png");
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
        setContentPane(background);
        
        SoundUtil.playLooping("voice/time.wav");

     // Panel konten soal dan pilihan
        JPanel quizPanel = new JPanel();
        quizPanel.setLayout(new BoxLayout(quizPanel, BoxLayout.Y_AXIS));
        quizPanel.setOpaque(false);
        quizPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        quizPanel.add(Box.createVerticalStrut(10));
        quizPanel.add(questionLabel);
        quizPanel.add(Box.createVerticalStrut(10));
        quizPanel.add(imageLabel);
        quizPanel.add(Box.createVerticalStrut(10));
        quizPanel.add(wrapOption(optionA));
        quizPanel.add(wrapOption(optionB));
        quizPanel.add(wrapOption(optionC));
        quizPanel.add(wrapOption(optionD));

        // Panel bawah (timer, skor, tombol)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(scoreLabel);
        bottomPanel.add(timerLabel);
        bottomPanel.add(submitButton);

        // Tambahkan ke background agar seluruhnya ditengah
        background.add(Box.createVerticalGlue());
        background.add(quizPanel);
        background.add(Box.createVerticalStrut(10));
        background.add(bottomPanel);
        background.add(Box.createVerticalGlue());
        

        if (!questions.isEmpty()) {
            showQuestion(currentIndex);
            startTimer();
        } else {
            JOptionPane.showMessageDialog(this, "Tidak ada soal ditemukan di database!");
            submitButton.setEnabled(false);
        }

        submitButton.addActionListener(e -> {
            timer.stop();
            checkAnswer();
            showExplanation(currentIndex);

            currentIndex++;
            if (currentIndex < questions.size()) {
                showQuestion(currentIndex);
                resetTimer();
            } else {
                showResult();
            }
        });
        
    }
    
    private JPanel wrapOption(JRadioButton option) {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrapper.setOpaque(false);
        wrapper.add(option);
        wrapper.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 0)); // padding antar opsi
        return wrapper;
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


    public QuizPage(String string, int level) {
        // Constructor tambahan jika diperlukan
    }

    private void loadQuestionsFromDB(String category, int level) {
        String url = "jdbc:mysql://localhost:3306/questions";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT * FROM quiz_questions WHERE category = ? AND level = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, category);
            stmt.setInt(2, level);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Question q = new Question(
                    rs.getInt("id"),
                    rs.getString("category"),
                    rs.getInt("level"),
                    rs.getString("question"),
                    rs.getString("option_a"),
                    rs.getString("option_b"),
                    rs.getString("option_c"),
                    rs.getString("option_d"),
                    rs.getString("correct_answer").charAt(0),
                    rs.getInt("time_limit"),
                    rs.getString("explanation"),
                    rs.getString("image_path")
                );
                questions.add(q);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat soal dari database.");
        }
    }


    private void showQuestion(int index) {
        Question q = questions.get(index);
        optionsGroup.clearSelection();
        
        questionLabel.setText("<html><body style='width: 500px'>" + (index + 1) + ". " + q.getQuestion() + "</body></html>");
        optionA.setText("A. " + q.getOptionA());
        optionB.setText("B. " + q.getOptionB());
        optionC.setText("C. " + q.getOptionC());
        optionD.setText("D. " + q.getOptionD());
        
        optionA.setOpaque(false);
        optionB.setOpaque(false);
        optionC.setOpaque(false);
        optionD.setOpaque(false);
        optionsGroup.add(optionA);
        optionsGroup.add(optionB);
        optionsGroup.add(optionC);
        optionsGroup.add(optionD);


        timeLeft = q.getTimeLimit();
        timerLabel.setText("Time: " + timeLeft);
        
        String imagePath = q.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
        	ImageIcon imageIcon = new ImageIcon(imagePath); 
            if (imageIcon.getIconWidth() > 0) {
                Image img = imageIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
            } else {
                System.out.println("Gagal memuat gambar: " + imagePath);
                imageLabel.setIcon(null);
            }
        } else {
            System.out.println("Path gambar kosong atau null");
            imageLabel.setIcon(null);
        }

        
     // Reset semua opsi
        optionsGroup.clearSelection();
        resetOptionColors();

        optionA.setText("A. " + q.getOptionA());
        optionB.setText("B. " + q.getOptionB());
        optionC.setText("C. " + q.getOptionC());
        optionD.setText("D. " + q.getOptionD());

        // Reset warna background
        optionA.setBackground(null);
        optionB.setBackground(null);
        optionC.setBackground(null);
        optionD.setBackground(null);


    }

    private void checkAnswer() {
        Question q = questions.get(currentIndex);
        char selected = ' ';

        if (optionA.isSelected()) selected = 'A';
        else if (optionB.isSelected()) selected = 'B';
        else if (optionC.isSelected()) selected = 'C';
        else if (optionD.isSelected()) selected = 'D';



        // Skor
        if (selected == q.getCorrectAnswer()) {
            score++;
            scoreLabel.setText("Skor: " + score);
            SoundUtil.playOnce("voice/correct.wav");
        } else {
            SoundUtil.playOnce("voice/incorrect.wav");
        }

    }


    private void resetOptionColors() {
        optionA.setForeground(Color.BLACK);
        optionB.setForeground(Color.BLACK);
        optionC.setForeground(Color.BLACK);
        optionD.setForeground(Color.BLACK);

        optionA.setOpaque(false);
        optionB.setOpaque(false);
        optionC.setOpaque(false);
        optionD.setOpaque(false);
    }


    private JRadioButton getSelectedOptionButton(char option) {
        switch (option) {
            case 'A': return optionA;
            case 'B': return optionB;
            case 'C': return optionC;
            case 'D': return optionD;
            default: return null;
        }
    }
   

    private void showExplanation(int index) {
        Question q = questions.get(index);
        char selected = ' ';

        if (optionA.isSelected()) selected = 'A';
        else if (optionB.isSelected()) selected = 'B';
        else if (optionC.isSelected()) selected = 'C';
        else if (optionD.isSelected()) selected = 'D';

        String summary = (index + 1) + ". " + q.getQuestion() + "\n"
                + "Jawaban kamu: " + (selected != ' ' ? selected : "Tidak menjawab") + "\n"
                + "Jawaban benar: " + q.getCorrectAnswer() + "\n"
                + "Penjelasan: " + q.getExplanation() + "\n";

        answerSummaries.add(summary);
    }

    private void showResult() {
        saveToLeaderboard();

        if (score >= (questions.size() * 0.7)) {
            unlockNextLevel();
        }

        new LeaderboardPage(username).setVisible(true);
        new ExplanationPage(answerSummaries, username, score, questions.size()); // <-- buka halaman penjelasan

        dispose();
        SoundUtil.stopLooping(); // stop suara background looping

    }
    
    private void unlockNextLevel() {
        int nextLevel = currentLevel + 1;

        String url = "jdbc:mysql://localhost:3306/questions";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String checkSql = "SELECT * FROM level_progress WHERE username = ? AND category = ? AND level = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            checkStmt.setString(2, category);
            checkStmt.setInt(3, nextLevel);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                String insertSql = "INSERT INTO level_progress (username, category, level) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, username);
                insertStmt.setString(2, category);
                insertStmt.setInt(3, nextLevel);
                insertStmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void startTimer() {
        timerLabel.setText("Time: " + timeLeft);

        timer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time: " + timeLeft);

            if (timeLeft <= 0) {
                timer.stop();
                checkAnswer();
                showExplanation(currentIndex);

                currentIndex++;
                if (currentIndex < questions.size()) {
                    showQuestion(currentIndex);
                    resetTimer();
                } else {
                    showResult();
                }
            }
        });
        timer.start();
    }

    private void resetTimer() {
        if (timer != null) {
            timer.stop();
        }
        startTimer();
    }

    

    private static class Question {
        private int id;
        private String category;
        private int level;
        private String question, optionA, optionB, optionC, optionD;
        private char correctAnswer;
        private int timeLimit;
        private String explanation;
        private String imagePath; 

        public Question(int id, String category, int level, String question, String optionA, String optionB,String optionC, String optionD, char correctAnswer, int timeLimit, String explanation, String imagePath) {
            this.id = id;
            this.category = category;
            this.level = level;
            this.question = question;
            this.optionA = optionA;
            this.optionB = optionB;
            this.optionC = optionC;
            this.optionD = optionD;
            this.correctAnswer = correctAnswer;
            this.timeLimit = timeLimit;
            this.explanation = explanation;
            this.imagePath = imagePath;
        }

        public String getQuestion() { return question; }
        public String getOptionA() { return optionA; }
        public String getOptionB() { return optionB; }
        public String getOptionC() { return optionC; }
        public String getOptionD() { return optionD; }
        public char getCorrectAnswer() { return correctAnswer; }
        public int getTimeLimit() { return timeLimit; }
        public String getExplanation() { return explanation; }
        public String getImagePath() { return imagePath; }
    }
    
    private void saveToLeaderboard() {
        String url = "jdbc:mysql://localhost:3306/questions";
        String user = "root";
        String password = "root";

       try  (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "INSERT INTO leaderboard (username, category, level, score) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, category);
            stmt.setInt(3, currentLevel);
            stmt.setInt(4, score);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Contoh username dan level untuk pengujian
            String username = "testuser";
            int level = 1;
            String category = "slang";

            // Karena tidak ada SlangPage saat ini, parentFrame bisa null
            new QuizPage(username, category, level, null).setVisible(true);
        });
    }



}
