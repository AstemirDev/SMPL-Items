package org.astemir.uniblend.core.community;

public class UPlayerIcon {
    private String icon = "";
    private String player = "";

    public UPlayerIcon(String icon, String player) {
        this.icon = icon;
        this.player = player;
    }


    public String icon() {
        return icon;
    }

    public UPlayerIcon icon(String icon) {
        this.icon = icon;
        return this;
    }

    public String player() {
        return player;
    }

    public UPlayerIcon player(String player) {
        this.player = player;
        return this;
    }
}