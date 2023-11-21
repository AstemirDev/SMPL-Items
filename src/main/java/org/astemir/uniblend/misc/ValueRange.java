package org.astemir.uniblend.misc;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import org.astemir.uniblend.io.json.UJsonDeserializer;
import org.astemir.uniblend.utils.RandomUtils;

public class ValueRange {

    public static final UJsonDeserializer<ValueRange> DESERIALIZER = (json)->{
        if (json.isJsonPrimitive()){
            JsonPrimitive primitive = json.getAsJsonPrimitive();
            if (primitive.isString()){
                String str = primitive.getAsString();
                if (str.contains("-")) {
                    String[] split = str.split("-");
                    return new ValueRange(Float.parseFloat(split[0]), Float.parseFloat(split[1]));
                }else{
                    float value = Float.parseFloat(str);
                    return new ValueRange(value,value);
                }
            }else
            if (primitive.isNumber()){
                float value = primitive.getAsFloat();
                return new ValueRange(value,value);
            }
        }else
        if (json.isJsonArray()){
            JsonArray array = json.getAsJsonArray();
            if (array.size() == 1){
                return new ValueRange(array.get(0).getAsFloat(),array.get(1).getAsFloat());
            }
            if (array.size() == 2){
                float value = array.get(0).getAsFloat();
                return new ValueRange(value,value);
            }
        }
        return new ValueRange(0,0);
    };

    private float minValue;
    private float maxValue;

    public ValueRange(float minValue, float maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public ValueRange(float value) {
        this.minValue = value;
        this.maxValue = value;
    }

    public float get(){
        if (minValue != maxValue) {
            return RandomUtils.randomFloat(minValue, maxValue);
        }else{
            return minValue;
        }
    }

    public float getMinValue() {
        return minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }
}
