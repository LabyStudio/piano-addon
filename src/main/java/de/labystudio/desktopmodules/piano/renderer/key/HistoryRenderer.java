package de.labystudio.desktopmodules.piano.renderer.key;

import de.labystudio.desktopmodules.core.renderer.IRenderContext;
import de.labystudio.desktopmodules.piano.receiver.NoteController;
import de.labystudio.desktopmodules.piano.receiver.PlayedNote;
import de.labystudio.desktopmodules.piano.renderer.LayoutRenderer;

public class HistoryRenderer implements KeyRenderer {

    private static final float SPEED_FACTOR = 4F;

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
        for (PlayedNote note : controller.getHistory()) {
            if (note.getKeyId() == id) {
                float durationStart = System.currentTimeMillis() - note.getTimeStart();
                float durationEnd = note.isReleased() ? System.currentTimeMillis() - note.getTimeEnd() : 0;

                float noteYStart = top - durationStart / SPEED_FACTOR;
                float noteYEnd = top - durationEnd / SPEED_FACTOR;

                context.drawGradientRect(
                        left,
                        noteYStart,
                        right,
                        noteYEnd,
                        LayoutRenderer.COLOR_RIGHT_HAND_BRIGHT,
                        left,
                        noteYStart,
                        LayoutRenderer.COLOR_RIGHT_HAND,
                        right,
                        noteYStart
                );
            }
        }
    }
}
