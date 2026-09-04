/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
 *  net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
 *  net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
 *  net.minecraft.class_239
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_304
 *  net.minecraft.class_304$class_11900
 *  net.minecraft.class_310
 *  net.minecraft.class_3675$class_307
 *  net.minecraft.class_3965
 *  net.minecraft.class_437
 *  net.minecraft.class_7923
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.ruok0214.renderhide;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.ruok0214.renderhide.HiddenRegion;
import com.ruok0214.renderhide.RegionManager;
import com.ruok0214.renderhide.RenderHideScreen;
import com.ruok0214.renderhide.SelectionOverlayRenderer;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.class_239;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import net.minecraft.class_3965;
import net.minecraft.class_437;
import net.minecraft.class_7923;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class RenderHideClient
implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"renderhide");
    private static final class_304.class_11900 CATEGORY = class_304.class_11900.method_74698((class_2960)class_2960.method_60655((String)"renderhide", (String)"keys"));
    private static class_304 toggleKey;
    private static class_304 pos1Key;
    private static class_304 pos2Key;
    private static class_304 addKey;
    private static class_304 savedOutlinesKey;
    private static class_304 virtualLightKey;
    private static class_304 guiKey;

    public void onInitializeClient() {
        RegionManager.load();
        SelectionOverlayRenderer.register();
        this.registerKeys();
        this.registerCommands();
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        LOGGER.info("Render Hide 1.0.0 initialized by RUOK0214");
    }

    private void registerKeys() {
        toggleKey = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.renderhide.toggle", class_3675.class_307.field_1668, 297, CATEGORY));
        pos1Key = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.renderhide.pos1", class_3675.class_307.field_1668, 298, CATEGORY));
        pos2Key = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.renderhide.pos2", class_3675.class_307.field_1668, 299, CATEGORY));
        addKey = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.renderhide.add", class_3675.class_307.field_1668, 296, CATEGORY));
        savedOutlinesKey = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.renderhide.saved_outlines", class_3675.class_307.field_1668, 295, CATEGORY));
        virtualLightKey = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.renderhide.virtual_light", class_3675.class_307.field_1668, -1, CATEGORY));
        guiKey = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.renderhide.gui", class_3675.class_307.field_1668, 72, CATEGORY));
    }

    private void onTick(class_310 client) {
        RegionManager.updateDimension(client);
        while (toggleKey.method_1436()) {
            RegionManager.toggleGlobal();
        }
        while (pos1Key.method_1436()) {
            RenderHideClient.setLookedAt(true);
        }
        while (pos2Key.method_1436()) {
            RenderHideClient.setLookedAt(false);
        }
        while (addKey.method_1436()) {
            RegionManager.add("region");
        }
        while (savedOutlinesKey.method_1436()) {
            SelectionOverlayRenderer.toggleSavedRegions();
        }
        while (virtualLightKey.method_1436()) {
            RegionManager.toggleVirtualLight();
        }
        while (guiKey.method_1436()) {
            if (client.field_1755 instanceof RenderHideScreen) {
                client.method_1507(null);
                continue;
            }
            client.method_1507((class_437)new RenderHideScreen());
        }
    }

    private static boolean setLookedAt(boolean first) {
        class_3965 hit;
        class_310 client = class_310.method_1551();
        class_239 class_2392 = client.field_1765;
        if (!(class_2392 instanceof class_3965) || (hit = (class_3965)class_2392).method_17783() != class_239.class_240.field_1332) {
            RegionManager.message("Look at a block first.");
            return false;
        }
        if (first) {
            RegionManager.setPos1(hit.method_17777());
        } else {
            RegionManager.setPos2(hit.method_17777());
        }
        return true;
    }

    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)ClientCommandManager.literal((String)"renderhide").executes(ctx -> RenderHideClient.help((FabricClientCommandSource)ctx.getSource()))).then(ClientCommandManager.literal((String)"pos1").executes(ctx -> RenderHideClient.setLookedAt(true) ? 1 : 0))).then(ClientCommandManager.literal((String)"pos2").executes(ctx -> RenderHideClient.setLookedAt(false) ? 1 : 0))).then(((LiteralArgumentBuilder)ClientCommandManager.literal((String)"add").executes(ctx -> RegionManager.add("region") ? 1 : 0)).then(ClientCommandManager.argument((String)"name", (ArgumentType)StringArgumentType.word()).executes(ctx -> RegionManager.add(StringArgumentType.getString((CommandContext)ctx, (String)"name")) ? 1 : 0)))).then(ClientCommandManager.literal((String)"remove").then(ClientCommandManager.argument((String)"name", (ArgumentType)StringArgumentType.word()).suggests((ctx, builder) -> RenderHideClient.suggestNames(builder)).executes(ctx -> RenderHideClient.result(RegionManager.remove(StringArgumentType.getString((CommandContext)ctx, (String)"name")), "Region not found."))))).then(ClientCommandManager.literal((String)"toggle").then(ClientCommandManager.argument((String)"name", (ArgumentType)StringArgumentType.word()).suggests((ctx, builder) -> RenderHideClient.suggestNames(builder)).executes(ctx -> RenderHideClient.result(RegionManager.toggle(StringArgumentType.getString((CommandContext)ctx, (String)"name")), "Region not found."))))).then(ClientCommandManager.literal((String)"global").executes(ctx -> {
            RegionManager.toggleGlobal();
            return 1;
        }))).then(ClientCommandManager.literal((String)"outlines").executes(ctx -> {
            SelectionOverlayRenderer.toggleSavedRegions();
            return 1;
        }))).then(ClientCommandManager.literal((String)"light").executes(ctx -> {
            RegionManager.toggleVirtualLight();
            return 1;
        }))).then(ClientCommandManager.literal((String)"gui").executes(ctx -> {
            class_310.method_1551().method_1507((class_437)new RenderHideScreen());
            return 1;
        }))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)ClientCommandManager.literal((String)"filter").then(ClientCommandManager.literal((String)"add").then(ClientCommandManager.argument((String)"block", (ArgumentType)StringArgumentType.greedyString()).suggests((ctx, builder) -> RenderHideClient.suggestBlocks(builder, false)).executes(ctx -> RenderHideClient.addFilter(StringArgumentType.getString((CommandContext)ctx, (String)"block")))))).then(ClientCommandManager.literal((String)"remove").then(ClientCommandManager.argument((String)"block", (ArgumentType)StringArgumentType.greedyString()).suggests((ctx, builder) -> RenderHideClient.suggestBlocks(builder, true)).executes(ctx -> RenderHideClient.removeFilter(StringArgumentType.getString((CommandContext)ctx, (String)"block")))))).then(ClientCommandManager.literal((String)"list").executes(ctx -> RenderHideClient.listFilters((FabricClientCommandSource)ctx.getSource())))).then(ClientCommandManager.literal((String)"clear").executes(ctx -> {
            RegionManager.clearVisibleBlockFilters();
            return 1;
        })))).then(ClientCommandManager.literal((String)"clear").executes(ctx -> {
            RegionManager.clear();
            return 1;
        }))).then(ClientCommandManager.literal((String)"list").executes(ctx -> RenderHideClient.list((FabricClientCommandSource)ctx.getSource())))).then(ClientCommandManager.literal((String)"reload").executes(ctx -> {
            RegionManager.refreshAll();
            return 1;
        }))));
    }

    private static CompletableFuture<Suggestions> suggestNames(SuggestionsBuilder builder) {
        for (HiddenRegion region : RegionManager.regions()) {
            builder.suggest(region.name());
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestBlocks(SuggestionsBuilder builder, boolean filtersOnly) {
        if (filtersOnly) {
            RegionManager.visibleBlockFilters().forEach(id -> builder.suggest(id.toString()));
        } else {
            class_7923.field_41175.method_10235().forEach(id -> builder.suggest(id.toString()));
        }
        return builder.buildFuture();
    }

    private static int addFilter(String value) {
        class_2960 id = class_2960.method_12829((String)(value = value.trim()));
        if (id == null || !class_7923.field_41175.method_10250(id)) {
            RegionManager.message("Unknown block: " + value);
            return 0;
        }
        return RenderHideClient.result(RegionManager.addVisibleBlockFilter(id), "Block is already in the visible filter.");
    }

    private static int removeFilter(String value) {
        class_2960 id = class_2960.method_12829((String)(value = value.trim()));
        return RenderHideClient.result(id != null && RegionManager.removeVisibleBlockFilter(id), "Block is not in the visible filter.");
    }

    private static int listFilters(FabricClientCommandSource source) {
        if (RegionManager.visibleBlockFilters().isEmpty()) {
            source.sendFeedback((class_2561)class_2561.method_43470((String)"[Render Hide] Visible block filters: none (all blocks are hidden)."));
            return 1;
        }
        source.sendFeedback((class_2561)class_2561.method_43470((String)"[Render Hide] Blocks kept visible inside hidden regions:"));
        RegionManager.visibleBlockFilters().stream().sorted().forEach(id -> source.sendFeedback((class_2561)class_2561.method_43470((String)("- " + String.valueOf(id)))));
        return RegionManager.visibleBlockFilters().size();
    }

    private static int result(boolean success, String failure) {
        if (!success) {
            RegionManager.message(failure);
        }
        return success ? 1 : 0;
    }

    private static int list(FabricClientCommandSource source) {
        if (RegionManager.regions().isEmpty()) {
            source.sendFeedback((class_2561)class_2561.method_43470((String)"[Render Hide] No regions saved."));
            return 1;
        }
        source.sendFeedback((class_2561)class_2561.method_43470((String)"[Render Hide] Saved regions:"));
        for (HiddenRegion region : RegionManager.regions()) {
            source.sendFeedback((class_2561)class_2561.method_43470((String)("- " + region.name() + " | " + (region.enabled() ? "hidden" : "visible") + " | " + region.blockCount() + " blocks | " + region.dimension())));
        }
        return RegionManager.regions().size();
    }

    private static int help(FabricClientCommandSource source) {
        source.sendFeedback((class_2561)class_2561.method_43470((String)"[Render Hide] H=GUI, F9=pos1, F10=pos2, F7=add, F8=global toggle, F6=saved outlines"));
        source.sendFeedback((class_2561)class_2561.method_43470((String)"/renderhide add [name] | remove <name> | toggle <name> | list | clear"));
        source.sendFeedback((class_2561)class_2561.method_43470((String)"/renderhide filter add|remove <block> | filter list|clear"));
        source.sendFeedback((class_2561)class_2561.method_43470((String)"/renderhide light = virtual light toggle"));
        return 1;
    }
}


