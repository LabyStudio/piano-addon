package de.labystudio.desktopmodules.piano.renderer.key;

import de.labystudio.desktopmodules.core.renderer.IRenderContext;
import de.labystudio.desktopmodules.piano.receiver.NoteController;
import de.labystudio.desktopmodules.piano.renderer.LayoutRenderer;

import java.awt.Color;

public class DefaultKeyRenderer implements KeyRenderer {

    @Override
    public void render(
            NoteController controller,
            IRenderContext context,
            int id,
            float left,
            float top,
            float right,
            float bottom,
            boolean pressed,
            boolean blackKey
    ) {
        Color color1 = pressed ? LayoutRenderer.COLOR_RIGHT_HAND : (blackKey ? LayoutRenderer.COLOR_BLACK : LayoutRenderer.COLOR_WHITE);
        Color color2 = pressed ? LayoutRenderer.COLOR_RIGHT_HAND_BRIGHT : (blackKey ? LayoutRenderer.COLOR_BLACK_BRIGHT : LayoutRenderer.COLOR_WHITE_BRIGHT);

        context.drawGradientRect(
                left,
                top,
                right,
                bottom,
                color1,
                left,
                top,
                color2,
                right,
                top
        );
    }
}
