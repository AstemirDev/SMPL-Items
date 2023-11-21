package org.astemir.uniblend.utils;

import org.bukkit.potion.PotionEffectType;

public class PotionUtils {

    public static boolean isHarmful(PotionEffectType type){
        if (type.equals(PotionEffectType.WITHER) || type.equals(PotionEffectType.BLINDNESS) || type.equals(PotionEffectType.HUNGER) || type.equals(PotionEffectType.BAD_OMEN) || type.equals(PotionEffectType.SLOW) || type.equals(PotionEffectType.SLOW_DIGGING) || type.equals(PotionEffectType.WEAKNESS) || type.equals(PotionEffectType.HARM) || type.equals(PotionEffectType.POISON)){
            return true;
        }
        return false;
    }
}
