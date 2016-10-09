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
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextHandler;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.*;
//import World.World;

import Engine.*;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjglx.Sys;
import org.lwjglx.input.Mouse;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;
import particles.Particle;
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
        TextHandler.innit(loader);
        ParticleHandler.init(loader, renderer.getProjectionMatrix());

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

        ModelTexture crateTexture = new ModelTexture((loader.loadTexture("crate")));
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
        TexturedModel tree2TexturedModel = new TexturedModel(treeModel2,treeTextureAtlas);
        TexturedModel fernTexturedModel = new TexturedModel(OBJLoader.loadObjModel("fern", loader), fernAtlas );
        TexturedModel lamp = new TexturedModel(lampModel, new ModelTexture(loader.loadTexture("lamp")));
        fernTexturedModel.getTexture().setHasTransparency(true);
        TexturedModel dragon = new TexturedModel(dragonModel,dragonTexture);
        lamp.getTexture().setUseFakeLighting(true);

        TexturedModel crate = new TexturedModel(crateModel,crateTexture);
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

        normalMapEntities.add(new Entity(crate,new Vector3f(419,-15,-401), 0,0,0,0.025f));
        normalMapEntities.add(new Entity(rock,new Vector3f(395,-15,-418), 0,0,0,0.75f));
        normalMapEntities.add(new Entity(barrel,new Vector3f(409,-15,-413), 0,0,0,0.5f));

        /*********************************************CREATE LIGHTS*************************************************************************/
        lights.add(new Light(new Vector3f(0, 10000, -7000), new Vector3f(0.8f, 0.8f, 0.8f)));
        lights.add(new Light(new Vector3f(380, 0, -380), new Vector3f(3, 3, 3), new Vector3f(1,0.01f,0.002f)));
        lights.add(new Light(new Vector3f(570,32.5f,-600), new Vector3f(1, 0.725f, 0.137f), new Vector3f(1,0.01f,0.002f)));


        /*********************************************CREATE GUIS***************************************************************************/
        GUIRenderer guiRenderer = new GUIRenderer(loader);
        List<GUITexture> guis = new ArrayList<>();
        GUITexture gui = new GUITexture(loader.loadTexture("gui"), new Vector2f(0f,0f), new Vector2f(1f,1f) );
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
        MousePicker picker = new MousePicker(player, renderer.getProjectionMatrix(), terrain);

        FontType font = new FontType(loader.loadTexture("centaur"), new File("assets/textures/centaur.fnt"));
        GUIText text = new GUIText("Sample text", 3, font, new Vector2f(0.5f,0.5f), 0.5f, false);
        text.setColour(0,0,0);

        ParticleTexture firetexture = new ParticleTexture(loader.loadTexture("fire"),8);
        ParticleSystem particleSystem = new ParticleSystem(30,25,-0.01f,3,25,firetexture);
        particleSystem.randomizeRotation();
        particleSystem.setDirection(new Vector3f(-1,0.1f,0.5f), 0.1f);
        particleSystem.setLifeError(0.1f);
        particleSystem.setSpeedError(0.4f);
        particleSystem.setScaleError(0.8f);

        ParticleTexture smoketexture = new ParticleTexture(loader.loadTexture("smoke"),8);
        ParticleSystem smokeParticles = new ParticleSystem(5,15,-0.01f,3,25,smoketexture);
        particleSystem.randomizeRotation();
        particleSystem.setDirection(new Vector3f(-1,0.1f,0.5f), 0.1f);
        particleSystem.setLifeError(0.1f);
        particleSystem.setSpeedError(0.4f);
        particleSystem.setScaleError(0.8f);

        /***********************************************************************************************************************************/

        while(!KeyboardHandler.isKeyDown(GLFW_KEY_ESCAPE) && !WindowHandler.close()) {
            checkInputs();
            player.move(terrain);

            picker.update();
            //System.out.println(picker.getCurrentRay());
            ParticleHandler.update(player);

            /**Uncomment to display particles**/
//            particleSystem.generateParticles(new Vector3f(570,32.5f,-600));
//            smokeParticles.generateParticles(new Vector3f(570,32.5f,-600));
            renderer.processTerrain(terrain);
            entities.forEach(renderer:: processEntity);

            normalMapEntities.forEach(renderer::processNormalMappedEntity);
            renderer.render(lights, player,new Vector4f(0,1,0,10000000)); //backup incase some drivers dont support gldisable properly (clip at unreasonable height)
            entities.forEach(renderer:: processEntity);
            normalMapEntities.forEach(renderer::processNormalMappedEntity);


            //just call this to make the water
            //must have all entities in the list and not created seperately (unless not needed for reflection)\
            //the sun must be the first light in list of lights
            water.setWater(renderer,player,terrain,entities,normalMapEntities,lights);

            ParticleHandler.renderParticles(player);

            /**Uncomment to  display GUI**/
            //guiRenderer.render(guis);

            /**Uncomment for text rendering**/
            //TextHandler.render();
            WindowHandler.updateWindow();
        }
        //then call this to clean up water
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
