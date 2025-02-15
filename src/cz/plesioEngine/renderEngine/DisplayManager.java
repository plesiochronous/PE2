package cz.plesioEngine.renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int FPS_CAP = 144;

    private static long lastFrameTime;
    private static float delta;

    public static void createDisplay() {
        ContextAttribs attribs = new ContextAttribs(4, 5)
                .withForwardCompatible(true)
                .withProfileCore(true);
        
        PixelFormat pf = new PixelFormat().withDepthBits(24);
        //pf.withDepthBits(24);

        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.create(pf, attribs);
            Display.setTitle("PE2");
            GL11.glEnable(GL13.GL_MULTISAMPLE);
        } catch (LWJGLException e) {
            e.printStackTrace(System.err);
        }
        
        GL11.glViewport(0, 0, WIDTH, HEIGHT);
        lastFrameTime = getCurrentTime();
        Mouse.setGrabbed(true);
    }

    public static void updateDisplay() {
        Display.setVSyncEnabled(false);
        Display.sync(FPS_CAP);
        Display.update();
        long currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;
    }

    public static float getFrameTimeSeconds() {
        return delta;
    }

    public static void closeDisplay() {

        Display.destroy();

    }

    private static long getCurrentTime() {
        return Sys.getTime() * 1000 / Sys.getTimerResolution();
    }

}
