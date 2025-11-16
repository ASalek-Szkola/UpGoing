package Model;

import java.awt.*;

public interface Collidable {
    void onCollision(GameObject other);
    Rectangle getBounds();
}