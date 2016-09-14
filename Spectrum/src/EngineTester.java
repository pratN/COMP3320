import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Entities.Player;
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

        ModelLoadHandler loader = new ModelLoadHandler();

        ModelData dragonData = OBJFileLoader.loadOBJ("dragon");
        ModelData tree2Data = OBJFileLoader.loadOBJ("lowPolyTree");

        RawModel dragonModel = loader.loadToVAO(dragonData.getVertices(), dragonData.getTextureCoords(), dragonData.getNormals(), dragonData.getIndices());
        RawModel treeModel2 = loader.loadToVAO(tree2Data.getVertices(), tree2Data.getTextureCoords(), tree2Data.getNormals(), tree2Data.getIndices());

        TexturedModel dragonTexturedModel = new TexturedModel(dragonModel, new ModelTexture(loader.loadTexture("red")));
        TexturedModel tree2TexturedModel = new TexturedModel(treeModel2, new ModelTexture((loader.loadTexture("lowPolyTree"))));
        TexturedModel grassTexturedModel = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));
        grassTexturedModel.getTexture().setHasTransparency(true);
        grassTexturedModel.getTexture().setUseFakeLighting(true);


        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass_HIGH_RES"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("soil_HIGH_RES"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("flowers_HIGH_RES"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("road_HIGH_RES"));

        TerrainTexPack texturePack = new TerrainTexPack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));


        Terrain terrain = new Terrain(0,-1, loader, texturePack, blendMap,"heightMap");
        List<Entity> flora = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < 400; i++) {
            if (i%20 ==0){
                float z = random.nextFloat() *800-400;
                float x = random.nextFloat() *600;
                float y = terrain.getHeightOfTerrain(x,z);
                flora.add(new Entity(grassTexturedModel, new Vector3f(x,y,z), 0, random.nextFloat()*360, 0, 1));

            }if(i%5==0){
                float z = random.nextFloat() *800-400;
                float x = random.nextFloat() *600;
                float y = terrain.getHeightOfTerrain(x,z);
                flora.add(new Entity(tree2TexturedModel, new Vector3f(x,y,z), 0, random.nextFloat()*360, 0, 0.4f));
            }

        }

        ModelTexture dragonTexture = dragonTexturedModel.getTexture();
        dragonTexture.setShineDamper(5);
        dragonTexture.setReflectivity(0.75f);
        Entity dragonEntity = new Entity(dragonTexturedModel, new Vector3f(0, 0, -10), 0, 0, 0, 0.25f);
        Light light = new Light(new Vector3f(3000, 2000, 20), new Vector3f(1, 1, 1));



        Player player = new Player(mouseCallback);
        MasterRenderHandler renderer = new MasterRenderHandler();


        while(!KeyboardHandler.isKeyDown(GLFW_KEY_ESCAPE) && !WindowHandler.close()) {
            player.move(terrain);
            renderer.processTerrain(terrain);
            renderer.render(light, player);
            renderer.processEntity(dragonEntity);
            flora.forEach(renderer:: processEntity);
            WindowHandler.updateWindow();
        }
        renderer.cleanUp();
        loader.cleanUp();
    }
}
