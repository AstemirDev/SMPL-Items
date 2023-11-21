package org.astemir.uniblend.core.command;

import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.astemir.uniblend.io.json.USerialization;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class UCommand implements CommandExecutor,TabCompleter {

    private String command;
    private LinkedHashMap<CmdPattern,CmdRun> patterns = new LinkedHashMap<>();

    public UCommand(String command) {
        this.command = command;
    }

    public UCommand variant(CmdPattern pattern, CmdRun run){
        patterns.put(pattern,run);
        return this;
    }


    public UCommand variant(CmdPattern pattern, CmdRun run, String description){
        pattern.setDescription(description);
        patterns.put(pattern,run);
        return this;
    }


    public void register(){
        PluginCommand pluginCommand = Bukkit.getPluginCommand(command);
        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        CmdSender cmdSender = new CmdSender(sender);
        CmdPattern pattern = null;
        CmdRun run = null;
        Map<CmdArgument,CmdArgument.Value> values = new HashMap<>();
        for (CmdPattern cmdPattern : patterns.keySet()) {
            if (cmdPattern.check(args)){
                pattern = cmdPattern;
                run = patterns.get(cmdPattern);
                break;
            }
        }
        if (run != null && pattern != null) {
            for (Map.Entry<Integer,CmdArgument> argumentEntry : pattern.arguments.entrySet()) {
                CmdArgument argument = argumentEntry.getValue();
                CmdArgument.Value value = argument.parseValue(argumentEntry.getKey(),args);
                if (argument.getParseErrorMessage() != null) {
                    if (value.getValue() != null) {
                        values.put(argument, value);
                    } else {
                        sender.sendMessage(argument.getParseErrorMessage());
                        return false;
                    }
                }else{
                    values.put(argument, value);
                }
            }
            return run.onRun(cmdSender,values);
        }else{
            for (CmdPattern cmdPattern : patterns.keySet()) {
                StringBuilder builder = new StringBuilder();
                builder.append("/");
                builder.append(this.command);
                for (CmdArgument argument : cmdPattern.arguments.values()) {
                    builder.append(" ");
                    if (argument.type != CmdArgType.PATTERN) {
                        builder.append("[");
                    }
                    builder.append(argument.name);
                    if (argument.type != CmdArgType.PATTERN) {
                        builder.append("]");
                    }
                }
                if (cmdPattern.getDescription() != null){
                    builder.append(" - ");
                    builder.append(cmdPattern.getDescription());
                }
                sender.sendMessage(builder.toString());
            }
            return false;
        }
    }

    public List<CmdArgument> getArguments(String[] args) {
        List<CmdArgument> result = new ArrayList<>();
        for (CmdPattern cmdPattern : patterns.keySet()) {
            if (cmdPattern.checkPrevious(args)) {
                if (cmdPattern.arguments.containsKey(args.length - 1)) {
                    result.add(cmdPattern.arguments.get(args.length - 1));
                }
            }
        }
        return result;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<CmdArgument> possibleArgs = getArguments(args);
        List<String> result = new ArrayList<>();
        for (CmdArgument possibleArg : possibleArgs) {
            if (possibleArg.type == CmdArgType.PATTERN){
                result.add(possibleArg.name);
            }else{
                result.addAll(possibleArg.type.getTypeAutocomplete());
                if (possibleArg.getAutoComplete() != null) {
                    result.addAll(possibleArg.getAutoComplete().getSuggestionList());
                }
            }
        }
        return result;
    }

    public interface CmdRun{
        boolean onRun(CmdSender sender,Map<CmdArgument,CmdArgument.Value> values);
    }

    public class CmdPattern {

        private Map<Integer,CmdArgument> arguments = new HashMap<>();

        private String description;

        public CmdPattern(CmdArgument... args) {
            for (int i = 0; i < args.length; i++) {
                arguments.put(i,args[i]);
            }
        }

        public boolean check(String[] args){
            int patternSize = arguments.size();
            if (args.length >= patternSize) {
                for (int i = 0; i < arguments.size(); i++) {
                    CmdArgument argument = arguments.get(i);
                    if (!argument.isArgument(args[i])){
                        return false;
                    }
                }
            }
            return args.length >= patternSize;
        }

        public boolean checkPrevious(String[] args){
            int prev = args.length-1;
            if (prev < 0){
                return true;
            }else{
                if (arguments.size() >= prev) {
                    for (int i = 0; i < prev; i++) {
                        CmdArgument argument = arguments.get(i);
                        if (!argument.isArgument(args[i])) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return "CmdPattern{" +
                    "arguments=" + arguments +
                    '}';
        }
    }

    public static class CmdArgument{

        private CmdArgType type;
        private String name;

        private Autocomplete autoComplete = () -> Arrays.asList();

        private String parseErrorMessage;

        public CmdArgument(String name,CmdArgType type) {
            this.name = name;
            this.type = type;
        }

        public CmdArgument(String name) {
            this.name = name;
            this.type = CmdArgType.PATTERN;
        }


        public boolean isArgument(String argument){
            if (type == CmdArgType.PATTERN) {
                return getName().equalsIgnoreCase(argument);
            }
            return true;
        }

        public String getParseErrorMessage() {
            return parseErrorMessage;
        }

        public CmdArgument errorMessage(String parseErrorMessage) {
            this.parseErrorMessage = parseErrorMessage;
            return this;
        }

        public CmdArgument autoComplete(Autocomplete autoComplete) {
            this.autoComplete = autoComplete;
            return this;
        }

        public Autocomplete getAutoComplete() {
            return autoComplete;
        }

        public String getName() {
            return name;
        }

        public Value parseValue(int index,String[] args){
            return new Value(type.parse(args,index));
        }

        public class Value{
            private Object value;

            public Value(Object value) {
                this.value = value;
            }

            public <T> T getValue(){
                return (T)value;
            }
        }

        public interface Autocomplete{
            List<String> getSuggestionList();
        }

        public static CmdArgument arg(String name){
            return new CmdArgument(name,CmdArgType.PATTERN);
        }

        public static CmdArgument intArg(String name){
            return new CmdArgument(name,CmdArgType.INT);
        }

        public static CmdArgument colorArg(String name){
            return new CmdArgument(name,CmdArgType.COLOR);
        }

        public static CmdArgument player(String name){
            return new CmdArgument(name,CmdArgType.PLAYER);
        }

        public static CmdArgument strArg(String name){
            return new CmdArgument(name,CmdArgType.STRING);
        }

        public static CmdArgument doubleArg(String name){
            return new CmdArgument(name,CmdArgType.DOUBLE);
        }

        public static CmdArgument floatArg(String name){
            return new CmdArgument(name,CmdArgType.FLOAT);
        }

        public static CmdArgument booleanArg(String name){
            return new CmdArgument(name,CmdArgType.BOOLEAN);
        }

        public static CmdArgument strArrayArg(String name){
            return new CmdArgument(name,CmdArgType.STRING_ARRAY);
        }

        @Override
        public String toString() {
            return "CmdArgument{" +
                    "type=" + type +
                    ", name='" + name + '\'' +
                    '}';
        }
    }


    public enum CmdArgType{

        PATTERN,
        INT,
        DOUBLE,
        FLOAT,
        STRING,
        BOOLEAN,
        COLOR,
        PLAYER,
        STRING_ARRAY,
        INT_ARRAY,
        DOUBLE_ARRAY,
        FLOAT_ARRAY,
        BOOLEAN_ARRAY;


        public Object parse(String[] arguments,int index){
            switch (this) {
                case INT -> {
                    try {
                        return Integer.parseInt(arguments[index]);
                    } catch (Exception e) {
                        return null;
                    }
                }
                case DOUBLE -> {
                    try {
                        return Double.parseDouble(arguments[index]);
                    } catch (Exception e) {
                        return null;
                    }
                }
                case FLOAT -> {
                    try {
                        return Float.parseFloat(arguments[index]);
                    } catch (Exception e) {
                        return null;
                    }
                }
                case BOOLEAN -> {
                    try {
                        return Boolean.parseBoolean(arguments[index]);
                    } catch (Exception e) {
                        return null;
                    }
                }
                case COLOR -> {
                    String colorStr = arguments[index];
                    if (colorStr.startsWith("#")){
                        colorStr = "\""+colorStr+"\"";
                    }
                    JsonElement colorElement = USerialization.deserialize(colorStr,JsonElement.class);
                    return USerialization.deserialize(colorElement,TextColor.class);
                }
                case PLAYER -> {
                    if (arguments[index] != null) {
                        return Bukkit.getPlayer(arguments[index]);
                    }
                    return null;
                }
                case STRING, PATTERN -> {
                    return arguments[index];
                }
                case STRING_ARRAY -> {
                    try {
                        String[] array = new String[arguments.length - index];
                        for (int i = index; i < arguments.length; i++) {
                            array[i] = arguments[i];
                        }
                        return array;
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
            return null;
        }

        public List<String> getTypeAutocomplete(){
            List<String> result = new ArrayList<>();
            switch (this){
                case BOOLEAN -> {
                    return Arrays.asList("true","false");
                }
                case PLAYER -> {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        result.add(onlinePlayer.getName());
                    }
                    return result;
                }
                case COLOR -> {
                    for (String key : NamedTextColor.NAMES.keys()) {
                        result.add(key.toLowerCase());
                    }
                    return result;
                }
            }
            return result;
        }
    }


    public class CmdSender{

        private CommandSender sender;

        public CmdSender(CommandSender sender) {
            this.sender = sender;
        }

        public ConsoleCommandSender getConsoleSender(){
            return (ConsoleCommandSender) sender;
        }

        public Player getPlayerSender(){
            return (Player) sender;
        }

        public boolean isPlayer(){
            return sender instanceof Player;
        }

        public boolean isConsole(){
            return sender instanceof ConsoleCommandSender;
        }

        public void sendMessage(String message){
            sender.sendMessage(message);
        }

        public void sendMessage(String... args){
            sender.sendMessage(String.join(" " ,args));
        }

        public void sendMessage(Component component){
            sender.sendMessage(component);
        }
    }
}
