package de.hsharz.virusscan;

public enum ThreatLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    public String toString(){
        return this.name().toLowerCase();
    }
}
