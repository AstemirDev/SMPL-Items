package org.astemir.uniblend.core.gui.command;


import org.astemir.uniblend.core.command.UCommand;
import org.astemir.uniblend.core.gui.UniblendGuis;
import org.astemir.uniblend.core.gui.UGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class CommandUGui extends UCommand {


    public CommandUGui() {
        super("ugui");
        CmdArgument ARG_SHOW = CmdArgument.arg("show");
        CmdArgument ARG_GUI_NAME = CmdArgument.strArg("gui_name").autoComplete(()->guis());
        CmdArgument ARG_PLAYER = CmdArgument.strArg("player").autoComplete(()->CmdArgType.PLAYER.getTypeAutocomplete());
        variant(new CmdPattern(ARG_SHOW,ARG_PLAYER,ARG_GUI_NAME),(sender, values) -> {
            String guiId = values.get(ARG_GUI_NAME).getValue();
            String playerName = values.get(ARG_PLAYER).getValue();
            UGui gui = UniblendGuis.INSTANCE.matchEntry(guiId).create();
            if (gui != null) {
                Player player = Bukkit.getPlayer(playerName);
                if (player != null) {
                    gui.show(player);
                }
            }
            return true;
        },"высветить меню игроку");
        variant(new CmdPattern(ARG_SHOW,ARG_GUI_NAME),(sender, values) -> {
            if (sender.isPlayer()) {
                String guiId = values.get(ARG_GUI_NAME).getValue();
                UGui gui = UniblendGuis.INSTANCE.matchEntry(guiId).create();
                if (gui != null) {
                    gui.show(sender.getPlayerSender());
                    sender.sendMessage("Открываем меню:",guiId);
                }else{
                    sender.sendMessage("§cТакого меню не существует.");
                }
            }else{
                sender.sendMessage("§cЭту команду могут использовать только игроки.");
            }
            return true;
        },"высветить меню себе");
    }

    public List<String> guis(){
        List<String> list = new ArrayList<>();
        for (UGui entry : UniblendGuis.INSTANCE.getEntries()) {
            list.add(entry.getNameKey());
        }
        return list;
    }
}
