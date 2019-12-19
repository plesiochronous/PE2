/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.plesioEngine.engineTester;

import cz.plesioEngine.entities.Camera;
import cz.plesioEngine.entities.Entity;
import cz.plesioEngine.entities.Light;
import cz.plesioEngine.fontMeshCreator.FontType;
import cz.plesioEngine.fontMeshCreator.GUIText;
import cz.plesioEngine.fontRendering.TextMaster;
import cz.plesioEngine.guis.GuiRenderer;
import cz.plesioEngine.guis.GuiTexture;
import cz.plesioEngine.particles.ParticleMaster;
import cz.plesioEngine.particles.ParticleSystem;
import cz.plesioEngine.particles.ParticleTexture;
import cz.plesioEngine.renderEngine.DisplayManager;
import cz.plesioEngine.renderEngine.Loader;
import cz.plesioEngine.renderEngine.MasterRenderer;
import cz.plesioEngine.terrains.Terrain;
import cz.plesioEngine.textures.TerrainTexture;
import cz.plesioEngine.textures.TerrainTexturePack;
import cz.plesioEngine.toolbox.ConsoleInput;
import cz.plesioEngine.toolbox.ConsoleOutput;
import cz.plesioEngine.toolbox.EntityCreator;
import cz.plesioEngine.toolbox.Janitor;
import cz.plesioEngine.toolbox.MousePicker;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author plesio
 */
public class ChristmasEngineLoop {

