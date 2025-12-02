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

    // CHANGED: Track time instead of frames
    private double timeSinceGrounded = 0;

    // Gravity (Pixels/Second^2)
    private final double gravity = ConfigManager.getPlayerSettings().getGravity();

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

        // --- COYOTE TIME LOGIC ---
        if (onPlatform) {
            // If we are standing on solid ground, reset the timer to 0
            timeSinceGrounded = 0;

            // Reset vertical velocity
            if (velocityY > 0) velocityY = 0;
        } else {
            // If we are falling (or just walked off), increase the timer by seconds elapsed
            timeSinceGrounded += deltaTime;

            // Apply Gravity
            velocityY += gravity * deltaTime;
        }

        // Apply Movement
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
        if (other instanceof Platform) {
            Rectangle playerBounds = getBounds();
            Rectangle platformBounds = other.getBounds();
            // Checking logic is handled mostly in GamePanel
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }

    public void jump() {
        // Apply jump velocity
        velocityY = ConfigManager.getPlayerSettings().getJump_power();

        // IMPORTANT: Invalidate Coyote Time immediately.
        // If we don't do this, the player could spam jump while in the air
        // if the window was long enough.
        timeSinceGrounded = 100.0; // Set to a high number effectively closing the window
    }

    // --- UPDATED HELPER METHODS ---

    public boolean canJumpNow() {
        // We can jump if the time since we left the platform is less than the config limit
        return timeSinceGrounded <= ConfigManager.getPlayerSettings().getJump_time_window();
    }

    public void setVelocityX(double vx) { this.velocityX = vx; }
    public void setVelocityY(double vy) { this.velocityY = vy; }
    public double getVelocityY() { return velocityY; }
    public void setY(double y) { this.y = y; }
    public int getHeight() { return height; }

    public void setOnPlatform(boolean value) {
        this.onPlatform = value;
        // Note: We don't reset timer here, we do it in update()
        // because this might be called multiple times per frame.
    }

    public double getPrevY() { return prevY; }
}