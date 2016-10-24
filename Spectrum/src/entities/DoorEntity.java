package entities;

import models.TexturedModel;
import org.lwjglx.util.vector.Vector3f;

/**
 * Created by Beau on 24/10/2016.
 */
public class DoorEntity extends Entity {
    private float maxX;
    private float maxY;
    private float maxZ;
    private float minX;
    private float minY;
    private float minZ;

    public DoorEntity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    public void setBounds(float x, float X, float y, float Y, float z, float Z) {
        maxX = X;
        maxY = Y;
        maxZ = Z;
        minX = x;
        minY = y;
        minZ = z;
    }

    public boolean checkCollision(Player player) {
        if((player.getPosition().x >= minX && player.getPosition().x <= maxX) &&
                (player.getPosition().y >= minY && player.getPosition().y <= maxY) &&
                (player.getPosition().z >= minZ && player.getPosition().z <= maxZ)) {
            System.out.println("WIN");
            return true;
        } else {
            return false;
        }
    }
}
