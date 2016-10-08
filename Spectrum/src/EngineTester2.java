import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Entities.Player;
import Interface.GUITexture;
import Models.RawModel;
import Models.TexturedModel;
import Terrain.Terrain;
import Textures.ModelTexture;
import Textures.TerrainTexPack;
import Textures.TerrainTexture;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.*;
//import World.World;

import Engine.*;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjglx.input.Mouse;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;
import util.KeyboardHandler;
import util.MouseHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import util.MousePicker;
import util.NormalMappedObjLoader;
import water.*;

import static org.lwjgl.glfw.GLFW.*;

public class EngineTester2 {

    private static int WIDTH = 1280;
    private static int HEIGHT = 720;
    private static GLFWKeyCallback keyCallback;
    private static MouseHandler mouseCallback;
    private static int state = 0;
    private static  List<Light> lights = new ArrayList<>();
    private static List<Entity>  entities = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        try {
            init();
            loop();
        } finally {
            WindowHandler.closeWindow();
        }
    }

    //Initialisation
    private static void init() {
        String title = "Spectrum";
        WindowHandler.createWindow(WIDTH, HEIGHT, title);
        glfwSetKeyCallback(WindowHandler.getWindow(), keyCallback = new KeyboardHandler());
        glfwSetCursorPosCallback(WindowHandler.getWindow(), mouseCallback = new MouseHandler());
        glfwSetInputMode(WindowHandler.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);

    }

    //Main Loop
    private static void loop() {
        /*********************************************LOAD RENDERER AND LOADER**************************************************************/
        ModelLoadHandler loader = new ModelLoadHandler();
        MasterRenderHandler renderer = new MasterRenderHandler(loader);


        /*********************************************PARSE OBJECTS*************************************************************************/
        ModelData dragonData = OBJFileLoader.loadOBJ("dragon");
        ModelData tree2Data = OBJFileLoader.loadOBJ("lowPolyTree");
        ModelData lampData = OBJFileLoader.loadOBJ("lamp");


        /*********************************************LOAD RAW DATA AS MODELS***************************************************************/
        RawModel dragonModel = loader.loadToVAO(dragonData.getVertices(), dragonData.getTextureCoords(), dragonData.getNormals(), dragonData.getIndices());
        RawModel treeModel2 = loader.loadToVAO(tree2Data.getVertices(), tree2Data.getTextureCoords(), tree2Data.getNormals(), tree2Data.getIndices());
        RawModel lampModel =  loader.loadToVAO(lampData.getVertices(),lampData.getTextureCoords(),lampData.getNormals(),lampData.getIndices());
        RawModel crateModel = NormalMappedObjLoader.loadOBJ("crate",loader);
        RawModel barrelModel = NormalMappedObjLoader.loadOBJ("barrel",loader);
        RawModel rockModel = NormalMappedObjLoader.loadOBJ("boulder",loader);


        /*********************************************CREATE MODEL TEXTURES*****************************************************************/
        ModelTexture fernAtlas = new ModelTexture(loader.loadTexture("fern"));
        fernAtlas.setNumberOfRows(2);
        ModelTexture treeTextureAtlas =  new ModelTexture(loader.loadTexture("lowPolyTree"));
        ModelTexture dragonTexture = new ModelTexture(loader.loadTexture("red"));
        dragonTexture.setShineDamper(5);
        dragonTexture.setReflectivity(0.75f);
        treeTextureAtlas.setNumberOfRows(2);

        ModelTexture crateAtlasTexture = new ModelTexture((loader.loadTexture("crateAtlas")));
        crateAtlasTexture.setNumberOfRows(2);
        crateAtlasTexture.setNormalMap(loader.loadTexture("crateNormal"));
        crateAtlasTexture.setShineDamper(10);
        crateAtlasTexture.setReflectivity(0.3f);

        ModelTexture barrelTexture = new ModelTexture((loader.loadTexture("barrel")));
        barrelTexture.setNormalMap(loader.loadTexture("barrelNormal"));
        barrelTexture.setShineDamper(10);
        barrelTexture.setReflectivity(0.2f);

        ModelTexture rockTexture = new ModelTexture((loader.loadTexture("boulder")));
        rockTexture.setNormalMap(loader.loadTexture("boulderNormal"));
        rockTexture.setShineDamper(10);
        rockTexture.setReflectivity(0.1f);


        /*********************************************TEXTURE RAW MODELS*********************************************************************/
        TexturedModel tree2TexturedModel = new TexturedModel(treeModel2,treeTextureAtlas);
        TexturedModel fernTexturedModel = new TexturedModel(OBJLoader.loadObjModel("fern", loader), fernAtlas );
        TexturedModel lamp = new TexturedModel(lampModel, new ModelTexture(loader.loadTexture("lamp")));
        fernTexturedModel.getTexture().setHasTransparency(true);
        TexturedModel dragon = new TexturedModel(dragonModel,dragonTexture);
        lamp.getTexture().setUseFakeLighting(true);

        TexturedModel crate = new TexturedModel(crateModel,crateAtlasTexture);
        TexturedModel barrel = new TexturedModel(barrelModel,barrelTexture);
        TexturedModel rock = new TexturedModel(rockModel,rockTexture);


        /*********************************************TEXTURE TERRAIN***********************************************************************/
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass_HIGH_RES"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("soil_HIGH_RES"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("flowers_HIGH_RES"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("road_HIGH_RES"));

        TerrainTexPack texturePack = new TerrainTexPack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));


        /*********************************************LOAD TERRAIN*************************************************************************/
        Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "hm3");


        /*********************************************CREATE ENTITIES***********************************************************************/
        List<Entity> entities = new ArrayList<>();
        List<Entity> normalMapEntities = new ArrayList<>();
        Random random = new Random(676452);
        for(int i = 0; i < 2000; i++) {
            if(i % 2 == 0) {
                float z = random.nextFloat() * -800;
                float x = random.nextFloat() * 800;
                float y = terrain.getHeightOfTerrain(x, z);
                if(y > -20) {
                    entities.add(new Entity(fernTexturedModel, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.5f));
                }
            }
            if(i%50 == 0) {
                float z = random.nextFloat() * -800;
                float x = random.nextFloat() * 800;
                float y = terrain.getHeightOfTerrain(x, z)+3;
                float rotY = random.nextInt();

                if(y > -20) {
                    normalMapEntities.add(new Entity(crate,random.nextInt(4),new Vector3f(x,y,z), 0,rotY,0,0.025f));

                }
            }
            if(i%100 == 0) {
                float z = random.nextFloat() * -800;
                float x = random.nextFloat() * 800;
                float y = terrain.getHeightOfTerrain(x, z)+3;

                if(y > -20) {
                    normalMapEntities.add(new Entity(barrel,new Vector3f(x,y,z), 0,0,0,1));

                }
            }
            if(i%10 == 0) {
                float z = random.nextFloat() * -800;
                float x = random.nextFloat() * 800;
                float y = terrain.getHeightOfTerrain(x, z)+3;
                float rotX = random.nextInt();
                float rotY = random.nextInt();
                float rotZ = random.nextInt();
                if(y > -20) {
                    normalMapEntities.add(new Entity(rock,new Vector3f(x,y,z), rotX,rotY,rotZ,1));

                }
            }
            if(i % 5 == 0) {
                float z = random.nextFloat() * -800;
                float x = random.nextFloat() * 800;
                float y = terrain.getHeightOfTerrain(x, z);
                if(y > -20) {
                    entities.add(new Entity(tree2TexturedModel, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.4f));
                }
            }

        }
        entities.add(new Entity(dragon,new Vector3f(600, -10, -600), 0, 0, 0, 6f));
        entities.add(new Entity(lamp,new Vector3f(380, -20, -380),0,0,0,1 ));


        /*********************************************CREATE LIGHTS*************************************************************************/
        lights.add(new Light(new Vector3f(0, 10000, -7000), new Vector3f(0.8f, 0.8f, 0.8f)));
        lights.add(new Light(new Vector3f(380, 0, -380), new Vector3f(3, 3, 3), new Vector3f(1,0.01f,0.002f)));


        /*********************************************CREATE GUIS***************************************************************************/
        GUIRenderer guiRenderer = new GUIRenderer(loader);
        List<GUITexture> guis = new ArrayList<>();
        GUITexture gui = new GUITexture(loader.loadTexture("fern"), new Vector2f(0f,0f), new Vector2f(1f,1f) );
        guis.add(gui);


        /*********************************************CREATE PLAYER*************************************************************************/
        Player player = new Player(mouseCallback,  new Vector3f(424,-5,-432));


        /*********************************************CREATE WATER**************************************************************************/
        //make a list of water tiles
        //ideally only 1 tile or atleast have all same height as reflection only works off one height for now
        List<WaterTile> waters = new ArrayList<>();
        waters.add(new WaterTile(400,-400,-20,300,20)); //the tiles where to add the water (size specified in tiles class)
        //waters.add(new WaterTile(20,0,20,300,20));
        //(x,z,y,size,#tiles used for texturing)
        Water water = new Water(waters,loader,renderer);

        /***********************************************************************************************************************************/
        /*********************************************FUNCTIONALITY PROTOTYPING*************************************************************/
        /***********************************************************************************************************************************/
        //Uncomment to enable a mouse picker
        //MousePicker picker = new MousePicker(player, renderer.getProjectionMatrix());



        /***********************************************************************************************************************************/

        while(!KeyboardHandler.isKeyDown(GLFW_KEY_ESCAPE) && !WindowHandler.close()) {
            checkInputs();
            player.move(terrain);
            renderer.processTerrain(terrain);
            entities.forEach(renderer:: processEntity);
            normalMapEntities.forEach(renderer::processNormalMappedEntity);
            renderer.render(lights, player,new Vector4f(0,1,0,10000000)); //backup incase some drivers dont support gldisable properly (clip at unreasonable height)
            entities.forEach(renderer:: processEntity);
            normalMapEntities.forEach(renderer::processNormalMappedEntity);


            // just call this to make the water
            //must have all entities in the list and not created seperately (unless not needed for reflection)\
            //the sun must be the first light in list of lights
            water.setWater(renderer,player,terrain,entities,lights);
            // Uncomment to  display GUI
           // guiRenderer.render(guis);
            WindowHandler.updateWindow();
        }
        //then call this to clean up water
        water.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
    }

    public static void checkInputs() {
        if(KeyboardHandler.isKeyDown(GLFW_KEY_1)) {
            state = 1;
            lights.get(0).setColour(new Vector3f(1, 0.1f,0.1f ));
        } else if(KeyboardHandler.isKeyDown((GLFW_KEY_0))) {
            state = 0;
            lights.get(0).setColour(new Vector3f(1, 1, 1));
        } else if(KeyboardHandler.isKeyDown((GLFW_KEY_2))) {
            state =  2;
           lights.get(0).setColour(new Vector3f(0.1f, 1, 0.1f));
        } else if(KeyboardHandler.isKeyDown((GLFW_KEY_3))) {
            state = 3;
            lights.get(0).setColour(new Vector3f(0.1f, 0.1f, 1));
        }

    }
}
