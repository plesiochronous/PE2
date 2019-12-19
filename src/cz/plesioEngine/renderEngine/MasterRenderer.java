package cz.plesioEngine.renderEngine;

import cz.plesioEngine.terrains.TerrainRenderer;
import cz.plesioEngine.entities.EntityRenderer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import cz.plesioEngine.entities.Camera;
import cz.plesioEngine.entities.Entity;
import cz.plesioEngine.entities.Light;
import cz.plesioEngine.models.TexturedModel;
import cz.plesioEngine.normalMappingRenderer.NormalMappingRenderer;
import cz.plesioEngine.normalMappingRenderer.NormalMappingShader;
import cz.plesioEngine.shaders.StaticShader;
import cz.plesioEngine.terrains.TerrainShader;
import cz.plesioEngine.shadows.ShadowMapMasterRenderer;
import cz.plesioEngine.skybox.SkyboxRenderer;
import cz.plesioEngine.terrains.Terrain;
import cz.plesioEngine.water.WaterFrameBuffers;
import cz.plesioEngine.water.WaterRenderer;
import cz.plesioEngine.water.WaterShader;
import cz.plesioEngine.water.WaterTile;
import org.lwjgl.opengl.GL30;

public class MasterRenderer {

    //==========================================================================
    // Constants
    //==========================================================================    
    public static final float FOV = 70;
    public static final float FAR_PLANE = 1000;
    public static final float NEAR_PLANE = 0.1f;

    //public static final float SKY_RED = 0.05f;
    //public static final float SKY_GREEN = 0.153f;
    //public static final float SKY_BLUE = 0.204f;

    public static final float SKY_RED = 0f;
    public static final float SKY_GREEN = 0f;
    public static final float SKY_BLUE = 0f;
    
    //==========================================================================
    // Shaders
    //==========================================================================
    private final StaticShader staticShader = new StaticShader();
    private final WaterShader waterShader = new WaterShader();
    private final TerrainShader terrainShader = new TerrainShader();
    private final NormalMappingShader normalMappingShader = new NormalMappingShader();

    //==========================================================================
    // Renderers
    //==========================================================================
    private final EntityRenderer entityRenderer;
    private final ShadowMapMasterRenderer shadowMapRenderer;
    private final WaterRenderer waterRenderer;
    private final TerrainRenderer terrainRenderer;
    private final NormalMappingRenderer normalMapRenderer;
    private final SkyboxRenderer skyboxRenderer;

    //==========================================================================
    // Framebuffer Objects
    //==========================================================================
    private final WaterFrameBuffers waterFrameBuffers = new WaterFrameBuffers();

    //==========================================================================
    // Lists
    //==========================================================================
    private final Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private final Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<>();
    private final List<Terrain> terrains = new ArrayList<>();

    //==========================================================================
    // Other
    //==========================================================================
    private static Matrix4f PROJECTION_MATRIX;

    public MasterRenderer(Loader loader, Camera camera) {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        createProjectionMatrix();
        entityRenderer = new EntityRenderer(staticShader, PROJECTION_MATRIX);
        terrainRenderer = new TerrainRenderer(terrainShader, PROJECTION_MATRIX);
        skyboxRenderer = new SkyboxRenderer(loader, PROJECTION_MATRIX);
        normalMapRenderer = new NormalMappingRenderer(PROJECTION_MATRIX);
        shadowMapRenderer = new ShadowMapMasterRenderer(camera);
        waterRenderer = new WaterRenderer(loader, waterShader, PROJECTION_MATRIX, waterFrameBuffers);
    }

    public static Matrix4f getProjectionMatrix() {
        return PROJECTION_MATRIX;
    }

    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void renderFrame(List<Entity> entities, List<Entity> normalEntities,
            List<Terrain> terrains, Light sun, List<Light> lights, Camera camera) {
        renderShadowMap(entities, sun);
        renderScene(entities, normalEntities, terrains, lights, camera);
    }

