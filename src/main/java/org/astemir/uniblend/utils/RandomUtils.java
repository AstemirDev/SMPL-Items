package org.astemir.uniblend.utils;

import java.util.Random;

public class RandomUtils {
    private static Random random = new Random();
    public static float randomFloat(float min,float max){
        return min + random.nextFloat() * (max - min);
    }

    public static boolean doWithChance(double chance) {
        double randomNumber = random.nextDouble() * 100;
        return randomNumber <= chance;
    }

    public static int randomInt(int range){return random.nextInt(range);}

    public static int randomInt(int min,int max){
        return min+random.nextInt(max);
    }

}
