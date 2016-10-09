package particles;

/**
 * Created by Beau on 10/10/2016.
 */
public class ParticleTexture {

    private int textureID;
    private int numOfRows;

    public ParticleTexture(int textureID, int numOfRows) {
        this.textureID = textureID;
        this.numOfRows = numOfRows;
    }

    public int getTextureID() {
        return textureID;
    }

    public int getNumOfRows() {
        return numOfRows;
    }
}
