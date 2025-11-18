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
        this.velocityY = ConfigManager.getPlatformsSettings().getSpeed();
        this.prevY = y;
    }

    @Override
    public void update(double deltaTime) {
        prevY = y;
        y += velocityY * deltaTime;
        double fps = ConfigManager.getGameSettings().getFps();
        double velocityYPerFrame = velocityY / fps;
        this.y += velocityYPerFrame;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect((int)x, (int)y, width, height);
    }

    @Override
    public void onCollision(GameObject other) {
        // Platform collision logic if needed
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }

    public double getPrevY() { return prevY; }
}
