/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.plesioEngine.toolbox;

import cz.plesioEngine.fontMeshCreator.FontType;
import cz.plesioEngine.fontMeshCreator.GUIText;
import cz.plesioEngine.fontRendering.TextMaster;
import cz.plesioEngine.renderEngine.Loader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author plesio
 */
public class ConsoleOutput {

    public enum LogType {
        ERR, INFO, METHOD_OUTPUT
    }

    private static final float FONT_SIZE = 1f;
    private static final float MAX_LINE_LENGTH = 1f;
    private static final boolean CENTERED = false;
    private static final Vector2f POSITION = new Vector2f(0.01f, 0.5f);

    private static FontType font;

    private static final List<String> LOG_STRINGS = new ArrayList<>();
    private static final List<LogType> LOG_TYPES = new ArrayList<>();

    public static void init(Loader loader) {
        font = new FontType(loader.LoadFontTextureAtlas("candara"),
                new File("res/font/candara.fnt"));
    }

    private static void createText() {

        for (int i = 0; i < LOG_STRINGS.size(); i++) {
            POSITION.y += 0.025f;
            //POSITION.y += 0.05f;
            GUIText text = new GUIText(LOG_STRINGS.get(i), FONT_SIZE, font, POSITION,
                    MAX_LINE_LENGTH, CENTERED);

            switch (LOG_TYPES.get(i)) {
                case ERR:
                    text.setColour(1, 0.1f, 0.1f);
                    break;
                case INFO:
                    text.setColour(1, 1, 1);
                    break;
                case METHOD_OUTPUT:
                    text.setColour(0.5f, 1, 0.5f);
                    break;
            }

            TextMaster.render();
            TextMaster.RemoveText(text);
        }
    }

    public static void clearLog() {
        LOG_STRINGS.clear();
        LOG_TYPES.clear();
    }

    private static void resetTextPosition() {
        POSITION.y = 0.5f;
    }

    public static void appendToLog(String s, LogType type) {
        if (LOG_STRINGS.size() > 15) {
            LOG_STRINGS.clear();
            LOG_TYPES.clear();
        }
        LOG_STRINGS.add(s);
        LOG_TYPES.add(type);
    }

    public static void update() {
        createText();
        resetTextPosition();
    }

}
