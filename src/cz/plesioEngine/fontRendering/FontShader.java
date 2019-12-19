package cz.plesioEngine.fontRendering;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import cz.plesioEngine.shaders.ShaderProgram;

public class FontShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/cz/plesioEngine/fontRendering/fontVertex.glsl";
    private static final String FRAGMENT_FILE = "src/cz/plesioEngine/fontRendering/fontFragment.glsl";

    private int location_color;
    private int location_translation;

    public FontShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_color = super.getUniformLocation("color");
        location_translation = super.getUniformLocation("translation");
    }

    protected void loadColor(Vector3f color) {
        super.loadVector3f(location_color, color);
    }

    protected void loadTranslation(Vector2f translation) {
        super.loadVector2f(location_translation, translation);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

}
