package org.astemir.uniblend.io.json;

import com.google.gson.*;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;
import org.joml.Vector2d;
import org.joml.Vector3d;

import java.lang.reflect.Type;
import java.util.UUID;

public interface UJsonSerializer<T> extends JsonSerializer<T>, USerialization {
    UJsonSerializer<UUID> UUID = (uuid)-> new JsonPrimitive(uuid.toString());

    UJsonSerializer<EntityType> ENTITY_TYPE = (type)-> new JsonPrimitive(type.toString());

    UJsonSerializer<Vector> VECTOR = (vector)->{
        JsonArray array = new JsonArray();
        array.add(vector.getX());
        array.add(vector.getY());
        array.add(vector.getZ());
        return array;
    };
    UJsonSerializer<Vector3d> VECTOR3D = (vector3d)->{
        JsonArray array = new JsonArray();
        array.add(vector3d.x);
        array.add(vector3d.y);
        array.add(vector3d.z);
        return array;
    };

    UJsonSerializer<Vector2d> VECTOR2D = (vector2d)->{
        JsonArray array = new JsonArray();
        array.add(vector2d.x);
        array.add(vector2d.y);
        return array;
    };

    UJsonSerializer<Location> LOCATION = (location)->{
        JsonObject object = new JsonObject();
        object.add("position", USerialization.serialize(new Vector3d(location.x(),location.y(),location.z())));
        object.addProperty("world",location.getWorld().getName());
        object.addProperty("yaw",location.getYaw());
        object.addProperty("pitch",location.getPitch());
        return object;
    };

    @Override
    default JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        return serialize(src);
    }

    JsonElement serialize(T object);
}
