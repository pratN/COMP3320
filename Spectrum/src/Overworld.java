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

public class Overworld {

    private static GLFWKeyCallback keyCallback;
    private static MouseHandler mouseCallback;
    private static int state = 0;
    private static List<Light> lights = new ArrayList<>();
    private static List<Entity> entities = new ArrayList<>();
    private static List<Entity> shadowEntities = new ArrayList<>();
    private static float WATER_LEVEL = -10;
    private static Vector3f startingPos = new Vector3f(724, 12, -441);
    private static List<GUITexture> guis = new ArrayList<>();
    private static GUITexture redLamp;
    private static GUITexture blueLamp;
    private static GUITexture greenLamp;
    private static GUITexture offLamp;


    public static void run() {
        try {
            loop();

        } finally {
            WindowHandler.closeWindow();
        }
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

        ModelData tree2Data = OBJFileLoader.loadOBJ("newTree");
        ModelData tree3Data = OBJFileLoader.loadOBJ("tree3");
        ModelData lampData = OBJFileLoader.loadOBJ("lamp");
        ModelData mapData = OBJFileLoader.loadOBJ("map");


        /*********************************************LOAD RAW DATA AS MODELS***************************************************************/
        RawModel treeModel2 = loader.loadToVAO(tree2Data.getVertices(), tree2Data.getTextureCoords(), tree2Data.getNormals(), tree2Data.getIndices());
        RawModel treeModel3 = loader.loadToVAO(tree3Data.getVertices(), tree3Data.getTextureCoords(), tree3Data.getNormals(), tree3Data.getIndices());
        RawModel lampModel = loader.loadToVAO(lampData.getVertices(), lampData.getTextureCoords(), lampData.getNormals(), lampData.getIndices());
        RawModel mapModel = loader.loadToVAO(mapData.getVertices(), mapData.getTextureCoords(), mapData.getNormals(), mapData.getIndices());
        RawModel crateModel = NormalMappedObjLoader.loadOBJ("crate", loader);
        RawModel rockModel = NormalMappedObjLoader.loadOBJ("boulder", loader);
        RawModel doorModel = NormalMappedObjLoader.loadOBJ("door", loader);
        /*********************************************CREATE MODEL TEXTURES*****************************************************************/
        ModelTexture shrubTex = new ModelTexture(loader.loadTexture("shrub7"));
        shrubTex.setHasTransparency(true);
        ModelTexture fernAtlas = new ModelTexture(loader.loadTexture("fern"));
        fernAtlas.setNumberOfRows(2);
        ModelTexture treeTexture = new ModelTexture(loader.loadTexture("tree9"));
        ModelTexture tree3Texture = new ModelTexture(loader.loadTexture("tree3"));
        ModelTexture dragonTexture = new ModelTexture(loader.loadTexture("normalMap"));
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
        TexturedModel fernTexturedModel = new TexturedModel(OBJLoader.loadObjModel("fern", loader), fernAtlas);
        TexturedModel lamp = new TexturedModel(lampModel, new ModelTexture(loader.loadTexture("lamp")));
        fernTexturedModel.getTexture().setHasTransparency(true);
        lamp.getTexture().setUseFakeLighting(true);

        TexturedModel crate = new TexturedModel(crateModel, crateTexture);
        TexturedModel rock = new TexturedModel(rockModel, rockTexture);
        /*********************************************TEXTURE TERRAIN***********************************************************************/
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("path"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("mud"));

        TerrainTexPack texturePack = new TerrainTexPack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("overworldBM"));


        /*********************************************LOAD TERRAIN*************************************************************************/
        Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap);
        List<Entity> normalMapEntities = new ArrayList<>();
        Random random = new Random(676472);
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
                float treeHeight = (0.5f * random.nextFloat()) / 100;
                if(i % 2 == 0) {
                    if(y > WATER_LEVEL) {
                        entities.add(new Entity(tree2TexturedModel, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.075f));
                    }
                } else {
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

        /*********************************************CREATE ENTITIES***********************************************************************/


        /*****************CRATE MODELS FOR LEVEL************************/

        /******uncomment the line below to manipulate the last object in the entities list******/

        /*********************************************CREATE LIGHTS*************************************************************************/
        Light sun = new Light(new Vector3f(7500000, 15000000, 5000000), new Vector3f(1f, 1, 1));
        lights.add(sun);
        lights.add(new Light(player.getPosition(), new Vector3f(1, 0, 0), new Vector3f(0.5f, 0.0001f, 0.0001f)));
        //lights.add(new Light(new Vector3f(570, 32.5f, -600), new Vector3f(1, 0.725f, 0.137f), new Vector3f(1, 0.01f, 0.002f)));


        /*********************************************CREATE GUIS***************************************************************************/
        GUIRenderer guiRenderer = new GUIRenderer(loader);
//        GUITexture gui = new GUITexture(loader.loadTexture("gui"), new Vector2f(0f, -0.75f), new Vector2f(1f, 0.25f));
        //guis.add(shadowMap);
        GUITexture colours = new GUITexture(loader.loadTexture("colours"), new Vector2f(-0.75f, 0.9f), new Vector2f(0.2f, 0.05f));
        GUITexture sunGUI = new GUITexture(loader.loadTexture("sun"), new Vector2f(-0.565f, 0.865f), new Vector2f(0.075f, 0.075f));
        redLamp = new GUITexture(loader.loadTexture("redLamp"), new Vector2f(-0.8f, 0.6f), new Vector2f(0.1f, 0.17f));
        greenLamp = new GUITexture(loader.loadTexture("greenLamp"), new Vector2f(-0.8f, 0.6f), new Vector2f(0.1f, 0.17f));
        blueLamp = new GUITexture(loader.loadTexture("blueLamp"), new Vector2f(-0.8f, 0.6f), new Vector2f(0.1f, 0.17f));
        offLamp = new GUITexture(loader.loadTexture("offLamp"), new Vector2f(-0.8f, 0.6f), new Vector2f(0.1f, 0.17f));
        GUITexture goodJob = new GUITexture(loader.loadTexture("goodJob"), new Vector2f(0f, 0.25f), new Vector2f(0.5f, 0.5f));
        List<GUITexture> win = new ArrayList<>();
        win.add(goodJob);
        guis.add(colours);
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
            lights.get(1).setPosition(player.getPosition());
            lights.get(1).changeColour(guis, redLamp, blueLamp, greenLamp, offLamp);
            ParticleHandler.update(player);

            renderer.renderShadowMap(entities, normalMapEntities, sun);

            fbo.bindFrameBuffer();
            renderer.processTerrain(terrain);
            entities.forEach(renderer:: processEntity);
            normalMapEntities.forEach(renderer:: processNormalMappedEntity);


            renderer.render(lights, player, new Vector4f(0, 1, 0, 10000000)); //backup incase some drivers dont support gldisable properly (clip at unreasonable height)

            fbo.unbindFrameBuffer();
            water.setWater(renderer, player, terrain, entities, normalMapEntities, lights);

            PostProcessing.doPostProcessingNoBlur(fbo.getColourTexture());


            /**Uncomment to  display GUI**/
            guiRenderer.render(guis);
            guiRenderer.render(win);
            /**Uncomment for text rendering**/
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

    // uses the direction keys to move the last created entity and prints its location in the vector space
    // this was used to place the elements in the world
    public static void checkInputs() {

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


}
