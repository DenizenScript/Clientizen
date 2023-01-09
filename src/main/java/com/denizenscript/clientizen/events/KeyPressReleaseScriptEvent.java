package com.denizenscript.clientizen.events;

import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.util.InputUtil;

public class KeyPressReleaseScriptEvent extends ScriptEvent {

	public static KeyPressReleaseScriptEvent instance;

	public Keys key;
	public boolean pressed;
	public KeyType type;


	public KeyPressReleaseScriptEvent() {
		registerCouldMatcher("key pressed|released");
		registerSwitches("key", "type");
		instance = this;
	}

	@Override
	public boolean matches(ScriptPath path) {
		if (!runGenericSwitchCheck(path, "key", key.getName())) {
			return false;
		}
		if (!runGenericSwitchCheck(path, "type", type.name())) {
			return false;
		}
		if (pressed != path.eventArgLowerAt(2).equals("pressed")) {
			return false;
		}
		return super.matches(path);
	}

	@Override
	public ObjectTag getContext(String name) {
		return switch (name) {
			case "key" -> new ElementTag(key.getName(), true);
			case "type" -> new ElementTag(type);
			default -> super.getContext(name);
		};
	}

	public void handleKeyPressStateChange(InputUtil.Key key, boolean pressed) {
		this.key = Keys.keysByCode.get(key.getCode());
		this.pressed = pressed;
		this.type = key.getCategory() == InputUtil.Type.KEYSYM ? KeyType.KEYBOARD : KeyType.MOUSE;
		fire();
	}

	enum KeyType { KEYBOARD, MOUSE }

	enum Keys {
		UNKNOWN(-1),
		// Keyboard keys
		SPACE(32),
		APOSTROPHE(39),
		COMMA(44),
		MINUS(45),
		PERIOD(46),
		SLASH(47),
		KEY_0(48, "0"),
		KEY_1(49, "1"),
		KEY_2(50, "2"),
		KEY_3(51, "3"),
		KEY_4(52, "4"),
		KEY_5(53, "5"),
		KEY_6(54, "6"),
		KEY_7(55, "7"),
		KEY_8(56, "8"),
		KEY_9(57, "9"),
		SEMICOLON(59),
		EQUAL(61),
		A(65),
		B(66),
		C(67),
		D(68),
		E(69),
		F(70),
		G(71),
		H(72),
		I(73),
		J(74),
		K(75),
		L(76),
		M(77),
		N(78),
		O(79),
		P(80),
		Q(81),
		R(82),
		S(83),
		T(84),
		U(85),
		V(86),
		W(87),
		X(88),
		Y(89),
		Z(90),
		LEFT_BRACKET(91),
		BACKSLASH(92),
		RIGHT_BRACKET(93),
		GRAVE_ACCENT(96),
		WORLD_1(161),
		WORLD_2(162),
		ESCAPE(256),
		ENTER(257),
		TAB(258),
		BACKSPACE(259),
		INSERT(260),
		DELETE(261),
		ARROW_RIGHT(262),
		ARROW_LEFT(263),
		ARROW_DOWN(264),
		ARROW_UP(265),
		PAGE_UP(266),
		PAGE_DOWN(267),
		HOME(268),
		END(269),
		CAPS_LOCK(280),
		SCROLL_LOCK(281),
		NUM_LOCK(282),
		PRINT_SCREEN(283),
		PAUSE(284),
		F1(290),
		F2(291),
		F3(292),
		F4(293),
		F5(294),
		F6(295),
		F7(296),
		F8(297),
		F9(298),
		F10(299),
		F11(300),
		F12(301),
		F13(302),
		F14(303),
		F15(304),
		F16(305),
		F17(306),
		F18(307),
		F19(308),
		F20(309),
		F21(310),
		F22(311),
		F23(312),
		F24(313),
		F25(314),
		KEYPAD_0(320),
		KEYPAD_1(321),
		KEYPAD_2(322),
		KEYPAD_3(323),
		KEYPAD_4(324),
		KEYPAD_5(325),
		KEYPAD_6(326),
		KEYPAD_7(327),
		KEYPAD_8(328),
		KEYPAD_9(329),
		KEYPAD_DECIMAL(330),
		KEYPAD_DIVIDE(331),
		KEYPAD_MULTIPLY(332),
		KEYPAD_SUBTRACT(333),
		KEYPAD_ADD(334),
		KEYPAD_ENTER(335),
		KEYPAD_EQUAL(336),
		LEFT_SHIFT(340),
		LEFT_CONTROL(341),
		LEFT_ALT(342),
		LEFT_SUPER(343),
		RIGHT_SHIFT(344),
		RIGHT_CONTROL(345),
		RIGHT_ALT(346),
		RIGHT_SUPER(347),
		MENU(348),

		// Mouse buttons
		MOUSE_LEFT(0),
		MOUSE_RIGHT(1),
		MOUSE_MIDDLE(2),
		MOUSE_BUTTON_4(3),
		MOUSE_BUTTON_5(4),
		MOUSE_BUTTON_6(5),
		MOUSE_BUTTON_7(6),
		MOUSE_BUTTON_8(7);

		public final int code;
		public String alternateName;

		Keys(int code) {
			this.code = code;
		}

		Keys(int code, String alternateName) {
			this.code = code;
			this.alternateName = alternateName;
		}

		public String getName() {
			return alternateName == null ? name() : alternateName;
		}

		public static final Int2ObjectMap<Keys> keysByCode;

		static {
			Keys[] keys = Keys.values();
			keysByCode = new Int2ObjectOpenHashMap<>(keys.length);
			for (Keys key : keys) {
				keysByCode.put(key.code, key);
			}
		}
	}
}
