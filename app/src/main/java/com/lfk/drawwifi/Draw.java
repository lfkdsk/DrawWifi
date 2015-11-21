package com.lfk.drawwifi;

import cn.bmob.v3.BmobObject;

/**
 * Created by liufengkai on 15/11/15.
 */
public class Draw extends BmobObject{
    private String controller;
    private String controlled;
    private String message;

    public Draw(String controller, String controlled, String message) {
        this.controller = controller;
        this.controlled = controlled;
        this.message = message;
    }

    public String getController() {
        return controller;
    }

    public String getControlled() {
        return controlled;
    }

    public String getMessage() {
        return message;
    }
}
