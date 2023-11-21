package org.astemir.uniblend.io.json;

import com.google.gson.*;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.astemir.uniblend.core.UniblendModule;
import org.astemir.uniblend.core.command.UBlockedCommand;
import org.astemir.uniblend.core.display.RenderProperty;
import org.astemir.uniblend.core.entity.action.ActionController;
import org.astemir.uniblend.core.fishing.FishingDrop;
import org.astemir.uniblend.core.particle.beta.BetaParticleEmitter;
import org.astemir.uniblend.core.recipe.CraftComponent;
import org.astemir.uniblend.io.StringMap;
import org.astemir.uniblend.io.TextComponentMap;
import org.astemir.uniblend.misc.*;
import org.astemir.uniblend.core.entity.parent.UEntity;
import org.astemir.uniblend.core.gui.UGui;
import org.astemir.uniblend.core.gui.slot.UGuiSlot;
import org.astemir.uniblend.core.projectile.UProjectile;
import org.astemir.uniblend.core.setbonus.USetBonus;
import org.astemir.uniblend.core.recipe.parent.UCraftingRecipe;
import org.astemir.uniblend.core.attribute.UItemAttribute;
import org.astemir.uniblend.core.item.parent.UItem;
import org.astemir.uniblend.core.particle.UParticleEffect;
import org.astemir.uniblend.utils.ReflectionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.joml.Vector2d;
import org.joml.Vector3d;
import java.util.UUID;

public interface USerialization {
    Gson GSON = new GsonBuilder().
            registerTypeAdapter(AttributeModifier.class, UJsonDeserializer.ATTRIBUTE_MODIFIER).
            registerTypeAdapter(Class.class, UJsonDeserializer.CLASS).
            registerTypeAdapter(Component.class, UJsonDeserializer.TEXT_COMPONENT).
            registerTypeAdapter(TextColor.class, UJsonDeserializer.TEXT_COLOR).
            registerTypeAdapter(UUID.class, UJsonDeserializer.UUID).
            registerTypeAdapter(BlockData.class, UJsonDeserializer.BLOCK_DATA).
            registerTypeAdapter(Material.class, UJsonDeserializer.MATERIAL).
            registerTypeAdapter(Enchantment.class, UJsonDeserializer.ENCHANTMENT).
            registerTypeAdapter(Attribute.class, UJsonDeserializer.ATTRIBUTE).
            registerTypeAdapter(EquipmentSlot.class, UJsonDeserializer.EQUIPMENT_SLOT).
            registerTypeAdapter(NamespacedKey.class, UJsonDeserializer.NAMESPACED_KEY).
            registerTypeAdapter(PotionEffect.class, UJsonDeserializer.POTION_EFFECT).
            registerTypeAdapter(BossBar.class, UJsonDeserializer.BOSS_BAR).
            registerTypeAdapter(Vector3d.class, UJsonDeserializer.VECTOR3D).
            registerTypeAdapter(Vector3d.class, UJsonSerializer.VECTOR3D).
            registerTypeAdapter(Vector2d.class, UJsonDeserializer.VECTOR2D).
            registerTypeAdapter(Vector2d.class, UJsonSerializer.VECTOR2D).
            registerTypeAdapter(Vector.class, UJsonSerializer.VECTOR).
            registerTypeAdapter(Location.class, UJsonDeserializer.LOCATION).
            registerTypeAdapter(Location.class, UJsonSerializer.LOCATION).
            registerTypeAdapter(EntityType.class, UJsonDeserializer.ENTITY_TYPE).
            registerTypeAdapter(EntityType.class, UJsonSerializer.ENTITY_TYPE).
            registerTypeAdapter(ItemStack.class, UJsonDeserializer.ITEM_STACK).
            registerTypeAdapter(ItemComponent.class, ItemComponent.DESERIALIZER).
            registerTypeAdapter(CraftComponent.class, CraftComponent.DESERIALIZER).
            registerTypeAdapter(BetaParticleEmitter.class, BetaParticleEmitter.DESERIALIZER).
            registerTypeAdapter(UGui.Size.class, UGui.Size.DESERIALIZER).
            registerTypeAdapter(UCraftingRecipe.class, UCraftingRecipe.DESERIALIZER).
            registerTypeAdapter(UItemAttribute.class, UItemAttribute.DESERIALIZER).
            registerTypeAdapter(UItem.class, UItem.DESERIALIZER).
            registerTypeAdapter(UGui.class, UGui.DESERIALIZER).
            registerTypeAdapter(UGuiSlot.class, UGuiSlot.DESERIALIZER).
            registerTypeAdapter(SoundInstance.class,SoundInstance.DESERIALIZER).
            registerTypeAdapter(UParticleEffect.class, UParticleEffect.DESERIALIZER).
            registerTypeAdapter(USetBonus.class, USetBonus.DESERIALIZER).
            registerTypeAdapter(UEntity.class, UEntity.DESERIALIZER).
            registerTypeAdapter(UProjectile.class, UProjectile.DESERIALIZER).
            registerTypeAdapter(UBlockedCommand.class, UBlockedCommand.DESERIALIZER).
            registerTypeAdapter(ValueRange.class, ValueRange.DESERIALIZER).
            registerTypeAdapter(RandomizedColor.class, RandomizedColor.DESERIALIZER).
            registerTypeAdapter(ItemDrops.class, ItemDrops.DESERIALIZER).
            registerTypeAdapter(FishingDrop.class,FishingDrop.DESERIALIZER).
            registerTypeAdapter(TextComponentMap.class, TextComponentMap.DESERIALIZER).
            registerTypeAdapter(StringMap.class, StringMap.DESERIALIZER).
            registerTypeAdapter(ActionController.class,ActionController.DESERIALIZER).
            registerTypeAdapter(RenderProperty.class, RenderProperty.DESERIALIZER).
            create();


