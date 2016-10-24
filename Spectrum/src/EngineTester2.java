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

public class EngineTester2 {

    private static int WIDTH = 1920;
    private static int HEIGHT = 1082;
    private static GLFWKeyCallback keyCallback;
    private static MouseHandler mouseCallback;
    private static int state = 0;
    private static List<Light> lights = new ArrayList<>();
    private static List<Entity> entities = new ArrayList<>();
    private static float WATER_LEVEL = -20;
    private static Vector3f startingPos = new Vector3f(724,12,-441);



    public static void main(String[] args) {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        try {
            init();
            loop();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            WindowHandler.closeWindow();
        }
    }

    //Initialisation
    private static void init() throws FileNotFoundException {
        setConfigurations();
        String title = "Spectrum";

        WIDTH = GraphicsConfig.WINDOW_WIDTH;
        HEIGHT = GraphicsConfig.WINDOW_HEIGHT;

        WindowHandler.createWindow(WIDTH, HEIGHT, title);
        glfwSetKeyCallback(WindowHandler.getWindow(), keyCallback = new KeyboardHandler());
        glfwSetCursorPosCallback(WindowHandler.getWindow(), mouseCallback = new MouseHandler());
        glfwSetInputMode(WindowHandler.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);

    }

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
        Fbo fbo = new Fbo(WIDTH, HEIGHT, Fbo.DEPTH_RENDER_BUFFER);

        /*********************************************PARSE OBJECTS*************************************************************************/
        ModelData dragonData = OBJFileLoader.loadOBJ("dragon");
        ModelData tree2Data = OBJFileLoader.loadOBJ("newTree");
        ModelData tree3Data = OBJFileLoader.loadOBJ("tree3");
        ModelData lampData = OBJFileLoader.loadOBJ("lamp");
        ModelData bridgeData = OBJFileLoader.loadOBJ("bridge");


        /*********************************************LOAD RAW DATA AS MODELS***************************************************************/
        RawModel dragonModel = loader.loadToVAO(dragonData.getVertices(), dragonData.getTextureCoords(), dragonData.getNormals(), dragonData.getIndices());
        RawModel treeModel2 = loader.loadToVAO(tree2Data.getVertices(), tree2Data.getTextureCoords(), tree2Data.getNormals(), tree2Data.getIndices());
        RawModel treeModel3 = loader.loadToVAO(tree3Data.getVertices(), tree3Data.getTextureCoords(), tree3Data.getNormals(), tree3Data.getIndices());
        RawModel lampModel = loader.loadToVAO(lampData.getVertices(), lampData.getTextureCoords(), lampData.getNormals(), lampData.getIndices());
        RawModel bridgeModel = loader.loadToVAO(bridgeData.getVertices(), bridgeData.getTextureCoords(), bridgeData.getNormals(), bridgeData.getIndices());
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
        ModelTexture whiteCrateTexture = new ModelTexture((loader.loadTexture("woodGrain")));
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
        TexturedModel bridgeTexturedModel = new TexturedModel(bridgeModel, whiteCrateTexture);
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

        TerrainTexPack texturePack = new TerrainTexPack(backgroundTexture, rTexture,
                gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("mazeBM"));


        /*********************************************LOAD TERRAIN*************************************************************************/
        Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightMapMaze");
        Terrain terrain2 = new Terrain(0, -1, loader, texturePack, blendMap, "visibleTerrainHM");
        terrain.setInvis(0);
        //terrain terrain = new terrain(0, -1, loader, texturePack, blendMap);
        //flat terrain for testing
//        terrain terrain = new terrain(0, -1, loader, texturePack, blendMap, "flatHM");


