package Model.Objects;

import java.awt.*;

public class AutoJumpingPlatform extends Platform {

    public AutoJumpingPlatform(double x, double y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(50, 205, 50)); // Lime Green
        g.fillRect((int)x, (int)y, width, height);

        g.setColor(new Color(144, 238, 144));
        g.fillRect((int)x, (int)y, width, 5);

        g.setColor(Color.BLACK);
        g.drawRect((int)x, (int)y, width, height);
    }
}