package util;

import entities.Camera;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

/**
 * Created by Beau on 8/09/2016.
 */
public class Maths {
    /**
     * Creates the transformation matrix for a 3D model
     * @param translation
     * @param rx
     * @param ry
     * @param rz
     * @param scale
     * @return
     */
    public static Matrix4f createTransformationMatrix(Vector3f translation,float rx, float ry, float rz, float scale){
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation,matrix,matrix);
        Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0),matrix,matrix);
        Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0),matrix,matrix);
        Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1),matrix,matrix);
        Matrix4f.scale(new Vector3f(scale,scale,scale), matrix,matrix);
        return matrix;
    }

    /**
     * Used for calculating hight of terrain at a given point
     * @param p1
     * @param p2
     * @param p3
     * @param pos
     * @return
     */
    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    /**
     * Creates the view matrix for trhe camera
     * @param camera
     * @return
     */
    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setIdentity();
        Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix,
                viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
        return viewMatrix;
    }

    /**
     * Creates the transformation matrix in 2D for gui elements
     * @param translation
     * @param scale
     * @return
     */
    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
        return matrix;
    }

}
