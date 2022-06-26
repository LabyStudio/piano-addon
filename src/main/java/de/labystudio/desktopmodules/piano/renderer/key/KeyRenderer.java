package de.labystudio.desktopmodules.piano.renderer.key;

import de.labystudio.desktopmodules.core.renderer.IRenderContext;
import de.labystudio.desktopmodules.piano.receiver.NoteController;

public interface KeyRenderer {
    void render(
            NoteController controller,
            IRenderContext context,
            int id,
            float left,
            float top,
            float right,
            float bottom,
            boolean pressed,
            boolean blackKey
    );
}
