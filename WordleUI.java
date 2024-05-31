package com.example.demo1;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;


public class WordleUI implements ActionListener {
    private JTextField text = new JTextField();
    int played = 0;
    Boolean used = false;
    double winPercentage = 0.0;
    int currentStreak = 0;
    int maxStreak = 0;
    int totalWins = 0;
    private JFrame jframe;
    private List<WordPanel> panelList;
    private LastPanel lastPanel;
    private int colCount = 0;
    private String wordleWord;

    public void createNew(){
        jframe.getContentPane().removeAll();
        wordleWord = fetchWord().trim().toUpperCase();
        jframe.setLayout(new GridLayout(6, 1));
        panelList.clear();
        jframe.setVisible(true);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(800, 800);

        for (int i = 0; i < 5; i++) {
            panelList.add(new WordPanel());
            jframe.add(panelList.get(i));
        }

        lastPanel = new LastPanel();
        lastPanel.getSubmit().addActionListener(this);
        jframe.add(lastPanel);
        jframe.revalidate();
        jframe.repaint();
    }

    public WordleUI() {
        wordleWord = fetchWord().trim().toUpperCase();
        jframe = new JFrame("Wordle game");
        jframe.setLayout(new GridLayout(6, 1));

        jframe.setVisible(true);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(800, 800);

        panelList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            panelList.add(new WordPanel());
            jframe.add(panelList.get(i));
        }

