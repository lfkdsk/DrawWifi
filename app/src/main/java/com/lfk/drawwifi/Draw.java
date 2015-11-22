package com.lfk.drawwifi;

import cn.bmob.v3.BmobObject;

/**
 * Created by liufengkai on 15/11/15.
 */
public class Draw extends BmobObject {
    private String controller;
    private String message;
    private boolean open;

    public Draw(String controller, String message, boolean open) {
        this.controller = controller;
        this.message = message;
        this.open = open;
    }

    public String getController() {
        return controller;
    }

    public String getMessage() {
        return message;
    }

    public boolean isOpen() {
        return open;
    }
}