    static <T> Class<T> getClass(UniblendModule module, JsonObject object, String key){
        if (object.has(key)) {
            Class result = module.searchClass(object.get(key).getAsString());
            if (result != null) {
                return result;
            }
        }
        throw new RuntimeException("Class not found "+key);
    }

    static boolean getBoolean(JsonObject object,String key,boolean defaultValue){
        if (object.has(key)) {
            return object.get(key).getAsBoolean();
        }
        return defaultValue;
    }

    static <T extends Enum> T getEnum(JsonObject object,String key,Class<T> enumClass,T defaultValue){
        if (object.has(key)) {
            return ReflectionUtils.searchEnum(enumClass,object.get(key).getAsString());
        }
        return defaultValue;
    }

    static String getString(JsonObject object,String key,String defaultValue){
        if (object.has(key)) {
            return object.get(key).getAsString();
        }
        return defaultValue;
    }

    static int getInt(JsonObject object,String key,int defaultValue){
        if (object.has(key)) {
            return object.get(key).getAsInt();
        }
        return defaultValue;
    }

    static double getDouble(JsonObject object,String key,double defaultValue){
        if (object.has(key)) {
            return object.get(key).getAsDouble();
        }
        return defaultValue;
    }

    static float getFloat(JsonObject object,String key,float defaultValue){
        if (object.has(key)) {
            return object.get(key).getAsFloat();
        }
        return defaultValue;
    }


    static boolean getBoolean(JsonObject object,String key){
        if (object.has(key)) {
            return object.get(key).getAsBoolean();
        }
        throw new RuntimeException(key+" not found");
    }

    static <T extends Enum> T getEnum(JsonObject object,String key,Class<T> enumClass){
        if (object.has(key)) {
            return ReflectionUtils.searchEnum(enumClass,object.get(key).getAsString());
        }
        throw new RuntimeException(key+" not found");
    }

    static String getString(JsonObject object,String key){
        if (object.has(key)) {
            return object.get(key).getAsString();
        }
        throw new RuntimeException(key+" not found");
    }

    static int getInt(JsonObject object,String key){
        if (object.has(key)) {
            return object.get(key).getAsInt();
        }
        throw new RuntimeException(key+" not found");
    }

    static double getDouble(JsonObject object,String key){
        if (object.has(key)) {
            return object.get(key).getAsDouble();
        }
        throw new RuntimeException(key+" not found");
    }

    static float getFloat(JsonObject object,String key){
        if (object.has(key)) {
            return object.get(key).getAsFloat();
        }
        throw new RuntimeException(key+" not found");
    }

    static <T extends Enum> T asEnum(JsonElement element,Class<T> enumClass){
        return ReflectionUtils.searchEnum(enumClass,element.getAsString());
    }

    static <T> T as(JsonElement object,Class<T> className){
        return GSON.fromJson(object,className);
    }
    static <T> T get(JsonObject object,String key, Class<T> className){
        return GSON.fromJson(object.get(key),className);
    }
    static <T> T getOr(JsonObject object,String key, Class<T> className,T defaultValue){
        if (object.has(key)) {
            return GSON.fromJson(object.get(key), className);
        }else{
            return defaultValue;
        }
    }

    static <T> JsonElement serialize(T object){return GSON.toJsonTree(object);}
    static <T> T deserialize(JsonElement json,Class<T> className){
        return GSON.fromJson(json,className);
    }
    static <T> T deserialize(String json,Class<T> className){
        return GSON.fromJson(json,className);
    }
}
