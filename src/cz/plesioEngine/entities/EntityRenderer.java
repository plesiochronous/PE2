package cz.plesioEngine.entities;

import cz.plesioEngine.models.RawModel;
import cz.plesioEngine.models.TexturedModel;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import cz.plesioEngine.shaders.StaticShader;
import cz.plesioEngine.textures.ModelTexture;
import cz.plesioEngine.toolbox.Maths;

import cz.plesioEngine.renderEngine.MasterRenderer;

/**
 * Renders regular non-normal mapped entities, don't use separately! All
 * rendering should be handled by the MasterRenderer class.
 * @author plesio
 */
public class EntityRenderer {

    private final StaticShader shader;

    private Matrix4f TRANSFORMATION_MATRIX;
    
    public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(Map<TexturedModel, List<Entity>> entities) {
        for (TexturedModel model : entities.keySet()) {     
            prepareTexturedModel(model);
            List<Entity> batch = entities.get(model);
            for (Entity entity : batch) {
                prepareInstance(entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbindTexturedModel();
        }
    }
    
    private void prepareTexturedModel(TexturedModel model) {
        RawModel rawModel = model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        ModelTexture texture = model.getTexture();
        shader.loadNumberOfRows(texture.getNumberOfRows());
        if (texture.get_hasTransparency()) {
            MasterRenderer.disableCulling();
        }
        shader.loadFakeLightingVariable(texture.is_useFakeLighting());
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        shader.loadTileSize(texture.getTileSize());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
    }

    private void unbindTexturedModel() {
        MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(Entity entity) {
        TRANSFORMATION_MATRIX = Maths.createTransformationMatrix(entity.getPosition(),
        entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(TRANSFORMATION_MATRIX);
        shader.loadOffset(new Vector2f(entity.getTextureXOffset(), entity.getTextureYOffset()));
    }

}
