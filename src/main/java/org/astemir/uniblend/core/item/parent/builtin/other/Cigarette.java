package org.astemir.uniblend.core.item.parent.builtin.other;

import net.kyori.adventure.text.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import org.astemir.uniblend.UniblendCorePlugin;
import org.astemir.uniblend.core.cooldown.UCooldownHandler;
import org.astemir.uniblend.core.display.RenderDisplayMode;
import org.astemir.uniblend.core.display.RenderProperty;
import org.astemir.uniblend.core.display.RenderSettings;
import org.astemir.uniblend.core.display.URenderer;
import org.astemir.uniblend.core.entity.utils.EntityUtils;
import org.astemir.uniblend.core.item.UniblendItems;
import org.astemir.uniblend.core.item.parent.UItem;
import org.astemir.uniblend.core.item.utils.ItemUtils;
import org.astemir.uniblend.core.particle.UParticleEffect;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.event.PlayerClickEvent;
import org.astemir.uniblend.event.UniblendEventListener;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.python.antlr.op.Eq;

public class Cigarette extends UItem {

    @Property("self-effect")
    private PotionEffect selfEffect = new PotionEffect(PotionEffectType.CONFUSION,100,0,false,false);
    @Property("cooldown")
    private int cooldown = 200;
    @Property("ignited-model-data")
    private int ignitedModelData = -1;
    @Property("used-model-data")
    private int usedModelData = -1;
    @Property("name-ignited")
    private Component nameIgnited;
    @Property("name-used")
    private Component nameUsed;
    private static final UParticleEffect PARTICLE_UNUSED = new UParticleEffect(Particle.SMOKE_LARGE).renderTimes(1);

