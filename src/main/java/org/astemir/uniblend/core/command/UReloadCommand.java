package org.astemir.uniblend.core.command;

import org.astemir.uniblend.core.UModules;

public class UReloadCommand extends UCommand {


    public UReloadCommand() {
        super("ureload");
        variant(new CmdPattern(),(sender,values)->{
            try {
                UModules.getInstance().unloadAllModules();
                UModules.getInstance().enableAllModules(false);
                sender.sendMessage("§aКонфигурации перезагружены.");
            }catch (Throwable throwable){
                sender.sendMessage("§cОшибка в конфигурации.");
            }
            return true;
        },"перезагрузить конфиги");
    }

}
