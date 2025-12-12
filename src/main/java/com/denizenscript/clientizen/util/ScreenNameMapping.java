package com.denizenscript.clientizen.util;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptScreen;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.options.*;
import net.minecraft.client.gui.screens.options.controls.ControlsScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.reporting.ChatReportScreen;
import net.minecraft.client.gui.screens.reporting.ChatSelectionScreen;
import net.minecraft.client.gui.screens.reporting.ReportReasonSelectionScreen;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.gui.screens.telemetry.TelemetryInfoScreen;
import net.minecraft.client.gui.screens.worldselection.*;

import java.util.HashMap;
import java.util.Map;

public class ScreenNameMapping {

    private static final Map<Class<? extends Screen>, String> TYPE_MAP = new HashMap<>();

    static {
        // Advancement screens
        registerScreenName(AdvancementsScreen.class, "advancements");
        // In-game screens
        registerScreenName(AnvilScreen.class, "anvil");
        registerScreenName(BeaconScreen.class, "beacon");
        registerScreenName(BlastFurnaceScreen.class, "blast_furnace");
        registerScreenName(BookEditScreen.class, "book_edit");
        registerScreenName(BookViewScreen.class, "book");
        registerScreenName(BrewingStandScreen.class, "brewing_stand");
        registerScreenName(CartographyTableScreen.class, "cartography_table");
        registerScreenName(CommandBlockEditScreen.class, "command_block");
        registerScreenName(CraftingScreen.class, "crafting");
        registerScreenName(CreativeModeInventoryScreen.class, "creative");
        registerScreenName(EnchantmentScreen.class, "enchantment");
        registerScreenName(FurnaceScreen.class, "furnace");
        registerScreenName(DispenserScreen.class, "generic_3x3_container");
        registerScreenName(ContainerScreen.class, "generic_container");
        registerScreenName(GrindstoneScreen.class, "grindstone");
        registerScreenName(HangingSignEditScreen.class, "hanging_sign_edit");
        registerScreenName(HopperScreen.class, "hopper");
        registerScreenName(HopperScreen.class, "horse");
        registerScreenName(InventoryScreen.class, "inventory");
        registerScreenName(JigsawBlockEditScreen.class, "jigsaw");
        registerScreenName(LecternScreen.class, "lectern");
        registerScreenName(LoomScreen.class, "loom");
        registerScreenName(MerchantScreen.class, "merchant");
        registerScreenName(MinecartCommandBlockEditScreen.class, "command_block_minecart");
        registerScreenName(ShulkerBoxScreen.class, "shulker_box");
        registerScreenName(SignEditScreen.class, "sign_edit");
        registerScreenName(SmithingScreen.class, "smithing");
        registerScreenName(SmokerScreen.class, "smoker");
        registerScreenName(StonecutterScreen.class, "stonecutter");
        registerScreenName(StructureBlockEditScreen.class, "structure_block");
        // Multiplayer screens
        registerScreenName(JoinMultiplayerScreen.class, "multiplayer");
        registerScreenName(SafetyScreen.class, "multiplayer_warning");
        registerScreenName(SocialInteractionsScreen.class, "social_interactions");
        // Option screens
        registerScreenName(AccessibilityOptionsScreen.class, "accessibility_options");
        registerScreenName(ChatOptionsScreen.class, "chat_options");
        registerScreenName(ControlsScreen.class, "controls_options");
        registerScreenName(CreditsAndAttributionScreen.class, "credits_and_attribution");
        registerScreenName(OptionsSubScreen.class, "game_options");
        registerScreenName(KeyBindsScreen.class, "keybinds");
        registerScreenName(LanguageSelectScreen.class, "language_options");
        registerScreenName(MouseSettingsScreen.class, "mouse_options");
        registerScreenName(OnlineOptionsScreen.class, "online_options");
        registerScreenName(OptionsScreen.class, "options");
        registerScreenName(SkinCustomizationScreen.class, "skin_options");
        registerScreenName(SoundOptionsScreen.class, "sound_options");
        registerScreenName(TelemetryInfoScreen.class, "telemetry_info");
        registerScreenName(VideoSettingsScreen.class, "video_options");
        // Packs screens
        registerScreenName(ConfirmExperimentalFeaturesScreen.class, "experimental_warning");
        registerScreenName(ConfirmExperimentalFeaturesScreen.DetailsScreen.class, "experimental_warning_details");
        registerScreenName(PackSelectionScreen.class, "pack");
        // Report screens
        registerScreenName(ReportReasonSelectionScreen.class, "abuse_report_reason");
        registerScreenName(ChatReportScreen.class, "chat_report");
        registerScreenName(ChatSelectionScreen.class, "chat_selection");
        // World screens
        registerScreenName(CreateWorldScreen.class, "create_world");
        registerScreenName(EditGameRulesScreen.class, "edit_game_rules");
        registerScreenName(ExperimentsScreen.class, "experiments");
        registerScreenName(OptimizeWorldScreen.class, "optimize_world");
        registerScreenName(SelectWorldScreen.class, "select_world");
        registerScreenName(NoticeWithLinkScreen.class, "symlink_warning");
        // Other screens
        registerScreenName(AccessibilityOnboardingScreen.class, "accessibility_onboarding");
        registerScreenName(ManageServerScreen.class, "add_server");
        registerScreenName(BackupConfirmScreen.class, "backup_prompt");
        registerScreenName(ChatScreen.class, "chat");
        registerScreenName(ConfirmLinkScreen.class, "confirm_link");
        registerScreenName(ConfirmScreen.class, "confirm");
        registerScreenName(ConnectScreen.class, "connect");
        registerScreenName(WinScreen.class, "credits");
        registerScreenName(CreateBuffetWorldScreen.class, "customize_buffet_level");
        registerScreenName(CreateFlatWorldScreen.class, "customize_flat_level");
        registerScreenName(DatapackLoadFailureScreen.class, "datapack_failure");
        registerScreenName(DeathScreen.class, "death");
        registerScreenName(DemoIntroScreen.class, "demo");
        registerScreenName(UnsupportedGraphicsWarningScreen.class, "dialog");
        registerScreenName(DirectJoinServerScreen.class, "direct_connect");
        registerScreenName(DisconnectedScreen.class, "disconnected");
        registerScreenName(ErrorScreen.class, "fatal_error");
        registerScreenName(PauseScreen.class, "game_menu");
        registerScreenName(GameModeSwitcherScreen.class, "game_mode_selection");
        registerScreenName(LevelLoadingScreen.class, "level_loading");
        registerScreenName(GenericMessageScreen.class, "message");
        registerScreenName(AlertScreen.class, "notice_screen");
        registerScreenName(ShareToLanScreen.class, "open_to_lan");
        registerScreenName(OutOfMemoryScreen.class, "out_of_memory");
        registerScreenName(PresetFlatWorldScreen.class, "presets");
        registerScreenName(ProgressScreen.class, "progress");
        registerScreenName(InBedChatScreen.class, "sleeping_chat");
        registerScreenName(StatsScreen.class, "stats");
        registerScreenName(GenericWaitingScreen.class, "task");
        registerScreenName(TitleScreen.class, "title");
    }

    public static void registerScreenName(Class<? extends Screen> screenType, String name) {
        TYPE_MAP.put(screenType, name);
    }

    public static String getScreenName(Screen screen) {
        if (screen instanceof GuiScriptScreen guiScriptScreen) {
            return guiScriptScreen.getScript().getName();
        }
        return TYPE_MAP.computeIfAbsent(screen.getClass(), clazz -> {
            // Try dynamically generating the name - currently only for modded screens, as vanilla screens have remapped class names
            String className = clazz.getSimpleName();
            if (className.endsWith("Screen")) {
                className = className.substring(0, className.length() - "Screen".length());
            }
            return Utilities.camelCaseToSnake(className);
        });
    }
}
