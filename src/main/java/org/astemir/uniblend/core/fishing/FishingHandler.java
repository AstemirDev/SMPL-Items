package org.astemir.uniblend.core.fishing;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import org.astemir.uniblend.event.UniblendEventListener;
import org.astemir.uniblend.io.StringMap;
import org.astemir.uniblend.io.json.PluginJsonConfig;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.core.UniblendModule;
import org.astemir.uniblend.core.Registered;
import org.astemir.uniblend.io.TextComponentMap;
import org.astemir.uniblend.misc.SoundInstance;
import org.astemir.uniblend.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Registered("fishing")
public class FishingHandler extends UniblendModule implements Listener {
    private ConcurrentHashMap<UUID,FishingGame> games = new ConcurrentHashMap<>();
    private Map<Material, Double> vanillaDrops = new HashMap<>();
    private List<FishingDrop> customDrop = new ArrayList<>();
    private TextComponentMap messages = new TextComponentMap();
    private StringMap unicode = new StringMap();
    private SoundInstance successSound = new SoundInstance(Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,2);
    private SoundInstance failedSound = new SoundInstance(Sound.BLOCK_NOTE_BLOCK_PLING,1,0.5f);

    public static FishingHandler INSTANCE;
    public FishingHandler() {
        INSTANCE = this;
    }

    @Override
    public void onRegister() {
        registerEvent(this);
    }

    @Override
    public void onConfigLoad(List<PluginJsonConfig> configs) {
        vanillaDrops.clear();
        customDrop.clear();
        for (PluginJsonConfig config : configs) {
            JsonObject mapJson = config.json();
            unicode = config.getAs("unicode", StringMap.class);
            messages = config.getAs("messages", TextComponentMap.class);
            successSound = config.getAs("success-sound",SoundInstance.class);
            failedSound = config.getAs("failed-sound",SoundInstance.class);
            JsonObject lootsJson = mapJson.get("loots").getAsJsonObject();
            for (String key : lootsJson.keySet()) {
                JsonElement json = lootsJson.get(key);
                if (json.isJsonObject()){
                    customDrop.add(USerialization.as(json,FishingDrop.class));
                }else
                if (json.isJsonPrimitive()){
                    vanillaDrops.put(Material.matchMaterial(key),json.getAsDouble());
                }
            }
        }
    }

    @Override
    public void onUpdatePerPlayer(Player player, long tick) {
        if (player != null) {
            UUID uuid = player.getUniqueId();
            if (games.containsKey(uuid)) {
                FishingGame game = games.get(player.getUniqueId());
                if (!player.isDead() && !game.getHook().isDead()) {
                    game.render(player, tick);
                } else {
                    games.remove(uuid);
                    player.sendActionBar(Component.text());
                }
            }
        }
    }
    @EventHandler
    public void onFishing(PlayerFishEvent e){
        Player player = e.getPlayer();
        FishHook hook = e.getHook();
        PlayerFishEvent.State state = e.getState();
        Entity caughtEntity = e.getCaught();
        if (isPlayingFishGame(player)) {
            if (state == PlayerFishEvent.State.REEL_IN) {
                FishingGame game = getFishGame(e.getPlayer());
                game.pool(e.getPlayer());
            }
            e.setCancelled(true);
            return;
        }
        if (caughtEntity != null) {
            if (caughtEntity instanceof Item) {
                Item item = (Item) caughtEntity;
                if (!isPlayingFishGame(player)) {
                    ItemStack fishItemStack = item.getItemStack();
                    double power = 8;
                    boolean custom = false;
                    if (!customDrop.isEmpty()) {
                        for (FishingDrop fishingDrop : customDrop) {
                            if (RandomUtils.doWithChance(fishingDrop.getChance()) && fishingDrop.test(player,hook)){
                                fishItemStack = fishingDrop.getItem().getItemStack().get();
                                power = fishingDrop.getFishPower();
                                custom = true;
                                break;
                            }
                        }
                    }
                    if (!custom) {
                        if (vanillaDrops.containsKey(fishItemStack.getType())) {
                            power = vanillaDrops.get(fishItemStack.getType());
                        }
                    }
                    FishingGame game = new FishingGame(e.getPlayer().getLocation(),hook,fishItemStack, e.getExpToDrop(), RandomUtils.randomInt(8,14),power);
                    FishingHandler.INSTANCE.playFishGame(e.getPlayer(), game);
                    e.getPlayer().sendMessage(messages.get("fishing-hint"));
                }
                e.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        if (games.containsKey(e.getPlayer().getUniqueId())){
            games.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (isPlayingFishGame(e.getPlayer())) {
            FishingGame game = getFishGame(e.getPlayer());
            Location playerLocation = e.getTo();
            Location gameLocation = game.getLocation();
            if (Math.abs(playerLocation.getBlockX()-gameLocation.getBlockX()) > 4 || Math.abs(playerLocation.getBlockZ()-gameLocation.getBlockZ()) > 4) {
                e.setTo(e.getFrom());
                e.getPlayer().setVelocity(UniblendEventListener.getMotion(e.getPlayer()).multiply(-1));
            }
        }
    }

    public FishingGame getFishGame(Player player){
        return games.get(player.getUniqueId());
    }

    public void playFishGame(Player player, FishingGame game){
        this.games.put(player.getUniqueId(),game);
    }

    public void stopPlayingFishGame(Player player){
        FishingGame game = getFishGame(player);
        if (game != null){
            if (game.getHook() != null) {
                game.getHook().remove();
            }
            this.games.remove(player.getUniqueId());
            player.sendActionBar(Component.text());
        }
    }

    public SoundInstance getSuccessSound() {
        return successSound;
    }

    public SoundInstance getFailedSound() {
        return failedSound;
    }

    public boolean isPlayingFishGame(Player player){
        return games.containsKey(player.getUniqueId());
    }

    public TextComponentMap getMessages() {
        return messages;
    }

    public StringMap getUnicode() {
        return unicode;
    }
}
