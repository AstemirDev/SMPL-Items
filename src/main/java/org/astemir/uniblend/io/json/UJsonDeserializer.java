package org.astemir.uniblend.io.json;

import com.google.gson.*;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.astemir.lib.ia.ItemsAdderLib;
import org.astemir.uniblend.core.item.parent.UItem;
import org.astemir.uniblend.core.item.UniblendItems;
import org.astemir.uniblend.utils.TextUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.joml.Vector2d;
import org.joml.Vector3d;
import java.lang.reflect.Type;
import java.util.UUID;
import static org.astemir.uniblend.io.json.USerialization.*;

public interface UJsonDeserializer<T> extends JsonDeserializer<T>,USerialization {

    UJsonDeserializer<NamespacedKey> NAMESPACED_KEY = (json)->{
        String key = json.getAsString();
        if (key.contains(":")){
            String[] split = key.split(":");
            return new NamespacedKey(split[0],split[1]);
        }else{
            return new NamespacedKey("minecraft",key);
        }
    };
    UJsonDeserializer<BlockData> BLOCK_DATA = (json)-> USerialization.as(json,Material.class).createBlockData();
    UJsonDeserializer<EquipmentSlot> EQUIPMENT_SLOT = (json)-> asEnum(json,EquipmentSlot.class);
    UJsonDeserializer<EntityType> ENTITY_TYPE = (json)-> asEnum(json, EntityType.class);
    UJsonDeserializer<Enchantment> ENCHANTMENT = (json)-> Enchantment.getByKey(new NamespacedKey("minecraft",json.getAsString()));
    UJsonDeserializer<Material> MATERIAL = (json)-> Material.matchMaterial(json.getAsString());

