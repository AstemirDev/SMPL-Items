package org.astemir.uniblend.core.community.command;

import net.kyori.adventure.text.format.TextColor;
import org.astemir.uniblend.core.command.UCommand;
import org.astemir.uniblend.core.community.UniblendTeams;
import org.astemir.uniblend.core.community.UTeam;
import org.astemir.uniblend.misc.Pair;
import org.astemir.uniblend.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;


public class CommandUTeam extends UCommand {


    public static final String CREATED = "§eКоманда %s успешно создана";
    public static final String REMOVED = "§eКоманда %s была удалена";
    public static final String COLOR_CHANGED = "§eЦвет команды %s успешно изменен на %s";
    public static final String NAME_CHANGED = "§eНазвание команды %s успешно изменено на %s";
    public static final String PLAYER_ADDED = "§eИгрок %s был добавлен в команду %s";
    public static final String PLAYER_NOW_ADDED = "§eИгрок %s теперь в команде %s";
    public static final String PLAYER_ALREADY_IN_TEAM = "§eИгрок %s уже находится в команде %s";
    public static final String PLAYER_REMOVED = "§eИгрок %s был удален из команды %s";
    public static final String PLAYER_IS_NOT_REMOVED = "§eИгрока %s нет в этой команде";
    public static final String TEAM_EXISTS_ALREADY = "§eКоманда %s уже существует";
    public static final String TEAM_IS_NOT_EXISTS = "§eКоманды %s не существует";
    
