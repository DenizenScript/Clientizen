package com.denizenscript.clientizen.access;

public interface KeyBindingMixinAccess {

    void disableUntilPress();

    void forceSetPressed(boolean pressed);
}
