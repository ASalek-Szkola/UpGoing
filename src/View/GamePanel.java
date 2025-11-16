package View;

import Config.ConfigManager;
import Model.Objects.Player;
import Model.Objects.Platform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable {
    private Timer gameTimer;
    private boolean running = true;
    private Player player;
    private ArrayList<Platform> platforms;
    private boolean gameOver = false;

    private int platformSpacing = ConfigManager.getPlatformsSettings().getSpacing();
    private double platformSpeed = ConfigManager.getPlatformsSettings().getSpeed();
    private int platformWidth = ConfigManager.getPlatformsSettings().getMin_width();
    private int platformHeight = ConfigManager.getPlatformsSettings().getBase_height();
    private static final int MAX_PLATFORMS = 30;

    public GamePanel() {
        setPreferredSize(new Dimension(
            ConfigManager.getGameSettings().getWidth(),
            ConfigManager.getGameSettings().getHeight()
        ));
        setDoubleBuffered(true);
        setFocusable(true);
        player = new Player(ConfigManager.getGameSettings().getWidth() / 2.0, 0);
        platforms = new ArrayList<>();
        initPlatforms();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        player.setVelocityX(-ConfigManager.getPlayerSettings().getHorizontal_speed());
                        break;
                    case KeyEvent.VK_RIGHT:
                        player.setVelocityX(ConfigManager.getPlayerSettings().getHorizontal_speed());
                        break;
                    case KeyEvent.VK_SPACE:
                        if (player.canJumpNow()) {
                            player.jump();
                            player.setOnPlatform(false);
                        }
                        break;
                    case KeyEvent.VK_R:
                        restartGame();
                        break;
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_RIGHT:
                        player.setVelocityX(0);
                        break;
                }
            }
        });
        startGame();
    }

    private void initPlatforms() {
        int panelHeight = ConfigManager.getGameSettings().getHeight();
        int panelWidth = ConfigManager.getGameSettings().getWidth();
        for (int i = 0; i < panelHeight / platformSpacing; i++) {
            double x = Math.random() * (panelWidth - platformWidth);
            double y = panelHeight - i * platformSpacing;
            platforms.add(new Platform(x, y));
        }
    }

    public void startGame() {
        int fps = ConfigManager.getGameSettings().getFps();
        int delay = 1000 / fps;
        gameTimer = new Timer(delay, e -> {
            if (!gameOver) {
                update();
                repaint();
            }
        });
        gameTimer.start();
    }

    @Override
    public void run() {
        // No longer needed, replaced by Swing Timer
    }

    public void update() {
        // Update player and platforms first so prev/current positions are available
        player.update();
        for (Platform p : platforms) {
            p.update();
        }

        checkCollisions();
        removeOffscreenPlatforms();
        generateNewPlatforms();
        if (player.getY() > getHeight()) {
            gameOver = true;
        }
    }

    private void checkCollisions() {
        player.setOnPlatform(false);
        for (Platform p : platforms) {
            // swept collision: check if player crossed platform top this frame
            double playerPrevBottom = player.getPrevY() + player.getHeight();
            double playerCurrBottom = player.getY() + player.getHeight();
            double platformPrevTop = p.getPrevY();
            double platformCurrTop = p.getY();

            // compute relative motion (platform may be moving downward)
            double relativePrev = playerPrevBottom - platformPrevTop;
            double relativeCurr = playerCurrBottom - platformCurrTop;

            boolean crossedDownwards = relativePrev <= 0 && relativeCurr >= 0 && player.getVelocityY() >= 0;

            // horizontal overlap check (more robust than Rectangle.intersects in some edge cases)
            double playerLeft = player.getX();
            double playerRight = player.getX() + player.getWidth();
            double platformLeft = p.getX();
            double platformRight = p.getX() + p.getWidth();
            boolean horizontalOverlap = playerRight > platformLeft && playerLeft < platformRight;

            if (crossedDownwards && horizontalOverlap) {
                // snap player to current platform top
                player.setY(platformCurrTop - player.getHeight());
                player.setVelocityY(0);
                player.setOnPlatform(true);
                player.setAirborne(false);
                player.resetJumpWindow();
                return;
            }
        }
    }

    private void removeOffscreenPlatforms() {
        platforms.removeIf(p -> p.getY() > getHeight());
    }

    private void generateNewPlatforms() {
        if (platforms.size() >= MAX_PLATFORMS) return;
        if (platforms.isEmpty()) {
            double x = Math.random() * (getWidth() - platformWidth);
            platforms.add(new Platform(x, -platformHeight));
            return;
        }
        Platform last = platforms.get(platforms.size() - 1);
        // Only add a new platform if the last one is far enough down
        if (last.getY() > platformSpacing) {
            double x = Math.random() * (getWidth() - platformWidth);
            platforms.add(new Platform(x, -platformHeight));
        }
    }

    private void restartGame() {
        gameOver = false;
        player = new Player(getWidth() / 2.0, 0);
        platforms.clear();
        initPlatforms();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Platform p : platforms) {
            if (p.getY() + p.getHeight() > 0) {
                p.draw(g);
            }
        }
        player.draw(g);
        if (gameOver) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("Game Over", getWidth() / 2 - 120, getHeight() / 2);
        }
    }
}
