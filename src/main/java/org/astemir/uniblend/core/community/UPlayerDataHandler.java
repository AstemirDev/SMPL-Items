package org.astemir.uniblend.core.community;


import org.astemir.sqlite.DataRow;
import org.astemir.sqlite.DataValues;
import org.astemir.sqlite.Database;
import org.astemir.sqlite.DatabaseField;
import org.astemir.uniblend.core.UniblendRegistry;
import org.astemir.uniblend.core.community.command.CommandUClock;
import org.astemir.uniblend.misc.Pair;
import org.bukkit.entity.Player;

public class UPlayerDataHandler extends UniblendRegistry.Concurrent<UPlayerData> {

    public static UPlayerDataHandler INSTANCE;
    private Database database;
    public UPlayerDataHandler() {
        INSTANCE = this;
    }

    @Override
    public void onRegister() {
        registerCommand(new CommandUClock());
    }

    @Override
    public void onEnable() {
        database = new Database("playerdata");
        database.connect();
        database.createTable(true,
                DatabaseField.fieldId("id"),
                DatabaseField.fieldString("name",64),
                DatabaseField.fieldBoolean("showclock"));
        DataValues values = database.selectAll();
        if (!values.isEmpty()){
            for (DataRow row : values.getRows()) {
                UPlayerData playerData = new UPlayerData();
                playerData.setShowTime(row.getBoolean("showclock"));
                register(row.getString("name"),playerData);
            }
        }
    }

    @Override
    public void onDisable() {
        updateDatabase();
        database.close();
    }


    public UPlayerData getOrCreateData(Player player){
        if (containsEntry(player.getName())) {
            return getEntry(player.getName());
        }else{
            return register(player.getName(),new UPlayerData());
        }
    }

    public void updateDatabase(){
        getEntries().forEach((data) -> {
            DataValues values = database.selectAll("name", data.getNameKey());
            if (values.isEmpty()) {
                database.insert(
                        Pair.of("name", data.getNameKey()),
                        Pair.of("showclock", data.isShowTime())
                );
            }else{
                database.update("name", data.getNameKey(),
                        Pair.of("name", data.getNameKey()),
                        Pair.of("showclock", data.isShowTime())
                );
            }
        });
    }

}