    public void renderFrame(List<Entity> entities, List<Entity> normalEntities,
            List<Terrain> terrains, List<WaterTile> waterTiles, WaterTile water,
            Light sun, List<Light> lights, Camera camera) {

        renderShadowMap(entities, sun);

        GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

        waterFrameBuffers.bindReflectionFrameBuffer();
        float distance = 2 * (camera.getPosition().y - water.getHeight());
        camera.getPosition().y -= distance;
        camera.invertPitch();
        renderScene(entities, normalEntities, terrains, lights,
                camera, new Vector4f(0, 1, 0, -(water.getHeight() + 1f)));
        camera.getPosition().y += distance;
        camera.invertPitch();

        waterFrameBuffers.bindRefractionFrameBuffer();

        renderScene(entities, normalEntities, terrains, lights,
                camera, new Vector4f(0, -1, 0, water.getHeight() + 1f));

        GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
        waterFrameBuffers.unbindCurrentFrameBuffer();
        renderScene(entities, normalEntities, terrains, lights, camera);
        waterRenderer.render(waterTiles, camera, sun);

    }

    private void renderScene(List<Entity> entities, List<Entity> normalEntities,
            List<Terrain> terrains, List<Light> lights,
            Camera camera, Vector4f clipPlane) {
        terrains.forEach((terrain) -> {
            processTerrain(terrain);
        });
        entities.forEach((entity) -> {
            processEntity(entity);
        });
        normalEntities.forEach((entity) -> {
            processNormalMapEntity(entity);
        });
        render(lights, camera, clipPlane);
    }

    private void renderScene(List<Entity> entities, List<Entity> normalEntities,
            List<Terrain> terrains, List<Light> lights,
            Camera camera) {
        terrains.forEach((terrain) -> {
            processTerrain(terrain);
        });
        entities.forEach((entity) -> {
            processEntity(entity);
        });
        normalEntities.forEach((entity) -> {
            processNormalMapEntity(entity);
        });
        render(lights, camera, new Vector4f(0, 0, 0, 0));
    }

    private void render(List<Light> lights, Camera camera, Vector4f clipPlane) {
        prepare();

        //Entities
        {
            staticShader.start();
            staticShader.loadClipPlane(clipPlane);
            staticShader.loadSkyColor(SKY_RED, SKY_GREEN, SKY_BLUE);
            staticShader.loadLights(lights);
            staticShader.loadViewMatrix(camera);
            entityRenderer.render(entities);
            staticShader.stop();
        }

        //Normal mapped entities
        {
            normalMapRenderer.render(normalMapEntities, clipPlane, lights, camera);
        }

        //Terrains
        {
            terrainShader.start();
            terrainShader.loadShadowMapSize(ShadowMapMasterRenderer.SHADOW_MAP_SIZE);
            terrainShader.loadClipPlane(clipPlane);
            terrainShader.loadSkyColor(SKY_RED, SKY_GREEN, SKY_BLUE);
            terrainShader.loadLights(lights);
            terrainShader.loadViewMatrix(camera);
            terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());
            terrainShader.stop();
        }

        skyboxRenderer.render(camera, SKY_RED, SKY_GREEN, SKY_BLUE);

        terrains.clear();
        entities.clear();

        normalMapEntities.clear();
    }

    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void processNormalMapEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = normalMapEntities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            normalMapEntities.put(entityModel, newBatch);
        }
    }

    public void renderShadowMap(List<Entity> entityList, Light sun) {
        entityList.forEach((entity) -> {
            processEntity(entity);
        });
        shadowMapRenderer.render(entities, sun);
        entities.clear();
    }

    public int getShadowMapTexture() {
        return shadowMapRenderer.getShadowMap();
    }

    public void cleanUp() {
        staticShader.cleanUp();
        terrainShader.cleanUp();
        waterFrameBuffers.cleanUp();
        shadowMapRenderer.cleanUp();
    }

    private void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(SKY_RED, SKY_GREEN, SKY_BLUE, 1);
        GL13.glActiveTexture(GL13.GL_TEXTURE5);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());

    }

    private void createProjectionMatrix() {
        PROJECTION_MATRIX = new Matrix4f();
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        PROJECTION_MATRIX.m00 = x_scale;
        PROJECTION_MATRIX.m11 = y_scale;
        PROJECTION_MATRIX.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        PROJECTION_MATRIX.m23 = -1;
        PROJECTION_MATRIX.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        PROJECTION_MATRIX.m33 = 0;
    }

}
