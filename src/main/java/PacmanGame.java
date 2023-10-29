import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PacmanGame extends JPanel implements KeyListener {
    private int pacManX, pacManY; // Позиція Пакмана
    private int[] ghostX, ghostY; // Позиції привидів
    private boolean[] ghostMoving; // Чи привиди рухаються
    private int score; // Очки
    private int foodCount; // Кількість залишеної їжі

    private final int PACMAN_SPEED = 3; // Швидкість Пакмана
    private final int GHOST_SPEED = 2; // Швидкість привидів

    private final int CELL_SIZE = 30; // Розмір однієї клітинки
    private final int MAZE_WIDTH = 10; // Ширина лабіринту
    private final int MAZE_HEIGHT = 10; // Висота лабіринту
    private final int[][] maze = {
            // Лабіринт з 1 - стіна, 0 - порожнє місце, 2 - їжа
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 0, 1, 1, 0, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 0, 1, 1, 0, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 0, 1, 1, 0, 1, 1},
            {1, 0, 0, 1, 0, 1, 0, 0, 0, 1},
            {1, 0, 1, 0, 0, 0, 0, 1, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    private Color[] ghostColors; // Кольори привидів
    private int[] ghostDirections; // Напрямки руху привидів
    private boolean[] ghostSeesPacMan; // Чи бачать привиди Пакмана

    public PacmanGame() {
        pacManX = 1;
        pacManY = 1;
        ghostX = new int[3];
        ghostY = new int[3];
        ghostMoving = new boolean[3];
        for (int i = 0; i < 3; i++) {
            ghostX[i] = MAZE_WIDTH / 2;
            ghostY[i] = MAZE_HEIGHT / 2;
            ghostMoving[i] = true;
        }
        score = 0;
        foodCount = 0; // Початкова кількість зібраної їжі
        addKeyListener(this);
        setFocusable(true);
        setPreferredSize(new Dimension(CELL_SIZE * MAZE_WIDTH, CELL_SIZE * MAZE_HEIGHT));

        // Генерація виподкової позиції для їжі
        int foodX, foodY;
        do {
            foodX = (int) (Math.random() * MAZE_WIDTH);
            foodY = (int) (Math.random() * MAZE_HEIGHT);
        } while (maze[foodY][foodX] != 0);

        maze[foodY][foodX] = 2;

        ghostColors = new Color[3];
        ghostColors[0] = Color.RED;
        ghostColors[1] = Color.BLUE;
        ghostColors[2] = Color.ORANGE;

        ghostDirections = new int[3];
        ghostDirections[0] = 1; // Перший привид рухається вправо
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Малювання лабіринту
        for (int i = 0; i < MAZE_HEIGHT; i++) {
            for (int j = 0; j < MAZE_WIDTH; j++) {
                if (maze[i][j] == 1) {
                    g.setColor(Color.BLUE);
                    g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
        // Малювання Pacman
        g.setColor(Color.YELLOW);
        g.fillArc(pacManX * CELL_SIZE, pacManY * CELL_SIZE, CELL_SIZE, CELL_SIZE, 45, 270);
        // Отрисовка привидений
        for (int i = 0; i < ghostX.length; i++) {
            g.setColor(ghostColors[i]);
            g.fillOval(ghostX[i] * CELL_SIZE, ghostY[i] * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        //Малювання їжі
        g.setColor(Color.GREEN);
        for (int i = 0; i < MAZE_HEIGHT; i++) {
            for (int j = 0; j < MAZE_WIDTH; j++) {
                if (maze[i][j] == 2) {
                    g.fillOval(j * CELL_SIZE + CELL_SIZE / 2 - 5, i * CELL_SIZE + CELL_SIZE / 2 - 5, 10, 10);
                }
            }
        }
        // Малювання рахунку
        g.setColor(Color.BLACK);
        g.drawString("Очки: " + score, 10, 20);
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP) {
            movePacman(0, -1);
        } else if (key == KeyEvent.VK_DOWN) {
            movePacman(0, 1);
        } else if (key == KeyEvent.VK_LEFT) {
            movePacman(-1, 0);
        } else if (key == KeyEvent.VK_RIGHT) {
            movePacman(1, 0);
        }
    }

    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}

    public void movePacman(int dx, int dy) {
        int newX = pacManX + dx;
        int newY = pacManY + dy;
        if (newX >= 0 && newX < MAZE_WIDTH && newY >= 0 && newY < MAZE_HEIGHT && maze[newY][newX] != 1) {
            pacManX = newX;
            pacManY = newY;

            if (maze[newY][newX] == 2) {
                score += 10;
                foodCount--;
                maze[newY][newX] = 0;

                int foodX, foodY;
                do {
                    foodX = (int) (Math.random() * MAZE_WIDTH);
                    foodY = (int) (Math.random() * MAZE_HEIGHT);
                } while (maze[foodY][foodX] != 0);

                maze[foodY][foodX] = 2;
            }
        }
        repaint();
    }

    public void moveGhosts() {
        for (int i = 0; i < ghostX.length; i++) {
            int direction = ghostDirections[i];
            int newX = ghostX[i];
            int newY = ghostY[i];

            if (i == 0) {
                if (direction == 1) {
                    newX++;
                    if (newX >= MAZE_WIDTH || maze[newY][newX] == 1) {
                        ghostDirections[i] = -1;
                    }
                } else {
                    newX--;
                    if (newX < 0 || maze[newY][newX] == 1) {
                        ghostDirections[i] = 1;
                    }
                }
            } else {
                direction = (int) (Math.random() * 4);
                if (direction == 0) {
                    newY--;
                } else if (direction == 1) {
                    newY++;
                } else if (direction == 2) {
                    newX--;
                } else if (direction == 3) {
                    newX++;
                }
            }

            boolean willCollide = false;
            for (int j = 0; j < ghostX.length; j++) {
                if (i != j && newX == ghostX[j] && newY == ghostY[j]) {
                    willCollide = true;
                    break;
                }
            }

            if (!willCollide && newX >= 0 && newX < MAZE_WIDTH && newY >= 0 && newY < MAZE_HEIGHT && maze[newY][newX] != 1) {
                ghostX[i] = newX;
                ghostY[i] = newY;
            }
        }
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean checkCollision() {
        for (int i = 0; i < ghostX.length; i++) {
            if (pacManX == ghostX[i] && pacManY == ghostY[i]) {
                return true;
            }
        }
        return false;
    }

    public void restartGame() {
        pacManX = 1;
        pacManY = 1;
        for (int i = 0; i < ghostX.length; i++) {
            ghostX[i] = MAZE_WIDTH / 2;
            ghostY[i] = MAZE_HEIGHT / 2;
        }
        score = 0;
    }

    public void playGame() {
        while (true) {
            moveGhosts();
            if (checkCollision()) {
                JOptionPane.showMessageDialog(this, "Гра закінчена! Ваш рахунок: " + score);
                restartGame();
            }
            repaint();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Гра Пак-Мен");
        PacmanGame game = new PacmanGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        game.playGame();
    }
}