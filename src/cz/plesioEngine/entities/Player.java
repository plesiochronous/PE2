package cz.plesioEngine.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import cz.plesioEngine.models.TexturedModel;
import cz.plesioEngine.renderEngine.DisplayManager;
import cz.plesioEngine.terrains.Terrain;

public class Player extends Entity {

    private static final float RUN_SPEED = 20;
    private static final float TURN_SPEED = 160;
    public static final float GRAVITY = -50;
    private static final float JUMP_POWER = 30;

    //private static final float _TERRAIN_HEIGHT = 0;
    private float currentRunSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    private boolean isInAir = false;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    public void Move(Terrain terrain) {
        CheckInputs();
        super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
        float distance = currentRunSpeed * DisplayManager.getFrameTimeSeconds();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(dx, 0, dz);
        upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
        super.increasePosition(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);
        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
        if (super.getPosition().y < terrainHeight) {
            upwardsSpeed = 0;
            super.getPosition().y = terrainHeight;
            isInAir = false;
        }
    }

    private void Jump() {
        if (!isInAir) {
            this.upwardsSpeed = JUMP_POWER;
            isInAir = true;
        }
    }

    public boolean IsMoving() {
        if (currentRunSpeed > 0) {
            //	return true;
        } else if (currentTurnSpeed > 0) {
            //	return true;
        } else if (upwardsSpeed > 0) {
            //	return true;
        }
        return false;
    }

    private void CheckInputs() {
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            currentRunSpeed = RUN_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            currentRunSpeed = -(RUN_SPEED);
        } else {
            currentRunSpeed = 0;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            currentTurnSpeed = -(TURN_SPEED);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            currentTurnSpeed = TURN_SPEED;
        } else {
            currentTurnSpeed = 0;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            Jump();
        }
    }

}
