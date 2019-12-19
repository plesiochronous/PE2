package cz.plesioEngine.entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import cz.plesioEngine.terrains.Terrain;
import cz.plesioEngine.toolbox.ConsoleOutput;
import java.lang.reflect.Method;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author plesio
 */
public class Camera {

    private float distanceFromPlayer = 50;
    private float angleAroundThePlayer = 0;

    private static float freecamSpeed = 0.5f;

    private Vector3f position = new Vector3f(0, 5, 0);
    private final float TURN_SPEED = 0.01755f;
    private float pitch;
    private float yaw;
    private float roll;

    private boolean freecam = false;

    private boolean freezeMovement = false;

    private Player player;

    public Camera(Player player) {
        this.player = player;
    }

    public Camera() {
        freecam = true;
    }

    /**
     *
     */
    public final void move() {
        if (freecam && !freezeMovement) {
            if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
                Vector3f v = new Vector3f();
                v.x += (float) Math.sin(yaw * TURN_SPEED) * freecamSpeed;
                v.z -= (float) Math.cos(yaw * TURN_SPEED) * freecamSpeed;
                Vector3f.add(position, v, position);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                Vector3f v = new Vector3f();
                v.x -= (float) Math.sin(yaw * TURN_SPEED) * freecamSpeed;
                v.z += (float) Math.cos(yaw * TURN_SPEED) * freecamSpeed;
                Vector3f.add(position, v, position);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
                Vector3f v = new Vector3f();
                v.x -= (float) Math.cos(yaw * TURN_SPEED) * freecamSpeed;
                v.z -= (float) Math.sin(yaw * TURN_SPEED) * freecamSpeed;
                Vector3f.add(position, v, position);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                Vector3f v = new Vector3f();
                v.x += (float) Math.cos(yaw * TURN_SPEED) * freecamSpeed;
                v.z += (float) Math.sin(yaw * TURN_SPEED) * freecamSpeed;
                Vector3f.add(position, v, position);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                position.y += freecamSpeed;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                position.y -= freecamSpeed;
            }
            pitch -= Mouse.getDY() * 0.1f;
            yaw += Mouse.getDX() * 0.1f;
        }
    }
    
    public void setPitch(float pitch){
        this.pitch = pitch;
    }

    public void setYaw(float yaw){
        this.yaw = yaw;
    }
    
    public void move(Terrain terrain) {
        if (!freecam && !freezeMovement) {
            calculateZoom();
            calculateYaw();
            calculatePitch();
            calculateAngleAroundPlayer();
            float horizontalDistance = calculatoHorizontalDistance();
            float verticalDistance = calculatoVerticalDistance();
            calculateCameraPosition(horizontalDistance, verticalDistance);
            this.yaw = 180 - (player.getRotY() + angleAroundThePlayer);
            float terrainHeight = terrain.getHeightOfTerrain(position.x, position.z);
            if (position.y < terrainHeight) {
                position.y = terrainHeight + 0.2f;
            }
        }
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setPosition(float x, float y, float z) {
        this.position = new Vector3f(x,y,z);
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float invertPitch() {
        return this.pitch = -pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    public void calculateYaw() {
        if (player.IsMoving()) {
            angleAroundThePlayer = 0;
        }
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
        if (player.IsMoving()) {
            float theta = player.getRotY();
            float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
            float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
            position.x = player.getPosition().x - offsetX;
            position.y = player.getPosition().y + verticalDistance + 3;
            position.z = player.getPosition().z - offsetZ;
        } else {
            float theta = player.getRotY() + angleAroundThePlayer;
            float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
            float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
            position.x = player.getPosition().x - offsetX;
            position.y = player.getPosition().y + verticalDistance + 3;
            position.z = player.getPosition().z - offsetZ;
        }
    }

    private float calculatoHorizontalDistance() {
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculatoVerticalDistance() {
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }

    private void calculateZoom() {
        float zoomLevel = Mouse.getDWheel() * 0.1f;
        distanceFromPlayer -= zoomLevel;
    }

    private void calculatePitch() {
        if (Mouse.isButtonDown(1)) {
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
        }
    }

    private void calculateAngleAroundPlayer() {
        if (Mouse.isButtonDown(0) && !player.IsMoving()) {
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundThePlayer -= angleChange;
        }
    }

    public void setFreecamSpeed(float speed) {
        freecamSpeed = speed;
    }
    
    public void disableMovement() {
        freezeMovement = true;
    }

    public void enableMovement() {
        freezeMovement = false;
    }

    public void printCameraProps(){
        ConsoleOutput.appendToLog(position.toString(), ConsoleOutput.LogType.METHOD_OUTPUT);
        ConsoleOutput.appendToLog("Yaw: " + yaw, ConsoleOutput.LogType.METHOD_OUTPUT);
        ConsoleOutput.appendToLog("Pitch: " + pitch, ConsoleOutput.LogType.METHOD_OUTPUT);
    }
    
    public void printPosition() {
        ConsoleOutput.appendToLog(position.toString(), ConsoleOutput.LogType.INFO);
    }

    public Method getMethod(String method) throws NoSuchMethodException {
        return Camera.class.getDeclaredMethod(method, (Class<?>[]) null);
    }

}