    public CommandUTeam() {
        super("uteam");
        CmdArgument ARG_CREATE = CmdArgument.arg("create");
        CmdArgument ARG_DELETE = CmdArgument.arg("delete");
        CmdArgument ARG_MEMBERS = CmdArgument.arg("members");
        CmdArgument ARG_COLOR_CHANGE = CmdArgument.arg("color");
        CmdArgument ARG_LIST = CmdArgument.arg("list");
        CmdArgument ARG_JOIN = CmdArgument.arg("join");
        CmdArgument ARG_LEAVE = CmdArgument.arg("leave");
        CmdArgument ARG_TEAM = CmdArgument.strArg("team").autoComplete(()->teams());
        CmdArgument ARG_COLOR = CmdArgument.colorArg("color").errorMessage("§cНеправильно указан цвет.");
        CmdArgument ARG_PLAYER_NAME = CmdArgument.strArg("player").autoComplete(()->CmdArgType.PLAYER.getTypeAutocomplete());
        variant(new CmdPattern(ARG_CREATE,ARG_TEAM,ARG_COLOR),(sender, values) -> {
            String teamName = values.get(ARG_TEAM).getValue();
            if (UniblendTeams.INSTANCE.isTeamExists(teamName)){
                sender.sendMessage(String.format(TEAM_EXISTS_ALREADY,teamName));
            }
            UTeam team = new UTeam(teamName,values.get(ARG_COLOR).getValue());
            UniblendTeams.INSTANCE.addTeam(team);
            UniblendTeams.INSTANCE.updateDatabase();
            team.id(UniblendTeams.INSTANCE.getDatabase().select("name",team.name(),"id").getFirst().getInt("id"));
            sender.sendMessage(String.format(CREATED,teamName));
            team.updateBukkitTeam();
            return true;
        },"создать команду");

        variant(new CmdPattern(ARG_DELETE,ARG_TEAM),(sender,values)->{
            String teamName = values.get(ARG_TEAM).getValue();
            if (!UniblendTeams.INSTANCE.isTeamExists(teamName)){
                sender.sendMessage(String.format(TEAM_IS_NOT_EXISTS,teamName));
                return false;
            }
            UniblendTeams.INSTANCE.removeTeam(UniblendTeams.INSTANCE.getTeam(teamName));
            sender.sendMessage(String.format(REMOVED,teamName));
            UniblendTeams.INSTANCE.getDatabase().delete("name",teamName);
            return true;
        },"удалить команду");


        variant(new CmdPattern(ARG_MEMBERS,ARG_TEAM),(sender,values)->{
            String teamName = values.get(ARG_TEAM).getValue();
            if (!UniblendTeams.INSTANCE.isTeamExists(teamName)){
                sender.sendMessage(String.format(TEAM_IS_NOT_EXISTS,teamName));
                return false;
            }
            sender.sendMessage(UniblendTeams.INSTANCE.getTeam(teamName).members().toString());
            return true;
        },"участники команды");

        variant(new CmdPattern(ARG_LIST),(sender,values)->{
            UniblendTeams.INSTANCE.getEntries().forEach((team)->{
                sender.sendMessage(team.toString());
            });
            return true;
        },"список команд");

        variant(new CmdPattern(ARG_COLOR_CHANGE,ARG_TEAM,ARG_COLOR),(sender,values)->{
            String teamName = values.get(ARG_TEAM).getValue();
            TextColor color = values.get(ARG_COLOR).getValue();
            if (!UniblendTeams.INSTANCE.isTeamExists(teamName)){
                sender.sendMessage(String.format(TEAM_IS_NOT_EXISTS,teamName));
                return false;
            }
            UTeam team = UniblendTeams.INSTANCE.getTeam(teamName);
            team.color(TextColor.color(color));
            sender.sendMessage(COLOR_CHANGED.formatted(teamName,color));
            UniblendTeams.INSTANCE.getDatabase().update("name", team.name(), Pair.of("color", team.color().value()));
            return true;
        },"сменить цвет команды");

        variant(new CmdPattern(ARG_JOIN,ARG_TEAM,ARG_PLAYER_NAME),(sender,values)->{
            String teamName = values.get(ARG_TEAM).getValue();
            String playerName = values.get(ARG_PLAYER_NAME).getValue();
            if (!UniblendTeams.INSTANCE.isTeamExists(teamName)) {
                sender.sendMessage(String.format(TEAM_IS_NOT_EXISTS,teamName));
                return false;
            }
            UTeam team = UniblendTeams.INSTANCE.getTeam(teamName);
            if (!team.hasMember(playerName)) {
                if (!UniblendTeams.INSTANCE.hasTeam(playerName)) {
                    sender.sendMessage(String.format(PLAYER_ADDED,playerName,teamName));
                } else {
                    UTeam oldTeam = UniblendTeams.INSTANCE.getTeamOfPlayer(playerName);
                    oldTeam.removeMember(playerName);
                    UniblendTeams.INSTANCE.getDatabase().update("name", oldTeam.name(),
                            Pair.of("players", TextUtils.joinList(oldTeam.members())));
                    sender.sendMessage(String.format(PLAYER_NOW_ADDED,playerName,teamName));
                }
                team.addMember(playerName);
                UniblendTeams.INSTANCE.getDatabase().update("name", team.name(),
                        Pair.of("players", TextUtils.joinList(team.members())));
            } else {
                sender.sendMessage(String.format(PLAYER_ALREADY_IN_TEAM,playerName,teamName));
            }
            return true;
        },"добавить участника в команду");

        variant(new CmdPattern(ARG_LEAVE,ARG_TEAM,ARG_PLAYER_NAME),(sender,values)->{
            String teamName = values.get(ARG_TEAM).getValue();
            String playerName = values.get(ARG_PLAYER_NAME).getValue();
            if (!UniblendTeams.INSTANCE.isTeamExists(teamName)) {
                sender.sendMessage(String.format(TEAM_IS_NOT_EXISTS,teamName));
                return false;
            }
            UTeam team = UniblendTeams.INSTANCE.getTeam(teamName);
            if (team.hasMember(playerName)) {
                team.removeMember(playerName);
                sender.sendMessage(String.format(PLAYER_REMOVED,playerName,teamName));
                UniblendTeams.INSTANCE.getDatabase().update("name", team.name(),
                        Pair.of("players", TextUtils.joinList(team.members())));
            } else {
                sender.sendMessage(String.format(PLAYER_IS_NOT_REMOVED,playerName));
            }
            return true;
        },"удалить участника из команды");
    }

    private List<String> teams(){
        List<String> list = new ArrayList<>();
        for (UTeam team : UniblendTeams.INSTANCE.getEntries()) {
            list.add(team.name());
        }
        return list;
    }

}
