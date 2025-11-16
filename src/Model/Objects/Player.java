package Model.Objects;

import Config.ConfigManager;
import Model.Collidable;
import Model.GameObject;

import java.awt.*;

public class Player extends GameObject implements Collidable {
    private double velocityY;
    private double velocityX;

    public Player(double x, double y) {
        super(x, y,
                ConfigManager.getPlayerSettings().getWidth(),
                ConfigManager.getPlayerSettings().getHeight()
        );
        // ... inicjalizacja prędkości
    }

    // Nadpisywanie metod abstrakcyjnych z GameObject
    @Override
    public void update() {
        // Implementacja logiki gracza: grawitacja, ruch, itp.
        this.y += velocityY;
        this.x += velocityX;
        // ...
    }

    @Override
    public void draw(Graphics g) {
        // Rysowanie gracza
    }

    // Implementacja metod z interfejsu Collidable
    @Override
    public void onCollision(GameObject other) {
        // Co się dzieje, gdy gracz zderzy się z innym obiektem (np. platformą)
    }

    @Override
    public Rectangle getBounds() {
        // Zwraca prostokąt kolizji
        return new Rectangle((int)x, (int)y, width, height);
    }

    // Metody specyficzne dla gracza
    public void jump() {
        // logika skoku
    }
}