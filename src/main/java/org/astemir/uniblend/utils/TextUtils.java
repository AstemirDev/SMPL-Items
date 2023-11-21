package org.astemir.uniblend.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.astemir.uniblend.UniblendCorePlugin;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {
    public static Color color(TextColor textColor){
        return Color.fromRGB(textColor.red(),textColor.green(),textColor.blue());
    }
    public static Component text(String text, TextColor color){
        return Component.text(text).style(Style.style().decoration(TextDecoration.ITALIC,false).color(color));
    }

    public static Component translate(String text, TextColor color){
        return Component.translatable(text).style(Style.style().decoration(TextDecoration.ITALIC,false).color(color));
    }

    public static String[] namespaceKey(String str){
        String namespace = "minecraft";
        if (namespace.contains(":")){
            String[] split = str.split(":");
            return split;
        }else{
            return new String[]{namespace,str};
        }
    }

    public static List<String> split(String string){
        String[] res = string.split(",");
        List<String> list = new ArrayList<>();
        for (String element : res) {
            if (!element.isEmpty()){
                list.add(element);
            }
        }
        return list;
    }

    public static String joinList(List<String> list){
        StringBuilder res = new StringBuilder();
        if (list.isEmpty()){
            return "";
        }else{
            for (int i = 0; i < list.size(); i++) {
                String element = list.get(i);
                if (!element.isEmpty()) {
                    res = res.append(element);
                    if (i < list.size()-1) {
                        res = res.append(",");
                    }
                }
            }
        }
        return res.toString();
    }
}