        /*********************************************CREATE ENTITIES***********************************************************************/
        List<Entity> normalMapEntities = new ArrayList<>();
        Random random = new Random(676452);
//        for(int i = 0; i < 2000; i++) {
//            if(i % 2 == 0) {
//                float z = random.nextFloat() * -800;
//                float x = random.nextFloat() * 800;
//                float y = terrain.getHeightOfTerrain(x, z);
//                if(y > WATER_LEVEL) {
//                    entities.add(new Entity(fernTexturedModel, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.4f));
//                }
//            }
//            if(i % 2 == 0) {
//                float z = random.nextFloat() * -800;
//                float x = random.nextFloat() * 800;
//                float y = terrain.getHeightOfTerrain(x, z);
//                if(y > WATER_LEVEL) {
//                    entities.add(new Entity(shrubTexturedModel, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.1f));
//                }
//            }
//            if(i % 5 == 0) {
//                float z = random.nextFloat() * -800;
//                float x = random.nextFloat() * 800;
//                float y = terrain.getHeightOfTerrain(x, z);
//                float treeHeight = (0.5f*random.nextFloat())/100;
//                if(i%2==0){
//                    if(y > WATER_LEVEL) {
//                        entities.add(new Entity(tree2TexturedModel, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.075f));
//                    }
//                }else{
//                    if(y > WATER_LEVEL) {
//                        entities.add(new Entity(tree3TexturedModel, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.025f));
//                    }
//                }
//            }
//            if(i % 10 == 0) {
//                float z = random.nextFloat() * -800;
//                float x = random.nextFloat() * 800;
//                float y = terrain.getHeightOfTerrain(x, z);
//                if(y > WATER_LEVEL) {
//                    normalMapEntities.add(new Entity(rock, new Vector3f(x, y, z), random.nextFloat() * 360, random.nextFloat() * 360, random.nextFloat() * 360, 0.75f));
//                }
//            }
//
//        }

        /*****************CRATE MODELS FOR TESTING************************/
//        float yPos = 14.5f;
        float yPos = 15;
        entities.add(new Entity(dragon, new Vector3f(404, 10, -84), 0, -45, 0, 6f));
        //Before Island
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(670,        yPos,-372.5f),0,90,0,0.75f,     1   ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(625,        yPos,-372.5f),0,90,0,0.75f,      2  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(441.24847f, yPos, -507.71436f),0,0,0,0.75f,  1  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(451.31772f, yPos, -505.67557f),0,90,0,0.75f, 3  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(569.8842f, yPos, -376.54736f),0,0,0,0.75f,   2  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(412.20633f, yPos, -464.66635f),0,0,0,0.75f,  1  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(412.20633f, yPos, -428.83694f),0,0,0,0.75f,  4  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(406.2989f, yPos, -425.19186f),0,90,0,0.75f,  3  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(388.45737f, yPos, -382.14285f),0,0,0,0.75f,  3  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(397.4346f, yPos, -376.76596f),0,90,0,0.75f,  2  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(388.45737f, yPos, -335.78348f),0,0,0,0.75f,  1  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(542.65094f, yPos, -459.9122f),0,-90,0,0.75f, 3  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(493.08118f, yPos, -459.92096f),0,-90,0,0.75f,2  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(488.73325f, yPos, -464.53683f),0,0,0,0.75f,  1  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(569.8842f, yPos, -421.4191f),0,0,0,0.75f,    1  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(544.491f,   yPos, -466.3053f),0,0,0,0.75f,   2  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(576.5431f, yPos, -508.48062f),0,0,0,0.75f,   3  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(576.5431f, yPos, -539.871f),0,0,0,0.75f,     2  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(599.1711f, yPos, -503.00122f),0,90,0,0.75f,  1  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(614.8921f, yPos, -539.52484f),0,90,0,0.75f,  1  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(496.69675f, yPos, -505.33478f),0,90,0,0.75f, 4  ));
        entities.add(new Entity(bridgeTexturedModel,new Vector3f(496.69675f, yPos, -505.33478f),0,90,0,0.75f, 6  ));
