package org.astemir.uniblend;


import org.astemir.lib.ia.ItemsAdderLib;
import org.astemir.lib.jython.JythonLib;
import org.astemir.uniblend.core.*;
import org.astemir.uniblend.core.attribute.UniblendAttributes;
import org.astemir.uniblend.core.command.UBlockedCommandHandler;
import org.astemir.uniblend.core.community.UniblendIcons;
import org.astemir.uniblend.core.community.UPlayerDataHandler;
import org.astemir.uniblend.core.community.UniblendTeams;
import org.astemir.uniblend.core.cooldown.UCooldownHandler;
import org.astemir.uniblend.core.entity.EntityTaskHandler;
import org.astemir.uniblend.core.entity.UEntityHandler;
import org.astemir.uniblend.core.entity.UniblendEntities;
import org.astemir.uniblend.core.fishing.FishingHandler;
import org.astemir.uniblend.core.hud.UHUDHandler;
import org.astemir.uniblend.core.display.URenderHandler;
import org.astemir.uniblend.core.gui.UniblendGuis;
import org.astemir.uniblend.core.gui.UGuiHandler;
import org.astemir.uniblend.core.item.UniblendItems;
import org.astemir.uniblend.core.particle.beta.UniblendBetaParticles;
import org.astemir.uniblend.core.particle.UniblendParticleEffects;
import org.astemir.uniblend.core.particle.beta.UniblendBetaParticlesHandler;
import org.astemir.uniblend.core.projectile.UProjectileHandler;
import org.astemir.uniblend.core.projectile.UniblendProjectiles;
import org.astemir.uniblend.core.recipe.UniblendRecipes;
import org.astemir.uniblend.core.setbonus.UniblendSetBonuses;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;


public final class UniblendCorePlugin extends JavaPlugin {

    public static long GLOBAL_TICKS = 0;
    private static UniblendCorePlugin plugin;
    private UModules modules = new UModules(
            new UHUDHandler(),
            new UniblendParticleEffects(),
            new UniblendSetBonuses(),
            new UniblendAttributes(),
            new UniblendProjectiles(),
            new UniblendItems(),
            new UniblendRecipes(),
            new UniblendGuis(),
            new UniblendEntities(),
            new UBlockedCommandHandler(),
            new UGuiHandler(),
            new UEntityHandler(),
            new UPlayerDataHandler(),
            new UniblendTeams(),
            new UniblendIcons(),
            new EntityTaskHandler(),
            new URenderHandler(),
            new UProjectileHandler(),
            new UCooldownHandler(),
            new UniblendBetaParticles(),
            new UniblendBetaParticlesHandler(),
            new FishingHandler());

    private Scoreboard scoreboard;


    @Override
    public void onEnable() {
        plugin = this;
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        ItemsAdderLib.load();
        modules.register();
        modules.enable();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this,()->{
            GLOBAL_TICKS++;
            modules.update(GLOBAL_TICKS);
            Bukkit.getOnlinePlayers().forEach((player)-> modules.updatePerPlayer(player,GLOBAL_TICKS));
        },0,0);
        Bukkit.getScheduler().runTaskAsynchronously(this,()->JythonLib.initialize());
    }

    @Override
    public void onDisable() {
        try {
            modules.disable();
        }catch (Throwable e){}
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
    public static UniblendCorePlugin getPlugin() {
        return plugin;
    }
    public UModules getModules() {
        return modules;
    }
}
