package de.labystudio.desktopmodules.piano.receiver;

public class PlayedNote {

    private final int keyId;
    private final long timeStart;
    private final float strength;

    private long timeEnd;

    public PlayedNote(int keyId, long currentTimeMillis, float strength) {
        this.keyId = keyId;
        this.timeStart = currentTimeMillis;
        this.strength = strength;
    }

    public int getKeyId() {
        return this.keyId;
    }

    public long getTimeStart() {
        return this.timeStart;
    }

    public float getStrength() {
        return this.strength;
    }

    public long getTimeEnd() {
        return this.timeEnd;
    }

    public boolean isReleased() {
        return this.timeEnd != 0;
    }

    public void release() {
        this.timeEnd = System.currentTimeMillis();
    }
}
