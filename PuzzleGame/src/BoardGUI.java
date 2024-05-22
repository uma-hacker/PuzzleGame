
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Puzzle Game GUI
 * Author: Ahsan Riaz (with improvements)
 */
public class BoardGUI implements ActionListener {
    JFrame fr;
    JPanel mainPanel, controlPanel;
    JButton[][] button;
    int rows;
    int cols;
    int[][] board;
    JButton resetButton;
    JLabel movesLabel, timerLabel;
    int movesCount;
    Timer timer;
    int timeElapsed;

    public BoardGUI() {
        rows = 4;
        cols = 4;
        board = new int[rows][cols];
        movesCount = 0;
        timeElapsed = 0;
        initGUI();
    }

    public void initGUI() {
        fr = new JFrame("Puzzle Game");
        mainPanel = new JPanel();
        mainPanel.setBackground(Color.white);
        mainPanel.setLayout(new GridLayout(4, 4));
        button = new JButton[rows][cols];

        shuffleBoard();
        createBoard();

        controlPanel = new JPanel();
        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetGame());
        movesLabel = new JLabel("Moves: 0");
        timerLabel = new JLabel("Time: 0s");
        controlPanel.add(resetButton);
        controlPanel.add(movesLabel);
        controlPanel.add(timerLabel);

        fr.add(mainPanel, "Center");
        fr.add(controlPanel, "South");
        fr.setVisible(true);
        fr.setSize(550, 600);
        fr.setLocationRelativeTo(null);
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        timer = new Timer(1000, e -> updateTimer());
        timer.start();
    }

    private void createBoard() {
        mainPanel.removeAll();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                button[i][j] = new JButton();
                button[i][j].setFont(new Font("Arial", Font.BOLD, 48)); // Increased font size
                button[i][j].setForeground(Color.BLACK);
                button[i][j].setBackground(Color.LIGHT_GRAY);
                button[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                button[i][j].setOpaque(true);
                button[i][j].addActionListener(this);

                int val = board[i][j];
                if (val != -1) {
                    button[i][j].setText(String.valueOf(val));
                } else {
                    button[i][j].setText("");
                    button[i][j].setBackground(Color.WHITE);
                }

                mainPanel.add(button[i][j]);
            }
        }
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void shuffleBoard() {
        Random rand = new Random();
        int[] array = new int[16];
        for (int i = 0; i < 15; i++) {
            array[i] = i + 1;
        }
        array[15] = -1;

        do {
            for (int i = 0; i < 16; i++) {
                int index = rand.nextInt(16);
                int temp = array[i];
                array[i] = array[index];
                array[index] = temp;
            }
        } while (!isSolvable(array));

        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = array[count++];
            }
        }
    }

    private boolean isSolvable(int[] array) {
        int inversions = 0;
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (array[i] > array[j] && array[i] != -1 && array[j] != -1) {
                    inversions++;
                }
            }
        }
        int blankRow = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == -1) {
                blankRow = i / 4;
                break;
            }
        }
        return (inversions + blankRow) % 2 == 0;
    }

    private boolean isWin() {
        int count = 1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] != count && board[i][j] != -1) {
                    return false;
                }
                count++;
            }
        }
        return true;
    }

    private void displayWinMsg() {
        JFrame frame = new JFrame("Game Win");
        JLabel label = new JLabel("You Solved The Puzzle", JLabel.CENTER);
        label.setFont(new Font("TimesRoman", Font.BOLD, 20));
        frame.add(label);
        frame.setLayout(new GridLayout(1, 1));
        frame.setSize(300, 300);
        frame.setBackground(Color.white);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    }

    private void resetGame() {
        shuffleBoard();
        createBoard();
        movesCount = 0;
        timeElapsed = 0;
        movesLabel.setText("Moves: 0");
        timerLabel.setText("Time: 0s");
        timer.restart();
    }

    private void updateTimer() {
        timeElapsed++;
        timerLabel.setText("Time: " + timeElapsed + "s");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (isWin()) return;

        JButton source = (JButton) ae.getSource();
        String s = source.getText();
        if (s.equals("")) return; // Empty space cannot be clicked

        int r = -1, c = -1;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (button[i][j] == source) {
                    r = i;
                    c = j;
                    break;
                }
            }
        }

        if (r != -1 && c != -1) {
            if (r + 1 < rows && board[r + 1][c] == -1) { // move down
                moveTile(r, c, r + 1, c);
            } else if (r - 1 >= 0 && board[r - 1][c] == -1) { // move up
                moveTile(r, c, r - 1, c);
            } else if (c + 1 < cols && board[r][c + 1] == -1) { // move right
                moveTile(r, c, r, c + 1);
            } else if (c - 1 >= 0 && board[r][c - 1] == -1) { // move left
                moveTile(r, c, r, c - 1);
            }
        }

        movesCount++;
        movesLabel.setText("Moves: " + movesCount);

        if (isWin()) {
            timer.stop();
            displayWinMsg();
        }
    }

    private void moveTile(int r1, int c1, int r2, int c2) {
        button[r2][c2].setText(button[r1][c1].getText());
        button[r2][c2].setFont(new Font("Arial", Font.BOLD, 48)); // Increased font size
        button[r2][c2].setBackground(Color.LIGHT_GRAY);

        button[r1][c1].setText("");
        button[r1][c1].setBackground(Color.WHITE);

        int temp = board[r1][c1];
        board[r1][c1] = board[r2][c2];
        board[r2][c2] = temp;
    }

    public static void main(String[] args) {
        new BoardGUI();
    }
}
