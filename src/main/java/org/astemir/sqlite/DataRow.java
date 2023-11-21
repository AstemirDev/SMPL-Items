package org.astemir.sqlite;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DataRow {

    private Map<String,Object> rawData = new LinkedHashMap<>();

    public void add(String key,Object value){rawData.put(key,value);}
    public String getString(String key){
        return (String) rawData.get(key);
    }

    public int getInt(String key){
        return (int) rawData.get(key);
    }

    public double getDouble(String key){
        return (double) rawData.get(key);
    }

    public float getFloat(String key){
        return (float) rawData.get(key);
    }

    public boolean getBoolean(String key){
        return (int)rawData.get(key) == 1 ? true : false;
    }

    public Object getRaw(String key){
        return rawData.get(key);
    }

    public Set<String> keys(){
        return rawData.keySet();
    }

    public boolean contains(String key){
        return rawData.containsKey(key);
    }
}
