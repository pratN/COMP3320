import entities.DoorEntity;
import entities.Entity;
import entities.Light;
import entities.Player;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import ui.GUITexture;
import models.RawModel;
import models.TexturedModel;
import terrain.Terrain;
import textures.ModelTexture;
import textures.TerrainTexPack;
import textures.TerrainTexture;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextHandler;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.*;
//import world.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import engine.*;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;
import particles.ParticleHandler;
import particles.ParticleSystem;
import particles.ParticleTexture;
import util.KeyboardHandler;
import util.MouseHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import util.MousePicker;
import util.NormalMappedObjLoader;
import water.*;

import static org.lwjgl.glfw.GLFW.*;

public class GameLoop {

    private static GLFWKeyCallback keyCallback;
    private static MouseHandler mouseCallback;
    private static int state = 0;
    private static List<Light> lights = new ArrayList<>();
    private static List<Entity> entities = new ArrayList<>();
    private static List<Entity> shadowEntities = new ArrayList<>();
    private static float WATER_LEVEL = 0;
    private static Vector3f startingPos = new Vector3f(724, 12, -441);
    private static List<GUITexture> guis = new ArrayList<>();
    private static GUITexture redLamp;
    private static GUITexture blueLamp;
    private static GUITexture greenLamp;
    private static GUITexture offLamp;
    private static boolean win=false;


    public static void main(String[] args) {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        try {
            init();
            loop();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(!win)
            WindowHandler.closeWindow();
        }
    }

    //Initialisation
    private static void init() throws FileNotFoundException {
        setConfigurations();
        String title = "Spectrum";

        WindowHandler.createWindow(GraphicsConfig.WINDOW_WIDTH, GraphicsConfig.WINDOW_HEIGHT, title);
        glfwSetKeyCallback(WindowHandler.getWindow(), keyCallback = new KeyboardHandler());
        glfwSetCursorPosCallback(WindowHandler.getWindow(), mouseCallback = new MouseHandler());
        glfwSetInputMode(WindowHandler.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);

    }

    // loads any config files specified
    private static void setConfigurations() throws FileNotFoundException {
       readGraphicsSettings();
    }

