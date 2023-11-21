package org.astemir.uniblend.core.community;

import org.astemir.uniblend.core.Named;

public class UPlayerData implements Named {

    private boolean showTime = true;
    private String nameKey;

    public boolean isShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }

    @Override
    public String getNameKey() {
        return nameKey;
    }

    @Override
    public void setNameKey(String key) {
        this.nameKey = key;
    }
}
