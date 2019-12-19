package cz.plesioEngine.shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import cz.plesioEngine.toolbox.Maths;

import cz.plesioEngine.entities.Camera;
import cz.plesioEngine.entities.Light;

public class StaticShader extends ShaderProgram {

    private static final int MAX_LIGHTS = 16;

    private static final String VERTEX_FILE = "src/cz/plesioEngine/shaders/vertexShader.glsl";
    private static final String FRAGMENT_FILE = "src/cz/plesioEngine/shaders/fragmentShader.glsl";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_lightPosition[];
    private int location_lightColor[];
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_useFakeLighting;
    private int location_skyColor;
    private int location_numberOfRows;
    private int location_offset;
    private int location_plane;
    private int location_tileSize;
    private int location_attenuation[];

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
        super.bindAttribute(2, "normal");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_useFakeLighting = super.getUniformLocation("useFakeLighting");
        location_skyColor = super.getUniformLocation("skyColor");
        location_numberOfRows = super.getUniformLocation("numberOfRows");
        location_offset = super.getUniformLocation("offset");
        location_plane = super.getUniformLocation("plane");
        location_tileSize = super.getUniformLocation("tileSize");

        location_lightPosition = new int[MAX_LIGHTS];
        location_lightColor = new int[MAX_LIGHTS];
        location_attenuation = new int[MAX_LIGHTS];
        for (int i = 0; i < MAX_LIGHTS; i++) {
            location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
            location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
            location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
        }
    }
    
    public void loadTileSize(float factor){
        super.loadFloat(location_tileSize, factor);
    }

    public void loadClipPlane(Vector4f clipPlane) {
        super.LoadVector4f(location_plane, clipPlane);
    }

    public void loadNumberOfRows(int numberOfRows) {
        super.loadFloat(location_numberOfRows, numberOfRows);
    }

    public void loadOffset(Vector2f offset) {
        super.loadVector2f(location_offset, offset);
    }

    public void loadSkyColor(float r, float g, float b) {
        super.loadVector3f(location_skyColor, new Vector3f(r, g, b));
    }

    public void loadFakeLightingVariable(boolean useFake) {
        super.loadBoolean(location_useFakeLighting, useFake);
    }

    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(location_shineDamper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadLights(List<Light> lights) {
        for (int i = 0; i < MAX_LIGHTS; i++) {
            if (i < lights.size()) {
                super.loadVector3f(location_lightPosition[i], lights.get(i).getPosition());
                super.loadVector3f(location_lightColor[i], lights.get(i).getColor());
                super.loadVector3f(location_attenuation[i], lights.get(i).getAttenuation());
            } else {
                super.loadVector3f(location_lightPosition[i], new Vector3f(0, 0, 0));
                super.loadVector3f(location_lightColor[i], new Vector3f(0, 0, 0));
                super.loadVector3f(location_attenuation[i], new Vector3f(1, 0, 0));
            }
        }
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        super.loadMatrix(location_projectionMatrix, projection);
    }
}
