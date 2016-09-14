package World;

import java.util.ArrayList;
import java.util.List;

/**
 * World object, holds list of levels and main flow
 */
public class World {
    List<Level> levels = new ArrayList<>();
    public World(){
        //Create level object
        Level hub = new Level("modelObjList","textureObjList");
        levels.add(hub);
    }
    public void loadHub(){
        levels.get(0).create();
    }


}
