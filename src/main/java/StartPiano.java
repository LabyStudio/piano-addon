import de.labystudio.desktopmodules.piano.PianoAddon;

public class StartPiano {

    public static void main(String[] args) throws Exception {
        // Start the core with the addon
        Start.main(new String[]{PianoAddon.class.getName()});
    }

}