    //Main Loop
    private static void loop() {
        /*********************************************LOAD RENDERER AND LOADER**************************************************************/
        ModelLoadHandler loader = new ModelLoadHandler();
        Player player = new Player(mouseCallback, startingPos, WATER_LEVEL);
        player.increaseYaw(100);
        MasterRenderHandler renderer = new MasterRenderHandler(loader, player);
        TextHandler.init(loader);
        ParticleHandler.init(loader, renderer.getProjectionMatrix());
        Fbo fbo = new Fbo(GraphicsConfig.WINDOW_WIDTH, GraphicsConfig.WINDOW_HEIGHT, Fbo.DEPTH_RENDER_BUFFER);

        /*********************************************PARSE OBJECTS*************************************************************************/
        ModelData dragonData = OBJFileLoader.loadOBJ("dragon");
        ModelData tree2Data = OBJFileLoader.loadOBJ("newTree");
        ModelData tree3Data = OBJFileLoader.loadOBJ("tree3");
        ModelData lampData = OBJFileLoader.loadOBJ("lamp");
        ModelData bridgeData = OBJFileLoader.loadOBJ("bridge");
        ModelData portalData = OBJFileLoader.loadOBJ("portal");
        ModelData mapData = OBJFileLoader.loadOBJ("map");


        /*********************************************LOAD RAW DATA AS MODELS***************************************************************/
        RawModel dragonModel = loader.loadToVAO(dragonData.getVertices(), dragonData.getTextureCoords(), dragonData.getNormals(), dragonData.getIndices());
        RawModel treeModel2 = loader.loadToVAO(tree2Data.getVertices(), tree2Data.getTextureCoords(), tree2Data.getNormals(), tree2Data.getIndices());
        RawModel treeModel3 = loader.loadToVAO(tree3Data.getVertices(), tree3Data.getTextureCoords(), tree3Data.getNormals(), tree3Data.getIndices());
        RawModel lampModel = loader.loadToVAO(lampData.getVertices(), lampData.getTextureCoords(), lampData.getNormals(), lampData.getIndices());
        RawModel bridgeModel = loader.loadToVAO(bridgeData.getVertices(), bridgeData.getTextureCoords(), bridgeData.getNormals(), bridgeData.getIndices());
        RawModel mapModel = loader.loadToVAO(mapData.getVertices(), mapData.getTextureCoords(), mapData.getNormals(), mapData.getIndices());
        RawModel crateModel = NormalMappedObjLoader.loadOBJ("crate", loader);
        RawModel barrelModel = NormalMappedObjLoader.loadOBJ("barrel", loader);
        RawModel rockModel = NormalMappedObjLoader.loadOBJ("boulder", loader);
        RawModel stepsModel = NormalMappedObjLoader.loadOBJ("steps", loader);
        RawModel doorModel = NormalMappedObjLoader.loadOBJ("door", loader);
        RawModel portalModel = loader.loadToVAO(portalData.getVertices(),portalData.getTextureCoords(),portalData.getNormals(),portalData.getIndices());
        /*********************************************CREATE MODEL TEXTURES*****************************************************************/
        ModelTexture shrubTex = new ModelTexture(loader.loadTexture("shrub7"));
        shrubTex.setHasTransparency(true);
        ModelTexture fernAtlas = new ModelTexture(loader.loadTexture("fern"));
        fernAtlas.setNumberOfRows(2);
        ModelTexture treeTexture = new ModelTexture(loader.loadTexture("tree9"));
        ModelTexture tree3Texture = new ModelTexture(loader.loadTexture("tree3"));
        ModelTexture dragonTexture = new ModelTexture(loader.loadTexture("red"));
        ModelTexture portalTexture = new ModelTexture(loader.loadTexture("red"));
        portalTexture.setReflectivity(1);
        portalTexture.setShineDamper(5);
        ModelTexture doorTexture = new ModelTexture(loader.loadTexture("door"));
        doorTexture.setNormalMap(loader.loadTexture("doorN"));
        doorTexture.setReflectivity(0.2f);
        doorTexture.setShineDamper(10);
        ModelTexture mapTex = new ModelTexture(loader.loadTexture("mapTex"));
        dragonTexture.setShineDamper(5);
        dragonTexture.setReflectivity(0.75f);
        treeTexture.setHasTransparency(true);
        tree3Texture.setHasTransparency(true);
        ModelTexture crateTexture = new ModelTexture((loader.loadTexture("crate")));
        ModelTexture whiteCrateTexture = new ModelTexture((loader.loadTexture("woodGrain")));
        crateTexture.setNormalMap(loader.loadTexture("crateNormal"));
        crateTexture.setShineDamper(10);
        crateTexture.setReflectivity(0.3f);
        ModelTexture stepsTex = new ModelTexture(loader.loadTexture("stepTex"));
        stepsTex.setNormalMap(loader.loadTexture("stepTexN"));
        stepsTex.setShineDamper(10);
        stepsTex.setReflectivity(0.01f);
        ModelTexture barrelTexture = new ModelTexture((loader.loadTexture("barrel")));
        barrelTexture.setNormalMap(loader.loadTexture("barrelNormal"));
        barrelTexture.setShineDamper(10);
        barrelTexture.setReflectivity(0.5f);

        ModelTexture rockTexture = new ModelTexture((loader.loadTexture("boulder")));
        ModelTexture rockTexture2 = new ModelTexture((loader.loadTexture("boulder")));
        rockTexture.setNormalMap(loader.loadTexture("boulderNormal"));
        rockTexture.setShineDamper(10);
        rockTexture.setReflectivity(0.2f);


        /*********************************************TEXTURE RAW MODELS*********************************************************************/
        TexturedModel tree2TexturedModel = new TexturedModel(treeModel2, treeTexture);
        TexturedModel tree3TexturedModel = new TexturedModel(treeModel3, tree3Texture);
        TexturedModel shrubTexturedModel = new TexturedModel(OBJLoader.loadObjModel("shrub", loader), shrubTex);
        TexturedModel bridgeTexturedModel = new TexturedModel(bridgeModel, whiteCrateTexture);
        TexturedModel stepsTexturedModel = new TexturedModel(stepsModel, stepsTex);
        TexturedModel doorTexModel = new TexturedModel(doorModel, doorTexture);
        TexturedModel portalTexModel = new TexturedModel(portalModel,portalTexture);
        portalTexModel.getTexture().setUseFakeLighting(true);
        TexturedModel mapTexModel = new TexturedModel(mapModel,mapTex);
        bridgeTexturedModel.getTexture().setHasTransparency(true);
        bridgeTexturedModel.getTexture().setUseFakeLighting(true);
        TexturedModel fernTexturedModel = new TexturedModel(OBJLoader.loadObjModel("fern", loader), fernAtlas);
        TexturedModel lamp = new TexturedModel(lampModel, new ModelTexture(loader.loadTexture("lamp")));
        fernTexturedModel.getTexture().setHasTransparency(true);
        TexturedModel dragon = new TexturedModel(dragonModel, dragonTexture);
        lamp.getTexture().setUseFakeLighting(true);

        TexturedModel crate = new TexturedModel(crateModel, crateTexture);
        //  TexturedModel whiteCrate = new TexturedModel(crateModel,whiteCrateTexture);
        TexturedModel barrel = new TexturedModel(barrelModel, barrelTexture);
        TexturedModel rock = new TexturedModel(rockModel, rockTexture);
        //  whiteCrate.getTexture().setUseFakeLighting(true);
        //  whiteCrate.getTexture().setHasTransparency(true);

        /*********************************************TEXTURE TERRAIN***********************************************************************/
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("brown"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("mud"));

        TerrainTexPack texturePack = new TerrainTexPack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("mazeBM"));


