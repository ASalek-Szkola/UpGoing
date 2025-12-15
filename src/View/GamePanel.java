package View;

import Config.ConfigManager;
import Model.Objects.AutoJumpingPlatform;
import Model.Objects.Player;
import Model.Objects.Platform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Objects;

public class GamePanel extends JPanel {
    private Timer gameTimer;
    private Player player;
    private ArrayList<Platform> platforms;
    private Image backgroundImage;
    private boolean gameOver = false;

    private int platformSpacing = ConfigManager.getPlatformsSettings().getSpacing();
    private int platformHeight = ConfigManager.getPlatformsSettings().getBase_height();
    private static final int MAX_PLATFORMS = 30;

    private long lastUpdateTime;
    private static final double MAX_DELTA_TIME = 0.05;

    // Input Buffer
    private double jumpBufferTimer = 0;
    private static final double JUMP_BUFFER_DURATION = ConfigManager.getPlayerSettings().getJump_buffer_duration();

    private int score = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(
                ConfigManager.getGameSettings().getWidth(),
                ConfigManager.getGameSettings().getHeight()
        ));
        setDoubleBuffered(true);
        setFocusable(true);

        player = new Player(ConfigManager.getGameSettings().getWidth() / 2.0, 0);
        platforms = new ArrayList<>();

        try {
            backgroundImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/background.jpg"))).getImage();
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
                        // Instead of jumping immediately, register the request
                        jumpBufferTimer = JUMP_BUFFER_DURATION;
                        break;
                    case KeyEvent.VK_R:
                        if (gameOver) {
                            restartGame();
                        }
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

    private int generatePlatformWidth() {
        int minWidth = ConfigManager.getPlatformsSettings().getMin_width();
        int maxWidth = ConfigManager.getPlatformsSettings().getMax_width();
        return (int)(minWidth + (Math.random() * (maxWidth - minWidth)));
    }

    private Platform createRandomPlatform(double x, double y, int width, int height) {
        double chance = ConfigManager.getPlatformsSettings().getAuto_jump_chance();

        if (Math.random() < chance) {
            return new AutoJumpingPlatform(x, y, width, height);
        } else {
            return new Platform(x, y, width, height);
        }
    }

    private void initPlatforms() {
        int panelHeight = ConfigManager.getGameSettings().getHeight();
        int panelWidth = ConfigManager.getGameSettings().getWidth();
        for (int i = 0; i < panelHeight / platformSpacing; i++) {
            int platformWidth = generatePlatformWidth();
            double x = Math.random() * (panelWidth - platformWidth);
            double y = panelHeight - i * platformSpacing;

            platforms.add(createRandomPlatform(x, y, platformWidth, platformHeight));
        }
    }

    private void generateNewPlatforms() {
        if (platforms.size() >= MAX_PLATFORMS) return;
        int platformWidth = generatePlatformWidth();

        if (platforms.isEmpty()) {
            double x = Math.random() * (getWidth() - platformWidth);
            platforms.add(createRandomPlatform(x, -platformHeight, platformWidth, platformHeight));
            return;
        }
        Platform last = platforms.getLast();
        if (last.getY() > platformSpacing) {
            double x = Math.random() * (getWidth() - platformWidth);
            platforms.add(createRandomPlatform(x, -platformHeight, platformWidth, platformHeight));
        }
    }

    public void startGame() {
        int targetFps = ConfigManager.getGameSettings().getFps();
        int delay = 1000 / targetFps;

        lastUpdateTime = System.nanoTime();

        gameTimer = new Timer(delay, e -> {
            if (!gameOver) {
                long now = System.nanoTime();
                double deltaTime = (now - lastUpdateTime) / 1_000_000_000.0;
                lastUpdateTime = now;

                if (deltaTime > MAX_DELTA_TIME) deltaTime = MAX_DELTA_TIME;

                update(deltaTime);
                repaint();
            }
        });
        gameTimer.start();
    }

    public void update(double deltaTime) {
        // Process Input Buffer
        if (jumpBufferTimer > 0) {
            jumpBufferTimer -= deltaTime;
            if (player.canJumpNow()) {
                player.jump();
                jumpBufferTimer = 0; // Consumption of input
            }
        }

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
        player.setOnPlatform(false);
        double platformSpeed = ConfigManager.getPlatformsSettings().getSpeed();

        for (Platform p : platforms) {
            // This handles high-speed falling where the player might pass through the top edge between frames.

            double playerPrevBottom = player.getPrevY() + player.getHeight();
            double playerCurrBottom = player.getY() + player.getHeight();
            double platformPrevTop = p.getPrevY();
            double platformCurrTop = p.getY();

            double epsilon = 2.0;
            double relativePrev = playerPrevBottom - platformPrevTop;
            double relativeCurr = playerCurrBottom - platformCurrTop;

            boolean crossedDownwards = relativePrev <= epsilon && relativeCurr >= -epsilon;
            boolean falling = player.getVelocityY() >= 0; // Only land if falling or flat

            // Horizontal alignment check for landing
            double playerLeft = player.getX();
            double playerRight = player.getX() + player.getWidth();
            double platformLeft = p.getX();
            double platformRight = p.getX() + p.getWidth();
            // Shrink hit box slightly (2px) to prevent landing on the exact pixel edge
            boolean horizontalOverlap = playerRight > platformLeft + 2 && playerLeft < platformRight - 2;

            if (crossedDownwards && falling && horizontalOverlap) {
                // LANDING RESOLUTION
                player.setY(platformCurrTop - player.getHeight());

                if (!p.isTouchedByPlayer()) {
                    score++;
                    p.touchedByPlayer = true;
                }

                if (p instanceof AutoJumpingPlatform) {
                    player.jump();
                } else {
                    player.setVelocityY(platformSpeed); // Sync with platform
                    player.setOnPlatform(true);
                }

                // If we landed, we don't need to check for side collisions on this platform
                p.touchedByPlayer = true;
                continue;
            }

            // Side collisions
            Rectangle pRect = player.getBounds();
            Rectangle platRect = p.getBounds();

            if (pRect.intersects(platRect)) {
                // We are intersecting, but NOT landing. We must resolve the collision.

                // Calculate overlaps on both axes
                double overlapX = 0;
                double overlapY = 0;

                double pCenterX = player.getX() + player.getWidth() / 2.0;
                double platCenterX = p.getX() + p.getWidth() / 2.0;
                double dx = pCenterX - platCenterX;
                // Total width divided by 2
                double combinedHalfWidths = (player.getWidth() + p.getWidth()) / 2.0;
                overlapX = combinedHalfWidths - Math.abs(dx);

                double pCenterY = player.getY() + player.getHeight() / 2.0;
                double platCenterY = p.getY() + p.getHeight() / 2.0;
                double dy = pCenterY - platCenterY;
                double combinedHalfHeights = (player.getHeight() + p.getHeight()) / 2.0;
                overlapY = combinedHalfHeights - Math.abs(dy);

                // Determine the "Path of Least Resistance"
                // If overlapX is smaller, we hit the side. If overlapY is smaller, we hit vertical.

                if (overlapX < overlapY) {
                    // Left / Right

                    if (dx < 0) {
                        // Player is to the left, push left
                        player.setX(p.getX() - player.getWidth());
                    } else {
                        // Player is to the right, push right
                        player.setX(p.getX() + p.getWidth());
                    }

                    // Kill horizontal momentum
                    player.setVelocityX(0);

                } else {
                    // Bottom

                    if (dy > 0) {
                        // Player center is below Platform center -> Head Bump

                        // Push player down below the platform
                        player.setY(p.getY() + p.getHeight());

                        // Kill upward momentum.
                        player.setVelocityY(platformSpeed);
                    }
                }
            }
        }
    }

    private void removeOffscreenPlatforms() {
        platforms.removeIf(p -> p.getY() > getHeight());
    }

    private void restartGame() {
        gameOver = false;
        score = 0;
        player = new Player(getWidth() / 2.0, 0);
        platforms.clear();
        initPlatforms();
        lastUpdateTime = System.nanoTime();
    }

    private void drawCenteredString(Graphics g, String text, int y, Font font, Color color) {
        Font prev = g.getFont();
        g.setFont(font);
        g.setColor(color);
        FontMetrics fm = g.getFontMetrics(font);
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
        g.setFont(prev);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.CYAN);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        for (Platform p : platforms) {
            if (p.getY() + p.getHeight() > 0) p.draw(g);
        }
        player.draw(g);

        if (gameOver) {
            String title = "Your score: " + score;
            Font titleFont = new Font("Arial", Font.BOLD, 48);
            drawCenteredString(g, title, getHeight() / 2, titleFont, Color.WHITE);

            String subtitle = "Press 'R' to Restart";
            Font subFont = new Font("Arial", Font.PLAIN, 20);
            drawCenteredString(g, subtitle, getHeight() / 2 + 60, subFont, Color.WHITE);
        } else {
            // Score at top-right\
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, getWidth() - 100, 30);
        }
    }
}