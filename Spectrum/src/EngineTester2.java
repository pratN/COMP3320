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
//import World.World;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import util.MousePicker;
import util.NormalMappedObjLoader;
import water.*;

import static org.lwjgl.glfw.GLFW.*;

public class EngineTester2 {

    private static int WIDTH = 1920;
    private static int HEIGHT = 1082;
    private static GLFWKeyCallback keyCallback;
    private static MouseHandler mouseCallback;
    private static int state = 0;
    private static List<Light> lights = new ArrayList<>();
    private static List<Entity> entities = new ArrayList<>();
    private static float WATER_LEVEL = -30;
    private static Vector3f startingPos = new Vector3f(471,-2,-343);

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
        setConfigurations();
        String title = "Spectrum";

        WIDTH = GraphicsConfig.WINDOW_WIDTH;
        HEIGHT = GraphicsConfig.WINDOW_HEIGHT;

        WindowHandler.createWindow(WIDTH, HEIGHT, title);
        glfwSetKeyCallback(WindowHandler.getWindow(), keyCallback = new KeyboardHandler());
        glfwSetCursorPosCallback(WindowHandler.getWindow(), mouseCallback = new MouseHandler());
        glfwSetInputMode(WindowHandler.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);

    }

    private static void setConfigurations(){
        GraphicsConfig.MSAA = 8;
        GraphicsConfig.MIPMAP_BIAS = 0;
        GraphicsConfig.SHADOW_DISTANCE = 150;
        GraphicsConfig.SHADOW_MAP_SIZE = 8192;
        GraphicsConfig.SHADOW_OFFSET = 50;
        GraphicsConfig.WINDOW_HEIGHT = 1080;
        GraphicsConfig.WINDOW_WIDTH = 1920;
        GraphicsConfig.AF_LEVEL = 4;
        GraphicsConfig.DRAW_DISTANCE = 1000;
        GraphicsConfig.FOV = 70;
    }

    //Main Loop
    private static void loop() {
        /*********************************************LOAD RENDERER AND LOADER**************************************************************/
        ModelLoadHandler loader = new ModelLoadHandler();
        Player player = new Player(mouseCallback, startingPos);
        MasterRenderHandler renderer = new MasterRenderHandler(loader, player);
        TextHandler.init(loader);
        ParticleHandler.init(loader, renderer.getProjectionMatrix());
        Fbo fbo = new Fbo(WIDTH, HEIGHT, Fbo.DEPTH_RENDER_BUFFER);

        /*********************************************PARSE OBJECTS*************************************************************************/
        ModelData dragonData = OBJFileLoader.loadOBJ("dragon");
        ModelData tree2Data = OBJFileLoader.loadOBJ("newTree");
        ModelData tree3Data = OBJFileLoader.loadOBJ("tree3");
        ModelData lampData = OBJFileLoader.loadOBJ("lamp");


        /*********************************************LOAD RAW DATA AS MODELS***************************************************************/
        RawModel dragonModel = loader.loadToVAO(dragonData.getVertices(), dragonData.getTextureCoords(), dragonData.getNormals(), dragonData.getIndices());
        RawModel treeModel2 = loader.loadToVAO(tree2Data.getVertices(), tree2Data.getTextureCoords(), tree2Data.getNormals(), tree2Data.getIndices());
        RawModel treeModel3 = loader.loadToVAO(tree3Data.getVertices(), tree3Data.getTextureCoords(), tree3Data.getNormals(), tree3Data.getIndices());
        RawModel lampModel = loader.loadToVAO(lampData.getVertices(), lampData.getTextureCoords(), lampData.getNormals(), lampData.getIndices());
        RawModel crateModel = NormalMappedObjLoader.loadOBJ("crate", loader);
        RawModel barrelModel = NormalMappedObjLoader.loadOBJ("barrel", loader);
        RawModel rockModel = NormalMappedObjLoader.loadOBJ("boulder", loader);


        /*********************************************CREATE MODEL TEXTURES*****************************************************************/
        ModelTexture shrubTex = new ModelTexture(loader.loadTexture("shrub7"));
        shrubTex.setHasTransparency(true);
        ModelTexture fernAtlas = new ModelTexture(loader.loadTexture("fern"));
        fernAtlas.setNumberOfRows(2);
        ModelTexture treeTexture = new ModelTexture(loader.loadTexture("tree9"));
        ModelTexture tree3Texture = new ModelTexture(loader.loadTexture("tree3"));
        ModelTexture dragonTexture = new ModelTexture(loader.loadTexture("red"));
        dragonTexture.setShineDamper(5);
        dragonTexture.setReflectivity(0.75f);
        treeTexture.setHasTransparency(true);
        tree3Texture.setHasTransparency(true);
        ModelTexture crateTexture = new ModelTexture((loader.loadTexture("crate")));
//      ModelTexture whiteCrateTexture = new ModelTexture((loader.loadTexture("partiallyGreen")));
        crateTexture.setNormalMap(loader.loadTexture("crateNormal"));
        crateTexture.setShineDamper(10);
        crateTexture.setReflectivity(0.3f);

        ModelTexture barrelTexture = new ModelTexture((loader.loadTexture("barrel")));
        barrelTexture.setNormalMap(loader.loadTexture("barrelNormal"));
        barrelTexture.setShineDamper(10);
        barrelTexture.setReflectivity(0.5f);

        ModelTexture rockTexture = new ModelTexture((loader.loadTexture("boulder")));
        rockTexture.setNormalMap(loader.loadTexture("boulderNormal"));
        rockTexture.setShineDamper(10);
        rockTexture.setReflectivity(0.2f);


        /*********************************************TEXTURE RAW MODELS*********************************************************************/
        TexturedModel tree2TexturedModel = new TexturedModel(treeModel2, treeTexture);
        TexturedModel tree3TexturedModel = new TexturedModel(treeModel3, tree3Texture);
        TexturedModel shrubTexturedModel = new TexturedModel(OBJLoader.loadObjModel("shrub", loader), shrubTex);
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
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("transparent"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("transparent"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("transparent"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("transparent"));

        TerrainTexPack texturePack = new TerrainTexPack(backgroundTexture, rTexture,
                gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("transparent"));


        /*********************************************LOAD TERRAIN*************************************************************************/
        Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightMapMaze");
        terrain.setInvis(0);
        //Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap);
        //flat terrain for testing
//        Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "flatHM");


        /*********************************************CREATE ENTITIES***********************************************************************/
        List<Entity> entities = new ArrayList<>();
        List<Entity> normalMapEntities = new ArrayList<>();
        Random random = new Random(676452);
        for(int i = 0; i < 2000; i++) {
            if(i % 2 == 0) {
                float z = random.nextFloat() * -800;
                float x = random.nextFloat() * 800;
                float y = terrain.getHeightOfTerrain(x, z);
                if(y > WATER_LEVEL) {
                    entities.add(new Entity(fernTexturedModel, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.4f));
                }
            }
            if(i % 2 == 0) {
                float z = random.nextFloat() * -800;
                float x = random.nextFloat() * 800;
                float y = terrain.getHeightOfTerrain(x, z);
                if(y > WATER_LEVEL) {
                    entities.add(new Entity(shrubTexturedModel, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.1f));
                }
            }
            if(i % 5 == 0) {
                float z = random.nextFloat() * -800;
                float x = random.nextFloat() * 800;
                float y = terrain.getHeightOfTerrain(x, z);
                float treeHeight = (0.5f*random.nextFloat())/100;
                if(i%2==0){
                    if(y > WATER_LEVEL) {
                        entities.add(new Entity(tree2TexturedModel, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.075f));
                    }
                }else{
                    if(y > WATER_LEVEL) {
                        entities.add(new Entity(tree3TexturedModel, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.025f));
                    }
                }
            }
            if(i % 10 == 0) {
                float z = random.nextFloat() * -800;
                float x = random.nextFloat() * 800;
                float y = terrain.getHeightOfTerrain(x, z);
                if(y > WATER_LEVEL) {
                    normalMapEntities.add(new Entity(rock, new Vector3f(x, y, z), random.nextFloat() * 360, random.nextFloat() * 360, random.nextFloat() * 360, 0.75f));
                }
            }

        }

        /*****************CRATE MODELS FOR TESTING************************/
//        entities.add(new Entity(whiteCrate, new Vector3f(416,4,-456), 0, random.nextFloat() * 360, 0, 0.025f,1));
//        entities.add(new Entity(whiteCrate, new Vector3f(448, 4, -435), 0, random.nextFloat() * 360, 0, 0.025f,4));
//        entities.add(new Entity(whiteCrate, new Vector3f(455,4,-415), 0, random.nextFloat() * 360, 0, 0.025f,3));

        entities.add(new Entity(dragon, new Vector3f(600, -10, -600), 0, 0, 0, 6f));
        entities.add(new Entity(lamp, new Vector3f(380, -20, -380), 0, 0, 0, 1));


        /*********************************************CREATE LIGHTS*************************************************************************/
        Light sun = new Light(new Vector3f(7500000, 15000000, 5000000), new Vector3f(1, 1, 0.9f));
        lights.add(sun);
        lights.add(new Light(new Vector3f(380, 0, -380), new Vector3f(3, 3, 3), new Vector3f(1, 0.01f, 0.002f)));
        lights.add(new Light(new Vector3f(570, 32.5f, -600), new Vector3f(1, 0.725f, 0.137f), new Vector3f(1, 0.01f, 0.002f)));


        /*********************************************CREATE GUIS***************************************************************************/
        GUIRenderer guiRenderer = new GUIRenderer(loader);
        List<GUITexture> guis = new ArrayList<>();
        GUITexture shadowMap = new GUITexture(renderer.getShadowMapTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
        //GUITexture gui = new GUITexture(loader.loadTexture("gui"), new Vector2f(0f, -0.75f), new Vector2f(1f, 0.25f));
        //guis.add(shadowMap);


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
        MousePicker picker = new MousePicker(player, renderer.getProjectionMatrix(), terrain);

        FontType font = new FontType(loader.loadTexture("centaur"), new File("assets/textures/centaur.fnt"));
        GUIText text = new GUIText("Sample text", 3, font, new Vector2f(0.5f, 0.5f), 0.5f, false);
        text.setColour(0, 0, 0);

        ParticleTexture firetexture = new ParticleTexture(loader.loadTexture("fire"), 8);
        ParticleSystem particleSystem = new ParticleSystem(30, 25, -0.01f, 3, 25, firetexture);
        particleSystem.randomizeRotation();
        particleSystem.setDirection(new Vector3f(-1, 0.1f, 0.5f), 0.1f);
        particleSystem.setLifeError(0.1f);
        particleSystem.setSpeedError(0.4f);
        particleSystem.setScaleError(0.8f);

        ParticleTexture smoketexture = new ParticleTexture(loader.loadTexture("smoke"), 8);
        ParticleSystem smokeParticles = new ParticleSystem(5, 15, -0.01f, 3, 25, smoketexture);
        particleSystem.randomizeRotation();
        particleSystem.setDirection(new Vector3f(-1, 0.1f, 0.5f), 0.1f);
        particleSystem.setLifeError(0.1f);
        particleSystem.setSpeedError(0.4f);
        particleSystem.setScaleError(0.8f);

        /***********************************************************************************************************************************/

        PostProcessing.init(loader);
        /***********************************************************************************************************************************/

        while(!KeyboardHandler.isKeyDown(GLFW_KEY_ESCAPE) && !WindowHandler.close()) {
            checkInputs();
            player.move(terrain);

            ParticleHandler.update(player);

            renderer.renderShadowMap(entities, normalMapEntities, sun);

            /**Uncomment to display particles**/
            particleSystem.generateParticles(new Vector3f(570, 32.5f, -600));
            smokeParticles.generateParticles(new Vector3f(570, 32.5f, -600));

            fbo.bindFrameBuffer();
            renderer.processTerrain(terrain);
            entities.forEach(renderer:: processEntity);
            normalMapEntities.forEach(renderer:: processNormalMappedEntity);


            renderer.render(lights, player, new Vector4f(0, 1, 0, 10000000)); //backup incase some drivers dont support gldisable properly (clip at unreasonable height)

            //just call this to make the water
            //must have all entities in the list and not created seperately (unless not needed for reflection)\
            //the sun must be the first light in list of lights
            ParticleHandler.renderParticles(player);

            fbo.unbindFrameBuffer();
            water.setWater(renderer, player, terrain, entities, normalMapEntities, lights);

            PostProcessing.doPostProcessing(fbo.getColourTexture());

            /**Uncomment to  display GUI**/
            //guiRenderer.render(guis);

            /**Uncomment for text rendering**/
            //TextHandler.render();
            WindowHandler.updateWindow();
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
    }

    public static void checkInputs() {
        if(KeyboardHandler.isKeyDown(GLFW_KEY_1)) {
            state = 1;
            lights.get(0).setColour(new Vector3f(1, 0.1f, 0.1f));
        } else if(KeyboardHandler.isKeyDown((GLFW_KEY_0))) {
            state = 0;
            lights.get(0).setColour(new Vector3f(0, 0, 0));
        } else if(KeyboardHandler.isKeyDown((GLFW_KEY_2))) {
            state = 2;
            lights.get(0).setColour(new Vector3f(0.1f, 1, 0.1f));
        } else if(KeyboardHandler.isKeyDown((GLFW_KEY_3))) {
            state = 3;
            lights.get(0).setColour(new Vector3f(0.1f, 0.1f, 1));
        }


    }
}
