import Entities.Player;
import Entities.Entity;
import Entities.Light;
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
        ModelData treeData = OBJFileLoader.loadOBJ("tree");
        ModelData tree2Data = OBJFileLoader.loadOBJ("lowPolyTree");

        RawModel dragonModel = loader.loadToVAO(dragonData.getVertices(),dragonData.getTextureCoords(),dragonData.getNormals(),dragonData.getIndices());
        RawModel treeModel = loader.loadToVAO(treeData.getVertices(),treeData.getTextureCoords(),treeData.getNormals(),treeData.getIndices());
        RawModel treeModel2 = loader.loadToVAO(tree2Data.getVertices(),tree2Data.getTextureCoords(),tree2Data.getNormals(),tree2Data.getIndices());
        //RawModel dragonModel = OBJLoader.loadObjModel("dragon", loader);
        //RawModel treeModel = OBJLoader.loadObjModel("tree", loader);

        TexturedModel dragonTexturedModel = new TexturedModel(dragonModel, new ModelTexture(loader.loadTexture("red")));
        TexturedModel treeTexturedModel = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("tree")));
        TexturedModel tree2TexturedModel = new TexturedModel(treeModel2,new ModelTexture((loader.loadTexture("lowPolyTree"))));
        TexturedModel grassTexturedModel = new TexturedModel(OBJLoader.loadObjModel("grassModel",loader), new ModelTexture(loader.loadTexture("grassTexture")));
        grassTexturedModel.getTexture().setHasTransparency(true);
        grassTexturedModel.getTexture().setUseFakeLighting(true);
        TexturedModel fernTexturedModel = new TexturedModel(OBJLoader.loadObjModel("fern",loader),new ModelTexture(loader.loadTexture("fern")));
        fernTexturedModel.getTexture().setHasTransparency(true);

        List<Entity> flora = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < 500; i++){
            flora.add(new Entity(treeTexturedModel,new Vector3f(random.nextFloat() * 800 - 400, 0,random.nextFloat() * -600),0,0,0,3));
            flora.add(new Entity(grassTexturedModel,new Vector3f(random.nextFloat() * 800 - 400, 0,random.nextFloat() * -600),0,0,0,1));
            flora.add(new Entity(fernTexturedModel,new Vector3f(random.nextFloat() * 800 - 400, 0,random.nextFloat() * -600),0,0,0,0.6f));
            flora.add(new Entity(tree2TexturedModel,new Vector3f((random.nextFloat())*800-400,0,random.nextFloat()*-600),0,0,0,0.4f));

        }

        ModelTexture dragonTexture = dragonTexturedModel.getTexture();
        dragonTexture.setShineDamper(5);
        dragonTexture.setReflectivity(0.75f);
        Entity dragonEntity = new Entity(dragonTexturedModel, new Vector3f(0,0,-10),0,0,0, 0.25f);
        Light light = new Light(new Vector3f(3000,2000,20),new Vector3f(1,1,1));


        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexPack texturePack = new TerrainTexPack(backgroundTexture,rTexture,gTexture,bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));




        Terrain terrain = new Terrain(0,-1,loader, texturePack,blendMap);
         Terrain terrain2 = new Terrain(-1,-1,loader,  texturePack,blendMap);




        Player player = new Player(mouseCallback);
        MasterRenderHandler renderer = new MasterRenderHandler();


        while(!KeyboardHandler.isKeyDown(GLFW_KEY_ESCAPE)  && !WindowHandler.close()) {
            player.move();
            renderer.processTerrain(terrain);
            renderer.processTerrain(terrain2);
            renderer.render(light, player);
            renderer.processEntity(dragonEntity);
            flora.forEach(renderer::processEntity);
            WindowHandler.updateWindow();
        }
        renderer.cleanUp();
        loader.cleanUp();
    }
}
