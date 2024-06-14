import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * FlappyBird is a class that extends JPanel and implements ActionListener and KeyListener.
 * It represents the game logic for a simple Flappy Bird game.
 */
public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    // Game parameters
    int boardWidth = 360, boardHeight = 640, birdX = boardWidth/8, birdY = boardWidth/2, birdWidth = 34, birdHeight = 24;
    int pipeX = boardWidth, pipeY = 0, pipeWidth = 64, pipeHeight = 512;
    int velocityX = -4, velocityY = 0, gravity = 1;
    double score = 0;
    boolean gameOver = false;

    // Game assets
    Image backgroundImg, birdImg, topPipeImg, bottomPipeImg;
    Bird bird;
    ArrayList<Pipe> pipes = new ArrayList<>();
    Random random = new Random();

    // Game timers
    Timer gameLoop, placePipeTimer;

    /**
     * Constructor for the FlappyBird class.
     * Initializes the game parameters, loads the game assets, and starts the game loop.
     */
    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);
        loadImages();
        bird = new Bird(birdImg);
        placePipeTimer = new Timer(1500, e -> placePipes());
        placePipeTimer.start();
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    /**
     * Loads the images used in the game from the file system.
     */
    void loadImages() {
        backgroundImg = new ImageIcon("images/flappybirdbg.png").getImage();
        birdImg = new ImageIcon("images/flappybird.png").getImage();
        topPipeImg = new ImageIcon("images/toppipe.png").getImage();
        bottomPipeImg = new ImageIcon("images/bottompipe.png").getImage();
    }

    /**
     * Places a new pair of pipes in the game at a random height.
     */
    void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;
        pipes.add(new Pipe(topPipeImg, randomPipeY));
        pipes.add(new Pipe(bottomPipeImg, randomPipeY + pipeHeight + openingSpace));
    }

    /**
     * Overrides the paintComponent method from JPanel to draw the game assets on the screen.
     * @param g Graphics object used for drawing.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);
        pipes.forEach(pipe -> g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null));
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        g.drawString(gameOver ? "Game Over: " + (int) score : String.valueOf((int) score), 10, 35);
    }

    /**
     * Moves the bird and the pipes, checks for collisions, and updates the game state.
     */
    public void move() {
        velocityY += gravity;
        bird.y = Math.max(bird.y + velocityY, 0);
        pipes.forEach(pipe -> {
            pipe.x += velocityX;
            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                score += 0.5;
                pipe.passed = true;
            }
            if (collision(bird, pipe)) gameOver = true;
        });
        if (bird.y > boardHeight) gameOver = true;
    }

    /**
     * Checks for a collision between the bird and a pipe.
     * @param a The bird.
     * @param b The pipe.
     * @return true if there is a collision, false otherwise.
     */
    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    /**
     * Overrides the actionPerformed method from ActionListener to update the game state and redraw the screen.
     * @param e The action event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }

    /**
     * Overrides the keyPressed method from KeyListener to handle user input.
     * @param e The key event.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            if (gameOver) restartGame();
        }
    }

    /**
     * Restarts the game by resetting the game state and starting the game loop.
     */
    void restartGame() {
        bird.y = birdY;
        velocityY = 0;
        pipes.clear();
        gameOver = false;
        score = 0;
        gameLoop.start();
        placePipeTimer.start();
    }

    // Unused KeyListener methods
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    /**
     * Inner class representing the bird in the game.
     */
    class Bird {
        int x = birdX, y = birdY, width = birdWidth, height = birdHeight;
        Image img;

        /**
         * Constructor for the Bird class.
         * @param img The image representing the bird.
         */
        Bird(Image img) {
            this.img = img;
        }
    }

    /**
     * Inner class representing a pipe in the game.
     */
    class Pipe {
        int x = pipeX, y = pipeY, width = pipeWidth, height = pipeHeight;
        Image img;
        boolean passed = false;

        /**
         * Constructor for the Pipe class.
         * @param img The image representing the pipe.
         * @param y The vertical position of the pipe.
         */
        Pipe(Image img, int y) {
            this.img = img;
            this.y = y;
        }
    }
}