//        entities.add(new Entity(bridgeTexturedModel,new Vector3f(496.69675f, yPos, -505.33478f),0,90,0,0.75f, 6  ));
//        entities.add(new Entity(bridgeTexturedModel,new Vector3f(496.69675f, yPos, -505.33478f),0,90,0,0.75f  ));

        //After Island
       // entities.add(new Entity(bridgeTexturedModel,new Vector3f(461.89313f, yPos, -293.59564f),0,90,0,0.75f));





//        player.setPosition(entities.get(entities.size()-1).getPosition().x,entities.get(entities.size()-1).getPosition().y,entities.get(entities.size()-1).getPosition().z);

        /*********************************************CREATE LIGHTS*************************************************************************/
        Light sun = new Light(new Vector3f(7500000, 15000000, 5000000), new Vector3f(0.7f,0.7f,0.7f));
        lights.add(sun);
        lights.add(new Light(new Vector3f(380, 0, -380), new Vector3f(3, 3, 3), new Vector3f(1, 0.01f, 0.002f)));
        lights.add(new Light(player.getPosition(), new Vector3f(0,0,3), new Vector3f(1,0.005f,0.001f)));
        //lights.add(new Light(new Vector3f(570, 32.5f, -600), new Vector3f(1, 0.725f, 0.137f), new Vector3f(1, 0.01f, 0.002f)));


        /*********************************************CREATE GUIS***************************************************************************/
        GUIRenderer guiRenderer = new GUIRenderer(loader);
        List<GUITexture> guis = new ArrayList<>();
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
        FontType font = new FontType(loader.loadTexture("centaur"), new File("assets/textures/centaur.fnt"));
        GUIText text = new GUIText("Press <R> to restart", 3, font, new Vector2f(0.25f, 0.6f), 0.5f, true);
        GUIText text2 = new GUIText(":(", 12, font, new Vector2f(0.25f, 0.1f), 0.5f, true);
        text.setColour(0.25f, 0.25f, 0.25f);
        text2.setColour(0.25f, 0.25f, 0.25f);
        TextHandler.loadText(text);
        TextHandler.loadText(text2);


        /***********************************************************************************************************************************/

        PostProcessing.init(loader);
        /***********************************************************************************************************************************/

        while(!KeyboardHandler.isKeyDown(GLFW_KEY_ESCAPE) && !WindowHandler.close()) {
            checkInputs();
            player.move(terrain);
            lights.get(2).setPosition(player.getPosition());
            lights.get(2).changeColour();
            ParticleHandler.update(player);

            renderer.renderShadowMap(entities, normalMapEntities, sun);

            /**Uncomment to display particles**/
//            particleSystem.generateParticles(new Vector3f(570, 32.5f, -600));
//            smokeParticles.generateParticles(new Vector3f(570, 32.5f, -600));

            fbo.bindFrameBuffer();
            renderer.processTerrain(terrain);
            entities.forEach(renderer:: processEntity);
            normalMapEntities.forEach(renderer:: processNormalMappedEntity);


            renderer.render(lights, player, new Vector4f(0, 1, 0, 10000000)); //backup incase some drivers dont support gldisable properly (clip at unreasonable height)

            //just call this to make the water
            //must have all entities in the list and not created separately (unless not needed for reflection)\
            //the sun must be the first light in list of lights
            ParticleHandler.renderParticles(player);

            fbo.unbindFrameBuffer();
            water.setWater(renderer, player, terrain2, entities, normalMapEntities, lights);

            PostProcessing.doPostProcessing(fbo.getColourTexture());

            /**Uncomment to  display GUI**/
            //guiRenderer.render(guis);

            /**Uncomment for text rendering**/
        if(player.getPosition().y<WATER_LEVEL)
            TextHandler.render();
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
        if(KeyboardHandler.isKeyDown(GLFW_KEY_UP)){
            entities.get(entities.size()-1).increasePosition(0,0,10*WindowHandler.getFrameTimeSeconds());
            System.out.println(entities.get(entities.size()-1).getPosition().x+"f, yPos, " +entities.get(entities.size()-1).getPosition().z+"f, ");
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_DOWN)){
            entities.get(entities.size()-1).increasePosition(0,0,-10*WindowHandler.getFrameTimeSeconds());
            System.out.println(entities.get(entities.size()-1).getPosition().x+"f, yPos, " +entities.get(entities.size()-1).getPosition().z+"f, ");
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_LEFT)){
            entities.get(entities.size()-1).increasePosition(-10*WindowHandler.getFrameTimeSeconds(),0,0);
            System.out.println(entities.get(entities.size()-1).getPosition().x+"f, yPos, " +entities.get(entities.size()-1).getPosition().z+"f, ");
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_RIGHT)){
            entities.get(entities.size()-1).increasePosition(10*WindowHandler.getFrameTimeSeconds(),0,0);
            System.out.println(entities.get(entities.size()-1).getPosition().x+"f, yPos, " +entities.get(entities.size()-1).getPosition().z+"f, ");
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_1)) {
            state = 1;
            lights.get(0).setColour(new Vector3f(1f, 0.1f, 0.1f));
        } else if(KeyboardHandler.isKeyDown((GLFW_KEY_0))) {
            state = 0;
            lights.get(0).setColour(new Vector3f(0.7f,0.7f,0.7f));
        } else if(KeyboardHandler.isKeyDown((GLFW_KEY_2))) {
            state = 2;
            lights.get(0).setColour(new Vector3f(0.1f, 1, 0.1f));
        } else if(KeyboardHandler.isKeyDown((GLFW_KEY_3))) {
            state = 3;
            lights.get(0).setColour(new Vector3f(0.1f, 0.1f, 1));
        }


    }

    public static void readGraphicsSettings() throws FileNotFoundException {
        String setting = "";
        String valueString = "";
        String stringData = "";
        int posi = 0;
        File input = new File("assets/configFiles/graphicsConfigFile.txt");
        Scanner scan = new Scanner(input);
        while (scan.hasNext()) {
            stringData = scan.nextLine();
            posi = stringData.indexOf(",");
            setting = stringData.substring(0, posi);
            valueString = stringData.substring(posi + 1, stringData.length());
            updateSettings(setting, valueString);
        }
    }

    public static void updateSettings(String setting, String valueString){
        switch (setting){
            case "MSAA":{
                GraphicsConfig.MSAA = Integer.parseInt(valueString);
                break;
            }
            case "SHADOW_MAP_SIZE":{
                GraphicsConfig.SHADOW_MAP_SIZE = Integer.parseInt(valueString);

                break;
            }
            case "WINDOW_HEIGHT":{
                GraphicsConfig.WINDOW_HEIGHT = Integer.parseInt(valueString);

                break;
            }
            case "WINDOW_WIDTH":{
                GraphicsConfig.WINDOW_WIDTH = Integer.parseInt(valueString);

                break;
            }
            case "SHADOW_OFFSET":{
                GraphicsConfig.SHADOW_OFFSET = Integer.parseInt(valueString);

                break;
            }
            case "SHADOW_DISTANCE":{
                GraphicsConfig.SHADOW_DISTANCE = Integer.parseInt(valueString);

                break;
            }
            case "MIPMAP_BIAS":{
                GraphicsConfig.MIPMAP_BIAS = Integer.parseInt(valueString);

                break;
            }
            case "FOV":{
                GraphicsConfig.FOV = Float.parseFloat(valueString);

                break;
            }
            case "AF_LEVEL":{
                GraphicsConfig.AF_LEVEL = Float.parseFloat(valueString);

                break;
            }
            case "DRAW_DISTANCE":{
                GraphicsConfig.DRAW_DISTANCE = Float.parseFloat(valueString);

                break;
            }
            case "PCF_LEVEL":{
                GraphicsConfig.PCF_LEVEL = Integer.parseInt(valueString);

                break;
            }
            default:{
                System.out.println("Setting is not specified");
            }
        }
    }
}
