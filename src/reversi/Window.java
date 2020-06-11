package src.reversi;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class Window extends JFrame {

    public ImageIcon player_white = new ImageIcon(getClass().getResource("/res/PlayerWhite.png"));
    public ImageIcon player_black = new ImageIcon(getClass().getResource("/res/PlayerBlack.png"));
    public ImageIcon possible_move = new ImageIcon(getClass().getResource("/res/PossibleMove.png"));

    public static JLabel label_score_white = new JLabel("WHITE: 2", SwingConstants.CENTER);
    public static JLabel label_score_black = new JLabel("BLACK: 2", SwingConstants.CENTER);
    public static JLabel label_color = new JLabel("YOU ARE ", SwingConstants.CENTER);

    private static final int BOARD_SIZE = 8;
    public JButton[][] buttons = new JButton[BOARD_SIZE][BOARD_SIZE];
    public Image white;
    private JPanel board_panel;
    private JPanel score_panel;
    private int my_color;
    private final int WHITE = 1;
    private final int BLACK = -1;
    private int[] moves = new int[2];
    private boolean moved = false;
    private boolean new_game;

    /**
     * Constructor for Window GUI
     * @param color BLACK or WHITE
     */
    public Window(int color) {
        my_color = color;
        init();
        setSize(650, 680);
        setPreferredSize(new Dimension(650, 680));
        setTitle("REVERSI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Initialize board using JButtons in grid layout and JPanel for score
     */
    public void init() {
        board_panel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));

        for (int i = 0; i < BOARD_SIZE; ++i)
            for (int j = 0; j < BOARD_SIZE; ++j) {
                JButton button = new JButton();
                button.setSize(71, 71);
                button.setBackground(Color.GREEN);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                button.addActionListener(new Action());
                board_panel.add(button);
                buttons[i][j] = button;
            }
        score_panel = new JPanel(new GridLayout(1, 3));
        score_panel.setPreferredSize(new Dimension(650, 40));
        label_score_black.setFont(new Font("Serif", Font.PLAIN, 20));
        label_score_white.setFont(new Font("Serif", Font.PLAIN, 20));
        label_color.setFont(new Font("Serif", Font.PLAIN, 20));
        if (my_color == WHITE)
            label_color.setText("YOU ARE WHITE!");
        else
            label_color.setText("YOU ARE BLACK!");
        score_panel.add(label_score_black, BorderLayout.CENTER);
        score_panel.add(label_score_white, BorderLayout.CENTER);
        score_panel.add(label_color, BorderLayout.CENTER);
        add(score_panel, BorderLayout.NORTH);
        add(board_panel, BorderLayout.CENTER);
    }

    /**
     * Special ActionListener to identify which button have been pressed
     */
    public class Action implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < BOARD_SIZE; ++i)
                for (int j = 0; j < BOARD_SIZE; ++j)
                    if (buttons[i][j].equals(e.getSource()) && !moved) {
                        moves[0] = i;
                        moves[1] = j;
                        moved = true;
                    }
        }
    }

    /**
     * Update score panel with new score
     * @param points
     */
    public void drawScore(int[] points) {
        label_score_black.setText("Black: " + points[0]);
        label_score_white.setText("White: " + points[1]);
    }

    /**
     * According to values in board draw coresponding icons to buttons and turn them off
     * @param board
     * @param points
     */
    public void displayBoard(int[][] board, int[] points) {
        int player;
        for (int i = 0; i < BOARD_SIZE; ++i)
            for (int j = 0; j < BOARD_SIZE; ++j) {
                player = board[i][j];
                buttons[i][j].setEnabled(false);
                buttons[i][j].setIcon(null);
                if (player == WHITE) {
                    buttons[i][j].setIcon(player_white);
                    buttons[i][j].setDisabledIcon(player_white);
                }
                if (player == BLACK) {
                    buttons[i][j].setIcon(player_black);
                    buttons[i][j].setDisabledIcon(player_black);
                }
            }
        drawScore(points);
    }

    /**
     * Given possible moves set icons to coresponding buttons and enable them
     * @param possible_moves
     */
    public void displayPossibleMoves(int[][] possible_moves) {
        for (int i = 0; i < BOARD_SIZE; ++i)
            for (int j = 0; j < BOARD_SIZE; ++j) {
                if (possible_moves[i][j] == 1) {
                    buttons[i][j].setEnabled(true);
                    buttons[i][j].setIcon(possible_move);
                }
            }
        moved = false;
    }

    /**
     * Display winner message window and ask player if he wants to play again
     * @param winner
     * @return true if wants to play again
     *         false otherwise
     */
    public boolean displayWinner(int winner) {
        String message;
        if (winner == my_color)
            message = "YOU WIN! CONGRATULATIONS!";
        else if (winner == -my_color)
            message = "YOU LOOSE! BETTER LUCK NEXT TIME!";
        else
            message = "IT'S A DRAW!";
        int option = JOptionPane.showConfirmDialog(this, "DO YOU WANT TO PLAY AGAIN?", message,
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION)
            new_game = true;
        else if (option == JOptionPane.NO_OPTION || option == JOptionPane.CANCEL_OPTION
                || option == JOptionPane.CLOSED_OPTION)
            new_game = false;
        System.out.println("PLAY AGAIN: " + new_game);
        return new_game;
    }

    /**
     * wait for player to make a move
     * @return
     */
    public int[] getMove() {
        while (!moved) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        moved = false;
        return moves;
    }

}
