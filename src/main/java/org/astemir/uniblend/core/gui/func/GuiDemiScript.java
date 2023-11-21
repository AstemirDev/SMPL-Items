package org.astemir.uniblend.core.gui.func;

import org.astemir.uniblend.core.gui.UGui;
import org.astemir.uniblend.misc.TextScanner;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GuiDemiScript {

    private GuiFunction function;
    private GuiFunction.Executor executor;
    private Object[] args;
    public GuiDemiScript(GuiFunction function, GuiFunction.Executor executor, Object[] args) {
        this.function = function;
        this.executor = executor;
        this.args = args;
    }


    public void run(UGui gui, Player player){
        function.onExecute(gui,player,executor,args);
    }

    public static GuiDemiScript fromString(String cmdString){
        TextScanner scanner = new TextScanner(cmdString);
        GuiFunction function = GuiFunction.byName(scanner.readUntilSpace());
        scanner.consumeIf(' ');
        GuiFunction.Executor executor = GuiFunction.Executor.byName(scanner.readUntilSpace());
        scanner.consumeIf(' ');
        List<Object> args = new ArrayList<>();
        while(scanner.hasNext()){
            if (Character.isDigit(scanner.currentChar())){
                args.add(scanner.parseNumber());
            }else
            if (scanner.currentChar() == '"'){
                args.add(scanner.parseString());
            }else
            if (scanner.isAt("true") || scanner.isAt("false")){
                args.add(scanner.parseBoolean());
            }else {
                args.add(scanner.readUntilSpace());
                scanner.consumeIf(' ');
            }
        }
        return new GuiDemiScript(function, executor,args.toArray(new Object[args.size()]));
    }

}
