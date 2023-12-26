import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class FourInARow extends JFrame {
    private Image panelImage = new ImageIcon("src/Grid.png").getImage();
    private static final int COLUMNS = 7;
    private static final int ROWS = 6;
    private int circleSize = 80; // Adjust this size as needed
    private int currentPlayer = 1; // Player 1 starts
    private int[][] grid = new int[ROWS][COLUMNS];
    private JPanel gridPanel;
    private JPanel buttonPanel;
    private boolean twoPlayers = true;
    private Random random = new Random();
    FourInARow() {
        super("Four in a Row");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(712, 680);
        createGridPanel();
        createToggleButton();
        addComponents();
        setVisible(true);
    }

    private void createGridPanel() {
        gridPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(panelImage, 0, 0, null);
                drawCircles(g);
            }
        };

        gridPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });
    }

    private void createToggleButton() {
        JButton playerButton = new JButton("Toggle Player Mode");
        playerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePlayerMode(playerButton);
            }
        });
        buttonPanel = new JPanel();
        buttonPanel.add(playerButton);
    }

    private void addComponents() {
        add(gridPanel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void drawCircles(Graphics g) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int x = col * 100 + 10;
                int y = row * 100 + 10;
                if (grid[row][col] == 1) {
                    g.setColor(Color.BLUE);
                    g.fillOval(x, y, circleSize, circleSize);
                } else if (grid[row][col] == 2) {
                    g.setColor(Color.GREEN);
                    g.fillOval(x, y, circleSize, circleSize);
                }
            }
        }
    }
    private void showWinMessage() {
        JOptionPane.showMessageDialog(this, "Player " + currentPlayer + " wins!");
    }
    private void showDrawMessage() {
        JOptionPane.showMessageDialog(this, "It's a draw!");
    }
    private void handleMouseClick(MouseEvent e) {
        if (!twoPlayers && currentPlayer == 2) {
            return;
        }
        int column = e.getX() / 100;
        dropDisc(column);
        if (checkWin()) {
            showWinMessage();
            resetGame();
        } else if (isGridFull()) {
            showDrawMessage();
            resetGame();
        } else {
            switchPlayer();
            gridPanel.repaint();
            if (!twoPlayers && currentPlayer == 2) {
                playComputerTurn();
            }
        }
    }

    private void togglePlayerMode(JButton playerButton) {
        twoPlayers = !twoPlayers;
        if (twoPlayers) {
            playerButton.setText("Toggle Player Mode");
        } else {
            playerButton.setText("Toggle Computer Mode");
            if (currentPlayer == 2) {
                playComputerTurn();
            }
        }
        resetGame();
        gridPanel.repaint();
    }
    private void dropDisc(int column) {
        for (int row = ROWS - 1; row >= 0; row--) {
            if (grid[row][column] == 0) {
                grid[row][column] = currentPlayer;
                break;
            }
        }
    }
    private void switchPlayer() {
       if (currentPlayer == 1) {
           currentPlayer = 2;
       } else {
           currentPlayer = 1;
       }
    }
    private boolean checkWin() {
        // Check horizontal
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS - 3; col++) {
                if (grid[row][col] == currentPlayer &&
                        grid[row][col + 1] == currentPlayer &&
                        grid[row][col + 2] == currentPlayer &&
                        grid[row][col + 3] == currentPlayer) {
                    return true;
                }
            }
        }
        // Check vertical
        for (int row = 0; row < ROWS - 3; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                if (grid[row][col] == currentPlayer &&
                        grid[row + 1][col] == currentPlayer &&
                        grid[row + 2][col] == currentPlayer &&
                        grid[row + 3][col] == currentPlayer) {
                    return true;
                }
            }
        }
        // Check diagonal
        for (int row = 0; row < ROWS - 3; row++) {
            for (int col = 0; col < COLUMNS - 3; col++) {
                if (grid[row][col] == currentPlayer &&
                        grid[row + 1][col + 1] == currentPlayer &&
                        grid[row + 2][col + 2] == currentPlayer &&
                        grid[row + 3][col + 3] == currentPlayer) {
                    return true;
                }
            }
        }
        // Check other diagonal
        for (int row = 0; row < ROWS - 3; row++) {
            for (int col = 3; col < COLUMNS; col++) {
                if (grid[row][col] == currentPlayer &&
                        grid[row + 1][col - 1] == currentPlayer &&
                        grid[row + 2][col - 2] == currentPlayer &&
                        grid[row + 3][col - 3] == currentPlayer) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isGridFull() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                if (grid[row][col] == 0) {
                    return false; // There's an empty slot, the grid is not full
                }
            }
        }
        return true; // The grid is full
    }

    private void resetGame() {
        // Reset the grid and player
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                grid[row][col] = 0;
            }
        }
        currentPlayer = 1;
    }
    private void playComputerTurn() {
        if (currentPlayer == 2) {
            int column = findBestMove();
            dropDisc(column);

            if (checkWin()) {
                JOptionPane.showMessageDialog(FourInARow.this, "Computer wins!");
                resetGame();
            } else if (isGridFull()) {
                JOptionPane.showMessageDialog(FourInARow.this, "It's a draw!");
                resetGame();
            } else {
                switchPlayer();
                gridPanel.repaint();
            }
        }
    }
    private int findBestMove() {
        // Check if the computer can win in the next move
        for (int col = 0; col < COLUMNS; col++) {
            int row = getEmptyRow(col);
            if (row != -1) {
                grid[row][col] = 2; // Assume computer makes this move
                if (checkWin()) {
                    grid[row][col] = 0; // Undo the move
                    return col;
                }
                grid[row][col] = 0; // Undo the move
            }
        }
        // Check if the player can win in the next move and block
        for (int col = 0; col < COLUMNS; col++) {
            int row = getEmptyRow(col);
            if (row != -1) {
                grid[row][col] = 1; // Assume player makes this move
                if (checkWin()) {
                    grid[row][col] = 0; // Undo the move
                    return col;
                }
                grid[row][col] = 0; // Undo the move
            }
        }
        // If no winning or blocking move, make a random move
        int randomColumn;
        do {
            randomColumn = random.nextInt(COLUMNS);
        } while (!isValidMove(randomColumn));

        return randomColumn;
    }
    private int getEmptyRow(int col) {
        for (int row = ROWS - 1; row >= 0; row--) {
            if (grid[row][col] == 0) {
                return row;
            }
        }
        return -1; // Column is full
    }
    private boolean isValidMove(int column) {
        return grid[0][column] == 0; // Check if the top row in the selected column is empty
    }

    public static void main(String[] args) {
        new FourInARow();
    }
}