        /*********************************************LOAD TERRAIN*************************************************************************/
        Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightMapMaze");
        Terrain terrain2 = new Terrain(0, -1, loader, texturePack, blendMap, "visibleTerrainHM");
//        terrain.setInvis(0);
//        Terrain terrain2 = new Terrain(0, -1, loader, texturePack, blendMap);
        //flat terrain for testing
//        Terrain terrain2 = new Terrain(0, -1, loader, texturePack, blendMap, "flatHM");


        /*********************************************CREATE ENTITIES***********************************************************************/
        List<Entity> normalMapEntities = new ArrayList<>();
        Random random = new Random(676472);
        for(int i = 0; i < 2000; i++) {
            if(i % 2 == 0) {
                float z = random.nextFloat() * -800;
                float x = random.nextFloat() * 800;
                float y = terrain2.getHeightOfTerrain(x, z);
                if(y > WATER_LEVEL) {
                    shadowEntities.add(new Entity(fernTexturedModel, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.4f));

                }
            }
            if(i % 2 == 0) {
                float z = random.nextFloat() * -800;
                float x = random.nextFloat() * 800;
                float y = terrain2.getHeightOfTerrain(x, z);
                if(y > WATER_LEVEL) {
                    shadowEntities.add(new Entity(shrubTexturedModel, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.1f));
                }
            }
            if(i % 5 == 0) {
                float z = random.nextFloat() * -800;
                float x = random.nextFloat() * 800;
                float y = terrain2.getHeightOfTerrain(x, z);
                float treeHeight = (0.5f * random.nextFloat()) / 100;
                if(i % 2 == 0) {
                    if(y > WATER_LEVEL) {
                        shadowEntities.add(new Entity(tree2TexturedModel, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.075f));
                    }
                } else {
                    if(y > WATER_LEVEL) {
                        shadowEntities.add(new Entity(tree3TexturedModel, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.025f));
                    }
                }
            }
            if(i % 10 == 0) {
                float z = random.nextFloat() * -800;
                float x = random.nextFloat() * 800;
                float y = terrain2.getHeightOfTerrain(x, z);
                if(y > WATER_LEVEL) {
                    normalMapEntities.add(new Entity(rock, new Vector3f(x, y, z), random.nextFloat() * 360, random.nextFloat() * 360, random.nextFloat() * 360, 0.75f));

                }
            }

        }

