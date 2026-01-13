// java
package Model.Objects;

import Model.GameObject;
import Model.Collidable;
import java.awt.*;
import Config.ConfigManager;
import Model.GameStateManager;

public class Platform extends GameObject implements Collidable {
    private double velocityY;
    private double prevY;
    private boolean touchedByPlayer;

    // Domyślne wymiary platformy (przykładowe)
    private static final int DEFAULT_WIDTH = 60;
    private static final int DEFAULT_HEIGHT = 12;

    // Główny konstruktor
    public Platform(double x, double y, int width, int height) {
        super(x, y, width, height);
        this.velocityY = getSpeed(); // Pixels Per Second
        this.prevY = y;
        this.touchedByPlayer = false;
    }

    // Przeciążony konstruktor - używa domyślnych wymiarów
    public Platform(double x, double y) {
        this(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    public void update(double deltaTime) {
        velocityY = getSpeed();
        prevY = y;
        y += velocityY * deltaTime;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect((int)x, (int)y, width, height);
    }

    @Override
    public void onCollision(GameObject other) {}

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }

    public double getPrevY() { return prevY; }

    // Encapsulation: getter i setter dla touchedByPlayer
    public boolean isTouchedByPlayer() {
        return touchedByPlayer;
    }

    public void setTouchedByPlayer(boolean touched) {
        this.touchedByPlayer = touched;
    }

    private static double getSpeed() {
        double baseSpeed = ConfigManager.getPlatformsSettings().getSpeed();
        double maxSpeed = ConfigManager.getPlatformsSettings().getMax_speed();
        int currentScore = GameStateManager.getScore();

        double speed = baseSpeed + baseSpeed * (currentScore / 100.0);
        return Math.min(speed, maxSpeed);
    }
}
