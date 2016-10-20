package water;

/**
 * Created by Brendon on 28/09/2016.
 *
 * Simple class for water quad position and size
 */
public class WaterTile {

	private float height;
	private float x,z;
    public static float size;
    private float tileSize;

	
	public WaterTile(float centerX, float centerZ, float height, float size,float tileSize){
		this.x = centerX;
		this.z = centerZ;
		this.height = height;
        this.size = size;
        this.tileSize = tileSize;

	}

    public float getSize() {
        return size;
    }

    public float getTileSize(){return tileSize;}

	public float getHeight() {
		return height;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

}