        /*****************CRATE MODELS FOR LEVEL************************/
//        float yPos = 14.5f;
        float yPos = 15;
        //Before Island
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(670, yPos, -372.5f), 0, 90, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(625, yPos, -372.5f), 0, 90, 0, 0.75f, 2));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(441.24847f, yPos, -507.71436f), 0, 0, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(451.31772f, yPos, -505.67557f), 0, 90, 0, 0.75f, 3));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(569.8842f, yPos, -376.54736f), 0, 0, 0, 0.75f, 2));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(412.20633f, yPos, -464.66635f), 0, 0, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(412.20633f, yPos, -428.83694f), 0, 0, 0, 0.75f, 4));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(406.2989f, yPos, -425.19186f), 0, 90, 0, 0.75f, 3));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(388.45737f, yPos, -382.14285f), 0, 0, 0, 0.75f, 3));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(397.4346f, yPos, -376.76596f), 0, 90, 0, 0.75f, 2));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(388.45737f, yPos, -335.78348f), 0, 0, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(542.65094f, yPos, -459.9122f), 0, -90, 0, 0.75f, 3));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(493.08118f, yPos, -459.92096f), 0, -90, 0, 0.75f, 2));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(488.73325f, yPos, -464.53683f), 0, 0, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(569.8842f, yPos, -421.4191f), 0, 0, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(544.491f, yPos, -466.3053f), 0, 0, 0, 0.75f, 2));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(576.5431f, yPos, -508.48062f), 0, 0, 0, 0.75f, 3));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(576.5431f, yPos, -539.871f), 0, 0, 0, 0.75f, 2));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(599.1711f, yPos, -503.00122f), 0, 90, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(614.8921f, yPos, -539.52484f), 0, 90, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(496.69675f, yPos, -505.33478f), 0, 90, 0, 0.75f, 4));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(496.69675f, yPos, -505.33478f), 0, 90, 0, 0.75f, 6));

        //After Island
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(461.89313f, yPos, -291.4318f), 0, 90, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(502.1963f, yPos, -291.4318f), 0, 90, 0, 0.75f, 3));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(491.9959f, yPos, -250.03143f), 0, 0, 0, 0.75f, 4));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(491.99338f, yPos, -214.13712f), 0, 0, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(486.98438f, yPos, -211.32594f), 0, 90, 0, 0.75f, 6));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(433.74054f, yPos, -213.67116f), 0, 0, 0, 0.73f, 3));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(428.69974f, yPos, -210.46466f), 0, 90, 0, 0.75f, 2));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(384.2609f, yPos, -210.46466f), 0, 90, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(338.81982f, yPos, -210.46466f), 0, 90, 0, 0.75f, 5));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(324.14334f, yPos, -184.91844f), 0, 0, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(284.72943f, yPos, -213.33049f), 0, 0, 0, 0.75f, 3));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(284.72943f, yPos, -258.0789f), 0, 0, 0, 0.75f, 2));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(292.93362f, yPos, -284.79855f), 0, 90, 0, 0.75f, 6));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(252.70674f, yPos, -303.5186f), 0, 0, 0, 0.75f, 3));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(252.7187f, yPos, -344.6667f), 0, 0, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(307.36166f, yPos, -333.30197f), 0, 90, 0, 0.75f, 3));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(351.96466f, yPos, -333.30197f), 0, 90, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(330.08716f, yPos, -307.90884f), 0, 0, 0, 0.75f, 6));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(305.34604f, yPos, -372.38052f), 0, 90, 0, 0.75f, 2));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(307.66544f, yPos, -376.55795f), 0, 0, 0, 0.75f, 5));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(307.66544f, yPos, -421.15625f), 0, 0, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(307.66544f, yPos, -465.93457f), 0, 0, 0, 0.75f, 4));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(307.66544f, yPos, -497.48065f), 0, 0, 0, 0.75f, 2));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(346.25888f, yPos, -469.5884f), 0, 90, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(308.34827f, yPos, -449.6984f), 0, 90, 0, 0.75f, 2));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(268.7676f, yPos, -454.68552f), 0, 0, 0, 0.75f, 3));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(263.43643f, yPos, -482.56235f), 0, 90, 0, 0.75f, 5));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(358.35077f, yPos, -524.6396f), 0, 90, 0, 0.75f, 6));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(362.68536f, yPos, -509.45697f), 0, 0, 0, 0.75f, 3));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(362.68536f, yPos, -555.22565f), 0, 0, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(357.17316f, yPos, -582.78644f), 0, 90, 0, 0.75f, 2));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(311.59515f, yPos, -582.78644f), 0, 90, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(317.45377f, yPos, -583.6068f), 0, 0, 0, 0.75f, 5));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(281.86057f, yPos, -585.9258f), 0, 0, 0, 0.75f, 2));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(281.86057f, yPos, -621.5087f), 0, 0, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(276.52582f, yPos, -628.34735f), 0, 90, 0, 0.75f, 6));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(255.81393f, yPos, -555.8703f), 0, 0, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(255.81393f, yPos, -533.1449f), 0, 0, 0, 0.75f, 3));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(250.28558f, yPos, -531.1361f), 0, 90, 0, 0.75f, 2));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(227.89377f, yPos, -531.1361f), 0, 90, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(378.7226f, yPos, -181.24074f), 0, 90, 0, 0.75f, 5));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(405.93542f, yPos, -181.24074f), 0, 90, 0, 0.75f, 2));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(398.24646f, yPos, -154.52837f), 0, 0, 0, 0.75f, 4));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(398.24646f, yPos, -118.76799f), 0, 0, 0, 0.75f, 3));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(546.57465f, yPos, -239.6934f), 0, 90, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(582.4849f, yPos, -239.6934f), 0, 90, 0, 0.75f, 2));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(573.2938f, yPos, -260.22775f), 0, 0, 0, 0.75f, 6));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(466.57306f, yPos, -310.49854f), 0, 0, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(466.57306f, yPos, -346.24707f), 0, 0, 0, 0.75f, 4));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(508.98676f, yPos, -372.46277f), 0, 90, 0, 0.75f, 2));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(567.7948f, yPos, -553.6704f), 0, 90, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(524.53925f, yPos, -558.1871f), 0, 0, 0, 0.75f, 2));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(524.53925f, yPos, -576.24146f), 0, 0, 0, 0.75f, 3));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(482.60577f, yPos, -547.1781f), 0, 90, 0, 0.75f, 2));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(475.76157f, yPos, -566.7018f), 0, 0, 0, 0.75f, 1));
        entities.add(new Entity(bridgeTexturedModel, new Vector3f(469.4178f, yPos, -595.7776f), 0, 90, 0, 0.75f, 3));

        normalMapEntities.add(new Entity(stepsTexturedModel, new Vector3f(686.4702f, yPos, -381.19562f), 0, 90, 0, 0.75f));
        normalMapEntities.add(new Entity(stepsTexturedModel, new Vector3f(175.07776f, yPos - 1, -535.6924f), 0, -90, 0, 0.75f));
        normalMapEntities.add(new Entity(doorTexModel, new Vector3f(93,15,-543),0,-90,0,0.25f));
