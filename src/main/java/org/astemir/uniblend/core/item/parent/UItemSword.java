package org.astemir.uniblend.core.item.parent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.utils.TextUtils;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class UItemSword extends UItem {

    @Property("attack-damage")
    private double propDamage = 3.0;
    @Property("attack-speed")
    private double propAttackSpeed = -2.4;
    public double getDamage(){
        return propDamage;
    }

    public double getAttackSpeed(){
        return propAttackSpeed;
    }

    @Override
    public List<Component> setupLore(ItemStack itemStack) {
        return Arrays.asList(Component.empty(),
                TextUtils.translate("item.modifiers.mainhand", NamedTextColor.GRAY),
                Component.text(" ").
                        append(TextUtils.translate("attribute.name.generic.attack_damage",NamedTextColor.DARK_GREEN)).
                        append(TextUtils.text(": ",NamedTextColor.DARK_GREEN)).
                        append(TextUtils.text(DEFAULT_DOUBLE_FORMAT.format((1+getDamage())),NamedTextColor.DARK_GREEN)),
                Component.text(" ").
                        append(TextUtils.translate("attribute.name.generic.attack_speed",NamedTextColor.DARK_GREEN)).
                        append(TextUtils.text(": ",NamedTextColor.DARK_GREEN)).
                        append(TextUtils.text(DEFAULT_DOUBLE_FORMAT.format(4+getAttackSpeed()),NamedTextColor.DARK_GREEN))
        );
    }

    @Override
    public ItemMeta setupMeta(ItemStack itemStack, ItemMeta meta) {
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        return super.setupMeta(itemStack, meta);
    }

    @Override
    public ItemStack toItemStack() {
        ItemStack itemStack = super.toItemStack();
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        nmsItemStack.addAttributeModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF"), "Weapon modifier", getDamage(), AttributeModifier.Operation.ADDITION), EquipmentSlot.MAINHAND);
        nmsItemStack.addAttributeModifier(Attributes.ATTACK_SPEED, new AttributeModifier(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"), "Weapon modifier", getAttackSpeed(),AttributeModifier.Operation.ADDITION), EquipmentSlot.MAINHAND);
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }
}
