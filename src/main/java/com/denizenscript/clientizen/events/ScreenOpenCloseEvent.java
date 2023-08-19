package com.denizenscript.clientizen.events;

import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.screen.option.*;
import net.minecraft.client.gui.screen.pack.ExperimentalWarningScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.report.AbuseReportReasonScreen;
import net.minecraft.client.gui.screen.report.ChatReportScreen;
import net.minecraft.client.gui.screen.report.ChatSelectionScreen;
import net.minecraft.client.gui.screen.world.*;

import java.util.HashMap;
import java.util.Map;

public class ScreenOpenCloseEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // screen opened|closed
    //
    // @Switch type:<screen_type> to only process the event if the type of screen opened matches the specified matcher.
    //
    // @Triggers when a screen is opened or closed.
    //
    // @Context
    // <context.screen_type> returns an ElementTag of the screen type that opened.
    // <context.switched> returns an ElementTag(Boolean) of whether the screen was opened from another screen.
    //
    // -->

    // TODO: This event needs a partial redo, mainly:
    // - CreativeInventoryScreen no longer extends InventoryScreen
    // - Add all relevant screen types
    // - Potentially add support for handling screens that aren't directly defined via class name

    public static ScreenOpenCloseEvent instance;

    static {
        ScreenEvents.AFTER_INIT.register((client, openedScreen, scaledWidth, scaledHeight) -> {
            ScreenOpenCloseEvent.instance.handleScreenChange(openedScreen, client.currentScreen, true);
            ScreenEvents.remove(openedScreen).register(closedScreen -> ScreenOpenCloseEvent.instance.handleScreenChange(closedScreen, null, false));
        });
        TYPE_MAP = new HashMap<>();
        // Advancement screens
        registerScreenName(AdvancementsScreen.class, "advancements");
        // In-game screens
        registerScreenName(AnvilScreen.class, "anvil");
        registerScreenName(BeaconScreen.class, "beacon");
        registerScreenName(BlastFurnaceScreen.class, "blast_furnace");
        registerScreenName(BookEditScreen.class, "book_edit");
        registerScreenName(BookScreen.class, "book");
        registerScreenName(BrewingStandScreen.class, "brewing_stand");
        registerScreenName(CartographyTableScreen.class, "cartography_table");
        registerScreenName(CommandBlockScreen.class, "command_block");
        registerScreenName(CraftingScreen.class, "crafting");
        registerScreenName(CreativeInventoryScreen.class, "creative");
        registerScreenName(EnchantmentScreen.class, "enchantment");
        registerScreenName(FurnaceScreen.class, "furnace");
        registerScreenName(Generic3x3ContainerScreen.class, "generic_3x3_container");
        registerScreenName(GenericContainerScreen.class, "generic_container");
        registerScreenName(GrindstoneScreen.class, "grindstone");
        registerScreenName(HangingSignEditScreen.class, "hanging_sign_edit");
        registerScreenName(HopperScreen.class, "hopper");
        registerScreenName(HopperScreen.class, "horse");
        registerScreenName(InventoryScreen.class, "inventory");
        registerScreenName(JigsawBlockScreen.class, "jigsaw");
        registerScreenName(LecternScreen.class, "lectern");
        registerScreenName(LoomScreen.class, "loom");
        registerScreenName(MerchantScreen.class, "merchant");
        registerScreenName(MinecartCommandBlockScreen.class, "command_block_minecart");
        registerScreenName(ShulkerBoxScreen.class, "shulker_box");
        registerScreenName(SignEditScreen.class, "sign_edit");
        registerScreenName(SmithingScreen.class, "smithing");
        registerScreenName(SmokerScreen.class, "smoker");
        registerScreenName(StonecutterScreen.class, "stunecutter");
        registerScreenName(StructureBlockScreen.class, "structure_block");
        // Multiplayer screens
        registerScreenName(MultiplayerScreen.class, "multiplayer");
        registerScreenName(MultiplayerWarningScreen.class, "multiplayer_warning");
        registerScreenName(SocialInteractionsScreen.class, "social_interactions");
        // Option screens
        registerScreenName(AccessibilityOptionsScreen.class, "accessibility_options");
        registerScreenName(ChatOptionsScreen.class, "chat_options");
        registerScreenName(ControlsOptionsScreen.class, "controls_options");
        registerScreenName(CreditsAndAttributionScreen.class, "credits_and_attribution");
        registerScreenName(GameOptionsScreen.class, "game_options");
        registerScreenName(KeybindsScreen.class, "keybinds");
        registerScreenName(LanguageOptionsScreen.class, "language_options");
        registerScreenName(MouseOptionsScreen.class, "mouse_options");
        registerScreenName(OnlineOptionsScreen.class, "online_options");
        registerScreenName(OptionsScreen.class, "options");
        registerScreenName(SkinOptionsScreen.class, "skin_options");
        registerScreenName(SoundOptionsScreen.class, "sound_options");
        registerScreenName(TelemetryInfoScreen.class, "telemetry_info");
        registerScreenName(VideoOptionsScreen.class, "video_options");
        // Packs screens
        registerScreenName(ExperimentalWarningScreen.class, "experimental_warning");
        registerScreenName((Class<? extends Screen>) ExperimentalWarningScreen.class.getDeclaredClasses()[0], "experimental_warning_details");
        registerScreenName(PackScreen.class, "pack");
        // Report screens
        registerScreenName(AbuseReportReasonScreen.class, "abuse_report_reason");
        registerScreenName(ChatReportScreen.class, "chat_report");
        registerScreenName(ChatSelectionScreen.class, "chat_selection");
        // World screens
        registerScreenName(CreateWorldScreen.class, "create_world");
        registerScreenName(EditGameRulesScreen.class, "edit_game_rules");
        registerScreenName(ExperimentsScreen.class, "experiments");
        registerScreenName(OptimizeWorldScreen.class, "optimize_world");
        registerScreenName(SelectWorldScreen.class, "select_world");
        registerScreenName(SymlinkWarningScreen.class, "symlink_warning");
        // Other screens
        registerScreenName(AccessibilityOnboardingScreen.class, "accessibility_onboarding");
        registerScreenName(AddServerScreen.class, "add_server");
        registerScreenName(BackupPromptScreen.class, "backup_prompt");
        registerScreenName(ChatScreen.class, "chat");
        registerScreenName(ConfirmLinkScreen.class, "confirm_link");
        registerScreenName(ConfirmScreen.class, "confirm");
        registerScreenName(ConnectScreen.class, "connect");
        registerScreenName(CreditsScreen.class, "credits");
        registerScreenName(CustomizeBuffetLevelScreen.class, "customize_buffet_level");
        registerScreenName(CustomizeFlatLevelScreen.class, "customize_flat_level");
        registerScreenName(DatapackFailureScreen.class, "datapack_failure");
        registerScreenName(DeathScreen.class, "death");
        registerScreenName(DemoScreen.class, "demo");
        registerScreenName(DialogScreen.class, "dialog");
        registerScreenName(DirectConnectScreen.class, "direct_connect");
        registerScreenName(DisconnectedScreen.class, "disconnected");
        registerScreenName(DownloadingTerrainScreen.class, "downloading_terrain");
        registerScreenName(FatalErrorScreen.class, "fatal_error");
        registerScreenName(GameMenuScreen.class, "game_menu");
        registerScreenName(GameModeSelectionScreen.class, "game_mode_selection");
        registerScreenName(LevelLoadingScreen.class, "level_loading");
        registerScreenName(MessageScreen.class, "message");
        registerScreenName(NoticeScreen.class, "notice_screen");
        registerScreenName(OpenToLanScreen.class, "open_to_lan");
        registerScreenName(OutOfMemoryScreen.class, "out_of_memory");
        registerScreenName(PresetsScreen.class, "presets");
        registerScreenName(ProgressScreen.class, "progress");
        registerScreenName(Realms32BitWarningScreen.class, "realms_32_bit_warning");
        registerScreenName(SleepingChatScreen.class, "sleeping_chat");
        registerScreenName(StatsScreen.class, "stats");
        registerScreenName(TaskScreen.class, "task");
        registerScreenName(TitleScreen.class, "title");
    }

    private static final Map<Class<? extends Screen>, String> TYPE_MAP;

    public static void registerScreenName(Class<? extends Screen> screenType, String name) {
        TYPE_MAP.put(screenType, name);
    }

    public String type;
    public boolean opened;
    public boolean switched;

    public ScreenOpenCloseEvent() {
        registerCouldMatcher("screen opened|closed");
        registerSwitches("type");
        instance = this;
    }

    @Override
    public boolean matches(ScriptPath path) {
        if (!runGenericSwitchCheck(path, "type", type)) {
            return false;
        }
        if (opened != path.eventArgLowerAt(1).equals("opened")) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "screen_type" -> new ElementTag(type, true);
            case "switched" -> new ElementTag(switched);
            default -> super.getContext(name);
        };
    }

    public void handleScreenChange(Screen screen, Screen otherScreen, boolean open) {
        if (!enabled) {
            return;
        }
        // An InventoryScreen is always opened, which then opens a creative screen if needed
        if (screen instanceof InventoryScreen && MinecraftClient.getInstance().interactionManager.hasCreativeInventory()) {
            return;
        }
        type = TYPE_MAP.computeIfAbsent(screen.getClass(), clazz -> {
            String className = clazz.getSimpleName();
            if (className.endsWith("Screen")) {
                className = className.substring(0, className.length() - "Screen".length());
            }
            return Utilities.camelCaseToSnake(className);
        });
        opened = open;
        switched = otherScreen != null;
        fire();
    }

    boolean enabled = false;

    @Override
    public void init() {
        enabled = true;
    }

    @Override
    public void destroy() {
        enabled = false;
    }
}
