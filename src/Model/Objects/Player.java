package Model.Objects;

import Config.ConfigManager;
import Model.Collidable;
import Model.GameObject;

import java.awt.*;

public class Player extends GameObject implements Collidable {
    private double velocityY;
    private double velocityX;
    private boolean onPlatform = false;
    private double prevY;

    private double timeSinceGrounded = 0;
    private final double gravity = ConfigManager.getPlayerSettings().getGravity();
    private final double platformSpeed = ConfigManager.getPlatformsSettings().getSpeed();

    public Player(double x, double y) {
        super(x, y,
                ConfigManager.getPlayerSettings().getWidth(),
                ConfigManager.getPlayerSettings().getHeight()
        );
        this.velocityY = 0;
        this.velocityX = 0;
    }

    @Override
    public void update(double deltaTime) {
        prevY = y;

        if (onPlatform) {
            timeSinceGrounded = 0;

            // Move down at the same speed as the platform, to prevent stutters
            velocityY = platformSpeed;
        } else {
            timeSinceGrounded += deltaTime;
            velocityY += gravity * deltaTime;
        }

        x += velocityX * deltaTime;
        y += velocityY * deltaTime;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval((int)x, (int)y, width, height);
    }

    @Override
    public void onCollision(GameObject other) {
        // Handled in GamePanel
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }

    public void jump() {
        velocityY = ConfigManager.getPlayerSettings().getJump_power();

        // Break connection immediately
        onPlatform = false;

        // Invalidate Coyote Time
        timeSinceGrounded = 100.0;
    }

    public boolean canJumpNow() {
        return timeSinceGrounded <= ConfigManager.getPlayerSettings().getJump_time_window();
    }

    public void setVelocityX(double vx) { this.velocityX = vx; }
    public void setVelocityY(double vy) { this.velocityY = vy; }
    public double getVelocityY() { return velocityY; }
    public void setY(double y) { this.y = y; }
    public int getHeight() { return height; }
    public void setOnPlatform(boolean value) { this.onPlatform = value; }
    public double getPrevY() { return prevY; }
}