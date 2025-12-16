package Model.Objects;

import Model.GameObject;
import Model.Collidable;
import java.awt.*;
import Config.ConfigManager;
import Model.GameStateManager;

public class Platform extends GameObject implements Collidable {
    private double velocityY;
    private double prevY;

    public boolean isTouchedByPlayer() {
        return touchedByPlayer;
    }

    public boolean touchedByPlayer;

    public Platform(double x, double y, int width, int height) {
        super(x, y, width, height);
        // Speed in Pixels Per Second
        this.velocityY = getSpeed();
        this.prevY = y;
    }

    @Override
    public void update(double deltaTime) {
        velocityY = getSpeed();
        prevY = y;

        // Position += Velocity * Time
        y += velocityY * deltaTime;
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

    private static double getSpeed() {
        double baseSpeed = ConfigManager.getPlatformsSettings().getSpeed();
        double maxSpeed = ConfigManager.getPlatformsSettings().getMax_speed();
        int currentScore = GameStateManager.getScore();

        double speed = baseSpeed + baseSpeed * (currentScore / 100.0);
        return Math.min(speed, maxSpeed);
    }
}