//        player.setPosition(85,15,-543);
        DoorEntity levelPortal = new DoorEntity(portalTexModel, new Vector3f(93,14.5f,-543),0,-90,0,0.3f);
        levelPortal.setBounds(91,93,0,25,-546,-538);
        entities.add(levelPortal);
        shadowEntities.add(new Entity(mapTexModel, new Vector3f(413, 16, -83), 0, 90, 0, 5f));
        shadowEntities.add(new Entity(dragon, new Vector3f(680, 11, -389), 0, 180, 0, 1f));

        /******uncomment the line below to manipulate the last object in the entities list******/

        /*********************************************CREATE LIGHTS*************************************************************************/
        Light sun = new Light(new Vector3f(-7500000, 15000000, 5000000), new Vector3f(1f, 1, 1));
        lights.add(sun);
        lights.add(new Light(new Vector3f(380, 0, -380), new Vector3f(3, 3, 3), new Vector3f(1, 0.01f, 0.002f)));
        lights.add(new Light(player.getPosition(), new Vector3f(1, 0, 0), new Vector3f(0.5f, 0.0001f, 0.0001f)));
        //lights.add(new Light(new Vector3f(570, 32.5f, -600), new Vector3f(1, 0.725f, 0.137f), new Vector3f(1, 0.01f, 0.002f)));


        /*********************************************CREATE GUIS***************************************************************************/
        GUIRenderer guiRenderer = new GUIRenderer(loader);
