package cz.plesioEngine.particles;

public class ParticleTexture {

    private final int textureID;
    private final int numberOfRows;
    private final boolean glowing;

    public ParticleTexture(int textureID, int numberOfRows, boolean isGlowing) {
        super();
        this.textureID = textureID;
        this.numberOfRows = numberOfRows;
        this.glowing = isGlowing;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public int getTextureID() {
        return textureID;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

}
