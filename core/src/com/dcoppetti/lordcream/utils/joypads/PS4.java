package com.dcoppetti.lordcream.utils.joypads;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;

// Mapping class for ps4 controller buttons
public class PS4 {
    public static final int BUTTON_CROSS = 1;
    public static final int BUTTON_SQUARE = 0;
    public static final int BUTTON_TRIANGLE = 3;
    public static final int BUTTON_CIRCLE = 2;
    public static final int BUTTON_OPTIONS = 9;
    public static final int BUTTON_SHARE = 8;
    public static final int BUTTON_R1 = 5;
    public static final int BUTTON_R2 = 7;
    public static final int BUTTON_R3 = 11;
    public static final int BUTTON_L1 = 4;
    public static final int BUTTON_L2 = 6;
    public static final int BUTTON_L3 = 10;
    public static final int BUTTON_MOUSE = 13;
    public static final int BUTTON_PS = 12;
    public static final int LEFT_STICK_X = 1;

    public static final PovDirection BUTTON_DPAD_UP = PovDirection.north;
    public static final PovDirection BUTTON_DPAD_DOWN = PovDirection.south;
    public static final PovDirection BUTTON_DPAD_RIGHT = PovDirection.east;
    public static final PovDirection BUTTON_DPAD_LEFT = PovDirection.west;

    public static boolean isPs4Controller(Controller controller) {
        return controller.getName().toLowerCase().contains("sony");
    }
}
