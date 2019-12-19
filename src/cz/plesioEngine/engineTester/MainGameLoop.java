package cz.plesioEngine.engineTester;

import cz.plesioEngine.models.TexturedModel;
import cz.plesioEngine.normalMappingObjConverter.NormalMappedObjLoader;
import cz.plesioEngine.objConverter.ModelData;
import cz.plesioEngine.objConverter.OBJFileLoader;
import cz.plesioEngine.particles.ParticleMaster;
import cz.plesioEngine.particles.ParticleSystem;
import cz.plesioEngine.particles.ParticleTexture;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import cz.plesioEngine.renderEngine.DisplayManager;
import cz.plesioEngine.renderEngine.Loader;
import cz.plesioEngine.renderEngine.MasterRenderer;
import cz.plesioEngine.terrains.Terrain;
import cz.plesioEngine.textures.ModelTexture;
import cz.plesioEngine.textures.TerrainTexture;
import cz.plesioEngine.textures.TerrainTexturePack;
import cz.plesioEngine.toolbox.ConsoleInput;
import cz.plesioEngine.toolbox.MousePicker;
import cz.plesioEngine.water.WaterTile;
import cz.plesioEngine.entities.Camera;
import cz.plesioEngine.entities.Entity;
import cz.plesioEngine.entities.Light;
import cz.plesioEngine.fontMeshCreator.FontType;
import cz.plesioEngine.fontMeshCreator.GUIText;
import cz.plesioEngine.fontRendering.TextMaster;
import cz.plesioEngine.guis.GuiRenderer;
import cz.plesioEngine.guis.GuiTexture;
import cz.plesioEngine.toolbox.EntityCreator;
import cz.plesioEngine.toolbox.Janitor;

public class MainGameLoop {

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();

        List<GuiTexture> guis = new ArrayList<>();

        //ModelData playerModelData = OBJFileLoader.loadOBJ("person");
        Camera camera = new Camera();

        MasterRenderer masterRenderer = new MasterRenderer(loader, camera);

        TextMaster.init(loader);
        ConsoleInput.init(loader);
        ParticleMaster.init(loader, MasterRenderer.getProjectionMatrix());

        ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particleAtlas"), 4, true);

        ParticleSystem ps = new ParticleSystem(particleTexture, 500, 5, 0.25f, 0.25f, 3);

        FontType font = new FontType(loader.LoadFontTextureAtlas("candara"), new File("res/font/candara.fnt"));
        GUIText text = new GUIText("Plesio Engine v2(0.01)", 1, font, new Vector2f(0f, 0.95f), 0.2f, true);
        text.setColour(1, 1, 0);

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
        Terrain terrain1 = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
        List<Terrain> terrains = new ArrayList<>();
        terrains.add(terrain1);

        List<Light> lights = new ArrayList<>();
        Light sun = new Light(new Vector3f(100000, 100000, -70000), new Vector3f(1, 1, 1));
        lights.add(sun);
        
