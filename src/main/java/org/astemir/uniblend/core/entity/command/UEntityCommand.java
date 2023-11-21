package org.astemir.uniblend.core.entity.command;

import org.astemir.uniblend.core.command.UCommand;
import org.astemir.uniblend.core.entity.UEntityHandler;
import org.astemir.uniblend.core.entity.UniblendEntities;
import org.astemir.uniblend.core.entity.parent.UEntity;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UEntityCommand extends UCommand {


    public UEntityCommand() {
        super("uentity");
        CmdArgument ARG_SPAWN = CmdArgument.arg("spawn");
        CmdArgument ARG_KILL = CmdArgument.arg("kill");
        CmdArgument ARG_RADIUS = CmdArgument.doubleArg("radius").autoComplete(()-> Arrays.asList("5","10","15","20"));
        CmdArgument ARG_ENTITY_ID = CmdArgument.strArg("entity_id").autoComplete(()->entityNames());
        variant(new CmdPattern(ARG_SPAWN,ARG_ENTITY_ID),(sender, values) -> {
            if (sender.isPlayer()) {
                String entityId = values.get(ARG_ENTITY_ID).getValue();
                UniblendEntities.spawn(entityId,sender.getPlayerSender().getLocation());
            }else{
                sender.sendMessage("§cЭту команду могут использовать только игроки.");
            }
            return true;
        },"заспавнить моба");

        variant(new CmdPattern(ARG_KILL,ARG_RADIUS),(sender,values)->{
            if (sender.isPlayer()) {
                double radius = values.get(ARG_RADIUS).getValue();
                for (UEntity entity : UEntityHandler.INSTANCE.getEntries()) {
                    if (entity.isValid()){
                        Location loc = entity.getLocation();
                        if (loc.distance(sender.getPlayerSender().getLocation()) <= radius){
                            entity.remove();
                        }
                    }
                }
            }else{
                sender.sendMessage("§cЭту команду могут использовать только игроки.");
            }
            return true;
        },"удалить мобов в радиусе");
    }


    public List<String> entityNames(){
        List<String> list = new ArrayList<>();
        for (UEntity entry : UniblendEntities.INSTANCE.getEntries()) {
            list.add(entry.getNameKey());
        }
        return list;
    }

}
