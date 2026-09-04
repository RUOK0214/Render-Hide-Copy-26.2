/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_1297
 *  net.minecraft.class_1944
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2350
 *  net.minecraft.class_2374
 *  net.minecraft.class_2561
 *  net.minecraft.class_2586
 *  net.minecraft.class_2669
 *  net.minecraft.class_2671
 *  net.minecraft.class_2680
 *  net.minecraft.class_2764
 *  net.minecraft.class_2769
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4076
 *  net.minecraft.class_7923
 */
package com.ruok0214.renderhide;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ruok0214.renderhide.HiddenRegion;
import com.ruok0214.renderhide.RenderHideClient;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1297;
import net.minecraft.class_1944;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2374;
import net.minecraft.class_2561;
import net.minecraft.class_2586;
import net.minecraft.class_2669;
import net.minecraft.class_2671;
import net.minecraft.class_2680;
import net.minecraft.class_2764;
import net.minecraft.class_2769;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4076;
import net.minecraft.class_7923;

@Environment(value=EnvType.CLIENT)
public final class RegionManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type REGION_LIST = new TypeToken<List<HiddenRegion>>(){}.getType();
    private static final Path CONFIG = FabricLoader.getInstance().getConfigDir().resolve("renderhide-regions.json");
    private static final Path FILTER_CONFIG = FabricLoader.getInstance().getConfigDir().resolve("renderhide-filters.json");
    private static final Path REGION_FILTER_CONFIG = FabricLoader.getInstance().getConfigDir().resolve("renderhide-region-filters.json");
    private static final Path ENTITY_FILTER_CONFIG = FabricLoader.getInstance().getConfigDir().resolve("renderhide-entity-filters.json");
    private static final Path REGION_ENTITY_FILTER_CONFIG = FabricLoader.getInstance().getConfigDir().resolve("renderhide-region-entity-filters.json");
    private static final Path OPACITY_CONFIG = FabricLoader.getInstance().getConfigDir().resolve("renderhide-opacity.json");
    private static volatile List<HiddenRegion> snapshot = List.of();
    private static volatile String activeDimension = "";
    private static volatile boolean globallyEnabled = true;
    private static volatile boolean virtualLightEnabled = true;
    private static volatile float hiddenBlockOpacity = 0.0f;
    private static volatile Set<class_2960> visibleBlockFilters = Set.of();
    private static volatile Map<String, Set<class_2960>> regionVisibleBlockFilters = Map.of();
    private static volatile Set<class_2960> visibleEntityFilters = Set.of();
    private static volatile Map<String, Set<class_2960>> regionVisibleEntityFilters = Map.of();
    private static class_2338 pos1;
    private static class_2338 pos2;

    private RegionManager() {
    }

    public static void load() {
        Object loaded;
        BufferedReader reader;
        if (Files.exists(CONFIG, new LinkOption[0])) {
            try {
                reader = Files.newBufferedReader(CONFIG);
                try {
                    loaded = (List)GSON.fromJson((Reader)reader, REGION_LIST);
                    snapshot = loaded == null ? List.of() : List.copyOf(loaded);
                }
                finally {
                    if (reader != null) {
                        ((Reader)reader).close();
                    }
                }
            }
            catch (Exception e) {
                RenderHideClient.LOGGER.error("Could not load hidden regions", (Throwable)e);
            }
        }
        if (Files.exists(FILTER_CONFIG, new LinkOption[0])) {
            try {
                reader = Files.newBufferedReader(FILTER_CONFIG);
                try {
                    loaded = (List)GSON.fromJson((Reader)reader, new TypeToken<List<String>>(){}.getType());
                    LinkedHashSet<class_2960> filters = new LinkedHashSet<class_2960>();
                    if (loaded != null) {
                        Iterator iterator = loaded.iterator();
                        while (iterator.hasNext()) {
                            String value = (String)iterator.next();
                            class_2960 id = class_2960.method_12829((String)value);
                            if (id == null || !class_7923.field_41175.method_10250(id)) continue;
                            filters.add(id);
                        }
                    }
                    visibleBlockFilters = Set.copyOf(filters);
                }
                finally {
                    if (reader != null) {
                        ((Reader)reader).close();
                    }
                }
            }
            catch (Exception e) {
                RenderHideClient.LOGGER.error("Could not load visible block filters", (Throwable)e);
            }
        }
        if (Files.exists(REGION_FILTER_CONFIG, new LinkOption[0])) {
            try {
                reader = Files.newBufferedReader(REGION_FILTER_CONFIG);
                try {
                    Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
                    Map loaded2 = (Map)GSON.fromJson((Reader)reader, type);
                    LinkedHashMap result = new LinkedHashMap();
                    if (loaded2 != null) {
                        loaded2.forEach((name, values) -> {
                            LinkedHashSet<class_2960> filters = new LinkedHashSet<class_2960>();
                            if (values != null) {
                                for (String value : values) {
                                    class_2960 id = class_2960.method_12829((String)value);
                                    if (id == null || !class_7923.field_41175.method_10250(id)) continue;
                                    filters.add(id);
                                }
                            }
                            if (!filters.isEmpty()) {
                                result.put(name.toLowerCase(Locale.ROOT), Set.copyOf(filters));
                            }
                        });
                    }
                    regionVisibleBlockFilters = Map.copyOf(result);
                }
                finally {
                    if (reader != null) {
                        ((Reader)reader).close();
                    }
                }
            }
            catch (Exception e) {
                RenderHideClient.LOGGER.error("Could not load region visible block filters", (Throwable)e);
            }
        }
        visibleEntityFilters = RegionManager.loadIdList(ENTITY_FILTER_CONFIG, true);
        regionVisibleEntityFilters = RegionManager.loadRegionIdMap(REGION_ENTITY_FILTER_CONFIG, true);
        if (Files.exists(OPACITY_CONFIG, new LinkOption[0])) {
            try {
                reader = Files.newBufferedReader(OPACITY_CONFIG);
                try {
                    loaded = (Float)GSON.fromJson((Reader)reader, Float.class);
                    if (loaded != null) {
                        hiddenBlockOpacity = Math.max(0.0f, Math.min(1.0f, ((Float)loaded).floatValue()));
                    }
                }
                finally {
                    if (reader != null) {
                        ((Reader)reader).close();
                    }
                }
            }
            catch (Exception e) {
                RenderHideClient.LOGGER.error("Could not load hidden block opacity", (Throwable)e);
            }
        }
    }

    public static void updateDimension(class_310 client) {
        activeDimension = client.field_1687 == null ? "" : client.field_1687.method_27983().method_29177().toString();
    }

    public static boolean isHidden(class_2338 pos, class_2680 state) {
        if (!globallyEnabled) {
            return false;
        }
        if (RegionManager.isVisibleAt(pos, state)) {
            return false;
        }
        String dimension = activeDimension;
        boolean inside = false;
        for (HiddenRegion region : snapshot) {
            if (!region.contains(pos, dimension)) continue;
            inside = true;
        }
        return inside;
    }

    public static boolean shouldImproveVisibleBlockLighting(class_2338 pos) {
        if (!globallyEnabled) {
            return false;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null) {
            return false;
        }
        class_2680 state = client.field_1687.method_8320(pos);
        String dimension = activeDimension;
        for (HiddenRegion region : snapshot) {
            if (!region.contains(pos, dimension) || !RegionManager.isVisibleFilterState(state, visibleBlockFilters) && !RegionManager.isVisibleFilterState(state, RegionManager.regionFilters(region.name()))) continue;
            return true;
        }
        return false;
    }

    public static boolean isInsideActiveRegion(class_2338 pos) {
        if (!globallyEnabled) {
            return false;
        }
        String dimension = activeDimension;
        for (HiddenRegion region : snapshot) {
            if (!region.contains(pos, dimension)) continue;
            return true;
        }
        return false;
    }

    public static boolean isVirtualLightAffected(class_2338 origin) {
        if (!virtualLightEnabled || !globallyEnabled) {
            return false;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null) {
            return false;
        }
        if (RegionManager.isInsideActiveRegion(origin)) {
            return true;
        }
        for (class_2350 direction : class_2350.values()) {
            class_2338 neighbor = origin.method_10093(direction);
            if (!RegionManager.isHidden(neighbor, client.field_1687.method_8320(neighbor))) continue;
            return true;
        }
        return false;
    }

    public static boolean isVirtualLightAffected(class_2338 origin, class_2350 face) {
        if (!virtualLightEnabled || !globallyEnabled) {
            return false;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null) {
            return false;
        }
        if (RegionManager.isInsideActiveRegion(origin)) {
            return true;
        }
        if (face == null) {
            return RegionManager.isVirtualLightAffected(origin);
        }
        class_2338 neighbor = origin.method_10093(face);
        return RegionManager.isHidden(neighbor, client.field_1687.method_8320(neighbor));
    }

    public static int virtualLightLevel(class_1944 type, class_2338 origin, int original) {
        if (!RegionManager.isVirtualLightAffected(origin)) {
            return original;
        }
        return RegionManager.calculateVirtualLightLevel(type, origin, null, original);
    }

    public static int virtualLightLevel(class_1944 type, class_2338 origin, class_2350 face, int original) {
        if (!RegionManager.isVirtualLightAffected(origin, face)) {
            return original;
        }
        return RegionManager.calculateVirtualLightLevel(type, origin, face, original);
    }

    private static int calculateVirtualLightLevel(class_1944 type, class_2338 origin, class_2350 face, int original) {
        class_2680 entryState;
        class_2338 entry;
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null) {
            return original;
        }
        int best = original;
        if (type == class_1944.field_9284 && face != null && RegionManager.isHidden(entry = origin.method_10093(face), entryState = client.field_1687.method_8320(entry))) {
            class_2338.class_2339 skyCursor = entry.method_25503();
            for (int distance = 0; distance <= 512; ++distance) {
                class_2680 state = client.field_1687.method_8320((class_2338)skyCursor);
                if (!RegionManager.isInsideActiveRegion((class_2338)skyCursor)) {
                    if (!state.method_26215() && state.method_26216() && state.method_26193() > 0) break;
                    int boundary = client.field_1687.method_8314(class_1944.field_9284, (class_2338)skyCursor);
                    int turnDecay = face == class_2350.field_11036 ? 0 : 1;
                    best = Math.max(best, boundary - turnDecay);
                    break;
                }
                if (RegionManager.isVisibleAt((class_2338)skyCursor, state) && !state.method_26215()) break;
                skyCursor.method_10098(class_2350.field_11036);
            }
        }
        block1: for (class_2350 direction : class_2350.values()) {
            class_2338.class_2339 cursor = origin.method_25503();
            int searchDistance = type == class_1944.field_9284 && direction == class_2350.field_11036 ? 512 : 15;
            for (int distance = 1; distance <= searchDistance; ++distance) {
                cursor.method_10098(direction);
                class_2680 state = client.field_1687.method_8320((class_2338)cursor);
                boolean inRegion = RegionManager.isInsideActiveRegion((class_2338)cursor);
                boolean filterVisible = RegionManager.isVisibleAt((class_2338)cursor, state);
                if (type == class_1944.field_9282) {
                    best = Math.max(best, state.method_26213() - distance + 1);
                }
                if (!inRegion) {
                    int boundary = client.field_1687.method_8314(type, (class_2338)cursor);
                    int decay = type == class_1944.field_9284 && direction == class_2350.field_11036 ? 0 : distance - 1;
                    best = Math.max(best, boundary - decay);
                    continue block1;
                }
                if (filterVisible && !state.method_26215()) continue block1;
            }
        }
        return Math.max(0, Math.min(15, best));
    }

    public static class_2338 getPos1() {
        return pos1;
    }

    public static class_2338 getPos2() {
        return pos2;
    }

    public static List<HiddenRegion> regions() {
        return snapshot;
    }

    public static boolean isGloballyEnabled() {
        return globallyEnabled;
    }

    public static boolean isVirtualLightEnabled() {
        return virtualLightEnabled;
    }

    public static float hiddenBlockOpacity() {
        return hiddenBlockOpacity;
    }

    public static boolean isGhostRendered(class_2338 pos, class_2680 state) {
        return hiddenBlockOpacity > 0.0f && RegionManager.isHidden(pos, state);
    }

    public static boolean isFullyHidden(class_2338 pos, class_2680 state) {
        return hiddenBlockOpacity <= 0.0f && RegionManager.isHidden(pos, state);
    }

    public static Set<class_2960> visibleBlockFilters() {
        return visibleBlockFilters;
    }

    public static Set<class_2960> regionFilters(String regionName) {
        return regionVisibleBlockFilters.getOrDefault(regionName.toLowerCase(Locale.ROOT), Set.of());
    }

    public static Set<class_2960> visibleEntityFilters() {
        return visibleEntityFilters;
    }

    public static Set<class_2960> regionEntityFilters(String regionName) {
        return regionVisibleEntityFilters.getOrDefault(regionName.toLowerCase(Locale.ROOT), Set.of());
    }

    public static boolean isEntityHidden(class_1297 entity) {
        if (!globallyEnabled) {
            return false;
        }
        class_2960 id = class_7923.field_41177.method_10221((Object)entity.method_5864());
        if (visibleEntityFilters.contains(id)) {
            return false;
        }
        class_2338 pos = class_2338.method_49638((class_2374)entity.method_5829().method_1005());
        boolean inside = false;
        for (HiddenRegion region : snapshot) {
            if (!region.contains(pos, activeDimension)) continue;
            inside = true;
            if (!RegionManager.regionEntityFilters(region.name()).contains(id)) continue;
            return false;
        }
        return inside;
    }

    public static boolean addVisibleEntityFilter(class_2960 id) {
        if (!class_7923.field_41177.method_10250(id)) {
            return false;
        }
        LinkedHashSet<class_2960> filters = new LinkedHashSet<class_2960>(visibleEntityFilters);
        if (!filters.add(id)) {
            return false;
        }
        visibleEntityFilters = Set.copyOf(filters);
        RegionManager.saveIdList(ENTITY_FILTER_CONFIG, visibleEntityFilters, "entity filters");
        RegionManager.message("Visible entity filter added: " + String.valueOf(id));
        return true;
    }

    public static boolean removeVisibleEntityFilter(class_2960 id) {
        LinkedHashSet<class_2960> filters = new LinkedHashSet<class_2960>(visibleEntityFilters);
        if (!filters.remove(id)) {
            return false;
        }
        visibleEntityFilters = Set.copyOf(filters);
        RegionManager.saveIdList(ENTITY_FILTER_CONFIG, visibleEntityFilters, "entity filters");
        RegionManager.message("Visible entity filter removed: " + String.valueOf(id));
        return true;
    }

    public static void clearVisibleEntityFilters() {
        int count = visibleEntityFilters.size();
        visibleEntityFilters = Set.of();
        RegionManager.saveIdList(ENTITY_FILTER_CONFIG, visibleEntityFilters, "entity filters");
        RegionManager.message("Visible entity filters cleared (" + count + ").");
    }

    public static int addVisibleFiltersBatch(List<class_2960> ids, boolean entities, String regionName) {
        boolean regional;
        boolean bl = regional = regionName != null;
        if (regional && RegionManager.find(regionName) == null) {
            return 0;
        }
        Set<class_2960> current = entities ? (regional ? RegionManager.regionEntityFilters(regionName) : visibleEntityFilters) : (regional ? RegionManager.regionFilters(regionName) : visibleBlockFilters);
        LinkedHashSet<class_2960> merged = new LinkedHashSet<class_2960>(current);
        for (class_2960 id : ids) {
            boolean valid = entities ? class_7923.field_41177.method_10250(id) : class_7923.field_41175.method_10250(id);
            if (!valid) continue;
            merged.add(id);
        }
        int added = merged.size() - current.size();
        if (added == 0) {
            return 0;
        }
        Set<class_2960> result = Set.copyOf(merged);
        if (entities && regional) {
            LinkedHashMap<String, Set<class_2960>> all = new LinkedHashMap<String, Set<class_2960>>(regionVisibleEntityFilters);
            all.put(regionName.toLowerCase(Locale.ROOT), result);
            regionVisibleEntityFilters = Map.copyOf(all);
            RegionManager.saveRegionIdMap(REGION_ENTITY_FILTER_CONFIG, regionVisibleEntityFilters, "region entity filters");
        } else if (entities) {
            visibleEntityFilters = result;
            RegionManager.saveIdList(ENTITY_FILTER_CONFIG, visibleEntityFilters, "entity filters");
        } else if (regional) {
            LinkedHashMap<String, Set<class_2960>> all = new LinkedHashMap<String, Set<class_2960>>(regionVisibleBlockFilters);
            all.put(regionName.toLowerCase(Locale.ROOT), result);
            regionVisibleBlockFilters = Map.copyOf(all);
            RegionManager.saveRegionFilters();
            RegionManager.refreshAll();
        } else {
            visibleBlockFilters = result;
            RegionManager.saveFilters();
            RegionManager.refreshAll();
        }
        return added;
    }

    public static boolean addRegionVisibleEntityFilter(String regionName, class_2960 id) {
        if (RegionManager.find(regionName) == null || !class_7923.field_41177.method_10250(id)) {
            return false;
        }
        String key = regionName.toLowerCase(Locale.ROOT);
        LinkedHashSet<class_2960> filters = new LinkedHashSet<class_2960>(RegionManager.regionEntityFilters(key));
        if (!filters.add(id)) {
            return false;
        }
        LinkedHashMap<String, Set<class_2960>> all = new LinkedHashMap<String, Set<class_2960>>(regionVisibleEntityFilters);
        all.put(key, Set.copyOf(filters));
        regionVisibleEntityFilters = Map.copyOf(all);
        RegionManager.saveRegionIdMap(REGION_ENTITY_FILTER_CONFIG, regionVisibleEntityFilters, "region entity filters");
        RegionManager.message("Region entity filter added to " + regionName + ": " + String.valueOf(id));
        return true;
    }

    public static boolean removeRegionVisibleEntityFilter(String regionName, class_2960 id) {
        String key = regionName.toLowerCase(Locale.ROOT);
        LinkedHashSet<class_2960> filters = new LinkedHashSet<class_2960>(RegionManager.regionEntityFilters(key));
        if (!filters.remove(id)) {
            return false;
        }
        LinkedHashMap<String, Set<class_2960>> all = new LinkedHashMap<String, Set<class_2960>>(regionVisibleEntityFilters);
        if (filters.isEmpty()) {
            all.remove(key);
        } else {
            all.put(key, Set.copyOf(filters));
        }
        regionVisibleEntityFilters = Map.copyOf(all);
        RegionManager.saveRegionIdMap(REGION_ENTITY_FILTER_CONFIG, regionVisibleEntityFilters, "region entity filters");
        RegionManager.message("Region entity filter removed from " + regionName + ": " + String.valueOf(id));
        return true;
    }

    public static void clearRegionVisibleEntityFilters(String regionName) {
        String key = regionName.toLowerCase(Locale.ROOT);
        int count = RegionManager.regionEntityFilters(key).size();
        LinkedHashMap<String, Set<class_2960>> all = new LinkedHashMap<String, Set<class_2960>>(regionVisibleEntityFilters);
        all.remove(key);
        regionVisibleEntityFilters = Map.copyOf(all);
        RegionManager.saveRegionIdMap(REGION_ENTITY_FILTER_CONFIG, regionVisibleEntityFilters, "region entity filters");
        RegionManager.message("Region entity filters cleared for " + regionName + " (" + count + ").");
    }

    public static boolean shouldRenderMovingPistonBlock(class_2338 pos, class_2680 pushedState) {
        return !RegionManager.isInsideActiveRegion(pos) || RegionManager.isVisibleAt(pos, pushedState);
    }

    private static boolean isVisibleAt(class_2338 pos, class_2680 state) {
        if (state.method_27852(class_2246.field_10008) && RegionManager.isMovingPushedBlockVisible(pos)) {
            return true;
        }
        if (RegionManager.isVisibleFilterState(state, visibleBlockFilters)) {
            return true;
        }
        String dimension = activeDimension;
        for (HiddenRegion region : snapshot) {
            if (!region.contains(pos, dimension) || !RegionManager.isVisibleFilterState(state, RegionManager.regionFilters(region.name()))) continue;
            return true;
        }
        return false;
    }

    private static boolean isMovingPushedBlockVisible(class_2338 pos) {
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null) {
            return false;
        }
        class_2586 blockEntity = client.field_1687.method_8321(pos);
        if (!(blockEntity instanceof class_2669)) {
            return false;
        }
        class_2669 piston = (class_2669)blockEntity;
        class_2680 pushed = piston.method_11495();
        if (RegionManager.isVisibleFilterState(pushed, visibleBlockFilters)) {
            return true;
        }
        String dimension = activeDimension;
        for (HiddenRegion region : snapshot) {
            if (!region.contains(pos, dimension) || !RegionManager.isVisibleFilterState(pushed, RegionManager.regionFilters(region.name()))) continue;
            return true;
        }
        return false;
    }

    private static boolean isVisibleFilterState(class_2680 state, Set<class_2960> filters) {
        class_2960 stateId = class_7923.field_41175.method_10221((Object)state.method_26204());
        if (filters.contains(stateId)) {
            return true;
        }
        if (state.method_27852(class_2246.field_10379)) {
            class_2764 type = (class_2764)state.method_11654((class_2769)class_2671.field_12224);
            return type == class_2764.field_12634 ? filters.contains(class_7923.field_41175.method_10221((Object)class_2246.field_10615)) : filters.contains(class_7923.field_41175.method_10221((Object)class_2246.field_10560));
        }
        if (state.method_27852(class_2246.field_10008)) {
            return filters.contains(class_7923.field_41175.method_10221((Object)class_2246.field_10560)) || filters.contains(class_7923.field_41175.method_10221((Object)class_2246.field_10615));
        }
        if (!stateId.method_12836().equals("minecraft")) {
            return false;
        }
        String family = RegionManager.linkedFilterFamily(stateId.method_12832());
        if (family == null) {
            return false;
        }
        for (class_2960 filter : filters) {
            if (!filter.method_12836().equals("minecraft") || !family.equals(RegionManager.linkedFilterFamily(filter.method_12832()))) continue;
            return true;
        }
        return false;
    }

    private static String linkedFilterFamily(String path) {
        if (path.equals("wall_torch") || path.equals("torch")) {
            return "torch:normal";
        }
        if (path.endsWith("_wall_hanging_sign")) {
            return "hanging_sign:" + path.substring(0, path.length() - "_wall_hanging_sign".length());
        }
        if (path.endsWith("_hanging_sign")) {
            return "hanging_sign:" + path.substring(0, path.length() - "_hanging_sign".length());
        }
        if (path.endsWith("_wall_sign")) {
            return "sign:" + path.substring(0, path.length() - "_wall_sign".length());
        }
        if (path.endsWith("_sign")) {
            return "sign:" + path.substring(0, path.length() - "_sign".length());
        }
        if (path.endsWith("_wall_torch")) {
            return "torch:" + path.substring(0, path.length() - "_wall_torch".length());
        }
        if (path.endsWith("_torch")) {
            return "torch:" + path.substring(0, path.length() - "_torch".length());
        }
        if (path.endsWith("_wall_banner")) {
            return "banner:" + path.substring(0, path.length() - "_wall_banner".length());
        }
        if (path.endsWith("_banner")) {
            return "banner:" + path.substring(0, path.length() - "_banner".length());
        }
        if (path.endsWith("_wall_skull")) {
            return "skull:" + path.substring(0, path.length() - "_wall_skull".length());
        }
        if (path.endsWith("_skull")) {
            return "skull:" + path.substring(0, path.length() - "_skull".length());
        }
        if (path.endsWith("_wall_head")) {
            return "head:" + path.substring(0, path.length() - "_wall_head".length());
        }
        if (path.endsWith("_head")) {
            return "head:" + path.substring(0, path.length() - "_head".length());
        }
        if (path.equals("kelp") || path.equals("kelp_plant")) {
            return "plant:kelp";
        }
        if (path.equals("cave_vines") || path.equals("cave_vines_plant")) {
            return "plant:cave_vines";
        }
        if (path.equals("twisting_vines") || path.equals("twisting_vines_plant")) {
            return "plant:twisting_vines";
        }
        if (path.equals("weeping_vines") || path.equals("weeping_vines_plant")) {
            return "plant:weeping_vines";
        }
        if (path.equals("cauldron") || path.equals("water_cauldron") || path.equals("lava_cauldron") || path.equals("powder_snow_cauldron")) {
            return "content:cauldron";
        }
        if (path.equals("water") || path.equals("bubble_column")) {
            return "content:water";
        }
        if (path.equals("cake") || path.equals("candle_cake") || path.endsWith("_candle_cake")) {
            return "content:cake";
        }
        return null;
    }

    public static boolean addVisibleBlockFilter(class_2960 id) {
        if (!class_7923.field_41175.method_10250(id)) {
            return false;
        }
        LinkedHashSet<class_2960> filters = new LinkedHashSet<class_2960>(visibleBlockFilters);
        if (!filters.add(id)) {
            return false;
        }
        visibleBlockFilters = Set.copyOf(filters);
        RegionManager.saveFilters();
        RegionManager.refreshAll();
        RegionManager.message("Visible block filter added: " + String.valueOf(id));
        return true;
    }

    public static boolean addRegionVisibleBlockFilter(String regionName, class_2960 id) {
        if (RegionManager.find(regionName) == null || !class_7923.field_41175.method_10250(id)) {
            return false;
        }
        String key = regionName.toLowerCase(Locale.ROOT);
        LinkedHashSet<class_2960> filters = new LinkedHashSet<class_2960>(RegionManager.regionFilters(key));
        if (!filters.add(id)) {
            return false;
        }
        LinkedHashMap<String, Set<class_2960>> all = new LinkedHashMap<String, Set<class_2960>>(regionVisibleBlockFilters);
        all.put(key, Set.copyOf(filters));
        regionVisibleBlockFilters = Map.copyOf(all);
        RegionManager.saveRegionFilters();
        RegionManager.refreshAll();
        RegionManager.message("Region filter added to " + regionName + ": " + String.valueOf(id));
        return true;
    }

    public static boolean removeRegionVisibleBlockFilter(String regionName, class_2960 id) {
        String key = regionName.toLowerCase(Locale.ROOT);
        LinkedHashSet<class_2960> filters = new LinkedHashSet<class_2960>(RegionManager.regionFilters(key));
        if (!filters.remove(id)) {
            return false;
        }
        LinkedHashMap<String, Set<class_2960>> all = new LinkedHashMap<String, Set<class_2960>>(regionVisibleBlockFilters);
        if (filters.isEmpty()) {
            all.remove(key);
        } else {
            all.put(key, Set.copyOf(filters));
        }
        regionVisibleBlockFilters = Map.copyOf(all);
        RegionManager.saveRegionFilters();
        RegionManager.refreshAll();
        RegionManager.message("Region filter removed from " + regionName + ": " + String.valueOf(id));
        return true;
    }

    public static void clearRegionVisibleBlockFilters(String regionName) {
        String key = regionName.toLowerCase(Locale.ROOT);
        LinkedHashMap<String, Set<class_2960>> all = new LinkedHashMap<String, Set<class_2960>>(regionVisibleBlockFilters);
        int count = RegionManager.regionFilters(key).size();
        all.remove(key);
        regionVisibleBlockFilters = Map.copyOf(all);
        RegionManager.saveRegionFilters();
        RegionManager.refreshAll();
        RegionManager.message("Region filters cleared for " + regionName + " (" + count + ").");
    }

    public static boolean removeVisibleBlockFilter(class_2960 id) {
        LinkedHashSet<class_2960> filters = new LinkedHashSet<class_2960>(visibleBlockFilters);
        if (!filters.remove(id)) {
            return false;
        }
        visibleBlockFilters = Set.copyOf(filters);
        RegionManager.saveFilters();
        RegionManager.refreshAll();
        RegionManager.message("Visible block filter removed: " + String.valueOf(id));
        return true;
    }

    public static void clearVisibleBlockFilters() {
        int count = visibleBlockFilters.size();
        visibleBlockFilters = Set.of();
        RegionManager.saveFilters();
        RegionManager.refreshAll();
        RegionManager.message("Visible block filters cleared (" + count + ").");
    }

    public static void setPos1(class_2338 pos) {
        pos1 = pos.method_10062();
        RegionManager.message("Position 1: " + RegionManager.format(pos1));
    }

    public static void setPos2(class_2338 pos) {
        pos2 = pos.method_10062();
        RegionManager.message("Position 2: " + RegionManager.format(pos2));
    }

    public static boolean add(String requestedName) {
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null || pos1 == null || pos2 == null) {
            RegionManager.message("Set both positions first.");
            return false;
        }
        String base = RegionManager.sanitizeName(requestedName == null || requestedName.isBlank() ? "region" : requestedName);
        String name = RegionManager.uniqueName(base);
        ArrayList<HiddenRegion> regions = new ArrayList<HiddenRegion>(snapshot);
        HiddenRegion region = HiddenRegion.create(name, client.field_1687.method_27983().method_29177().toString(), pos1, pos2);
        regions.add(region);
        RegionManager.publish(regions, region);
        RegionManager.clearSelection();
        RegionManager.message("Hidden region added: " + name + " (" + region.blockCount() + " blocks)");
        return true;
    }

    public static boolean remove(String name) {
        ArrayList<HiddenRegion> regions = new ArrayList<HiddenRegion>(snapshot);
        HiddenRegion found = RegionManager.find(name);
        if (found == null) {
            return false;
        }
        regions.remove(found);
        LinkedHashMap<String, Set<class_2960>> filters = new LinkedHashMap<String, Set<class_2960>>(regionVisibleBlockFilters);
        filters.remove(found.name().toLowerCase(Locale.ROOT));
        regionVisibleBlockFilters = Map.copyOf(filters);
        RegionManager.saveRegionFilters();
        LinkedHashMap<String, Set<class_2960>> entityFilters = new LinkedHashMap<String, Set<class_2960>>(regionVisibleEntityFilters);
        entityFilters.remove(found.name().toLowerCase(Locale.ROOT));
        regionVisibleEntityFilters = Map.copyOf(entityFilters);
        RegionManager.saveRegionIdMap(REGION_ENTITY_FILTER_CONFIG, regionVisibleEntityFilters, "region entity filters");
        RegionManager.publish(regions, found);
        RegionManager.message("Removed region: " + found.name());
        return true;
    }

    public static boolean toggle(String name) {
        ArrayList<HiddenRegion> regions = new ArrayList<HiddenRegion>(snapshot);
        for (int i = 0; i < regions.size(); ++i) {
            HiddenRegion current = regions.get(i);
            if (!current.name().equalsIgnoreCase(name)) continue;
            HiddenRegion changed = current.withEnabled(!current.enabled());
            regions.set(i, changed);
            RegionManager.publish(regions, current);
            RegionManager.message(changed.name() + ": " + (changed.enabled() ? "hidden" : "visible"));
            return true;
        }
        return false;
    }

    public static boolean updateCoordinates(String name, class_2338 first, class_2338 second) {
        ArrayList<HiddenRegion> regions = new ArrayList<HiddenRegion>(snapshot);
        for (int i = 0; i < regions.size(); ++i) {
            HiddenRegion current = regions.get(i);
            if (!current.name().equalsIgnoreCase(name)) continue;
            HiddenRegion changed = new HiddenRegion(current.name(), current.dimension(), Math.min(first.method_10263(), second.method_10263()), Math.min(first.method_10264(), second.method_10264()), Math.min(first.method_10260(), second.method_10260()), Math.max(first.method_10263(), second.method_10263()), Math.max(first.method_10264(), second.method_10264()), Math.max(first.method_10260(), second.method_10260()), current.enabled());
            regions.set(i, changed);
            snapshot = List.copyOf(regions);
            RegionManager.save();
            RegionManager.refreshAll();
            RegionManager.message("Region coordinates updated: " + current.name());
            return true;
        }
        return false;
    }

    public static void clear() {
        List<HiddenRegion> old = snapshot;
        snapshot = List.of();
        regionVisibleBlockFilters = Map.of();
        regionVisibleEntityFilters = Map.of();
        RegionManager.save();
        RegionManager.saveRegionFilters();
        RegionManager.saveRegionIdMap(REGION_ENTITY_FILTER_CONFIG, regionVisibleEntityFilters, "region entity filters");
        RegionManager.refreshAll();
        RegionManager.message("All hidden regions cleared (" + old.size() + ").");
    }

    public static void toggleGlobal() {
        globallyEnabled = !globallyEnabled;
        RegionManager.refreshAll();
        RegionManager.message("Render hiding: " + (globallyEnabled ? "ON" : "OFF"));
    }

    public static void toggleVirtualLight() {
        virtualLightEnabled = !virtualLightEnabled;
        RegionManager.refreshAll();
        RegionManager.message("Virtual light: " + (virtualLightEnabled ? "ON" : "OFF"));
    }

    public static void setHiddenBlockOpacity(double opacity) {
        float changed = (float)Math.round((float)Math.max(0.0, Math.min(1.0, opacity)) * 20.0f) / 20.0f;
        if (Math.abs(changed - hiddenBlockOpacity) < 1.0E-4f) {
            return;
        }
        hiddenBlockOpacity = changed;
        try {
            Files.createDirectories(OPACITY_CONFIG.getParent(), new FileAttribute[0]);
            try (BufferedWriter writer = Files.newBufferedWriter(OPACITY_CONFIG, new OpenOption[0]);){
                GSON.toJson((Object)Float.valueOf(hiddenBlockOpacity), (Appendable)writer);
            }
        }
        catch (Exception e) {
            RenderHideClient.LOGGER.error("Could not save hidden block opacity", (Throwable)e);
        }
        RegionManager.refreshAll();
    }

    public static void clearSelection() {
        pos1 = null;
        pos2 = null;
    }

    public static HiddenRegion find(String name) {
        for (HiddenRegion region : snapshot) {
            if (!region.name().equalsIgnoreCase(name)) continue;
            return region;
        }
        return null;
    }

    private static String uniqueName(String base) {
        if (RegionManager.find(base) == null) {
            return base;
        }
        int index = 2;
        while (RegionManager.find(base + "-" + index) != null) {
            ++index;
        }
        return base + "-" + index;
    }

    private static String sanitizeName(String name) {
        String cleaned = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]", "-");
        return cleaned.isBlank() ? "region" : cleaned;
    }

    private static void publish(List<HiddenRegion> regions, HiddenRegion changed) {
        snapshot = List.copyOf(regions);
        RegionManager.save();
        RegionManager.refresh(changed);
    }

    private static void save() {
        try {
            Files.createDirectories(CONFIG.getParent(), new FileAttribute[0]);
            try (BufferedWriter writer = Files.newBufferedWriter(CONFIG, new OpenOption[0]);){
                GSON.toJson(snapshot, REGION_LIST, (Appendable)writer);
            }
        }
        catch (Exception e) {
            RenderHideClient.LOGGER.error("Could not save hidden regions", (Throwable)e);
        }
    }

    private static void saveFilters() {
        try {
            Files.createDirectories(FILTER_CONFIG.getParent(), new FileAttribute[0]);
            try (BufferedWriter writer = Files.newBufferedWriter(FILTER_CONFIG, new OpenOption[0]);){
                GSON.toJson(visibleBlockFilters.stream().map(class_2960::toString).sorted().toList(), (Appendable)writer);
            }
        }
        catch (Exception e) {
            RenderHideClient.LOGGER.error("Could not save visible block filters", (Throwable)e);
        }
    }

    private static void saveRegionFilters() {
        try {
            Files.createDirectories(REGION_FILTER_CONFIG.getParent(), new FileAttribute[0]);
            LinkedHashMap serialized = new LinkedHashMap();
            regionVisibleBlockFilters.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> serialized.put((String)entry.getKey(), ((Set)entry.getValue()).stream().map(class_2960::toString).sorted().toList()));
            try (BufferedWriter writer = Files.newBufferedWriter(REGION_FILTER_CONFIG, new OpenOption[0]);){
                GSON.toJson(serialized, (Appendable)writer);
            }
        }
        catch (Exception e) {
            RenderHideClient.LOGGER.error("Could not save region visible block filters", (Throwable)e);
        }
    }

    private static Set<class_2960> loadIdList(Path path, boolean entityIds) {
        Set set;
        block11: {
            if (!Files.exists(path, new LinkOption[0])) {
                return Set.of();
            }
            BufferedReader reader = Files.newBufferedReader(path);
            try {
                List loaded = (List)GSON.fromJson((Reader)reader, new TypeToken<List<String>>(){}.getType());
                LinkedHashSet<class_2960> result = new LinkedHashSet<class_2960>();
                if (loaded != null) {
                    for (String value : loaded) {
                        class_2960 id = class_2960.method_12829((String)value);
                        if (id == null || !(entityIds ? class_7923.field_41177.method_10250(id) : class_7923.field_41175.method_10250(id))) continue;
                        result.add(id);
                    }
                }
                set = Set.copyOf(result);
                if (reader == null) break block11;
            }
            catch (Throwable throwable) {
                try {
                    if (reader != null) {
                        try {
                            ((Reader)reader).close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception e) {
                    RenderHideClient.LOGGER.error("Could not load filters from " + String.valueOf(path), (Throwable)e);
                    return Set.of();
                }
            }
            ((Reader)reader).close();
        }
        return set;
    }

    private static Map<String, Set<class_2960>> loadRegionIdMap(Path path, boolean entityIds) {
        Map<String, Set<class_2960>> map;
        block10: {
            if (!Files.exists(path, new LinkOption[0])) {
                return Map.of();
            }
            BufferedReader reader = Files.newBufferedReader(path);
            try {
                Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
                Map loaded = (Map)GSON.fromJson((Reader)reader, type);
                LinkedHashMap result = new LinkedHashMap();
                if (loaded != null) {
                    loaded.forEach((name, values) -> {
                        LinkedHashSet<class_2960> ids = new LinkedHashSet<class_2960>();
                        if (values != null) {
                            for (String value : values) {
                                class_2960 id = class_2960.method_12829((String)value);
                                if (id == null || !(entityIds ? class_7923.field_41177.method_10250(id) : class_7923.field_41175.method_10250(id))) continue;
                                ids.add(id);
                            }
                        }
                        if (!ids.isEmpty()) {
                            result.put(name.toLowerCase(Locale.ROOT), Set.copyOf(ids));
                        }
                    });
                }
                map = Map.copyOf(result);
                if (reader == null) break block10;
            }
            catch (Throwable throwable) {
                try {
                    if (reader != null) {
                        try {
                            ((Reader)reader).close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception e) {
                    RenderHideClient.LOGGER.error("Could not load region filters from " + String.valueOf(path), (Throwable)e);
                    return Map.of();
                }
            }
            ((Reader)reader).close();
        }
        return map;
    }

    private static void saveIdList(Path path, Set<class_2960> ids, String description) {
        try {
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
                GSON.toJson(ids.stream().map(class_2960::toString).sorted().toList(), (Appendable)writer);
            }
        }
        catch (Exception e) {
            RenderHideClient.LOGGER.error("Could not save " + description, (Throwable)e);
        }
    }

    private static void saveRegionIdMap(Path path, Map<String, Set<class_2960>> values, String description) {
        try {
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            LinkedHashMap serialized = new LinkedHashMap();
            values.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> serialized.put((String)entry.getKey(), ((Set)entry.getValue()).stream().map(class_2960::toString).sorted().toList()));
            try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
                GSON.toJson(serialized, (Appendable)writer);
            }
        }
        catch (Exception e) {
            RenderHideClient.LOGGER.error("Could not save " + description, (Throwable)e);
        }
    }

    private static void refresh(HiddenRegion region) {
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null) {
            return;
        }
        if (!region.dimension().equals(client.field_1687.method_27983().method_29177().toString())) {
            return;
        }
        client.field_1769.method_62219(class_4076.method_18675((int)(region.minX() - 1)), class_4076.method_18675((int)(region.minY() - 1)), class_4076.method_18675((int)(region.minZ() - 1)), class_4076.method_18675((int)(region.maxX() + 1)), class_4076.method_18675((int)(region.maxY() + 1)), class_4076.method_18675((int)(region.maxZ() + 1)));
    }

    public static void refreshAll() {
        class_310 client = class_310.method_1551();
        if (client.field_1769 != null) {
            client.field_1769.method_3279();
        }
    }

    private static String format(class_2338 pos) {
        return pos.method_10263() + ", " + pos.method_10264() + ", " + pos.method_10260();
    }

    public static void message(String value) {
        class_310 client = class_310.method_1551();
        if (client.field_1724 != null) {
            client.field_1724.method_7353((class_2561)class_2561.method_43470((String)("[Render Hide] " + value)), false);
        }
    }
}