        lights.add(new Light(new Vector3f(370, 4.2f, -300), new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.001f)));
        lights.add(new Light(new Vector3f(293, -6.8f, -305), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.001f)));

        ModelData grassData = OBJFileLoader.loadOBJ("grassModel");
        TexturedModel grass = new TexturedModel(loader.loadToVAO(grassData.getVertices(), grassData.getTextureCoords(), grassData.getIndices(), grassData.getNormals()), new ModelTexture(loader.loadTexture("grassTexture")));
        grass.getTexture().set_hasTransparency(true);
        grass.getTexture().set_useFakeLighting(true);
        ModelData fernData = OBJFileLoader.loadOBJ("fern");
        TexturedModel fern = new TexturedModel(loader.loadToVAO(fernData.getVertices(), fernData.getTextureCoords(), fernData.getIndices(), fernData.getNormals()), new ModelTexture(loader.loadTexture("fern")));
        fern.getTexture().set_hasTransparency(true);
        fern.getTexture().set_useFakeLighting(true);
        fern.getTexture().setNumberOfRows(2);
        ModelData flowerData = OBJFileLoader.loadOBJ("grassModel");
        TexturedModel flower = new TexturedModel(loader.loadToVAO(flowerData.getVertices(), flowerData.getTextureCoords(), flowerData.getIndices(), flowerData.getNormals()), new ModelTexture(loader.loadTexture("flower")));
        flower.getTexture().set_hasTransparency(true);
        flower.getTexture().set_useFakeLighting(true);
        ModelData treeData = OBJFileLoader.loadOBJ("tree");
        TexturedModel tree = new TexturedModel(loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getIndices(), treeData.getNormals()), new ModelTexture(loader.loadTexture("tree")));

        List<Entity> entities = new ArrayList<>();
        List<Entity> normalMapEntities = new ArrayList<>();

        Entity barrel = new Entity(new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader), new ModelTexture(loader.loadTexture("barrel"))), new Vector3f(185, 20, -293), 0, 0, 0, 1);
        barrel.getModel().getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
        barrel.getModel().getTexture().setReflectivity(0.01f);
        barrel.getModel().getTexture().setShineDamper(0.6f);
        //normalMapEntities.add(barrel);

        Random random = new Random();
        for (int i = 0; i < 500; i++) {
            float xf = random.nextFloat() * 800 - 400;
            float zf = random.nextFloat() * 800 - 400;
            float yf = terrain1.getHeightOfTerrain(xf, zf);
            entities.add(new Entity(fern, new Vector3f(xf, yf, zf), 0, 0, 0, 0.3f, random.nextInt(4)));

            //fauna.add(new Entity(fern, new Vector3f(random.nextFloat() * 800, 0, random.nextFloat() * 800), 0, 0, 0, 0.3f));
            //fauna.add(new Entity(flower, new Vector3f(random.nextFloat() * 800, 0, random.nextFloat() * 800), 0, 0, 0, 1));
            float xt = random.nextFloat() * 800 - 400;
            float zt = random.nextFloat() * 800 - 400;
            float yt = terrain1.getHeightOfTerrain(xt, zt);
            entities.add(new Entity(tree, new Vector3f(xt, yt, zt), 0, 0, 0, (random.nextFloat() * 3) + 3f));
        }

        List<WaterTile> waters = new ArrayList<>();
        WaterTile water = new WaterTile(185, -170, 0);
        waters.add(water);

        GuiRenderer guiRenderer = new GuiRenderer(loader);

        Entity gift = EntityCreator.createEntity("christmas/gift", "christmas/blueGift", loader);
        gift.setPosition(new Vector3f(185, 5, -293));

        entities.add(gift);

        Entity christmasTree = EntityCreator.createEntity("christmas/christmasTree", "christmas/christmasTreeTexture", loader);
        christmasTree.setPosition(new Vector3f(200, 5, -300));
        entities.add(christmasTree);
        
        Entity furnace = EntityCreator.createEntity("christmas/fireplace", "christmas/furnaceTexture", loader);
        furnace.setPosition(new Vector3f(210, 5, -300));
        entities.add(furnace);
        
        Light fireplace = new Light(new Vector3f(210, 6, -300), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.001f));
        
        lights.add(fireplace);
        
        camera.setPosition(new Vector3f(210, 10, -300));
        
        //entities.add(player);

        MousePicker picker = new MousePicker(camera,
                MasterRenderer.getProjectionMatrix(), terrain1);

        
        while (!Display.isCloseRequested()) {
            camera.move();
            //player.Move(terrain1);
            picker.update();
            ParticleMaster.update(camera);

            ps.generateParticles(new Vector3f(210, 5, -300));
            
            masterRenderer.renderFrame(entities, normalMapEntities, terrains, waters,
                    water, sun, lights, camera);

            ParticleMaster.renderParticles(camera);

            guiRenderer.render(guis);

            //DebugConsole.update();
            TextMaster.render();
            //DebugConsole.destroyText();

            DisplayManager.updateDisplay();
        }

        Janitor.cleanUp(guiRenderer, masterRenderer, loader);
        DisplayManager.closeDisplay();

    }

}
