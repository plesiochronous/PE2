package cz.plesioEngine.textures;

public class ModelTexture {

    private int textureID;
    private int normalMap;

    private float shineDamper = 1;
    private float reflectivity = 0;

    private boolean hasTransparency = false;
    private boolean useFakeLighting = false;

    private int tileSize = 1;
    
    private int numberOfRows = 1;

    public int getNormalMap() {
        return normalMap;
    }

    public int getTileSize() {
        return tileSize;
    }

    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
    }
    
    public void setNormalMap(int normalMap) {
        this.normalMap = normalMap;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public float getShineDamper() {
        return shineDamper;
    }

    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    public ModelTexture(int texture) {
        this.textureID = texture;
    }

    public int getID() {
        return textureID;
    }

    public boolean get_hasTransparency() {
        return hasTransparency;
    }

    public void set_hasTransparency(boolean _hasTransparency) {
        this.hasTransparency = _hasTransparency;
    }

    public boolean is_useFakeLighting() {
        return useFakeLighting;
    }

    public void set_useFakeLighting(boolean _useFakeLighting) {
        this.useFakeLighting = _useFakeLighting;
    }

}
