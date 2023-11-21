package org.astemir.uniblend.core.entity.utils;

import org.astemir.uniblend.UniblendCorePlugin;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

public class MetadataUtils {



    public static void setData(Metadatable object,String key,Object value){
        setData(UniblendCorePlugin.getPlugin(),object,key,value);
    }

    public static void setData(Plugin plugin,Metadatable object,String key,Object value){
        object.setMetadata(key,new FixedMetadataValue(plugin,value));
    }

    public static boolean hasData(Metadatable object,String key){
        return object.hasMetadata(key);
    }

    public static MetadataValue getData(Metadatable object, String key){
        return getData(UniblendCorePlugin.getPlugin(),object,key);
    }
    public static MetadataValue getData(Plugin plugin,Metadatable object,String key){
        for (MetadataValue data : object.getMetadata(key)) {
            if (data.getOwningPlugin().equals(plugin)){
                return data;
            }
        }
        return null;
    }

    public static void removeData(Plugin plugin,Metadatable object, String key){
        object.removeMetadata(key,plugin);
    }
}