        lastPanel = new LastPanel();
        lastPanel.getSubmit().addActionListener(this);
        jframe.add(lastPanel);
        jframe.setLocationRelativeTo(null);
        jframe.revalidate();

    }

    public static void main(String[] args) {
        new WordleUI();

    }

    private void throwWarning(String message) {
        JOptionPane.showMessageDialog(jframe, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public void resetGame(){
        colCount = 0;
        played = 0;
        winPercentage = 0.0;
        currentStreak = 0;
        maxStreak = 0;
        totalWins = 0;
        createNew();
    }

    public void resetBoard() {
        colCount = 0;
        createNew();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        String userWord = lastPanel.getTextField().getText().trim();
        if ("OK".equals(action) && userWord.length() == 5) {
            lastPanel.clearTextField();

            if (isWordleWordEqualsTo(userWord)) {
                totalWins++;
                played++;
                currentStreak++;
                maxStreak = currentStreak > maxStreak ? currentStreak : maxStreak;
                winPercentage = ((double) totalWins / (double) played) * 100;
                String message = String.format("You Win!!!\nPlayed: %d\nWin %%: %.2f\nCurrent Streak: %d\nMax Streak: %d", played, winPercentage, currentStreak, maxStreak);
                JOptionPane.showMessageDialog(null, message, "Congrats", JOptionPane.INFORMATION_MESSAGE);
                resetBoard();
            }
            else{
                colCount++;
                if (colCount > 4) {
                    played++;
                    currentStreak = 0;
                    winPercentage = ((double) totalWins / (double) played) * 100;
                    String message = String.format("You loose!!!\nPlayed: %d\nWin %%: %.2f\nCurrent Streak: %d\nMax Streak: %d", played, winPercentage, currentStreak, maxStreak);
                    JOptionPane.showMessageDialog(null, message, "Oops", 1);
                    resetBoard();
                }
            }
        }
        else if("OK".equals(action)){
            throwWarning("Enter a 5 letter word!");
        }

    }

    private boolean isWordleWordEqualsTo(String userWord) {
        String[] userWordLetterArray = userWord.toUpperCase().split("");
        List<String> wordleLetters = Arrays.asList(getWordleWord().split(""));
        List<Integer> wordMatchList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            if (wordleLetters.contains(userWordLetterArray[i])) {
                used = true;
                if (wordleLetters.get(i).equals(userWordLetterArray[i])) {

                    getCurrentActivePanel().updatePanel(userWordLetterArray[i], i, Color.GREEN);
                    wordMatchList.add(1);
                    lastPanel.greenButton(userWordLetterArray[i]);
                    for(int j = 0; j < i+1; j++){
                        if((wordMatchList.get(j) == 2) && (Objects.equals(userWordLetterArray[j], userWordLetterArray[i]))){
                            getCurrentActivePanel().updatePanel(userWordLetterArray[j], j, Color.GRAY);
                            lastPanel.grayButton(userWordLetterArray[i]);
                        }
                    }
                } else {
                    getCurrentActivePanel().updatePanel(userWordLetterArray[i], i, Color.YELLOW);
                    wordMatchList.add(2);
                    lastPanel.yellowButton(userWordLetterArray[i]);
                    for(int j = 0; j < i+1; j++){
                        if((wordMatchList.get(j) == 1) && (Objects.equals(userWordLetterArray[j], userWordLetterArray[i]))){
                            getCurrentActivePanel().updatePanel(userWordLetterArray[i], i, Color.GRAY);
                            lastPanel.grayButton(userWordLetterArray[i]);
                        }
                    }
                }
                used = false;
            } else {
                getCurrentActivePanel().updatePanel(userWordLetterArray[i], i, Color.GRAY);
                wordMatchList.add(3);
                lastPanel.disableUnusedLetterButtons(userWordLetterArray[i]);
            }
        }
        return !(wordMatchList.contains(2) || wordMatchList.contains(3));
    }

    public String getWordleWord() {
        return wordleWord;
    }

    public WordPanel getCurrentActivePanel() {
        return panelList.get(colCount);
    }

    private String fetchWord() {
        String fileName = "C:\\Users\\arjun\\OneDrive\\Desktop\\CSCE314\\Eclipse\\wordleWords.txt";
        List<String> words = new ArrayList<>();
        Random random = new Random();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] wordArray = line.split("\\s+");
                for (String word : wordArray) {
                    words.add(word.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int index = random.nextInt(words.size());
        String word = words.remove(index);
        System.out.println(word);
        return word;
    }


    public class LastPanel extends JPanel {
        private JTextField text;
        private JButton submit;
        private JButton redo;
        private JButton clear;
        private JButton[] letterButtons;

        LastPanel() {
            this.setLayout(new GridLayout(4, 7));
            text = new JTextField();
            this.add(text);
            String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
            letterButtons = new JButton[letters.length];
            for (int i = 0; i < letters.length; i++) {
                JButton letterButton = new JButton(letters[i]);
                letterButton.setBackground(Color.WHITE);
                letterButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        text.setText(text.getText() + letterButton.getText());
                    }
                });
                this.add(letterButton);
                letterButtons[i] = letterButton;
            }

            submit = new JButton("OK");
            redo = new JButton("RESET");
            clear = new JButton("CLEAR");
            redo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    text.setText("");
                    resetGame();
                    winPercentage = 0.0;
                    currentStreak = 0;
                    maxStreak = 0;
                    totalWins = 0;
                }
            });
            clear.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    text.setText("");
                }
            });
            this.add(submit);
            this.add(redo);
            this.add(clear);
            this.setVisible(true);
        }

        public JTextField getTextField() {
            return text;
        }

        public JButton getSubmit() {
            return submit;
        }

        public void clearTextField() {
            text.setText("");
        }

        public void disableUnusedLetterButtons(String letter) {
            for (int i = 0; i < letterButtons.length; i++) {
                if(letter.equals(letterButtons[i].getText())){
                    if(!used){
                        letterButtons[i].setEnabled(false);
                        letterButtons[i].setBackground(Color.GRAY);
                    }

                }
            }
        }

        public void greenButton(String letter) {
            for (int i = 0; i < letterButtons.length; i++) {
                if(letter.equals(letterButtons[i].getText())){
                    letterButtons[i].setBackground(Color.GREEN);
                }
            }
        }

        public void yellowButton(String letter) {
            for (int i = 0; i < letterButtons.length; i++) {
                if(letter.equals(letterButtons[i].getText())){
                    letterButtons[i].setBackground(Color.YELLOW);
                }
            }
        }

        public void grayButton(String letter) {
            for (int i = 0; i < letterButtons.length; i++) {
                if(letter.equals(letterButtons[i].getText())){
                    letterButtons[i].setBackground(Color.GRAY);
                }
            }
        }
    }

    class WordPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private JLabel[] charColumns = new JLabel[6];
        public WordPanel() {
            this.setLayout(new GridLayout(1, 5));
            this.setSize(300, 60);
            Border blackline = BorderFactory.createLineBorder(Color.lightGray);
            for (int i = 0; i < 5; i++) {
                charColumns[i] = new JLabel("", JLabel.CENTER);
                charColumns[i].setOpaque(true);
                charColumns[i].setBorder(blackline);
                this.add(charColumns[i]);
            }
        }
        public void updatePanel(String inputWord, int position, Color color) {
            charColumns[position].setBackground(color);
            charColumns[position].setText(inputWord);
        }
    }

}
