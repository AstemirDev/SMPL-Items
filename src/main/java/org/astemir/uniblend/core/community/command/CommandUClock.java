package org.astemir.uniblend.core.community.command;

import org.astemir.uniblend.core.command.UCommand;
import org.astemir.uniblend.core.community.UPlayerDataHandler;
import org.astemir.uniblend.core.community.UPlayerData;
import org.astemir.uniblend.core.hud.UHUDHandler;
import org.bukkit.entity.Player;

public class CommandUClock extends UCommand {
    public CommandUClock() {
        super("uclock");
        CmdArgument ARG_STATE = CmdArgument.booleanArg("state");
        variant(new CmdPattern(ARG_STATE),(sender,values)->{
            if (sender.isPlayer()){
                Player player = sender.getPlayerSender();
                UPlayerData data = UPlayerDataHandler.INSTANCE.getOrCreateData(player);
                data.setShowTime(values.get(ARG_STATE).getValue());
                if (!data.isShowTime()){
                    UHUDHandler.INSTANCE.removePlayer(player);
                }
                UPlayerDataHandler.INSTANCE.updateDatabase();
            }
            return true;
        },"вкл/выкл часы");

    }

}
