package de.labystudio.desktopmodules.piano.modules;

import de.labystudio.desktopmodules.core.loader.TextureLoader;
import de.labystudio.desktopmodules.core.module.Module;
import de.labystudio.desktopmodules.core.renderer.IRenderContext;
import de.labystudio.desktopmodules.piano.PianoAddon;
import de.labystudio.desktopmodules.piano.renderer.key.DefaultKeyRenderer;
import de.labystudio.desktopmodules.piano.renderer.key.HistoryRenderer;

import java.awt.Color;

public class PianoTutorialModule extends Module<PianoAddon> {

    private final DefaultKeyRenderer keyRenderer = new DefaultKeyRenderer();
    private final HistoryRenderer historyRenderer = new HistoryRenderer();

    private int backgroundTransparency = 0;

    public PianoTutorialModule() {
        super(1200, 800);
    }

    @Override
    public void onTick() {
        if (this.isDragging()) {
            if (this.backgroundTransparency < 20) {
                this.backgroundTransparency += 4;
            }
        } else {
            if (this.backgroundTransparency > 0) {
                this.backgroundTransparency -= 4;
            }
        }
    }

    @Override
    public void onRender(IRenderContext context, int width, int height, int mouseX, int mouseY) {
        if (this.backgroundTransparency > 0) {
            context.drawRect(0, 0, width, height, new Color(255, 255, 255, this.backgroundTransparency));
        }

        int boardHeight = 140;
        this.addon.getLayoutRenderer().renderLayout(context, 0, height - boardHeight, width, boardHeight, this.keyRenderer, this.historyRenderer);
    }


    @Override
    public void loadTextures(TextureLoader textureLoader) {

    }

    @Override
    protected String getIconPath() {
        return "textures/piano/tutorial.png";
    }

    @Override
    public String getDisplayName() {
        return "Piano Tutorial";
    }
}
