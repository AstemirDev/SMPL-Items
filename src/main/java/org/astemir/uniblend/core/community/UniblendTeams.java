package org.astemir.uniblend.core.community;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.astemir.sqlite.DataRow;
import org.astemir.sqlite.DataValues;
import org.astemir.uniblend.core.community.command.CommandUTeam;
import org.astemir.uniblend.misc.Pair;
import org.astemir.uniblend.core.UniblendRegistry;
import org.astemir.uniblend.core.community.event.CommunityEventListener;
import org.astemir.uniblend.utils.PlayerUtils;
import org.astemir.uniblend.utils.TextUtils;
import org.astemir.sqlite.Database;
import org.astemir.sqlite.DatabaseField;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public class UniblendTeams extends UniblendRegistry.Default<UTeam> {

    public static UniblendTeams INSTANCE;
    private Database teamsDB;
    public UniblendTeams() {
        INSTANCE = this;
    }

    @Override
    public void onRegister() {
        registerEvent(new CommunityEventListener());
        registerCommand(new CommandUTeam());
    }

    @Override
    public void onEnable() {
        teamsDB = new Database("teams");
        teamsDB.connect();
        teamsDB.createTable(true,
                DatabaseField.fieldId("id"),
                DatabaseField.fieldString("name",64),
                DatabaseField.fieldInt("color"),
                DatabaseField.fieldString("players"));
        DataValues values = teamsDB.selectAll();
        if (!values.isEmpty()){
            for (DataRow row : values.getRows()) {
                int id = row.getInt("id");
                String name = row.getString("name");
                int color = row.getInt("color");
                List<String> players = TextUtils.split(row.getString("players"));
                UTeam team = new UTeam(name, TextColor.color(color)).id(id).members(players);
                addTeam(team);
                team.updateBukkitTeam();
            }
        }
    }

    @Override
    public void onUpdatePerPlayer(Player player, long tick) {
        updatePlayerTabName(player.getName());
    }

    @Override
    public void onDisable() {
        updateDatabase();
        teamsDB.close();
    }

    public void updateDatabase(){
        getEntries().forEach((team) -> {
            DataValues values = teamsDB.selectAll("name", team.name());
            if (values.isEmpty()) {
                teamsDB.insert(
                        Pair.of("name", team.name()),
                        Pair.of("color", team.color().value()),
                        Pair.of("players", TextUtils.joinList(team.members())));
            } else {
                teamsDB.update("name", team.name(),
                        Pair.of("name", team.name()),
                        Pair.of("color", team.color().value()),
                        Pair.of("players", TextUtils.joinList(team.members())));
            }
        });
    }

    public void addTeam(UTeam team){
        if (!isTeamExists(team.name())) {
            add(team);
        }
    }

    public UTeam getTeam(String name){
        for (UTeam team : getEntries()) {
            if (team.name().equals(name)){
                return team;
            }
        }
        return null;
    }

    public void removeTeam(UTeam team){
        remove(team);
        team.members().forEach((member)->{
            updatePlayerTabName(member);
        });
        team.unregisterBukkitTeam();
    }

    public UTeam getTeamOfPlayer(String name){
        for (UTeam team : getEntries()) {
            if (team.members().contains(name)){
                return team;
            }
        }
        return null;
    }

    public boolean isTeamExists(String name){
        return getTeam(name) != null;
    }

    public boolean hasTeam(Player player){
        return getTeamOfPlayer(player.getName()) != null;
    }

    public boolean hasTeam(String player){
        return getTeamOfPlayer(player) != null;
    }

    public Database getDatabase() {
        return teamsDB;
    }

    public void updatePlayerTabName(String name){
        TextColor nameColor = WHITE;
        if (hasTeam(name)){
            UTeam team = getTeamOfPlayer(name);
            nameColor = team.color();
        }
        TextComponent tabName = Component.text(name).color(nameColor);
        if (UniblendIcons.INSTANCE.hasIcon(name)){
            UPlayerIcon icon = UniblendIcons.INSTANCE.getIcon(name);
            TextComponent prefix = Component.text(ChatColor.translateAlternateColorCodes('&',icon.icon()+"&r &f| "));
            tabName = prefix.append(Component.text(name).color(nameColor));
        }
        PlayerUtils.setPlayerTabName(name,tabName);
    }

}
