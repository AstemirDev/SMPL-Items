package org.astemir.uniblend.utils;

import java.util.Arrays;

public class ArrayUtils {

    public static <T> T[] create(T... elements){
        return elements;
    }

    public static <T> T[] add(T[] array,T element){
        int newSize = array.length+1;
        T[] result = resize(array,newSize);
        result[newSize-1] = element;
        return result;
    }

    public static int[] add(int[] array,int element){
        int newSize = array.length+1;
        int[] result = resize(array,newSize);
        result[newSize-1] = element;
        return result;
    }

    public static <T> boolean contains(T[] array,T element){
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(element)){
                return true;
            }
        }
        return false;
    }

    public static <T> T[] remove(T[] array,T element){
        if (contains(array,element)) {
            int offset = 0;
            int newSize = array.length - 1;
            T[] result = resize(array, newSize);
            for (int i = 0; i < array.length; i++) {
                if (array[i].equals(element) && i < newSize) {
                    offset++;
                    result[i] = array[i + offset];
                } else {
                    if (i < newSize) {
                        result[i] = array[i + offset];
                    }
                }
            }
            return result;
        }
        return array;
    }

    public static <T> T[] removeAt(T[] array,int index){
        if (array.length > index) {
            if (contains(array, array[index])) {
                int offset = 0;
                int newSize = array.length - 1;
                T[] result = resize(array, newSize);
                for (int i = 0; i < array.length; i++) {
                    if (i == index && i < newSize) {
                        offset++;
                        result[i] = array[i + offset];
                    } else {
                        if (i < newSize) {
                            result[i] = array[i + offset];
                        }
                    }
                }
                return result;
            }
        }
        return array;
    }



    public static <T> T[] fill(T[] array,T... elements){
        T[] result = resize(array,elements.length);
        for (int i = 0; i < elements.length; i++) {
            result[i] = elements[i];
        }
        return result;
    }

    public static <T> T[] resize(T[] array,int newSize){
        return Arrays.copyOf(array,newSize);
    }

    public static int[] resize(int[] array,int newSize){
        return Arrays.copyOf(array,newSize);
    }
}
