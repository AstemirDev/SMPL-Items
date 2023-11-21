package org.astemir.uniblend.core.community;


import org.astemir.sqlite.*;
import org.astemir.uniblend.core.community.command.CommandUIcon;
import org.astemir.uniblend.misc.Pair;
import org.astemir.uniblend.core.UniblendRegistry;

public class UniblendIcons extends UniblendRegistry.Default<UPlayerIcon> {

    public static UniblendIcons INSTANCE;
    private Database iconsDB;
    public UniblendIcons() {
        INSTANCE = this;
    }
    @Override
    public void onRegister() {
        registerCommand(new CommandUIcon());
    }

    @Override
    public void onEnable() {
        iconsDB = new Database("icons");
        iconsDB.connect();
        iconsDB.createTable(true,
                DatabaseField.fieldId("id"),
                DatabaseField.fieldString("name",64),
                DatabaseField.fieldString("icon"));
        DataValues values = iconsDB.selectAll();
        if (!values.isEmpty()){
            for (DataRow row : values.getRows()) {
                String name = row.getString("name");
                String iconText = row.getString("icon");
                setIconWithoutUpdate(name,iconText);
            }
        }
    }

    @Override
    public void onDisable() {
        updateDatabase();
        iconsDB.close();
    }

    public void updateDatabase(){
        getEntries().forEach((icon) -> {
            DataValues values = iconsDB.selectAll("name", icon.player());
            if (values.isEmpty()) {
                iconsDB.insert(
                        Pair.of("name", icon.player()),
                        Pair.of("icon", icon.icon())
                );
            }else{
                iconsDB.update("name", icon.player(),
                        Pair.of("name", icon.player()),
                        Pair.of("icon", icon.icon())
                );
            }
        });
    }

    public void setIcon(String player,String icon){
        if (!hasIcon(player)){
            add(new UPlayerIcon(icon,player));
        }else{
            getIcon(player).icon(icon);
            iconsDB.update("name", player,
                    Pair.of("icon", icon));
        }
        UniblendTeams.INSTANCE.updatePlayerTabName(player);
    }

    public void setIconWithoutUpdate(String player,String icon){
        if (!hasIcon(player)){
            add(new UPlayerIcon(icon,player));
        }else{
            getIcon(player).icon(icon);
        }
    }

    public UPlayerIcon getIcon(String player){
        for (UPlayerIcon icon : getEntries()){
            if (icon.player().equals(player)){
                return icon;
            }
        }
        return null;
    }

    public void removeIcon(String player){
        remove(getIcon(player));
        UniblendTeams.INSTANCE.updatePlayerTabName(player);
    }

    public boolean hasIcon(String player){
        return getIcon(player) != null;
    }
}
