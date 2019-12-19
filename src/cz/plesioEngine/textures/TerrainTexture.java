package cz.plesioEngine.textures;

public class TerrainTexture {

    private int _textureID;

    public TerrainTexture(int textureID) {
        _textureID = textureID;
    }

    public int get_textureID() {
        return _textureID;
    }

    public void set_textureID(int _textureID) {
        this._textureID = _textureID;
    }

}
