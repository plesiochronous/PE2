/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.plesioEngine.toolbox;

import cz.plesioEngine.entities.Entity;
import cz.plesioEngine.models.TexturedModel;
import cz.plesioEngine.objConverter.ModelData;
import cz.plesioEngine.objConverter.OBJFileLoader;
import cz.plesioEngine.renderEngine.Loader;
import cz.plesioEngine.textures.ModelTexture;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author plesio
 */
public class EntityCreator {

    public static Entity createEntity(String objFileName, String TextureFileName,
            Loader loader) {
        ModelData entityData = OBJFileLoader.loadOBJ(objFileName);
        TexturedModel entityTextureModel = new TexturedModel(loader.loadToVAO(entityData.getVertices(),
                entityData.getTextureCoords(), entityData.getIndices(), entityData.getNormals()),
                new ModelTexture(loader.loadTexture(TextureFileName)));
        return new Entity(entityTextureModel, new Vector3f(0,0,0),0,0,0,1.0f);
    }
    
}
