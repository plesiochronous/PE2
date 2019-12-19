package cz.plesioEngine.textures;

public class TerrainTexturePack {

    private TerrainTexture backgroundTexture;
    private TerrainTexture rTexture;
    private TerrainTexture gTexture;
    private TerrainTexture bTexture;

    public TerrainTexturePack(TerrainTexture backgroundTexture,
            TerrainTexture rTexture,
            TerrainTexture gTexture,
            TerrainTexture bTexture) {
        this.backgroundTexture = backgroundTexture;
        this.rTexture = rTexture;
        this.gTexture = gTexture;
        this.bTexture = bTexture;
    }

    public TerrainTexture get_backgroundTexture() {
        return backgroundTexture;
    }

    public void set_backgroundTexture(TerrainTexture _backgroundTexture) {
        this.backgroundTexture = _backgroundTexture;
    }

    public TerrainTexture get_rTexture() {
        return rTexture;
    }

    public void set_rTexture(TerrainTexture _rTexture) {
        this.rTexture = _rTexture;
    }

    public TerrainTexture get_gTexture() {
        return gTexture;
    }

    public void set_gTexture(TerrainTexture _gTexture) {
        this.gTexture = _gTexture;
    }

    public TerrainTexture get_bTexture() {
        return bTexture;
    }

    public void set_bTexture(TerrainTexture _bTexture) {
        this.bTexture = _bTexture;
    }

}
