package Model.Objects;

import Model.GameObject;
import Model.Collidable;
import java.awt.*;
import Config.ConfigManager;

public class Platform extends GameObject implements Collidable {
    private final double velocityY;
    private double prevY;

    public Platform(double x, double y, int width, int height) {
        super(x, y, width, height);
        // Speed in Pixels Per Second
        this.velocityY = ConfigManager.getPlatformsSettings().getSpeed();
        this.prevY = y;
    }

    @Override
    public void update(double deltaTime) {
        prevY = y;
        // Position += Velocity * Time
        y += velocityY * deltaTime;

        // Removed the "velocityYPerFrame" logic entirely.
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect((int)x, (int)y, width, height);
    }

    @Override
    public void onCollision(GameObject other) {
        // Logic handled in GamePanel
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }

    public double getPrevY() { return prevY; }
}