    public static void main(String[] args) {

        DisplayManager.createDisplay();

        Boolean hellMode = false;
        Boolean tooGood = false;

        //======================================================================
        //  Loader, renderers, masters, camera
        //======================================================================
        Loader loader = new Loader();
        Camera camera = new Camera();

        camera.setPosition(new Vector3f(330, 10, 30));

        GuiRenderer guiRenderer = new GuiRenderer(loader);
        MasterRenderer masterRenderer = new MasterRenderer(loader, camera);

        TextMaster.init(loader);
        ConsoleInput.init(loader);
        ConsoleOutput.init(loader);
        ParticleMaster.init(loader, MasterRenderer.getProjectionMatrix());

        //======================================================================
        //  Lists: entities, guis, terrains, normal-mapped entites, etc
        //======================================================================
        List<GuiTexture> guis = new ArrayList<>();
        List<Entity> entities = new ArrayList<>();
        List<Entity> normalEntities = new ArrayList<>();
        List<Terrain> terrains = new ArrayList<>();
        List<Light> lights = new ArrayList<>();

        //======================================================================
        //  Terrain creation
        //======================================================================
        TerrainTexture backgroundTexture
                = new TerrainTexture(loader.loadTexture("christmas/snowLow"));

        TerrainTexture rTexture
                = new TerrainTexture(loader.loadTexture("dirt"));

        TerrainTexture gTexture
                = new TerrainTexture(loader.loadTexture("snowy2"));

        TerrainTexture bTexture
                = new TerrainTexture(loader.loadTexture("snowyDark"));

        TerrainTexturePack texturePack
                = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

        TerrainTexture blendMap
                = new TerrainTexture(loader.loadTexture("christmas/blendMap"));

        Terrain terrain1 = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
        terrains.add(terrain1);
        //======================================================================
        //  Particle creation
        //======================================================================
        ParticleTexture particleTexture
                = new ParticleTexture(loader.loadTexture("particleAtlas"), 4, true);
        ParticleSystem fire
                = new ParticleSystem(particleTexture, 100, 5, 0.25f, 0.25f, 4);

        ParticleTexture snowParticleTexture
                = new ParticleTexture(loader.loadTexture("christmas/particleSnow"), 1, true);
        ParticleSystem snowParticle
                = new ParticleSystem(snowParticleTexture, 300, 50, 1f, 5, 200);

        //======================================================================
        //  Entity creation
        //======================================================================
        Entity fireplace = EntityCreator.createEntity("christmas/fireplace",
                "christmas/furnaceTexture", loader);
        fireplace.setPosition(new Vector3f(335, 7, 5));

        fireplace.getModel().getTexture().setTileSize(3);
        fireplace.getModel().getTexture().setReflectivity(0.1f);
        fireplace.getModel().getTexture().setShineDamper(0.1f);

        fireplace.setRotY(-90);
        entities.add(fireplace);

        //======================================================================
        Entity christmasTree = EntityCreator.createEntity("christmas/christmasTreeNew",
                "christmas/christmasTree", loader);

        christmasTree.setPosition(new Vector3f(315, 4, 10));
        christmasTree.getModel().getTexture().setReflectivity(0.1f);
        christmasTree.getModel().getTexture().setShineDamper(0.1f);
        christmasTree.setScale(0.3f);

        entities.add(christmasTree);

        //======================================================================
        Entity wallWithWindow = EntityCreator.createEntity("christmas/wall",
                "christmas/wallRedBrick", loader);

        wallWithWindow.getModel().getTexture().setTileSize(5);
        wallWithWindow.getModel().getTexture().setReflectivity(0.1f);
        wallWithWindow.getModel().getTexture().setShineDamper(0.1f);

        wallWithWindow.setPosition(new Vector3f(340, 5, 0));
        wallWithWindow.setScale(2.25f);
        wallWithWindow.setRotY(90);

        entities.add(wallWithWindow);

        //======================================================================
        Entity wallFull = EntityCreator.createEntity("christmas/wallFull",
                "christmas/wallRedBrick", loader);

        wallFull.getModel().getTexture().setTileSize(5);
        wallFull.getModel().getTexture().setReflectivity(0.1f);
        wallFull.getModel().getTexture().setShineDamper(0.1f);

        wallFull.setPosition(new Vector3f(306, 5, 37));
        wallFull.setScale(2.25f);

        entities.add(wallFull);

        //======================================================================
        Entity wallFull2 = EntityCreator.createEntity("christmas/wallFull",
                "christmas/wallRedBrick", loader);

        wallFull2.getModel().getTexture().setTileSize(5);
        wallFull2.getModel().getTexture().setReflectivity(0.1f);
        wallFull2.getModel().getTexture().setShineDamper(0.1f);

        wallFull2.setPosition(new Vector3f(344, 5, 37));
        wallFull2.setScale(2.25f);

        entities.add(wallFull2);

        //======================================================================
        Entity ceiling = EntityCreator.createEntity("christmas/floor",
                "christmas/planks", loader);

        ceiling.getModel().getTexture().setTileSize(10);
        ceiling.getModel().getTexture().setReflectivity(0.1f);
        ceiling.getModel().getTexture().setShineDamper(0.1f);

        ceiling.setScale(3f);
        ceiling.setPosition(new Vector3f(303.5f, 18, 50));

        entities.add(ceiling);

        //======================================================================
        Entity floor = EntityCreator.createEntity("christmas/floor",
                "christmas/woodFloor", loader);

        floor.getModel().getTexture().setTileSize(20);
        floor.getModel().getTexture().setReflectivity(0.05f);
        floor.getModel().getTexture().setShineDamper(1f);

        floor.setScale(3f);
        floor.setPosition(new Vector3f(303.5f, 3, 50));

        entities.add(floor);

        //======================================================================
        Entity window = EntityCreator.createEntity("christmas/window",
                "christmas/floor", loader);

        window.setScale(2.25f);
        window.setRotY(90);
        window.setPosition(new Vector3f(340, 5, 0));

        entities.add(window);

        //======================================================================
        Entity pictureFrame = EntityCreator.createEntity("christmas/pictureFrame",
                "christmas/pictureCat", loader);

        pictureFrame.setScale(2.25f);
        pictureFrame.setRotY(90);
        pictureFrame.setRotX(180);
        pictureFrame.setPosition(new Vector3f(333.5f, 15, 3));

        entities.add(pictureFrame);
        //======================================================================
        Entity woodStorage = EntityCreator.createEntity("christmas/woodStorage",
                "christmas/rustedMetalDark", loader);

        woodStorage.getModel().getTexture()
                .setNormalMap(loader.loadTexture("christmas/rustedMetalNormal"));
        woodStorage.getModel().getTexture().setReflectivity(0.01f);
        woodStorage.getModel().getTexture().setShineDamper(0.6f);

        woodStorage.setScale(2.25f);
        //woodStorage.setRotY(180);
        woodStorage.setPosition(new Vector3f(329.6f, 4, 5));

        entities.add(woodStorage);

        //======================================================================
        List<Entity> logs = new ArrayList<>();

        for (int i = 1; i <= 7; i++) {
            Entity log = EntityCreator.createEntity("christmas/log",
                    "christmas/logTexture", loader);

            log.getModel().getTexture().setReflectivity(0.01f);
            log.getModel().getTexture().setShineDamper(0.6f);

            log.setScale(2.25f);
            //woodStorage.setRotY(180);
            log.setPosition(new Vector3f(329.6f, 4, 5));

            logs.add(log);
        }

        logs.get(1).setPosition(new Vector3f(331, 4, 5));
        logs.get(2).setPosition(new Vector3f(330.3f, 5.2f, 5));
        logs.get(3).setPosition(new Vector3f(332.1f, 4.8f, 5));
        logs.get(5).setPosition(new Vector3f(335.6f, 5f, 5));
        logs.get(6).setPosition(new Vector3f(337f, 5f, 5));

        entities.addAll(logs);
        //======================================================================
        List<Entity> cats = new ArrayList<>();

        for (int i = 1; i <= 7; i++) {
            Entity newPicture = EntityCreator.createEntity("christmas/pictureFrame",
                    "christmas/pictureCat" + i, loader);

            newPicture.setScale(2.25f);
            newPicture.setRotY(90);
            newPicture.setRotX(180);
            newPicture.setPosition(new Vector3f(333.5f - i * 5, 15, 3));

            cats.add(newPicture);
        }

        cats.get(0).setPosition(new Vector3f(337, 15.2f, 3));
        cats.get(1).setPosition(new Vector3f(330, 15.1f, 3));
        cats.get(2).setPosition(new Vector3f(313, 15, 3));
        cats.get(3).setPosition(new Vector3f(309.5f, 14.8f, 3));
        cats.get(4).setPosition(new Vector3f(306f, 15.1f, 3));
        cats.get(5).setPosition(new Vector3f(313.3f, 10.1f, 3));
        cats.get(6).setPosition(new Vector3f(309.3f, 10.1f, 3));

        entities.addAll(cats);

        Entity largePicture = EntityCreator.createEntity("christmas/pictureFrameLarge",
                "christmas/largePictureFrame", loader);

        largePicture.setScale(5f);
        largePicture.setRotX(180);
        largePicture.setPosition(new Vector3f(305.8f, 15, 14));

        entities.add(largePicture);

        //======================================================================
        Entity carpet = EntityCreator.createEntity("christmas/carpet",
                "christmas/fabric", loader);

        carpet.getModel().getTexture().setTileSize(5);

        carpet.setScale(3f);
        carpet.setPosition(new Vector3f(323, 3.9f, 15));

        entities.add(carpet);

        //======================================================================
        Entity tree = EntityCreator.createEntity("tree", "christmas/tree", loader);

        Random random = new Random();
        for (int i = 0; i < 500; i++) {
            float xt = (random.nextFloat() * 800);
            float zt = (random.nextFloat() * 800) - 800;
            float yt = terrain1.getHeightOfTerrain(xt, zt);
            entities.add(new Entity(tree.getModel(), new Vector3f(xt, yt, zt),
                    0, 0, 0, (random.nextFloat() * 3) + 3f));
        }

        //======================================================================
        // Behaviour Index Handling
        //======================================================================
        int behaviourIndex = Integer.parseInt(args[0]);

        if (args.length > 0) {
            int giftAmount = behaviourIndex;
            if (behaviourIndex > 10) {
                tooGood = true;
                giftAmount = 10;
            }

            List<Entity> gifts = new ArrayList();
            for (int i = 0; i < giftAmount; i++) {
                Entity newGift = EntityCreator.createEntity("christmas/gift",
                        "christmas/gift" + i, loader);
                newGift.setScale(3f);
                gifts.add(newGift);
            }

            switch (giftAmount) {
                case 10:
                    gifts.get(9).setPosition(new Vector3f(325, 5.7f, 7));
                    gifts.get(9).setRotY(90);
                    gifts.get(9).setRotZ(90);
                    gifts.get(9).setScale(5f);
                case 9:
                    gifts.get(8).setPosition(new Vector3f(321, 5.7f, 14));
                    gifts.get(8).setRotY(-45);
                    gifts.get(8).setScale(1f);
                case 8:
                    gifts.get(7).setPosition(new Vector3f(313, 6.4f, 14));
                    gifts.get(7).setRotY(90);
                case 7:
                    gifts.get(6).setPosition(new Vector3f(321, 4.5f, 13));
                    gifts.get(6).setScale(2.4f);
                case 6:
                    gifts.get(5).setPosition(new Vector3f(313, 4.5f, 15));
                    gifts.get(5).setScale(2.3f);
                case 5:
                    gifts.get(4).setPosition(new Vector3f(310, 4.5f, 10));
                    gifts.get(4).setScale(2.2f);
                case 4:
                    gifts.get(3).setPosition(new Vector3f(317, 6f, 8));
                    gifts.get(3).setRotY(45);
                    gifts.get(3).setScale(1.5f);
                case 3:
                    gifts.get(2).setPosition(new Vector3f(317, 4.5f, 8));
                    gifts.get(2).setRotY(45);
                case 2:
                    gifts.get(1).setPosition(new Vector3f(317, 4.5f, 13));
                    gifts.get(1).setRotY(-45);
                case 1:
                    gifts.get(0).setPosition(new Vector3f(310, 4.5f, 15));
            }
            entities.addAll(gifts);

        }

        //======================================================================
        // Text Generation
        //======================================================================
        FontType font = new FontType(loader.LoadFontTextureAtlas("candara"), new File("res/font/candara.fnt"));
        if (behaviourIndex > 0 && !tooGood) {
            GUIText text = new GUIText("You've behaved yourself, you get "
                    + Integer.parseInt(args[0]) + " gifts, merry christmas.",
                    2f, font, new Vector2f(0, 0.05f), 1, true);
            text.setColour(1, 1, 1);
        } else if (behaviourIndex == 0) {
            GUIText text = new GUIText("You've been naughty this year, you get no gifts",
                    2f, font, new Vector2f(0, 0.05f), 1, true);
            text.setColour(1, 1, 1);
        } else if (tooGood) {
            GUIText text = new GUIText("You're too good, Santa doesn't have that many"
                    + " gifts, you get 10. Merry Christmas", 2f, font, new Vector2f(0, 0.05f), 1, true);
            text.setColour(0.2f, 1f, 0.2f);
        } else if (behaviourIndex < 0) {
            GUIText text = new GUIText("You've been naughty this year, "
                    + "Santa kidnapped you and left you outside to freeze... "
                    + "And it's raining blood now",
                    2f, font, new Vector2f(0, 0.05f), 1, true);
            hellMode = true;
            text.setColour(1, 0.2f, 0.2f);
        }

        if (args.length > 1) {
            GUIText text = new GUIText(args[1],
                    2f, font, new Vector2f(0, 0.9f), 1, true);
            text.setColour(0.5f, 1, 0.5f);
        }

        //======================================================================
        //  Light creation
        //======================================================================
        Light moodLight = new Light(new Vector3f(323, 4, 14),
                new Vector3f(1, 1, 1), new Vector3f(1, 0.01f, 0.001f));
        lights.add(moodLight);

        //======================================================================
        Light fireplaceLight = new Light(new Vector3f(335, 8, 15),
                new Vector3f(89, 35, 13), new Vector3f(0.001f, 0.001f, 1.5f));
        lights.add(fireplaceLight);

        Light fireplaceLightInner = new Light(new Vector3f(335, 7, 5),
                new Vector3f(89, 35, 13), new Vector3f(0.001f, 0.001f, 2f));
        lights.add(fireplaceLightInner);

        //======================================================================
        //  Mousepicker
        //======================================================================
        MousePicker picker = new MousePicker(camera,
                MasterRenderer.getProjectionMatrix(), terrain1);

        //======================================================================
        //  Method registers for debug console
        //======================================================================
        try {
            ConsoleInput.registerMethod(camera,
                    camera.getMethod("printPosition"), "getCamPos");
            ConsoleInput.registerMethod(camera,
                    camera.getMethod("disableMovement"), "dcm");
            ConsoleInput.registerMethod(camera,
                    camera.getMethod("enableMovement"), "ecm");
            ConsoleInput.registerMethod(camera,
                    Camera.class.getMethod("setPosition", Vector3f.class), "tp");
            ConsoleInput.registerMethod(null,
                    ConsoleOutput.class.getMethod("clearLog", (Class<?>[]) null), "clr");
            ConsoleInput.registerMethod(camera,
                    Camera.class.getMethod("printCameraProps", null), "pcp");
            ConsoleInput.registerMethod(camera, 
                    Camera.class.getMethod("setFreecamSpeed", Float.TYPE), "sfcs");
            ConsoleInput.registerMethod(null, 
                    ConsoleInput.class.getMethod("listCommands", (Class<?>[]) null), "help");
        } catch (NoSuchMethodException ex) {
            ConsoleOutput.appendToLog(ex.toString(), ConsoleOutput.LogType.ERR);
        }

        //======================================================================
        //  Config for very naughty people
        //======================================================================
        if (hellMode) {
            camera.setPosition(294, 24, -109);
        }
        ParticleTexture bloodParticle
                = new ParticleTexture(loader.loadTexture("blood"), 1, true);
        ParticleSystem blood
                = new ParticleSystem(bloodParticle, 500, 50, 1f, 5, 200);

        //======================================================================
        //  Final camera config
        //======================================================================
        if (!hellMode) {
            camera.setPosition(new Vector3f(333.32f, 8, 26));
            camera.setPitch(-3.4f);
            camera.setYaw(-32.2f);
        } else {
            camera.setPosition(new Vector3f(367.194f, 19, -152));
            camera.setPitch(3.4f);
            camera.setYaw(-54.2f);
        }

        camera.disableMovement();
        
        while (!Display.isCloseRequested()) {
            if (!ConsoleInput.getIsTyping()) {
                camera.move();
            }
            picker.update();
            ParticleMaster.update(camera);

            if (hellMode) {
                blood.generateParticles(new Vector3f(300, 200, -150));
            } else {
                fire.generateParticles(new Vector3f(335, 7, 5));
                snowParticle.generateParticles(new Vector3f(300, 200, -150));
            }

            masterRenderer.renderFrame(entities, normalEntities, terrains,
                    moodLight, lights, camera);

            ParticleMaster.renderParticles(camera);

            guiRenderer.render(guis);

            ConsoleOutput.update();
            ConsoleInput.update();

            TextMaster.render();

            DisplayManager.updateDisplay();
        }

        Janitor.cleanUp(guiRenderer, masterRenderer, loader);

        DisplayManager.closeDisplay();
    }
}
