import javax.swing.*;

public class MainMenu extends JFrame {
    MainMenu() {
        setTitle("Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(MainPanel);
        pack();
    }

    private JButton startButton;
    private JPanel MainPanel;
}