    UJsonDeserializer<UUID> UUID = (json)-> java.util.UUID.fromString(json.getAsString());
    UJsonDeserializer<Class> CLASS = (json)->{
        try {
            return Class.forName(json.getAsString());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    };

    UJsonDeserializer<ItemStack> ITEM_STACK = (json)->{
        if (json.isJsonPrimitive()){
            String id = json.getAsString();
            if (UniblendItems.INSTANCE.hasMatch(id)){
                return UniblendItems.INSTANCE.matchEntry(id).toItemStack();
            }else
            if (ItemsAdderLib.isCustom(id)){
                return ItemsAdderLib.getCustomStack(id).getItemStack();
            }else{
                return new ItemStack(Material.matchMaterial(id));
            }
        }else
        if (json.isJsonObject()){
            return USerialization.deserialize(json, UItem.class).toItemStack();
        }
        return null;
    };

    UJsonDeserializer<Vector3d> VECTOR3D = (json)->{
        JsonArray array = json.getAsJsonArray();
        return new Vector3d(array.get(0).getAsDouble(),array.get(1).getAsDouble(),array.get(2).getAsDouble());
    };

    UJsonDeserializer<Vector2d> VECTOR2D = (json)->{
        JsonArray array = json.getAsJsonArray();
        return new Vector2d(array.get(0).getAsDouble(),array.get(1).getAsDouble());
    };


    UJsonDeserializer<Location> LOCATION = (json)->{
        JsonObject jsonObject = json.getAsJsonObject();
        World world = Bukkit.getWorld(getString(jsonObject,"world"));
        Vector3d position = get(jsonObject,"position",Vector3d.class);
        float yaw = getFloat(jsonObject,"yaw",0);
        float pitch = getFloat(jsonObject,"pitch",0);
        return new Location(world,position.x,position.y,position.z,yaw,pitch);
    };

    UJsonDeserializer<BossBar> BOSS_BAR = (json)->{
        JsonObject jsonObject = json.getAsJsonObject();
        Component text = USerialization.get(jsonObject,"text",Component.class);
        float progress = USerialization.get(jsonObject,"value",Float.class);
        BossBar.Color color = USerialization.getEnum(jsonObject,"color",BossBar.Color.class, BossBar.Color.PURPLE);
        BossBar.Overlay overlay = USerialization.getEnum(jsonObject,"style",BossBar.Overlay.class, BossBar.Overlay.PROGRESS);
        return BossBar.bossBar(text,progress, color,overlay);
    };

    UJsonDeserializer<PotionEffect> POTION_EFFECT = (json)->{
        JsonObject jsonObject = json.getAsJsonObject();
        PotionEffectType effectType = PotionEffectType.getByName(jsonObject.get("effect").getAsString().toUpperCase());
        int duration = jsonObject.get("duration").getAsInt();
        int amplifier = jsonObject.get("amplifier").getAsInt();
        boolean showParticles = getBoolean(jsonObject,"show-particles",true);
        boolean isAmbient = getBoolean(jsonObject,"is-ambient",false);
        boolean hasIcon = getBoolean(jsonObject,"has-icon",true);
        return new PotionEffect(effectType,duration,amplifier,isAmbient,showParticles,hasIcon);
    };

    UJsonDeserializer<AttributeModifier> ATTRIBUTE_MODIFIER = (json)->{
        JsonObject object = json.getAsJsonObject();
        AttributeModifier.Operation operation = getEnum(object,"operation",AttributeModifier.Operation.class, AttributeModifier.Operation.ADD_NUMBER);
        EquipmentSlot slot = getEnum(object,"slot",EquipmentSlot.class, EquipmentSlot.HEAD);
        UUID uuid = get(object, "uuid", UUID.class);
        String name = getString(object,"bonus-name");
        double amount = getDouble(object,"amount");
        return new AttributeModifier(uuid, name, amount, operation, slot);
    };
    UJsonDeserializer<Attribute> ATTRIBUTE = (json)-> asEnum(json,Attribute.class);
    UJsonDeserializer<TextColor> TEXT_COLOR = (json) -> {
        if (json.isJsonPrimitive()){
            String colorStr = json.getAsString();
            if (colorStr.startsWith("#")) {
                return TextColor.fromHexString(colorStr);
            }else{
                return NamedTextColor.NAMES.value(colorStr);
            }
        }else
        if (json.isJsonArray()){
            JsonArray array = json.getAsJsonArray();
            return TextColor.color(array.get(0).getAsInt(),array.get(1).getAsInt(),array.get(2).getAsInt());
        }else
        if (json.isJsonObject()){
            JsonObject object = json.getAsJsonObject();
            TextColor from = get(object,"from", TextColor.class);
            TextColor to = get(object,"to", TextColor.class);
            float factor = getFloat(object,"factor",1.0f);
            return TextColor.lerp(factor,from,to);
        }
        return NamedTextColor.WHITE;
    };

    UJsonDeserializer TEXT_COMPONENT = new UJsonDeserializer<Component>() {
        @Override
        public Component deserialize(JsonElement json) {
            if (json.isJsonPrimitive()){
                return LegacyComponentSerializer.legacySection().deserialize(json.getAsString());
            }else
            if (json.isJsonObject()) {
                JsonObject jsonObject = json.getAsJsonObject();
                Component component = Component.empty();
                Style.Builder style = Style.style().decoration(TextDecoration.ITALIC, false);
                if (jsonObject.has("text")) {
                    component = TextUtils.text(jsonObject.get("text").getAsString(),NamedTextColor.WHITE);
                }else
                if (jsonObject.has("translate")) {
                    component = TextUtils.translate(jsonObject.get("translate").getAsString(),NamedTextColor.WHITE);
                    if (jsonObject.has("fallback")){
                        component = ((TranslatableComponent)component).fallback(jsonObject.get("fallback").getAsString());
                    }
                }
                if (jsonObject.has("color")){
                    style = style.color(USerialization.deserialize(jsonObject.get("color"), TextColor.class));
                }
                if (jsonObject.has("italic")){
                    style = style.decoration(TextDecoration.ITALIC,jsonObject.get("italic").getAsBoolean());
                }else{
                    style = style.decoration(TextDecoration.ITALIC,false);
                }
                if (jsonObject.has("bold")){
                    style = style.decoration(TextDecoration.BOLD,jsonObject.get("bold").getAsBoolean());
                }
                if (jsonObject.has("obfuscated")){
                    style = style.decoration(TextDecoration.OBFUSCATED,jsonObject.get("obfuscated").getAsBoolean());
                }
                if (jsonObject.has("strikethrough")){
                    style = style.decoration(TextDecoration.STRIKETHROUGH,jsonObject.get("strikethrough").getAsBoolean());
                }
                if (jsonObject.has("underlined")){
                    style = style.decoration(TextDecoration.UNDERLINED,jsonObject.get("underlined").getAsBoolean());
                }
                if (jsonObject.has("font")){
                    String keyFont = jsonObject.get("font").getAsString();
                    String[] key = TextUtils.namespaceKey(keyFont);
                    style = style.font(Key.key(key[0],key[1]));
                }
                return component.style(style);
            }else
            if (json.isJsonArray()){
                Component result = Component.empty();
                for (JsonElement jsonElement : json.getAsJsonArray()) {
                    result = result.append(deserialize(jsonElement));
                }
                return result;
            }
            return Component.empty();
        }
    };

    @Override
    default T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return deserialize(json);
    }

    T deserialize(JsonElement json);
}