//        GUITexture gui = new GUITexture(loader.loadTexture("gui"), new Vector2f(0f, -0.75f), new Vector2f(1f, 0.25f));
        //guis.add(shadowMap);
        GUITexture colours = new GUITexture(loader.loadTexture("colours"), new Vector2f(-0.75f, 0.9f), new Vector2f(0.2f, 0.05f));
        GUITexture sunGUI = new GUITexture(loader.loadTexture("sun"), new Vector2f(-0.565f, 0.865f), new Vector2f(0.075f, 0.075f));
        GUITexture backdropGUI = new GUITexture(loader.loadTexture("backdrop"), new Vector2f(0.75f, -0.25f), new Vector2f(0.25f, 0.5f));
        GUITexture failMessage = new GUITexture(loader.loadTexture("failMessage"), new Vector2f(0f, 0f), new Vector2f(0.5f, 0.5f));
        redLamp = new GUITexture(loader.loadTexture("redLamp"), new Vector2f(-0.8f, 0.6f), new Vector2f(0.1f, 0.17f));
        greenLamp = new GUITexture(loader.loadTexture("greenLamp"), new Vector2f(-0.8f, 0.6f), new Vector2f(0.1f, 0.17f));
        blueLamp = new GUITexture(loader.loadTexture("blueLamp"), new Vector2f(-0.8f, 0.6f), new Vector2f(0.1f, 0.17f));
        offLamp = new GUITexture(loader.loadTexture("offLamp"), new Vector2f(-0.8f, 0.6f), new Vector2f(0.1f, 0.17f));
        List<GUITexture> backdrop = new ArrayList<>();
        List<GUITexture> fail = new ArrayList<>();
        fail.add(failMessage);
        backdrop.add(backdropGUI);
        guis.add(colours);
        //guis.add(backdropGUI);
        guis.add(sunGUI);
        guis.add(offLamp);

        /*********************************************CREATE PLAYER*************************************************************************/


        /*********************************************CREATE WATER**************************************************************************/
        //make a list of water tiles
        //ideally only 1 tile or atleast have all same height as reflection only works off one height for now
        List<WaterTile> waters = new ArrayList<>();
        waters.add(new WaterTile(400, -400, WATER_LEVEL, 400, 20)); //the tiles where to add the water (size specified in tiles class)
        //(x,z,y,size,#tiles used for texturing)
        Water water = new Water(waters, loader, renderer, fbo);

        /***********************************************************************************************************************************/
        /*********************************************FUNCTIONALITY PROTOTYPING*************************************************************/
        /***********************************************************************************************************************************/
        FontType font = new FontType(loader.loadTexture("candara"), new File("assets/textures/candara.fnt"));
        ParticleTexture firetexture = new ParticleTexture(loader.loadTexture("cosmic"), 4);
        ParticleSystem particleSystem = new ParticleSystem(5, 5, -0.01f, 1, 5, firetexture);
        particleSystem.randomizeRotation();
        particleSystem.setDirection(new Vector3f(0.1f, 1f, 0.15f), 0.1f);
        particleSystem.setLifeError(0.1f);
        particleSystem.setSpeedError(0.4f);
        particleSystem.setScaleError(0.8f);

        ParticleTexture smoketexture = new ParticleTexture(loader.loadTexture("smoke"), 8);
        ParticleSystem smokeParticles = new ParticleSystem(5, 15, -0.01f, 3, 25, smoketexture);
        smokeParticles.randomizeRotation();
        smokeParticles.setDirection(new Vector3f(-1, 0.1f, 0.5f), 0.1f);
        smokeParticles.setLifeError(0.1f);
        smokeParticles.setSpeedError(0.4f);
        smokeParticles.setScaleError(0.8f);

        /***********************************************************************************************************************************/

        PostProcessing.init(loader);
        /***********************************************************************************************************************************/

        while(!KeyboardHandler.isKeyDown(GLFW_KEY_ESCAPE) && !WindowHandler.close()) {
            checkInputs();
            player.move(terrain);
            lights.get(2).setPosition(player.getPosition());
            lights.get(2).changeColour(guis, redLamp, blueLamp, greenLamp, offLamp);
            particleSystem.generateParticles(new Vector3f(92, 24, -547));
            particleSystem.generateParticles(new Vector3f(92, 24, -539));
            ParticleHandler.update(player);

            renderer.renderShadowMap(shadowEntities, normalMapEntities, sun);

            /**Uncomment to display particles**/
//            particleSystem.generateParticles(new Vector3f(570, 32.5f, -600));
//            smokeParticles.generateParticles(new Vector3f(570, 32.5f, -600));

            fbo.bindFrameBuffer();
            renderer.processTerrain(terrain2);
            entities.forEach(renderer:: processEntity);
            shadowEntities.forEach(renderer:: processEntity);
            normalMapEntities.forEach(renderer:: processNormalMappedEntity);


            renderer.render(lights, player, new Vector4f(0, 1, 0, 10000000)); //backup incase some drivers dont support gldisable properly (clip at unreasonable height)

            //just call this to make the water
            //must have all entities in the list and not created separately (unless not needed for reflection)\
            //the sun must be the first light in list of lights
            ParticleHandler.renderParticles(player);

            fbo.unbindFrameBuffer();
            water.setWater(renderer, player, terrain2, entities, normalMapEntities, lights);
            if(player.getPosition().y < WATER_LEVEL) {
                PostProcessing.doPostProcessing(fbo.getColourTexture());
                guiRenderer.render(fail);

            } else {
                PostProcessing.doPostProcessingNoBlur(fbo.getColourTexture());
            }

            /**Uncomment to  display GUI**/
            guiRenderer.render(guis);
            if(f1Pressed())guiRenderer.render(backdrop);
            TextHandler.render();
            /**Uncomment for text rendering**/
            WindowHandler.updateWindow();
            if(levelPortal.checkCollision(player)){
                win=true;
                break;
            }
        }
        //then call this to clean up water
        PostProcessing.cleanUp();
        fbo.cleanUp();
        water.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        TextHandler.cleanUp();
        ParticleHandler.cleanUp();
        if(win){
            Overworld.run();
        }
    }

    // uses the direction keys to move the last created entity and prints its location in the vector space
    // this was used to place the elements in the world
    public static boolean f1Pressed(){
        if(KeyboardHandler.isKeyDown(GLFW_KEY_F1))
            return true;
        return false;
    }
    public static void checkInputs() {
        if(KeyboardHandler.isKeyDown(GLFW_KEY_UP)) {
            entities.get(entities.size() - 1).increasePosition(0, 0, 10 * WindowHandler.getFrameTimeSeconds());
            System.out.println(entities.get(entities.size() - 1).getPosition().x + "f, yPos, " + entities.get(entities.size() - 1).getPosition().z + "f, ");
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_DOWN)) {
            entities.get(entities.size() - 1).increasePosition(0, 0, -10 * WindowHandler.getFrameTimeSeconds());
            System.out.println(entities.get(entities.size() - 1).getPosition().x + "f, yPos, " + entities.get(entities.size() - 1).getPosition().z + "f, ");
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_LEFT)) {
            entities.get(entities.size() - 1).increasePosition(-10 * WindowHandler.getFrameTimeSeconds(), 0, 0);
            System.out.println(entities.get(entities.size() - 1).getPosition().x + "f, yPos, " + entities.get(entities.size() - 1).getPosition().z + "f, ");
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_RIGHT)) {
            entities.get(entities.size() - 1).increasePosition(10 * WindowHandler.getFrameTimeSeconds(), 0, 0);
            System.out.println(entities.get(entities.size() - 1).getPosition().x + "f, yPos, " + entities.get(entities.size() - 1).getPosition().z + "f, ");
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_1)) {
            lights.get(0).setColour(new Vector3f(1f, 0.1f, 0.1f));
            guis.get(1).setPosition(new Vector2f(-0.865f, guis.get(1).getPosition().y));
        } else if(KeyboardHandler.isKeyDown((GLFW_KEY_0))) {
            lights.get(0).setColour(new Vector3f(0.7f, 0.7f, 0.7f));
            guis.get(1).setPosition(new Vector2f(-0.565f, guis.get(1).getPosition().y));
        } else if(KeyboardHandler.isKeyDown((GLFW_KEY_2))) {
            lights.get(0).setColour(new Vector3f(0.1f, 1, 0.1f));
            guis.get(1).setPosition(new Vector2f(-0.765f, guis.get(1).getPosition().y));
        } else if(KeyboardHandler.isKeyDown((GLFW_KEY_3))) {
            lights.get(0).setColour(new Vector3f(0.1f, 0.1f, 1));
            guis.get(1).setPosition(new Vector2f(-0.665f, guis.get(1).getPosition().y));
        }


    }

    // used to read graphics settings from a graphics config file
    public static void readGraphicsSettings() throws FileNotFoundException {
        String setting = "";
        String valueString = "";
        String stringData = "";
        int posi = 0;
        File input = new File("assets/configFiles/graphicsConfigFile.txt");
        Scanner scan = new Scanner(input);
        while(scan.hasNext()) {
            stringData = scan.nextLine();
            posi = stringData.indexOf(",");
            setting = stringData.substring(0, posi);
            valueString = stringData.substring(posi + 1, stringData.length());
            updateSettings(setting, valueString);
        }
    }

    // Used to upload graphics settings from the graphics file to the GraphicsConfig class
    public static void updateSettings(String setting, String valueString) {
        switch(setting) {
            case "MSAA": {
                GraphicsConfig.MSAA = Integer.parseInt(valueString);
                break;
            }
            case "SHADOW_MAP_SIZE": {
                GraphicsConfig.SHADOW_MAP_SIZE = Integer.parseInt(valueString);

                break;
            }
            case "WINDOW_HEIGHT": {
                GraphicsConfig.WINDOW_HEIGHT = Integer.parseInt(valueString);

                break;
            }
            case "WINDOW_WIDTH": {
                GraphicsConfig.WINDOW_WIDTH = Integer.parseInt(valueString);

                break;
            }
            case "SHADOW_OFFSET": {
                GraphicsConfig.SHADOW_OFFSET = Integer.parseInt(valueString);

                break;
            }
            case "SHADOW_DISTANCE": {
                GraphicsConfig.SHADOW_DISTANCE = Integer.parseInt(valueString);

                break;
            }
            case "MIPMAP_BIAS": {
                GraphicsConfig.MIPMAP_BIAS = Integer.parseInt(valueString);

                break;
            }
            case "FOV": {
                GraphicsConfig.FOV = Float.parseFloat(valueString);

                break;
            }
            case "AF_LEVEL": {
                GraphicsConfig.AF_LEVEL = Float.parseFloat(valueString);

                break;
            }
            case "DRAW_DISTANCE": {
                GraphicsConfig.DRAW_DISTANCE = Float.parseFloat(valueString);

                break;
            }
            case "PCF_LEVEL": {
                GraphicsConfig.PCF_LEVEL = Integer.parseInt(valueString);

                break;
            }
            default: {
                System.out.println("Setting is not specified");
            }
        }
    }
}
