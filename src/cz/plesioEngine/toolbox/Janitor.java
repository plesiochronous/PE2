/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.plesioEngine.toolbox;

import cz.plesioEngine.fontRendering.TextMaster;
import cz.plesioEngine.guis.GuiRenderer;
import cz.plesioEngine.particles.ParticleMaster;
import cz.plesioEngine.renderEngine.Loader;
import cz.plesioEngine.renderEngine.MasterRenderer;
import cz.plesioEngine.water.WaterFrameBuffers;

/**
 * Responsible for cleaning shaders, VBOs, VAOs
 * @author plesio
 */
public class Janitor {

    public static void cleanUp( GuiRenderer guiRenderer, 
            MasterRenderer masterRenderer, Loader loader){
        guiRenderer.cleanUp();
        masterRenderer.cleanUp();
        loader.cleanUp();
        ParticleMaster.cleanUp();
        TextMaster.cleanUp();
    }
    
    
    
}
