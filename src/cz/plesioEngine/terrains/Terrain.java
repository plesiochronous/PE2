package cz.plesioEngine.terrains;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import cz.plesioEngine.models.RawModel;
import cz.plesioEngine.renderEngine.Loader;
import cz.plesioEngine.textures.TerrainTexture;
import cz.plesioEngine.textures.TerrainTexturePack;
import cz.plesioEngine.toolbox.ConsoleOutput;
import cz.plesioEngine.toolbox.Maths;

public class Terrain {

    private static final float SIZE = 800;
    private static final float MAX_HEIGHT = 100;
    private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;

    private float x;
    private float z;
    private RawModel model;
    private TerrainTexturePack texturePack;
    private TerrainTexture blendMap;

    private float[][] heights;

    public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texture,
            TerrainTexture blendMap, String heightMap) {
        texturePack = texture;
        this.blendMap = blendMap;
        x = gridX * SIZE;
        z = gridZ * SIZE;
        model = generateTerrain(loader, heightMap);
    }

    public float get_x() {
        return x;
    }

    public void set_x(float _x) {
        this.x = _x;
    }

    public float get_z() {
        return z;
    }

    public void set_z(float _z) {
        this.z = _z;
    }

    public RawModel get_model() {
        return model;
    }

    public void set_model(RawModel _model) {
        this.model = _model;
    }

    public TerrainTexturePack get_texturePack() {
        return texturePack;
    }

    public void set_texturePack(TerrainTexturePack _texturePack) {
        this.texturePack = _texturePack;
    }

    public TerrainTexture get_blendMap() {
        return blendMap;
    }

    public void set_blendMap(TerrainTexture _blendMap) {
        this.blendMap = _blendMap;
    }

    public float getHeightOfTerrain(float worldX, float worldZ) {
        float terrainX = worldX - this.x;
        float terrainZ = worldZ - this.z;
        float gridSquareSize = SIZE / ((float) heights.length - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
        if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
            return 0;
        }
        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
        float answer;
        if (xCoord <= (1 - zCoord)) {
            answer = Maths
                    .barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ], 0), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        } else {
            answer = Maths
                    .barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }
        return answer;
    }

    private RawModel generateTerrain(Loader loader, String heightMap) {

        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("res/" + heightMap + ".png"));
        } catch (Exception e) {
            ConsoleOutput.appendToLog(e.toString(), ConsoleOutput.LogType.ERR);
        }

        int _VERTEX_COUNT = image.getHeight();
        heights = new float[_VERTEX_COUNT][_VERTEX_COUNT];
        int count = _VERTEX_COUNT * _VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (_VERTEX_COUNT - 1) * (_VERTEX_COUNT - 1)];
        int vertexPointer = 0;
        for (int i = 0; i < _VERTEX_COUNT; i++) {
            for (int j = 0; j < _VERTEX_COUNT; j++) {
                vertices[vertexPointer * 3] = (float) j / ((float) _VERTEX_COUNT - 1) * SIZE;
                float height = getHeight(j, i, image);
                heights[j][i] = height;
                vertices[vertexPointer * 3 + 1] = height;
                vertices[vertexPointer * 3 + 2] = (float) i / ((float) _VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = calculateNormal(j, i, image);
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;
                textureCoords[vertexPointer * 2] = (float) j / ((float) _VERTEX_COUNT - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) _VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for (int gz = 0; gz < _VERTEX_COUNT - 1; gz++) {
            for (int gx = 0; gx < _VERTEX_COUNT - 1; gx++) {
                int topLeft = (gz * _VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * _VERTEX_COUNT) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, textureCoords, indices, normals);
    }

    private Vector3f calculateNormal(int x, int y, BufferedImage image) {
        float heightL = getHeight(x - 1, y, image);
        float heightR = getHeight(x + 1, y, image);
        float heightD = getHeight(x, y - 1, image);
        float heightU = getHeight(x, y + 1, image);
        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalise();
        return normal;
    }

    private float getHeight(int x, int y, BufferedImage image) {
        if (x < 0 || x >= image.getHeight() || y < 0 || y >= image.getHeight()) {
            return 0;
        }
        float height = image.getRGB(x, y);
        height += MAX_PIXEL_COLOR / 2;
        height /= MAX_PIXEL_COLOR / 2;
        height *= MAX_HEIGHT;
        return height;
    }
}
