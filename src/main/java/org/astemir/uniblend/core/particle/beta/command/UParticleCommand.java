package org.astemir.uniblend.core.particle.beta.command;

import org.astemir.uniblend.core.command.UCommand;
import org.astemir.uniblend.core.entity.UEntityHandler;
import org.astemir.uniblend.core.entity.UniblendEntities;
import org.astemir.uniblend.core.entity.parent.UEntity;
import org.astemir.uniblend.core.particle.beta.BetaParticleEmitter;
import org.astemir.uniblend.core.particle.beta.UniblendBetaParticles;
import org.astemir.uniblend.core.particle.beta.UniblendBetaParticlesHandler;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class UParticleCommand extends UCommand {


    public UParticleCommand() {
        super("uparticle");
        CmdArgument ARG_CREATE = CmdArgument.arg("create");
        CmdArgument ARG_REMOVE = CmdArgument.arg("remove");
        CmdArgument ARG_ID = CmdArgument.strArg("particle_id").autoComplete(()->particleNames());
        CmdArgument ARG_RADIUS = CmdArgument.doubleArg("radius").autoComplete(()-> Arrays.asList("5","10","15","20"));
        variant(new CmdPattern(ARG_CREATE,ARG_ID),(sender, values) -> {
            if (sender.isPlayer()) {
                String entityId = values.get(ARG_ID).getValue();
                UniblendBetaParticles.spawnEmitter(entityId).setLocation(sender.getPlayerSender().getLocation());
            }else{
                sender.sendMessage("§cЭту команду могут использовать только игроки.");
            }
            return true;
        },"создать частицы");
        variant(new CmdPattern(ARG_REMOVE,ARG_RADIUS),(sender,values)->{
            if (sender.isPlayer()) {
                double radius = values.get(ARG_RADIUS).getValue();
                Iterator<BetaParticleEmitter> iterator = UniblendBetaParticlesHandler.INSTANCE.getEntries().iterator();
                while (iterator.hasNext()){
                    BetaParticleEmitter emitter = iterator.next();
                    if (emitter.getLocation().distance(sender.getPlayerSender().getLocation()) <= radius){
                        iterator.remove();
                    }
                }
            }else{
                sender.sendMessage("§cЭту команду могут использовать только игроки.");
            }
            return true;
        },"удалить частицы в радиусе");
    }


    public List<String> particleNames(){
        List<String> list = new ArrayList<>();
        for (BetaParticleEmitter entry : UniblendBetaParticles.INSTANCE.getEntries()) {
            list.add(entry.getNameKey());
        }
        return list;
    }

}
