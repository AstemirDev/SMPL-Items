package org.astemir.uniblend.misc;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.EnumUtils;
import org.astemir.uniblend.io.json.UJsonDeserializer;
import org.astemir.uniblend.io.json.USerialization;
import org.bukkit.Location;
import org.bukkit.Sound;

public class SoundInstance {

    public static final UJsonDeserializer<SoundInstance> DESERIALIZER = (json)->{
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            String soundName = jsonObject.get("sound").getAsString();
            String replacedName = soundName.replaceAll("\\.","_").toUpperCase();
            ValueRange volume = new ValueRange(1,1);
            ValueRange pitch = new ValueRange(1,1);
            if (jsonObject.has("volume")) {
                volume = USerialization.get(jsonObject,"volume", ValueRange.class);
            }
            if (jsonObject.has("pitch")) {
                pitch = USerialization.get(jsonObject, "pitch", ValueRange.class);
            }
            if (EnumUtils.isValidEnum(Sound.class,replacedName)){
                return new SoundInstance(Sound.valueOf(replacedName), volume, pitch);
            }else{
                return new SoundInstance(soundName, volume, pitch);
            }
        }else{
            String soundName = json.getAsString();
            String replacedName = soundName.replaceAll("\\.","_");
            if (EnumUtils.isValidEnum(Sound.class,replacedName)){
                return new SoundInstance(Sound.valueOf(replacedName), 1, 1);
            }else{
                return new SoundInstance(soundName, 1, 1);
            }
        }
    };
    private Sound sound;
    private String customSound;
    private ValueRange volume;
    private ValueRange pitch;

    public SoundInstance(Sound sound, ValueRange volume, ValueRange pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public SoundInstance(String customSound, ValueRange volume, ValueRange pitch) {
        this.customSound = customSound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public SoundInstance(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = new ValueRange(volume,volume);
        this.pitch = new ValueRange(pitch,pitch);
    }

    public SoundInstance(String customSound, float volume, float pitch) {
        this.customSound = customSound;
        this.volume = new ValueRange(volume,volume);
        this.pitch = new ValueRange(pitch,pitch);
    }

    public void play(Location location){
        if (sound != null) {
            location.getWorld().playSound(location, sound, volume.get(), pitch.get());
        }else
        if (customSound != null){
            location.getWorld().playSound(location,customSound,volume.get(),pitch.get());
        }
    }

    public void play(Location location,float volume,float pitch){
        if (sound != null) {
            location.getWorld().playSound(location, sound, volume, pitch);
        }else
        if (customSound != null){
            location.getWorld().playSound(location,customSound,volume,pitch);
        }
    }
}
