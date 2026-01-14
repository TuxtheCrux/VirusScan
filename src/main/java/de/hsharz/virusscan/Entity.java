package de.hsharz.virusscan;

import java.io.Serializable;

public record Entity(String hash, String virusName,
                     String detectionDate, String fileName,
                     ThreatLevel threatLevel) implements Comparable<Entity>, Serializable {

    public static Entity fromStringArray(String[] s){
        return new Entity(s[0], s[1], s[2], s[3], ThreatLevel.valueOf(s[4].toUpperCase()));
    }

    @Override
    public int compareTo(Entity entity){
        return this.hash.compareTo(entity.hash);
    }
}
