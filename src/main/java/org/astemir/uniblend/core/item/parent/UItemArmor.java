package org.astemir.uniblend.core.item.parent;

import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.world.entity.ai.attributes.Attributes;

import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.utils.NMSUtils;
import org.astemir.uniblend.utils.TextUtils;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UItemArmor extends UItem {
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{
            UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
            UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
            UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
            UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    @Property("armor-defense")
    private int defense = 0;
    @Property("armor-toughness")
    private double toughness = 0.0;
    @Property("armor-knockback-resistance")
    private double knockbackResistance = 0.0;
    @Property("armor-slot")
    private EquipmentSlot equipmentSlot = EquipmentSlot.HEAD;

    @Override
    public ItemMeta setupMeta(ItemStack itemStack, ItemMeta meta) {
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        return super.setupMeta(itemStack, meta);
    }

    @Override
    public List<Component> setupLore(ItemStack itemStack) {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        if (getDefense() > 0 || getToughness() > 0 || getKnockbackResistance() > 0) {
            switch (equipmentSlot) {
                case HEAD: {
                    lore.add(TextUtils.translate("item.modifiers.head", NamedTextColor.GRAY));
                    break;
                }
                case CHEST: {
                    lore.add(TextUtils.translate("item.modifiers.chest", NamedTextColor.GRAY));
                    break;
                }
                case LEGS: {
                    lore.add(TextUtils.translate("item.modifiers.legs", NamedTextColor.GRAY));
                    break;
                }
                case FEET: {
                    lore.add(TextUtils.translate("item.modifiers.feet", NamedTextColor.GRAY));
                    break;
                }
            }
        }
        if (getDefense() > 0) {
            lore.add(TextUtils.text(" +"+DEFAULT_DOUBLE_FORMAT.format((getDefense()))+" ",NamedTextColor.BLUE).
                    append(TextUtils.translate("attribute.name.generic.armor",NamedTextColor.BLUE)));
        }
        if (getToughness() > 0) {
            lore.add(TextUtils.text(" +"+DEFAULT_DOUBLE_FORMAT.format((getToughness()))+" ",NamedTextColor.BLUE).
                    append(TextUtils.translate("attribute.name.generic.armor_toughness",NamedTextColor.BLUE)));
        }
        if (getKnockbackResistance() > 0) {
            lore.add(TextUtils.text(" +"+DEFAULT_DOUBLE_FORMAT.format((getKnockbackResistance()))+" ",NamedTextColor.BLUE).
                    append(TextUtils.translate("attribute.name.generic.knockback_resistance",NamedTextColor.BLUE)));
        }
        return lore;
    }

    @Override
    public ItemStack toItemStack() {
        ItemStack itemStack = super.toItemStack();
        UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[NMSUtils.convert(equipmentSlot).getIndex()];
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        nmsItemStack.addAttributeModifier(Attributes.ARMOR, new net.minecraft.world.entity.ai.attributes.AttributeModifier(uuid, "Armor modifier", getDefense(), net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION),NMSUtils.convert(equipmentSlot));
        nmsItemStack.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, new net.minecraft.world.entity.ai.attributes.AttributeModifier(uuid, "Armor modifier", getToughness(), net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION),NMSUtils.convert(equipmentSlot));
        nmsItemStack.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, new net.minecraft.world.entity.ai.attributes.AttributeModifier(uuid, "Armor modifier", getKnockbackResistance(), net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION),NMSUtils.convert(equipmentSlot));
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }

    public int getDefense() {
        return defense;
    }

    public double getToughness() {
        return toughness;
    }

    public double getKnockbackResistance() {
        return knockbackResistance;
    }

    public EquipmentSlot getEquipmentSlot() {
        return equipmentSlot;
    }
}
