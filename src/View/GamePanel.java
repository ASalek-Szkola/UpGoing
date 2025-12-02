// src/View/GamePanel.java

package View;

import Config.ConfigManager;
import Model.Objects.Player;
import Model.Objects.Platform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class GamePanel extends JPanel {
    private Timer gameTimer;
    private Player player;
    private ArrayList<Platform> platforms;
    private Image backgroundImage;
    private boolean gameOver = false;

    private int platformSpacing = ConfigManager.getPlatformsSettings().getSpacing();
    private int platformHeight = ConfigManager.getPlatformsSettings().getBase_height();
    private static final int MAX_PLATFORMS = 30;

    // Time tracking
    private long lastUpdateTime;

    // Physics safety cap: Max 0.05s (20 FPS) simulation step to prevent tunneling
    private static final double MAX_DELTA_TIME = 0.05;

    public GamePanel() {
        setPreferredSize(new Dimension(
                ConfigManager.getGameSettings().getWidth(),
                ConfigManager.getGameSettings().getHeight()
        ));
        setDoubleBuffered(true);
        setFocusable(true);

        // Initialize Player
        player = new Player(ConfigManager.getGameSettings().getWidth() / 2.0, 0);
        platforms = new ArrayList<>();

        // Load Background (Ensure this file exists in resources)
        try {
            backgroundImage = new ImageIcon(getClass().getResource("/images/background.jpg")).getImage();
        } catch (Exception e) {
            System.err.println("Background image not found.");
        }

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
                        // Updated to use the new time-based logic
                        if (player.canJumpNow()) {
                            player.jump();
                            // We don't need to manually set setOnPlatform(false) here,
                            // as the jump velocity will move the player up in the next update,
                            // naturally breaking the collision.
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
                        // Stop horizontal movement
                        player.setVelocityX(0);
                        break;
                }
            }
        });

        startGame();
    }

    private int generatePlatformWidth() {
        int minWidth = ConfigManager.getPlatformsSettings().getMin_width();
        int maxWidth = ConfigManager.getPlatformsSettings().getMax_width();
        return (int)(minWidth + (Math.random() * (maxWidth - minWidth)));
    }

    private void initPlatforms() {
        int panelHeight = ConfigManager.getGameSettings().getHeight();
        int panelWidth = ConfigManager.getGameSettings().getWidth();
        for (int i = 0; i < panelHeight / platformSpacing; i++) {
            int platformWidth = generatePlatformWidth();
            double x = Math.random() * (panelWidth - platformWidth);
            double y = panelHeight - i * platformSpacing;
            platforms.add(new Platform(x, y, platformWidth, platformHeight));
        }
    }

    public void startGame() {
        int targetFps = ConfigManager.getGameSettings().getFps();
        int delay = 1000 / targetFps; // Target delay in ms

        lastUpdateTime = System.nanoTime();

        gameTimer = new Timer(delay, e -> {
            if (!gameOver) {
                long now = System.nanoTime();
                // Calculate elapsed time in seconds
                double deltaTime = (now - lastUpdateTime) / 1_000_000_000.0;
                lastUpdateTime = now;

                // Prevent physics explosion if game hangs or window is dragged
                if (deltaTime > MAX_DELTA_TIME) {
                    deltaTime = MAX_DELTA_TIME;
                }

                update(deltaTime);
                repaint();
            }
        });
        gameTimer.start();
    }

    public void update(double deltaTime) {
        // Update entities with pure Delta Time
        player.update(deltaTime);
        for (Platform p : platforms) {
            p.update(deltaTime);
        }

        checkCollisions();
        removeOffscreenPlatforms();
        generateNewPlatforms();

        if (player.getY() > getHeight()) {
            gameOver = true;
        }
    }

    private void checkCollisions() {
        // Assume player is in air unless we find a collision
        player.setOnPlatform(false);

        for (Platform p : platforms) {
            // swept collision: check if player crossed platform top this frame
            double playerPrevBottom = player.getPrevY() + player.getHeight();
            double playerCurrBottom = player.getY() + player.getHeight();
            double platformPrevTop = p.getPrevY();
            double platformCurrTop = p.getY();

            // Check if player passed through the platform downwards
            double relativePrev = playerPrevBottom - platformPrevTop;
            double relativeCurr = playerCurrBottom - platformCurrTop;
            boolean crossedDownwards = relativePrev <= 0 && relativeCurr >= 0;

            // Simple velocity check to ensure we don't snap while jumping up through a platform
            boolean falling = player.getVelocityY() >= 0;

            // Horizontal overlap check
            double playerLeft = player.getX();
            double playerRight = player.getX() + player.getWidth();
            double platformLeft = p.getX();
            double platformRight = p.getX() + p.getWidth();
            boolean horizontalOverlap = playerRight > platformLeft && playerLeft < platformRight;

            if (crossedDownwards && falling && horizontalOverlap) {
                // Snap player to current platform top
                player.setY(platformCurrTop - player.getHeight());
                player.setVelocityY(0); // Stop falling

                // This flag enables the Coyote Time reset in the next Player.update()
                player.setOnPlatform(true);
                return;
            }
        }
    }

    private void removeOffscreenPlatforms() {
        platforms.removeIf(p -> p.getY() > getHeight());
    }

    private void generateNewPlatforms() {
        if (platforms.size() >= MAX_PLATFORMS) return;

        int platformWidth = generatePlatformWidth();

        if (platforms.isEmpty()) {
            double x = Math.random() * (getWidth() - platformWidth);
            platforms.add(new Platform(x, -platformHeight, platformWidth, platformHeight));
            return;
        }

        Platform last = platforms.getLast();
        if (last.getY() > platformSpacing) {
            double x = Math.random() * (getWidth() - platformWidth);
            platforms.add(new Platform(x, -platformHeight, platformWidth, platformHeight));
        }
    }

    private void restartGame() {
        gameOver = false;
        player = new Player(getWidth() / 2.0, 0);
        platforms.clear();
        initPlatforms();
        lastUpdateTime = System.nanoTime(); // Reset timer to prevent jump on restart
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback background
            g.setColor(Color.CYAN);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

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
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press 'R' to Restart", getWidth() / 2 - 80, getHeight() / 2 + 50);
        }
    }
}