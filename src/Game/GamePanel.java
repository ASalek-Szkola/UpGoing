package Game;

import Config.ConfigManager;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {
    private Thread gameThread;
    private boolean running = true;

    public GamePanel() {
        setPreferredSize(new Dimension(
                ConfigManager.getConfig().getGame().getWidth(),
                ConfigManager.getConfig().getGame().getHeight()
        ));
        setFocusable(true);
        startGame();
    }

    public void startGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = (double) 1000000000 / ConfigManager.getGameSettings().getFps();
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1) {
                update(); // Aktualizacja logiki gry
                repaint(); // Rysowanie na nowo
                delta--;
            }
        }
    }
    int i = 0;
    public void update() {
        System.out.println(i++);
        // 1. Ruch Platform w dół
//        for (Platform p : platforms) {
//            // Wszystkie platformy poruszają się w dół z stałą prędkością gry (GAME_SPEED)
//            p.y += GAME_SPEED;
//        }

        // 2. Ruch Gracza (grawitacja, skoki)
//        player.update();

        // 3. Sprawdzenie Kolizji
//        checkCollisions();

        // 4. Generowanie Nowych Platform
//        generateNewPlatforms();

        // 5. Warunek Przegranej
//        if (player.y > getHeight()) { // Gracz wpadł poniżej dolnej krawędzi
//            gameOver = true;
//        }
    }
}
