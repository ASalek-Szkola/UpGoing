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
    private boolean isAirborne = false;
    private int jumpWindow = 0;
    private final double gravity = ConfigManager.getPlayerSettings().getGravity();

    public Player(double x, double y) {
        super(x, y,
                ConfigManager.getPlayerSettings().getWidth(),
                ConfigManager.getPlayerSettings().getHeight()
        );
        this.velocityY = 0;
        this.velocityX = 0;
    }

    // Nadpisywanie metod abstrakcyjnych z GameObject
    @Override
    public void update(double deltaTime) {
        prevY = y;
        x += velocityX * deltaTime;
        velocityY += gravity * deltaTime;
        y += velocityY * deltaTime;
        prevY = y;
        double fps = ConfigManager.getGameSettings().getFps();
        double gravityPerFrame = ConfigManager.getPlayerSettings().getGravity() / fps;
        double velocityXPerFrame = velocityX / fps;
        if (!onPlatform) {
            velocityY += gravityPerFrame;
        } else {
            velocityY = 0;
            if (jumpWindow < ConfigManager.getPlayerSettings().getJump_window_frames()) jumpWindow++;
        }
        this.y += velocityY;
        this.x += velocityXPerFrame;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval((int)x, (int)y, width, height);
    }

    // Implementacja metod z interfejsu Collidable
    @Override
    public void onCollision(GameObject other) {
        // No automatic jump, handled by GamePanel
        // Only set position if intersecting
        if (other instanceof Platform) {
            Rectangle playerBounds = getBounds();
            Rectangle platformBounds = other.getBounds();
            if (playerBounds.intersects(platformBounds) && velocityY > 0) {
                y = other.getY() - height;
            }
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }

    // Metody specyficzne dla gracza
    public void jump() {
        velocityY = (ConfigManager.getPlayerSettings().getJump_power() * 1.2) / ConfigManager.getGameSettings().getFps();
        isAirborne = true;
        jumpWindow = 0;
    }
    public boolean isAirborne() {
        return isAirborne;
    }
    public void setAirborne(boolean value) {
        isAirborne = value;
    }

    public void setVelocityX(double vx) {
        this.velocityX = vx;
    }

    public void setVelocityY(double vy) {
        this.velocityY = vy;
    }

    public double getVelocityY() {
        return velocityY;
    }
    public void setY(double y) {
        this.y = y;
    }
    public int getHeight() {
        return height;
    }
    public void setOnPlatform(boolean value) {
        this.onPlatform = value;
    }
    public double getPrevY() {
        return prevY;
    }
    public boolean canJumpNow() {
        return jumpWindow > 0;
    }
    public void resetJumpWindow() {
        jumpWindow = 0;
    }
}