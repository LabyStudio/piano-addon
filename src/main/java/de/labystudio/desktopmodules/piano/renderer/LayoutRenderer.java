package de.labystudio.desktopmodules.piano.renderer;

import de.labystudio.desktopmodules.core.renderer.IRenderContext;
import de.labystudio.desktopmodules.piano.receiver.NoteController;
import de.labystudio.desktopmodules.piano.renderer.key.KeyRenderer;

import java.awt.Color;

public class LayoutRenderer {

    public static final Color COLOR_BLACK = new Color(0, 0, 0);
    public static final Color COLOR_WHITE = new Color(205, 205, 205);

    public static final Color COLOR_BLACK_BRIGHT = new Color(50, 50, 50);
    public static final Color COLOR_WHITE_BRIGHT = new Color(255, 255, 255);

    public static final Color COLOR_LEFT_HAND = new Color(114, 165, 224);
    public static final Color COLOR_RIGHT_HAND = new Color(140, 238, 57);

    public static final Color COLOR_LEFT_HAND_BRIGHT = new Color(175, 205, 239);
    public static final Color COLOR_RIGHT_HAND_BRIGHT = new Color(194, 253, 137);

    private final NoteController controller;

    public LayoutRenderer(NoteController controller) {
        this.controller = controller;
    }

    public void renderLayout(IRenderContext context, int x, int y, int width, int height, KeyRenderer... renderers) {
        int minNote = this.controller.getMinNote();
        int maxNote = this.controller.getMaxNote();

        // Calculate amounts
        int totalNotes = maxNote - minNote;
        int octaves = totalNotes / 12;
        int blackKeys = octaves * 5 + 1;
        int whiteKeys = totalNotes - blackKeys;

        float whiteKeyWidth = width / (float) whiteKeys;

        // Render each layer
        synchronized (this.controller) {
            for (int layer = 0; layer < 2; layer++) {
                boolean blackLayer = layer == 1;

                // Render all keys
                int whiteKeyIndex = 0;
                for (int id = minNote; id < maxNote; id++) {
                    boolean blackKey = id % 12 == 1 || id % 12 == 3 || id % 12 == 6 || id % 12 == 8 || id % 12 == 10;

                    // Render key
                    if (blackKey == blackLayer) {
                        // Calculate key size
                        float left = x + whiteKeyIndex * whiteKeyWidth - (blackKey ? whiteKeyWidth / 2.5F : 0);
                        float right = left + (blackKey ? whiteKeyWidth / 2.5F * 2 : whiteKeyWidth);
                        float top = y;
                        float bottom = top + (blackKey ? height / 1.8F : height);

                        // Render actual key
                        boolean pressed = this.controller.isNotePressed(id);
                        for (KeyRenderer renderer : renderers) {
                            renderer.render(
                                    this.controller,
                                    context,
                                    id,
                                    left,
                                    top,
                                    right,
                                    bottom,
                                    pressed,
                                    blackKey
                            );
                        }
                    }

                    if (!blackKey) {
                        whiteKeyIndex++;
                    }
                }
            }
        }
    }

}
