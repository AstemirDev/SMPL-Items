package org.astemir.uniblend.core.community.command;

import org.astemir.uniblend.core.command.UCommand;
import org.astemir.uniblend.core.community.UniblendIcons;
import org.astemir.uniblend.utils.PlayerUtils;
import org.bukkit.ChatColor;

public class CommandUIcon extends UCommand {

    public CommandUIcon() {
        super("uicon");
        CmdArgument ARG_SET = CmdArgument.arg("set");
        CmdArgument ARG_DELETE = CmdArgument.arg("delete");
        CmdArgument ARG_PLAYER_NAME = CmdArgument.strArg("player").autoComplete(()->CmdArgType.PLAYER.getTypeAutocomplete());
        CmdArgument ARG_TEXT = CmdArgument.strArg("icon_text");
        variant(new CmdPattern(ARG_SET,ARG_PLAYER_NAME,ARG_TEXT),(sender,values)->{
            String player = values.get(ARG_PLAYER_NAME).getValue();
            String text = values.get(ARG_TEXT).getValue();
            String original = textWithoutColor('&', text);
            if (original.length() <= 5) {
                UniblendIcons.INSTANCE.setIcon(player, text);
                PlayerUtils.sendMessage(player,String.format("§aВам установили префикс %s§a.", ChatColor.translateAlternateColorCodes('&', text)));
                sender.sendMessage(String.format("§aВы установили префикс %s§a игроку %s.", ChatColor.translateAlternateColorCodes('&', text),player));
            } else {
                sender.sendMessage("§cСлишком длинный текст.");
            }
            return true;
        },"установить иконку игроку");

        variant(new CmdPattern(ARG_DELETE,ARG_PLAYER_NAME),(sender,values)->{
            String player = values.get(ARG_PLAYER_NAME).getValue();
            if (UniblendIcons.INSTANCE.hasIcon(player)) {
                PlayerUtils.sendMessage(player,"§eВам удалили префикс.");
                sender.sendMessage(String.format("§aПрефикс удален у игрока %s.",player));
                UniblendIcons.INSTANCE.removeIcon(player);
            } else {
                sender.sendMessage("§cУ вас не стоит префикса.");
            }
            return true;
        },"удалить иконку у игрока");
    }

    public static String textWithoutColor(char altColorChar, String text) {
        StringBuilder builder = new StringBuilder();
        char[] b = text.toCharArray();
        for (int i = 0; i < b.length; i++) {
            if (b[i] == altColorChar && i < b.length-1) {
                i+=1;
            }else{
                builder.append(b[i]);
            }
        }
        return builder.toString();
    }

}
