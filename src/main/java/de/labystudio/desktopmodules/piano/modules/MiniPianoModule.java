package de.labystudio.desktopmodules.piano.modules;

import de.labystudio.desktopmodules.core.loader.TextureLoader;
import de.labystudio.desktopmodules.core.module.Module;
import de.labystudio.desktopmodules.core.renderer.IRenderContext;
import de.labystudio.desktopmodules.piano.PianoAddon;
import de.labystudio.desktopmodules.piano.renderer.key.DefaultKeyRenderer;

public class MiniPianoModule extends Module<PianoAddon> {

    private final DefaultKeyRenderer keyRenderer = new DefaultKeyRenderer();

    public MiniPianoModule() {
        super(300, 30);
    }


    @Override
    public void onRender(IRenderContext context, int width, int height, int mouseX, int mouseY) {
        this.addon.getLayoutRenderer().renderLayout(context, 0, 0, width, height, this.keyRenderer);
    }


    @Override
    public void loadTextures(TextureLoader textureLoader) {

    }

    @Override
    protected String getIconPath() {
        return "textures/piano/mini.png";
    }

    @Override
    public String getDisplayName() {
        return "Mini Piano";
    }
}
