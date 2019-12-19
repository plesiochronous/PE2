package cz.plesioEngine.toolbox;

import java.io.File;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import cz.plesioEngine.fontMeshCreator.FontType;
import cz.plesioEngine.fontMeshCreator.GUIText;
import cz.plesioEngine.fontRendering.TextMaster;
import cz.plesioEngine.renderEngine.Loader;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.util.vector.Vector3f;

public abstract class ConsoleInput extends InputStream {

    private static final float FONT_SIZE = 1f;
    private static final float MAX_LINE_LENGTH = 1f;
    private static final boolean CENTERED = false;
    private static final Vector2f POSITION = new Vector2f(0.01f, 0.97f);
    private static boolean isTyping = false;
    private static String text = "Debug Console Loaded";
    private static GUIText guiText;
    private static FontType font;

    private static boolean confirmed = false;

    private static final Map<String, Method> METHOD_MAP = new HashMap<>();
    private static final Map<String, Object> OBJECT_MAP = new HashMap<>();

    public static void init(Loader loader) {
        font = new FontType(loader.LoadFontTextureAtlas("candara"), new File("res/font/candara.fnt"));
    }

    private static void createText() {
        guiText = new GUIText(text, FONT_SIZE, font, POSITION, MAX_LINE_LENGTH, CENTERED);
        guiText.setColour(1, 1, 1);
    }

    private static void destroyText() {
        guiText.remove();
    }

    private static void changeText(char newCharacter) {
        if (newCharacter >= 32 && newCharacter <= 255) {
            text = text + String.valueOf(newCharacter);
        }
        if (newCharacter == 8 && text.length() > 0) {
            text = text.substring(0, text.length() - 1);
        }
    }

    private static void getKeyboardChange() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (isTyping) {
                    char newCharacter = Keyboard.getEventCharacter();
                    changeText(newCharacter);
                    if (Keyboard.getEventKey() == Keyboard.KEY_RETURN) {
                        confirmed = true;
                    }
                }
                if (Keyboard.getEventKey() == Keyboard.KEY_F2) {
                    isTyping = !isTyping;
                    text = "";
                }
            }
        }
    }

    public static boolean getIsTyping() {
        return isTyping;
    }

    public static void registerMethod(Object obj, Method method, String command) {
        METHOD_MAP.put(command, method);
        OBJECT_MAP.put(command, obj);
    }

    private static void changeEngineState() {
        if (confirmed) {

            ConsoleOutput.appendToLog(text, ConsoleOutput.LogType.INFO);

            findMethodAndExecute();

            text = "";
            confirmed = false;
            isTyping = false;
        }
    }

    private static void changeTextState() {
        getKeyboardChange();
        createText();
    }

    public static void update() {
        changeTextState();
        changeEngineState();
        TextMaster.render();
        destroyText();
    }

    public static void listCommands(){
        for (Map.Entry pair : METHOD_MAP.entrySet()) {
            ConsoleOutput.appendToLog(pair.getKey() + " " + pair.getValue(), 
                    ConsoleOutput.LogType.METHOD_OUTPUT);
        }
    }
    
    private static Object[] parseTypesAndConvert(Class<?>[] types,
            String[] arguments) {
        Object[] out = new Object[types.length];

        for (int i = 0; i < types.length; i++) {

            if (types[i].equals(Float.TYPE)) {
                out[i] = Float.parseFloat(arguments[i]);
                continue;
            }

            if (types[i].equals(Vector3f.class)) {
                Vector3f newVector = new Vector3f();

                newVector.x = Float.parseFloat(arguments[i]);
                newVector.y = Float.parseFloat(arguments[i + 1]);
                newVector.z = Float.parseFloat(arguments[i + 2]);

                out[i] = newVector;

                continue;
            }

        }

        return out;
    }

    private static void findMethodAndExecute() {
        String firstCommand;
        String[] arguments;

        firstCommand = text.split(" ")[0];

        text = text.replace(firstCommand + " ", "");

        arguments = text.split(" ");

        if (!METHOD_MAP.containsKey(firstCommand)) {
            ConsoleOutput.appendToLog("Unknown command: " + firstCommand,
                    ConsoleOutput.LogType.ERR);
            return;
        }
        try {
            Method getMethod = METHOD_MAP.get(firstCommand);
            Object obj = OBJECT_MAP.get(firstCommand);
            if (getMethod.getParameterCount() == 0) {
                getMethod.invoke(obj, (Object[]) null);
            } else {
                Class<?>[] types = getMethod.getParameterTypes();
                Object[] argumentObjects = parseTypesAndConvert(types, arguments);

                getMethod.invoke(obj, argumentObjects);
            }

        } catch (ArrayIndexOutOfBoundsException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            ConsoleOutput.appendToLog(ex.toString(), ConsoleOutput.LogType.ERR);
        }

    }

}
