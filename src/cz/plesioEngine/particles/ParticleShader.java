package cz.plesioEngine.particles;

import org.lwjgl.util.vector.Matrix4f;

import cz.plesioEngine.shaders.ShaderProgram;

public class ParticleShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/cz/plesioEngine/particles/particleVShader.glsl";
    private static final String FRAGMENT_FILE = "src/cz/plesioEngine/particles/particleFShader.glsl";

    private int location_numberOfRows;
    private int location_projectionMatrix;

    public ParticleShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_numberOfRows = super.getUniformLocation("numberOfRows");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
    }

    protected void loadNumberOfRows(float numberOfRows) {
        super.loadFloat(location_numberOfRows, numberOfRows);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "modelViewMatrix");
        super.bindAttribute(5, "texOffsets");
        super.bindAttribute(6, "blendFactor");
    }

    protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
        super.loadMatrix(location_projectionMatrix, projectionMatrix);
    }

}
