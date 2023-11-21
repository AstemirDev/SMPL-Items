package org.astemir.uniblend.core.community;

import net.kyori.adventure.text.format.TextColor;
import org.astemir.uniblend.UniblendCorePlugin;
import org.astemir.uniblend.utils.TextUtils;
import org.bukkit.scoreboard.Team;
import java.util.ArrayList;
import java.util.List;

public class UTeam {
    private String name;
    private TextColor color;
    private List<String> members;
    private Team bukkitTeam;
    private int id = -1;

    public UTeam(String name, TextColor color) {
        this.name = name;
        this.color = color;
        this.members = new ArrayList<>();
    }

    public String name() {
        return name;
    }

    public UTeam name(String name) {
        this.name = name;
        return this;
    }

    public TextColor color() {
        return color;
    }

    public UTeam color(TextColor color) {
        this.color = color;
        return this;
    }

    public List<String> members() {
        return members;
    }

    public UTeam members(List<String> members) {
        this.members = members;
        return this;
    }

    public void updateBukkitTeam(){
        if (bukkitTeam == null) {
            org.bukkit.scoreboard.Team bukkitTeam = UniblendCorePlugin.getPlugin().getScoreboard().getTeam(String.valueOf(id));
            if (bukkitTeam == null){
                bukkitTeam = UniblendCorePlugin.getPlugin().getScoreboard().registerNewTeam(String.valueOf(id));
            }
            this.bukkitTeam = bukkitTeam;
        }
        bukkitTeam.getEntries().forEach((entry)->bukkitTeam.removeEntry(entry));
        members.forEach((member) -> {
            if (bukkitTeam != null) {
                if (!bukkitTeam.hasEntry(member)) {
                    bukkitTeam.addEntry(member);
                }
            }
        });
    }

    public UTeam id(int id) {
        this.id = id;
        return this;
    }

    public void addMember(String player){
        if (!player.isEmpty()) {
            this.members.add(player);
            UniblendTeams.INSTANCE.updatePlayerTabName(player);
            updateBukkitTeam();
        }
    }

    public boolean hasMember(String player){
        return members.contains(player);
    }

    public void removeMember(String player){
        if (members.contains(player)) {
            members.remove(player);
            UniblendTeams.INSTANCE.updatePlayerTabName(player);
            updateBukkitTeam();
        }
    }

    public void unregisterBukkitTeam(){
        if (bukkitTeam != null){
            bukkitTeam.unregister();
        }
    }

    @Override
    public String toString() {
        return  name+", color: " + color + ", members:" + TextUtils.joinList(members);
    }
}