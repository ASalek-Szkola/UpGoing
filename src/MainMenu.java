import Config.ConfigManager;
import Config.GameConfig;
import Game.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {
    MainMenu() {
        setTitle(ConfigManager.getConfig().getGame().getTitle());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(MainPanel);

        MainPanel.setPreferredSize(new Dimension(
                ConfigManager.getConfig().getGame().getWidth(),
                ConfigManager.getConfig().getGame().getHeight()
        ));

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Start Button Clicked! Starting the game...");

                showGameWindow();

                dispose();
            }
        });

        MainPanel.setVisible(true);
        pack();
    }

    private void showGameWindow() {
        JFrame gameFrame = new JFrame(ConfigManager.getConfig().getGame().getTitle() + " - Game");

        GamePanel gamePanel = new GamePanel();

        gameFrame.add(gamePanel);

        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setResizable(false);
        gameFrame.pack(); // Sizes the frame to fit the preferred size of the GamePanel
        gameFrame.setLocationRelativeTo(null); // Center the new window
        gameFrame.setVisible(true);
    }

    private JButton startButton;
    private JPanel MainPanel;
}
