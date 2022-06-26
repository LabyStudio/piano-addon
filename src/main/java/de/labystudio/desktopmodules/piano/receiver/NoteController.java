package de.labystudio.desktopmodules.piano.receiver;

import de.labystudio.desktopmodules.piano.PianoAddon;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteController implements Receiver {

    private final PianoAddon addon;

    private final Map<Integer, PlayedNote> notes = new HashMap<>();
    private final List<PlayedNote> history = new ArrayList<>();
    private boolean pedal = false;

    public NoteController(PianoAddon addon) {
        this.addon = addon;
    }

    @Override
    public void send(MidiMessage midiMessage, long timeStamp) {
        int length = midiMessage.getLength();
        int status = midiMessage.getStatus();

        switch (length) {
            case 1:
                switch (status) {
                    case 254:
                        // Keep alive
                        break;
                    case 247:
                        this.disconnect();
                        break;
                }
                break;
            case 3:
                byte[] payload = midiMessage.getMessage();
                switch (status) {
                    case 128:
                        int keyId = payload[1];

                        synchronized (this) {
                            PlayedNote note = this.notes.remove(keyId);
                            if (note != null) {
                                note.release();

                                boolean clear = !this.pedal && this.notes.isEmpty();
                                this.addon.onNoteReleased(note, clear);
                            }
                        }
                        break;
                    case 176:
                        int action = payload[1];
                        int value = payload[2];

                        switch (payload[1]) {
                            case 64:
                                this.pedal = value > 0;
                                this.addon.onPedalUpdate(this.pedal);
                                break;
                            case 88:
                                // Unknown key data
                                break;
                            default:
                                System.out.println("Unknown action " + action + " and value " + value);
                                break;
                        }
                        break;
                    case 177:
                        // Unknown pedal data
                        break;
                    case 144:
                        keyId = payload[1];
                        float strength = Math.min(1, Math.max(payload[2] / 100.0F, 0));
                        PlayedNote note = new PlayedNote(keyId, System.currentTimeMillis(), strength);

                        synchronized (this) {
                            // Release previous note if it's still playing
                            if (this.isNotePressed(keyId)) {
                                this.notes.get(keyId).release();
                            }

                            // Add new note
                            this.notes.put(keyId, note);
                            this.history.add(note);
                            if (this.history.size() > 100) {
                                this.history.remove(0);
                            }

                            this.addon.onNotePressed(note, strength);
                        }
                        break;
                    default:
                        System.out.println("Unknown status " + status);
                        break;
                }
                break;
        }
    }

    @Override
    public void close() {
        this.disconnect();
    }

    private void disconnect() {
        this.notes.clear();
        this.history.clear();
        this.pedal = false;

        this.addon.onDeviceClosed();
    }

    public boolean isNotePressed(int keyId) {
        return this.notes.containsKey(keyId);
    }

    public PlayedNote getNote(int keyId) {
        return this.notes.get(keyId);
    }

    public List<PlayedNote> getHistory() {
        return this.history;
    }

    public boolean isPedal() {
        return this.pedal;
    }

    public int getMaxNote() {
        return this.getMinNote() + 88;
    }

    public int getMinNote() {
        return 21;
    }
}
