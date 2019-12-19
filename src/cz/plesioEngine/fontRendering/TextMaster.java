package cz.plesioEngine.fontRendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.plesioEngine.fontMeshCreator.FontType;
import cz.plesioEngine.fontMeshCreator.GUIText;
import cz.plesioEngine.fontMeshCreator.TextMeshData;
import cz.plesioEngine.renderEngine.Loader;

public class TextMaster {

    private static Loader _loader;
    private static final Map<FontType, List<GUIText>> TEXTS = new HashMap<>();
    private static FontRenderer renderer;

    public static void init(Loader loader) {
        renderer = new FontRenderer();
        _loader = loader;
    }

    public static void render() {
        renderer.render(TEXTS);
    }

    public static void LoadText(GUIText text) {
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = _loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<GUIText> textBatch = TEXTS.get(font);
        if (textBatch == null) {
            textBatch = new ArrayList<>();
            TEXTS.put(font, textBatch);
        }
        textBatch.add(text);
    }

    public static void RemoveText(GUIText text) {
        List<GUIText> textBatch = TEXTS.get(text.getFont());
        textBatch.remove(text);
        if (textBatch.isEmpty()) {
            TEXTS.remove(text.getFont());
        }
    }

    public static void cleanUp() {
        renderer.cleanUp();
    }

}