    @Override
    public EventExecutionResult onRightClick(PlayerClickEvent e) {
        ItemStack itemStack = e.getItem();
        Player player = e.getPlayer();
        Location location = e.getPlayer().getLocation();
        EquipmentSlot offSlot = getAnotherHand(e.getHand());
        ItemStack offHand = e.getPlayer().getInventory().getItem(offSlot);
        int modelData = itemStack.getCustomModelData();
        if (UCooldownHandler.doWithCooldown(e.getPlayer(),"cigarette",cooldown) && modelData == getCustomModelData() && offHand != null && offHand.getType() == Material.FLINT_AND_STEEL) {
            offHand.damage(1,player);
            player.getInventory().setItem(offSlot,null);
            EntityUtils.dropItem(player,offHand);
            ItemStack newItemStack = itemStack.clone();
            ItemMeta meta = newItemStack.getItemMeta();
            if (modelData == getCustomModelData()) {
                meta.displayName(nameIgnited);
                newItemStack.setItemMeta(meta);
                newItemStack.setCustomModelData(ignitedModelData);
            }
            location.getWorld().playSound(location, Sound.ITEM_FLINTANDSTEEL_USE,1,1.25f);
            newItemStack.setAmount(1);
            EquipmentSlot hand = e.getHand();
            if (hand == EquipmentSlot.HAND) {
                player.swingMainHand();
            }else{
                player.swingOffHand();
            }
            URenderer renderer = new URenderer(location,new RenderSettings(new RenderProperty.ItemProperty(newItemStack, RenderDisplayMode.HEAD)).brightness(8)){
                @Override
                public void update() {
                    super.update();
                    if (player != null){
                        Vector direction = EntityUtils.getEntityDirection(player);
                        float rotX = (float)Math.toRadians(player.getLocation().getPitch());
                        float rotY = (float)Math.toRadians(-player.getLocation().getYaw());
                        settings().rotation(new Quaternionf().rotateXYZ(0, rotY+(float) Math.toRadians(90),rotX+(float) Math.toRadians(45)));
                        setPosition(player.getEyeLocation().clone().add(UniblendEventListener.getMotion(player)).add(0,-0.2f,0).add(direction.multiply(0.25f)));
                    }else{
                        remove();
                    }
                }
            };
            new BukkitRunnable() {
                int i = 0;
                @Override
                public void run() {
                    if (i == 0){
                        player.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_BREATH,1,1.25f);
                    }
                    if (i < 3){
                        Vector direction = EntityUtils.getEntityDirection(player);
                        float speed = 0.1f*((i+1)/4f);
                        UParticleEffect effect = new UParticleEffect(Particle.CAMPFIRE_SIGNAL_SMOKE).speed(direction.getX()*speed,direction.getY()*speed,direction.getZ()*speed).renderTimes(4);
                        effect.play(player.getEyeLocation().clone().add(UniblendEventListener.getMotion(player)).add(direction.multiply(0.5f)));
                        player.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT,0.5f,2f);
                        i++;
                    }else{
                        meta.displayName(nameUsed);
                        newItemStack.setItemMeta(meta);
                        newItemStack.setCustomModelData(usedModelData);
                        if (UniblendItems.isItem(player.getEquipment().getItem(hand), Cigarette.class)) {
                            player.getEquipment().setItem(hand, newItemStack);
                        }
                        renderer.remove();
                        cancel();
                    }
                }
            }.runTaskTimer(UniblendCorePlugin.getPlugin(),cooldown/2,30);
            PlayerUtils.itemGive(player,itemStack.clone().subtract(1));
            player.getEquipment().setItem(hand,newItemStack);
            player.addPotionEffect(selfEffect);
        }
        return super.onRightClick(e);
    }

    @Override
    public EventExecutionResult onJoin(PlayerJoinEvent e,EquipmentSlot slot) {
        ItemStack itemStack = e.getPlayer().getInventory().getItem(slot);
        if (check(itemStack) == EventExecutionResult.CANCEL){
            e.getPlayer().getInventory().setItem(slot,null);
        }
        return super.onJoin(e,slot);
    }



    private EquipmentSlot getAnotherHand(EquipmentSlot hand){
        if (hand == EquipmentSlot.HAND){
            return EquipmentSlot.OFF_HAND;
        }else{
            return EquipmentSlot.HAND;
        }
    }

    private EventExecutionResult check(ItemStack... itemStacks){
        for (ItemStack itemStack : itemStacks) {
            if (itemStack != null) {
                ItemMeta meta = itemStack.getItemMeta();
                if (meta != null){
                    if (itemStack.hasCustomModelData()) {
                        if (itemStack.getCustomModelData() == ignitedModelData) {
                            return EventExecutionResult.CANCEL;
                        }
                    }
                }
            }
        }
        return EventExecutionResult.PROCEED;
    }

    @Override
    public EventExecutionResult onDrop(PlayerDropItemEvent e) {
        return check(e.getItemDrop().getItemStack());
    }

    @Override
    public EventExecutionResult onItemSwap(PlayerSwapHandItemsEvent e,EquipmentSlot slot) {
        return check(e.getPlayer().getInventory().getItem(slot),e.getPlayer().getInventory().getItem(getAnotherHand(slot)));
    }

    @Override
    public EventExecutionResult onItemChange(PlayerItemHeldEvent e,int slot) {
        return check(e.getPlayer().getInventory().getItem(slot));
    }

    @Override
    public EventExecutionResult onInventoryClick(InventoryClickEvent e) {
        EventExecutionResult res = check(e.getCurrentItem());
        if (e instanceof InventoryCreativeEvent){
            return super.onInventoryClick(e);
        }
        return res;
    }

    @Override
    public EventExecutionResult onTick(Player player, ItemStack itemStack, long tick) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            if (tick % 5 == 0 && itemStack.getCustomModelData() == ignitedModelData) {
                Vector direction = EntityUtils.getEntityDirection(player);
                PARTICLE_UNUSED.play(player.getEyeLocation().add(UniblendEventListener.getMotion(player)).clone().add(direction));
            }
        }
        return super.onTick(player, itemStack, tick);
    }
}
