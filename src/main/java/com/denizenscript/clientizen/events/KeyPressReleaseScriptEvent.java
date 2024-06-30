package com.denizenscript.clientizen.events;

import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyPressReleaseScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // <'input_device'> key pressed|released|toggled
    //
    // @Group input
    //
    // @Switch name:<key> to only process the event if the pressed key's name matches the specified matcher.
    //
    // @Triggers when a keyboard/mouse key is pressed.
    //
    // @Context
    // <context.key> returns an ElementTag of the pressed key's name.
    // <context.device> returns an ElementTag of the device used, will be either KEYBOARD or MOUSE.
    // <context.key_code> returns an ElementTag(Number) of the pressed key's raw key code.
    //
    // @Example
    // # Will send the server an event when the 'K' key is pressed
    // on keyboard key pressed name:k:
    // - serverevent id:activate_ability
    //
    // @Example
    // # Will listen to all presses on the keypad
    // on keyboard key pressed name:KEYPAD_*:
    // - narrate "You pressed the <context.key> key on your keypad!"
    // -->

    public static KeyPressReleaseScriptEvent instance;

    public Key key;
    public boolean pressed;
    public InputDevice device;

    public KeyPressReleaseScriptEvent() {
        registerCouldMatcher("<'input_device'> key pressed|released|toggled");
        registerSwitches("name");
        instance = this;
    }

    @Override
    public boolean matches(ScriptPath path) {
        if (!runGenericSwitchCheck(path, "name", key.getName())) {
            return false;
        }
        if (!runGenericCheck(path.eventArgLowerAt(0), device.name())) {
            return false;
        }
        String operation = path.eventArgLowerAt(2);
        if (operation.equals("pressed") && !pressed) {
            return false;
        }
        if (operation.equals("released") && pressed) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "key" -> new ElementTag(key.getName(), true);
            case "device" -> new ElementTag(device);
            case "key_code" -> new ElementTag(key.code);
            default -> super.getContext(name);
        };
    }

    public void handleKeyPressStateChange(InputUtil.Key key, boolean pressed) {
        this.key = Key.keysByCode.get(key.getCode());
        this.pressed = pressed;
        this.device = key.getCategory() == InputUtil.Type.KEYSYM ? InputDevice.KEYBOARD : InputDevice.MOUSE;
        fire();
    }

    public enum InputDevice { KEYBOARD, MOUSE }

    public enum Key {
        UNKNOWN(GLFW.GLFW_KEY_UNKNOWN),
        // Keyboard keys
        SPACE(GLFW.GLFW_KEY_SPACE),
        APOSTROPHE(GLFW.GLFW_KEY_APOSTROPHE),
        COMMA(GLFW.GLFW_KEY_COMMA),
        MINUS(GLFW.GLFW_KEY_MINUS),
        PERIOD(GLFW.GLFW_KEY_PERIOD),
        SLASH(GLFW.GLFW_KEY_SLASH),
        KEY_0(GLFW.GLFW_KEY_0, "0"),
        KEY_1(GLFW.GLFW_KEY_1, "1"),
        KEY_2(GLFW.GLFW_KEY_2, "2"),
        KEY_3(GLFW.GLFW_KEY_3, "3"),
        KEY_4(GLFW.GLFW_KEY_4, "4"),
        KEY_5(GLFW.GLFW_KEY_5, "5"),
        KEY_6(GLFW.GLFW_KEY_6, "6"),
        KEY_7(GLFW.GLFW_KEY_7, "7"),
        KEY_8(GLFW.GLFW_KEY_8, "8"),
        KEY_9(GLFW.GLFW_KEY_9, "9"),
        SEMICOLON(GLFW.GLFW_KEY_SEMICOLON),
        EQUAL(GLFW.GLFW_KEY_EQUAL),
        A(GLFW.GLFW_KEY_A),
        B(GLFW.GLFW_KEY_B),
        C(GLFW.GLFW_KEY_C),
        D(GLFW.GLFW_KEY_D),
        E(GLFW.GLFW_KEY_E),
        F(GLFW.GLFW_KEY_F),
        G(GLFW.GLFW_KEY_G),
        H(GLFW.GLFW_KEY_H),
        I(GLFW.GLFW_KEY_I),
        J(GLFW.GLFW_KEY_J),
        K(GLFW.GLFW_KEY_K),
        L(GLFW.GLFW_KEY_L),
        M(GLFW.GLFW_KEY_M),
        N(GLFW.GLFW_KEY_N),
        O(GLFW.GLFW_KEY_O),
        P(GLFW.GLFW_KEY_P),
        Q(GLFW.GLFW_KEY_Q),
        R(GLFW.GLFW_KEY_R),
        S(GLFW.GLFW_KEY_S),
        T(GLFW.GLFW_KEY_T),
        U(GLFW.GLFW_KEY_U),
        V(GLFW.GLFW_KEY_V),
        W(GLFW.GLFW_KEY_W),
        X(GLFW.GLFW_KEY_X),
        Y(GLFW.GLFW_KEY_Y),
        Z(GLFW.GLFW_KEY_Z),
        LEFT_BRACKET(GLFW.GLFW_KEY_LEFT_BRACKET),
        BACKSLASH(GLFW.GLFW_KEY_BACKSLASH),
        RIGHT_BRACKET(GLFW.GLFW_KEY_RIGHT_BRACKET),
        GRAVE_ACCENT(GLFW.GLFW_KEY_GRAVE_ACCENT),
        WORLD_1(GLFW.GLFW_KEY_WORLD_1),
        WORLD_2(GLFW.GLFW_KEY_WORLD_2),
        ESCAPE(GLFW.GLFW_KEY_ESCAPE),
        ENTER(GLFW.GLFW_KEY_ENTER),
        TAB(GLFW.GLFW_KEY_TAB),
        BACKSPACE(GLFW.GLFW_KEY_BACKSPACE),
        INSERT(GLFW.GLFW_KEY_INSERT),
        DELETE(GLFW.GLFW_KEY_DELETE),
        ARROW_RIGHT(GLFW.GLFW_KEY_RIGHT),
        ARROW_LEFT(GLFW.GLFW_KEY_LEFT),
        ARROW_DOWN(GLFW.GLFW_KEY_DOWN),
        ARROW_UP(GLFW.GLFW_KEY_UP),
        PAGE_UP(GLFW.GLFW_KEY_PAGE_UP),
        PAGE_DOWN(GLFW.GLFW_KEY_PAGE_DOWN),
        HOME(GLFW.GLFW_KEY_HOME),
        END(GLFW.GLFW_KEY_END),
        CAPS_LOCK(GLFW.GLFW_KEY_CAPS_LOCK),
        SCROLL_LOCK(GLFW.GLFW_KEY_SCROLL_LOCK),
        NUM_LOCK(GLFW.GLFW_KEY_NUM_LOCK),
        PRINT_SCREEN(GLFW.GLFW_KEY_PRINT_SCREEN),
        PAUSE(GLFW.GLFW_KEY_PAUSE),
        F1(GLFW.GLFW_KEY_F1),
        F2(GLFW.GLFW_KEY_F2),
        F3(GLFW.GLFW_KEY_F3),
        F4(GLFW.GLFW_KEY_F4),
        F5(GLFW.GLFW_KEY_F5),
        F6(GLFW.GLFW_KEY_F6),
        F7(GLFW.GLFW_KEY_F7),
        F8(GLFW.GLFW_KEY_F8),
        F9(GLFW.GLFW_KEY_F9),
        F10(GLFW.GLFW_KEY_F10),
        F11(GLFW.GLFW_KEY_F11),
        F12(GLFW.GLFW_KEY_F12),
        F13(GLFW.GLFW_KEY_F13),
        F14(GLFW.GLFW_KEY_F14),
        F15(GLFW.GLFW_KEY_F15),
        F16(GLFW.GLFW_KEY_F16),
        F17(GLFW.GLFW_KEY_F17),
        F18(GLFW.GLFW_KEY_F18),
        F19(GLFW.GLFW_KEY_F19),
        F20(GLFW.GLFW_KEY_F20),
        F21(GLFW.GLFW_KEY_F21),
        F22(GLFW.GLFW_KEY_F22),
        F23(GLFW.GLFW_KEY_F23),
        F24(GLFW.GLFW_KEY_F24),
        F25(GLFW.GLFW_KEY_F25),
        KEYPAD_0(GLFW.GLFW_KEY_KP_0),
        KEYPAD_1(GLFW.GLFW_KEY_KP_1),
        KEYPAD_2(GLFW.GLFW_KEY_KP_2),
        KEYPAD_3(GLFW.GLFW_KEY_KP_3),
        KEYPAD_4(GLFW.GLFW_KEY_KP_4),
        KEYPAD_5(GLFW.GLFW_KEY_KP_5),
        KEYPAD_6(GLFW.GLFW_KEY_KP_6),
        KEYPAD_7(GLFW.GLFW_KEY_KP_7),
        KEYPAD_8(GLFW.GLFW_KEY_KP_8),
        KEYPAD_9(GLFW.GLFW_KEY_KP_9),
        KEYPAD_DECIMAL(GLFW.GLFW_KEY_KP_DECIMAL),
        KEYPAD_DIVIDE(GLFW.GLFW_KEY_KP_DIVIDE),
        KEYPAD_MULTIPLY(GLFW.GLFW_KEY_KP_MULTIPLY),
        KEYPAD_SUBTRACT(GLFW.GLFW_KEY_KP_SUBTRACT),
        KEYPAD_ADD(GLFW.GLFW_KEY_KP_ADD),
        KEYPAD_ENTER(GLFW.GLFW_KEY_KP_ENTER),
        KEYPAD_EQUAL(GLFW.GLFW_KEY_KP_EQUAL),
        LEFT_SHIFT(GLFW.GLFW_KEY_LEFT_SHIFT),
        LEFT_CONTROL(GLFW.GLFW_KEY_LEFT_CONTROL),
        LEFT_ALT(GLFW.GLFW_KEY_LEFT_ALT),
        LEFT_SUPER(GLFW.GLFW_KEY_LEFT_SUPER),
        RIGHT_SHIFT(GLFW.GLFW_KEY_RIGHT_SHIFT),
        RIGHT_CONTROL(GLFW.GLFW_KEY_RIGHT_CONTROL),
        RIGHT_ALT(GLFW.GLFW_KEY_RIGHT_ALT),
        RIGHT_SUPER(GLFW.GLFW_KEY_RIGHT_SUPER),
        MENU(GLFW.GLFW_KEY_MENU),

        // Mouse buttons
        MOUSE_LEFT(GLFW.GLFW_MOUSE_BUTTON_LEFT),
        MOUSE_RIGHT(GLFW.GLFW_MOUSE_BUTTON_RIGHT),
        MOUSE_MIDDLE(GLFW.GLFW_MOUSE_BUTTON_MIDDLE),
        MOUSE_BUTTON_4(GLFW.GLFW_MOUSE_BUTTON_4),
        MOUSE_BUTTON_5(GLFW.GLFW_MOUSE_BUTTON_5),
        MOUSE_BUTTON_6(GLFW.GLFW_MOUSE_BUTTON_6),
        MOUSE_BUTTON_7(GLFW.GLFW_MOUSE_BUTTON_7),
        MOUSE_BUTTON_8(GLFW.GLFW_MOUSE_BUTTON_8);

        public final int code;
        public final String alternateName;

        Key(int code) {
            this(code, null);
        }

        Key(int code, String alternateName) {
            this.code = code;
            this.alternateName = alternateName;
        }

        public String getName() {
            return alternateName == null ? name() : alternateName;
        }

        public static final Int2ObjectMap<Key> keysByCode;

        static {
            Key[] keys = Key.values();
            keysByCode = new Int2ObjectOpenHashMap<>(keys.length);
            for (Key key : keys) {
                keysByCode.put(key.code, key);
            }
        }
    }

	// <--[language]
	// @name Keyboard Keys
	// @group Client Information
	// @description
	// The Minecraft client recognizes a specific set of keyboard and mouse keys, as listed below:
	// Keyboard keys:
	//  Letters: A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z
	//  Numbers: 0, 1, 2, 3, 4, 5, 6, 7, 8, 9
	//  F(unction) keys: F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16, F17, F18, F19, F20, F21, F22, F23, F24, F25
	//  Keypad: KEYPAD_0, KEYPAD_1, KEYPAD_2, KEYPAD_3, KEYPAD_4, KEYPAD_5, KEYPAD_6, KEYPAD_7, KEYPAD_8, KEYPAD_9, KEYPAD_DECIMAL,
	//          KEYPAD_DIVIDE, KEYPAD_MULTIPLY, KEYPAD_SUBTRACT, KEYPAD_ADD, KEYPAD_ENTER, KEYPAD_EQUAL
	//  Special characters: APOSTROPHE, COMMA, MINUS, PERIOD, SLASH, SEMICOLON, EQUAL, GRAVE_ACCENT, LEFT_BRACKET, RIGHT_BRACKET, BACKSLASH, SPACE
	//  Shift keys: LEFT_SHIFT, RIGHT_SHIFT
	//  World (globe/FN) keys: WORLD_1, WORLD_2
	//  Arrows: ARROW_RIGHT, ARROW_LEFT, ARROW_DOWN, ARROW_UP
	//  Control keys: LEFT_CONTROL, RIGHT_CONTROL
	//  Alt keys: LEFT_ALT, RIGHT_ALT
	//  Lock keys: CAPS_LOCK, NUM_LOCK, SCROLL_LOCK
	//  Page keys: PAGE_UP, PAGE_DOWN
	//  Super (Windows/system/command) keys: LEFT_SUPER, RIGHT_SUPER
	//  Other keys: ESCAPE, ENTER, TAB, BACKSPACE, INSERT, DELETE, HOME, END, PRINT_SCREEN, PAUSE, MENU
	//
	// Mouse buttons: MOUSE_LEFT, MOUSE_RIGHT, MOUSE_MIDDLE, MOUSE_BUTTON_4, MOUSE_BUTTON_5, MOUSE_BUTTON_6, MOUSE_BUTTON_7, MOUSE_BUTTON_8
    //
	// If a key is unrecognized, it will be 'UNKNOWN'.
	// -->
}
