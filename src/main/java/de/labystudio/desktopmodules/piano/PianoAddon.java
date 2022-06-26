package de.labystudio.desktopmodules.piano;

import com.google.gson.JsonObject;
import de.labystudio.desktopmodules.core.addon.Addon;
import de.labystudio.desktopmodules.core.module.Module;
import de.labystudio.desktopmodules.piano.modules.MiniPianoModule;
import de.labystudio.desktopmodules.piano.modules.PianoTutorialModule;
import de.labystudio.desktopmodules.piano.receiver.NoteController;
import de.labystudio.desktopmodules.piano.receiver.PlayedNote;
import de.labystudio.desktopmodules.piano.renderer.LayoutRenderer;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PianoAddon extends Addon {

    private final NoteController controller = new NoteController(this);
    private final LayoutRenderer layoutRenderer = new LayoutRenderer(this.controller);

    private final List<MidiDevice> opened = new ArrayList<>();

    private int prevExistingMidiDevices = 0;
    private boolean showMiniPianoOnDetect = false;

    private boolean broadcastEnabled;
    private InetSocketAddress broadcastAddress;
    private DatagramSocket broadcastSocket;

    @Override
    public void onInitialize() throws Exception {
        this.registerModule(MiniPianoModule.class);
        this.registerModule(PianoTutorialModule.class);

        // Update midi devices
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            if ((this.showMiniPianoOnDetect || this.hasActiveModules()) && MidiSystem.getMidiDeviceInfo().length != this.prevExistingMidiDevices) {
                this.refreshMidiDevices(true);
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    @Override
    public void loadConfig() throws IOException {
        super.loadConfig();

        this.showMiniPianoOnDetect = Addon.getConfigValue(this.config, "show_mini_piano_on_detect", false);

        JsonObject broadcastToServer = Addon.getConfigObject(this.config, "broadcast_to_server");
        this.broadcastEnabled = Addon.getConfigValue(broadcastToServer, "enabled", false);
        String address = Addon.getConfigValue(broadcastToServer, "address", "localhost");
        int port = Addon.getConfigValue(broadcastToServer, "port", 0);

        if (this.broadcastEnabled) {
            this.broadcastAddress = new InetSocketAddress(address, port);
            this.broadcastSocket = new DatagramSocket();
        }
    }

    @Override
    public void onEnable() throws Exception {
        this.refreshMidiDevices(false);
    }

    private void refreshMidiDevices(boolean readOnly) {
        synchronized (this) {
            JsonObject devicesObject = this.config.has("devices") ? this.config.getAsJsonObject("devices") : new JsonObject();

            try {
                MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
                for (MidiDevice.Info info : infos) {
                    MidiDevice device = MidiSystem.getMidiDevice(info);

                    // Check if device has transmitter
                    if (device.getMaxTransmitters() == 0) {
                        continue;
                    }

                    String id = info.getName();
                    boolean enabled = devicesObject.has(id) && devicesObject.get(id).getAsBoolean();

                    // Check if device is enabled in config
                    if (enabled) {
                        if (this.opened.contains(device)) {
                            continue;
                        }

                        // Open device and set receiver
                        device.open();
                        device.getTransmitter().setReceiver(this.controller);
                        this.opened.add(device);

                        // Show mini piano on detect
                        if (this.showMiniPianoOnDetect) {
                            this.setMiniPianoVisibility(true);
                        }
                    } else if (!devicesObject.has(id) && !readOnly) {
                        devicesObject.addProperty(id, false);
                    }
                }

                this.prevExistingMidiDevices = infos.length;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!readOnly) {
                this.config.add("devices", devicesObject);
            }
        }
    }

    @Override
    public void onDisable() throws Exception {
        // Close devices and remove receivers
        for (MidiDevice midiDevice : this.opened) {
            try {
                midiDevice.getTransmitter().setReceiver(null);
            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }
            midiDevice.close();
        }
        this.opened.clear();
    }

    public void onDeviceClosed() {
        // Remove receiver and remove device from list
        this.opened.stream().filter(device -> !device.isOpen()).forEach(device -> {
            try {
                device.getTransmitter().setReceiver(null);
            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }
        });
        this.opened.removeIf(device -> !device.isOpen());

        // Hide mini piano
        if (this.showMiniPianoOnDetect) {
            this.setMiniPianoVisibility(false);
        }
    }

    private void setMiniPianoVisibility(boolean visible) {
        for (Module<? extends Addon> module : this.getModules()) {
            if (module instanceof MiniPianoModule && module.isEnabled() != visible) {
                module.setEnabled(visible);
            }
        }
    }

    public LayoutRenderer getLayoutRenderer() {
        return this.layoutRenderer;
    }

    public void onNotePressed(PlayedNote note, float strength) {
        if (this.broadcastEnabled) {
            byte[] payload = new byte[]{0, (byte) note.getKeyId(), (byte) (strength * 127)};
            this.broadcast(payload);
        }
    }

    public void onNoteReleased(PlayedNote note, boolean clear) {
        if (this.broadcastEnabled) {
            byte[] payload = new byte[]{1, (byte) note.getKeyId(), (byte) (clear ? 1 : 0)};
            this.broadcast(payload);
        }
    }

    public void onPedalUpdate(boolean pressed) {
        if (this.broadcastEnabled) {
            byte[] payload = new byte[]{2, (byte) (pressed ? 1 : 0), 0};
            this.broadcast(payload);
        }
    }

    private void broadcast(byte[] payload) {
        try {
            this.broadcastSocket.send(new DatagramPacket(payload, 3, this.broadcastAddress));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
