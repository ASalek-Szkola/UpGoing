package Model.Objects;

import java.awt.*;

public class AutoJumpingPlatform extends Platform {

    public AutoJumpingPlatform(double x, double y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void draw(Graphics g) {
        // Draw green body of platform
        g.setColor(new Color(50, 205, 50)); // Lime Green
        g.fillRect((int)x, (int)y, width, height);

        // Optional: Draw a "Spring" or highlight on top
        g.setColor(new Color(144, 238, 144)); // Light Green
        g.fillRect((int)x, (int)y, width, 5); // A lighter strip on top

        g.setColor(Color.BLACK);
        g.drawRect((int)x, (int)y, width, height);
    }
}