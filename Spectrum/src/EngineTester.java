import Entities.Entity;
import Entities.Light;
import Entities.Player;
import Interface.GuiTexture;
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
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import util.KeyboardHandler;
import util.MouseHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

public class EngineTester {

    private static int WIDTH = 1280;
    private static int HEIGHT = 720;
    private static GLFWKeyCallback keyCallback;
    private static MouseHandler mouseCallback;
    private static Light light = new Light(new Vector3f(3000, 2000, 20), new Vector3f(1, 1, 1));
    private static int state = 0;

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
        //Raw model loader
        ModelLoadHandler loader = new ModelLoadHandler();

        //Parse objects
        ModelData dragonData = OBJFileLoader.loadOBJ("dragon");
        ModelData tree2Data = OBJFileLoader.loadOBJ("lowPolyTree");

        //Load  raw data as a model
        RawModel dragonModel = loader.loadToVAO(dragonData.getVertices(), dragonData.getTextureCoords(), dragonData.getNormals(), dragonData.getIndices());
        RawModel treeModel2 = loader.loadToVAO(tree2Data.getVertices(), tree2Data.getTextureCoords(), tree2Data.getNormals(), tree2Data.getIndices());

        //texture raw model
        ModelTexture fernAtlas = new ModelTexture(loader.loadTexture("fern"));
        fernAtlas.setNumberOfRows(2);
        ModelTexture treeTextureAtlas =  new ModelTexture(loader.loadTexture("lowPolyTree"));
        treeTextureAtlas.setNumberOfRows(2);
        TexturedModel tree2TexturedModel = new TexturedModel(treeModel2,treeTextureAtlas);
        TexturedModel fernTexturedModel = new TexturedModel(OBJLoader.loadObjModel("fern", loader), fernAtlas );
        fernTexturedModel.getTexture().setHasTransparency(true);

        //texture terrain
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass_HIGH_RES"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("soil_HIGH_RES"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("flowers_HIGH_RES"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("road_HIGH_RES"));

        TerrainTexPack texturePack = new TerrainTexPack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        //load terrain
        Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightMap");

        //create entities
        List<Entity> flora = new ArrayList<>();
        Random random = new Random(676452);
        for(int i = 0; i < 400; i++) {
            if(i % 2 == 0) {
                float z = random.nextFloat() * -800;
                float x = random.nextFloat() * 800;
                float y = terrain.getHeightOfTerrain(x, z);
                flora.add(new Entity(fernTexturedModel, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.5f));

            }
            if(i % 5 == 0) {
                float z = random.nextFloat() * -800;
                float x = random.nextFloat() * 800;
                float y = terrain.getHeightOfTerrain(x, z);
                flora.add(new Entity(tree2TexturedModel, random.nextInt(4),new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.4f));
            }

        }
        ModelTexture dragonTexture = new ModelTexture(loader.loadTexture("dragons"));
        dragonTexture.setNumberOfRows(2);

        TexturedModel dragon = new TexturedModel(dragonModel,dragonTexture);
        dragonTexture.setShineDamper(5);
        dragonTexture.setReflectivity(0.75f);
        Entity redDragonEntity = new Entity(dragon,0, new Vector3f(0, 1, -10), 0, 0, 0, 0.25f);
        Entity blueDragonEntity = new Entity(dragon,1, new Vector3f(5, 1, -10), 0, 0, 0, 0.25f);
        Entity greenDragonEntity = new Entity(dragon,2, new Vector3f(10, 1, -10), 0, 0, 0, 0.25f);
        Entity whiteDragonEntity = new Entity(dragon,3, new Vector3f(15, 1, -10), 0, 0, 0, 0.25f);
        List<GuiTexture> guis = new ArrayList<>();
        GuiTexture gui = new GuiTexture(loader.loadTexture("fern"), new Vector2f(0.5f,0.5f), new Vector2f(0.25f,0.25f) );
        guis.add(gui);

        GUIRenderer guiRenderer = new GUIRenderer(loader);
        Player player = new Player(mouseCallback);
        MasterRenderHandler renderer = new MasterRenderHandler();

        while(!KeyboardHandler.isKeyDown(GLFW_KEY_ESCAPE) && !WindowHandler.close()) {
            checkInputs();
            player.move(terrain);
            renderer.processTerrain(terrain);
            renderer.render(light, player);
            //if(state == 1)
                renderer.processEntity(redDragonEntity);
           // else if(state == 2)
                renderer.processEntity(blueDragonEntity);
            //else if(state == 3)
                renderer.processEntity(greenDragonEntity);
            renderer.processEntity(whiteDragonEntity);

            flora.forEach(renderer:: processEntity);
            // Uncomment to  display GUI
            // guiRenderer.render(guis);
            WindowHandler.updateWindow();
        }
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
    }

    public static void checkInputs() {
        if(KeyboardHandler.isKeyDown(GLFW_KEY_1)) {
            state = 1;
           light.setColour(new Vector3f(1, 0.25f,0.25f ));
        } else if(KeyboardHandler.isKeyDown((GLFW_KEY_0))) {
            state = 0;
           light.setColour(new Vector3f(1, 1, 1));
        } else if(KeyboardHandler.isKeyDown((GLFW_KEY_2))) {
            state =  2;
           light.setColour(new Vector3f(0.25f, 1, 0.25f));
        } else if(KeyboardHandler.isKeyDown((GLFW_KEY_3))) {
            state = 3;
            light.setColour(new Vector3f(0.25f, 0.25f, 1));
        }

    }
}
