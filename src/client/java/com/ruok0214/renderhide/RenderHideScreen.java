/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11908
 *  net.minecraft.class_2338
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_332
 *  net.minecraft.class_342
 *  net.minecraft.class_357
 *  net.minecraft.class_364
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 *  net.minecraft.class_7923
 */
package com.ruok0214.renderhide;

import com.ruok0214.renderhide.HiddenRegion;
import com.ruok0214.renderhide.RegionManager;
import com.ruok0214.renderhide.SelectionOverlayRenderer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11908;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_357;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_7923;

@Environment(value=EnvType.CLIENT)
public final class RenderHideScreen
extends class_437 {
    private static final int ROWS = 6;
    private int regionPage;
    private int filterPage;
    private class_342 regionName;
    private class_342 filterId;
    private String rememberedRegionName = "region";
    private String rememberedFilterId = "";
    private class_2960 filterSuggestion;
    private boolean editingSelection;
    private String editingRegion;
    private boolean editingRegionFilters;
    private boolean editingGlobalBlockFilters;
    private boolean editingEntityFilters;
    private boolean entityFiltersForRegion;
    private int regionFilterPage;
    private final class_342[] coordinates = new class_342[6];

    public RenderHideScreen() {
        super((class_2561)class_2561.method_43470((String)"Render Hide"));
    }

    protected void method_25426() {
        if (this.editingEntityFilters) {
            this.initEntityFilterEditor();
            return;
        }
        if (this.editingGlobalBlockFilters || this.editingRegionFilters && this.editingRegion != null) {
            this.initRegionFilterEditor();
            return;
        }
        if (this.editingSelection || this.editingRegion != null) {
            this.initCoordinateEditor();
            return;
        }
        int panelWidth = Math.min(470, this.field_22789 - 20);
        int left = (this.field_22789 - panelWidth) / 2;
        int gap = 8;
        int columnWidth = (panelWidth - gap) / 2;
        this.method_37063(class_4185.method_46430((class_2561)RenderHideScreen.toggleText("Hiding", RegionManager.isGloballyEnabled()), b -> {
            RegionManager.toggleGlobal();
            this.rebuild();
        }).method_46434(left, 28, (panelWidth - 2 * gap) / 3, 20).method_46431());
        this.method_37063(class_4185.method_46430((class_2561)RenderHideScreen.toggleText("Virtual light", RegionManager.isVirtualLightEnabled()), b -> {
            RegionManager.toggleVirtualLight();
            this.rebuild();
        }).method_46434(left + (panelWidth + gap) / 3, 28, (panelWidth - 2 * gap) / 3, 20).method_46431());
        this.method_37063(class_4185.method_46430((class_2561)RenderHideScreen.toggleText("Outlines", SelectionOverlayRenderer.areSavedRegionsShown()), b -> {
            SelectionOverlayRenderer.toggleSavedRegions();
            this.rebuild();
        }).method_46434(left + 2 * (panelWidth + gap) / 3, 28, (panelWidth - 2 * gap) / 3, 20).method_46431());
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)("Global block filters (" + RegionManager.visibleBlockFilters().size() + ")")), b -> {
            this.editingGlobalBlockFilters = true;
            this.rememberedFilterId = "";
            this.regionFilterPage = 0;
            this.rebuild();
        }).method_46434(left, 55, columnWidth, 20).method_46431());
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)("Global entity filters (" + RegionManager.visibleEntityFilters().size() + ")")), b -> {
            this.editingEntityFilters = true;
            this.entityFiltersForRegion = false;
            this.rememberedFilterId = "";
            this.regionFilterPage = 0;
            this.rebuild();
        }).method_46434(left + columnWidth + gap, 55, columnWidth, 20).method_46431());
        this.method_37063(new OpacitySlider(left, 81, panelWidth, 20));
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"Edit coords"), b -> {
            this.editingSelection = true;
            this.rebuild();
        }).method_46434(left, 107, panelWidth, 20).method_46431());
        this.regionName = new class_342(this.field_22793, left, 143, panelWidth - 64, 20, (class_2561)class_2561.method_43470((String)"Region name"));
        this.regionName.method_1880(32);
        this.regionName.method_47404((class_2561)class_2561.method_43470((String)"region name"));
        this.regionName.method_1852(this.rememberedRegionName);
        this.regionName.method_1863(value -> {
            this.rememberedRegionName = value;
        });
        this.method_37063(this.regionName);
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"Add"), b -> {
            if (RegionManager.add(this.regionName.method_1882())) {
                this.rebuild();
            }
        }).method_46434(left + panelWidth - 60, 143, 60, 20).method_46431());
        List<HiddenRegion> regions = RegionManager.regions();
        this.regionPage = RenderHideScreen.clampPage(this.regionPage, regions.size());
        int regionStart = this.regionPage * 6;
        for (int row = 0; row < 6 && regionStart + row < regions.size(); ++row) {
            HiddenRegion region = regions.get(regionStart + row);
            int y = 170 + row * 23;
            this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)((region.enabled() ? "ON  " : "OFF ") + region.name())), b -> {
                this.editingRegion = region.name();
                this.rebuild();
            }).method_46434(left, y, panelWidth, 20).method_46431());
        }
        int navY = 311;
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"\u2039"), b -> {
            if (this.regionPage > 0) {
                --this.regionPage;
                this.rebuild();
            }
        }).method_46434(left, navY, 28, 20).method_46431());
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"\u203a"), b -> {
            if ((this.regionPage + 1) * 6 < regions.size()) {
                ++this.regionPage;
                this.rebuild();
            }
        }).method_46434(left + panelWidth - 28, navY, 28, 20).method_46431());
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"Done"), b -> this.method_25419()).method_46434((this.field_22789 - 120) / 2, 338, 120, 20).method_46431());
    }

    private void updateFilterSuggestion(String value) {
        this.rememberedFilterId = value;
        String query = value.trim().toLowerCase(Locale.ROOT);
        this.filterSuggestion = null;
        this.filterId.method_1887(null);
        if (query.isEmpty()) {
            return;
        }
        boolean qualified = query.indexOf(58) >= 0;
        Set ids = this.editingEntityFilters ? class_7923.field_41177.method_10235() : class_7923.field_41175.method_10235();
        for (class_2960 id : ids.stream().sorted().toList()) {
            String candidate = qualified ? id.toString() : id.method_12832();
            if (!candidate.startsWith(query) || candidate.equals(query)) continue;
            this.filterSuggestion = id;
            this.filterId.method_1887(candidate.substring(query.length()));
            return;
        }
    }

    public boolean method_25404(class_11908 input) {
        if (!this.editingSelection && this.filterId != null && this.filterId.method_25370()) {
            if (input.comp_4795() == 258 && this.filterSuggestion != null) {
                this.filterId.method_1852(this.filterSuggestion.toString());
                return true;
            }
            if (input.comp_4795() == 257 || input.comp_4795() == 335) {
                this.addFilter();
                return true;
            }
        }
        return super.method_25404(input);
    }

    private void initCoordinateEditor() {
        class_2338 second;
        class_2338 first;
        HiddenRegion region;
        int panelWidth = Math.min(390, this.field_22789 - 20);
        int left = (this.field_22789 - panelWidth) / 2;
        HiddenRegion hiddenRegion = region = this.editingRegion == null ? null : RegionManager.find(this.editingRegion);
        if (this.editingSelection) {
            first = RegionManager.getPos1();
            second = RegionManager.getPos2();
        } else if (region != null) {
            first = new class_2338(region.minX(), region.minY(), region.minZ());
            second = new class_2338(region.maxX(), region.maxY(), region.maxZ());
        } else {
            this.editingRegion = null;
            this.rebuild();
            return;
        }
        int[] values = new int[]{first == null ? 0 : first.method_10263(), first == null ? 0 : first.method_10264(), first == null ? 0 : first.method_10260(), second == null ? 0 : second.method_10263(), second == null ? 0 : second.method_10264(), second == null ? 0 : second.method_10260()};
        String[] labels = new String[]{"X1", "Y1", "Z1", "X2", "Y2", "Z2"};
        for (int i = 0; i < 6; ++i) {
            int column = i % 3;
            int row = i / 3;
            int x = left + column * 130;
            int y = 82 + row * 48;
            this.coordinates[i] = new class_342(this.field_22793, x, y, 120, 20, (class_2561)class_2561.method_43470((String)labels[i]));
            this.coordinates[i].method_1880(12);
            this.coordinates[i].method_1852(Integer.toString(values[i]));
            this.coordinates[i].method_1890(value -> value.isEmpty() || value.equals("-") || value.matches("-?\\d+"));
            this.method_37063(this.coordinates[i]);
        }
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"Save coordinates"), b -> this.saveCoordinates()).method_46434(left, 187, panelWidth, 20).method_46431());
        if (region != null) {
            this.method_37063(class_4185.method_46430((class_2561)RenderHideScreen.toggleText("Region", region.enabled()), b -> {
                RegionManager.toggle(region.name());
                this.rebuild();
            }).method_46434(left, 214, 188, 20).method_46431());
            this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"Delete region"), b -> {
                RegionManager.remove(region.name());
                this.editingRegion = null;
                this.rebuild();
            }).method_46434(left + panelWidth - 188, 214, 188, 20).method_46431());
            this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)("Region filters (" + RegionManager.regionFilters(region.name()).size() + ")")), b -> {
                this.editingRegionFilters = true;
                this.rememberedFilterId = "";
                this.rebuild();
            }).method_46434(left, 241, panelWidth, 20).method_46431());
            this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)("Region entity filters (" + RegionManager.regionEntityFilters(region.name()).size() + ")")), b -> {
                this.editingEntityFilters = true;
                this.entityFiltersForRegion = true;
                this.rememberedFilterId = "";
                this.rebuild();
            }).method_46434(left, 268, panelWidth, 20).method_46431());
        } else {
            this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"Clear selection"), b -> {
                RegionManager.clearSelection();
                this.rebuild();
            }).method_46434(left, 214, panelWidth, 20).method_46431());
        }
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"Back"), b -> {
            this.editingSelection = false;
            this.editingRegion = null;
            this.rebuild();
        }).method_46434((this.field_22789 - 120) / 2, region == null ? 251 : 295, 120, 20).method_46431());
    }

    private void initEntityFilterEditor() {
        HiddenRegion region;
        HiddenRegion hiddenRegion = region = this.entityFiltersForRegion && this.editingRegion != null ? RegionManager.find(this.editingRegion) : null;
        if (this.entityFiltersForRegion && region == null) {
            this.editingEntityFilters = false;
            this.entityFiltersForRegion = false;
            this.editingRegion = null;
            this.rebuild();
            return;
        }
        int panelWidth = Math.min(390, this.field_22789 - 20);
        int left = (this.field_22789 - panelWidth) / 2;
        this.filterId = new class_342(this.field_22793, left, 68, panelWidth - 64, 20, (class_2561)class_2561.method_43470((String)"Entity ID"));
        this.filterId.method_1880(128);
        this.filterId.method_47404((class_2561)class_2561.method_43470((String)"minecraft:minecart"));
        this.filterId.method_1852(this.rememberedFilterId);
        this.filterId.method_1863(this::updateFilterSuggestion);
        this.method_37063(this.filterId);
        this.updateFilterSuggestion(this.rememberedFilterId);
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"Add"), b -> this.addFilter()).method_46434(left + panelWidth - 60, 68, 60, 20).method_46431());
        ArrayList<class_2960> filters = new ArrayList<class_2960>(this.entityFiltersForRegion ? RegionManager.regionEntityFilters(this.editingRegion) : RegionManager.visibleEntityFilters());
        filters.sort(Comparator.comparing(class_2960::toString));
        this.regionFilterPage = RenderHideScreen.clampPage(this.regionFilterPage, filters.size());
        int start = this.regionFilterPage * 6;
        for (int row = 0; row < 6 && start + row < filters.size(); ++row) {
            class_2960 id = (class_2960)filters.get(start + row);
            int y = 98 + row * 23;
            this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)id.toString()), b -> {}).method_46434(left, y, panelWidth - 27, 20).method_46431());
            this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"\u00d7"), b -> {
                if (this.entityFiltersForRegion) {
                    RegionManager.removeRegionVisibleEntityFilter(this.editingRegion, id);
                } else {
                    RegionManager.removeVisibleEntityFilter(id);
                }
                this.rebuild();
            }).method_46434(left + panelWidth - 24, y, 24, 20).method_46431());
        }
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"\u2039"), b -> {
            if (this.regionFilterPage > 0) {
                --this.regionFilterPage;
                this.rebuild();
            }
        }).method_46434(left, 239, 28, 20).method_46431());
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"Clear entity filters"), b -> {
            if (this.entityFiltersForRegion) {
                RegionManager.clearRegionVisibleEntityFilters(this.editingRegion);
            } else {
                RegionManager.clearVisibleEntityFilters();
            }
            this.rebuild();
        }).method_46434(left + 32, 239, panelWidth - 64, 20).method_46431());
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"\u203a"), b -> {
            if ((this.regionFilterPage + 1) * 6 < filters.size()) {
                ++this.regionFilterPage;
                this.rebuild();
            }
        }).method_46434(left + panelWidth - 28, 239, 28, 20).method_46431());
        this.addCopyPasteButtons(left, 266, panelWidth);
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)(this.entityFiltersForRegion ? "Back to region" : "Back")), b -> {
            this.editingEntityFilters = false;
            this.rememberedFilterId = "";
            if (!this.entityFiltersForRegion) {
                this.editingRegion = null;
            }
            this.entityFiltersForRegion = false;
            this.rebuild();
        }).method_46434((this.field_22789 - 140) / 2, 293, 140, 20).method_46431());
    }

    private void initRegionFilterEditor() {
        HiddenRegion region;
        HiddenRegion hiddenRegion = region = this.editingRegionFilters ? RegionManager.find(this.editingRegion) : null;
        if (this.editingRegionFilters && region == null) {
            this.editingRegionFilters = false;
            this.editingRegion = null;
            this.rebuild();
            return;
        }
        int panelWidth = Math.min(390, this.field_22789 - 20);
        int left = (this.field_22789 - panelWidth) / 2;
        this.filterId = new class_342(this.field_22793, left, 68, panelWidth - 64, 20, (class_2561)class_2561.method_43470((String)"Block ID"));
        this.filterId.method_1880(128);
        this.filterId.method_47404((class_2561)class_2561.method_43470((String)"minecraft:stone"));
        this.filterId.method_1852(this.rememberedFilterId);
        this.filterId.method_1863(this::updateFilterSuggestion);
        this.method_37063(this.filterId);
        this.updateFilterSuggestion(this.rememberedFilterId);
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"Add"), b -> this.addFilter()).method_46434(left + panelWidth - 60, 68, 60, 20).method_46431());
        ArrayList<class_2960> filters = new ArrayList<class_2960>(this.editingRegionFilters ? RegionManager.regionFilters(region.name()) : RegionManager.visibleBlockFilters());
        filters.sort(Comparator.comparing(class_2960::toString));
        this.regionFilterPage = RenderHideScreen.clampPage(this.regionFilterPage, filters.size());
        int start = this.regionFilterPage * 6;
        for (int row = 0; row < 6 && start + row < filters.size(); ++row) {
            class_2960 id = (class_2960)filters.get(start + row);
            int y = 98 + row * 23;
            this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)id.toString()), b -> {}).method_46434(left, y, panelWidth - 27, 20).method_46431());
            this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"\u00d7"), b -> {
                if (this.editingRegionFilters) {
                    RegionManager.removeRegionVisibleBlockFilter(region.name(), id);
                } else {
                    RegionManager.removeVisibleBlockFilter(id);
                }
                this.rebuild();
            }).method_46434(left + panelWidth - 24, y, 24, 20).method_46431());
        }
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"\u2039"), b -> {
            if (this.regionFilterPage > 0) {
                --this.regionFilterPage;
                this.rebuild();
            }
        }).method_46434(left, 239, 28, 20).method_46431());
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)(this.editingRegionFilters ? "Clear region filters" : "Clear global filters")), b -> {
            if (this.editingRegionFilters) {
                RegionManager.clearRegionVisibleBlockFilters(region.name());
            } else {
                RegionManager.clearVisibleBlockFilters();
            }
            this.rebuild();
        }).method_46434(left + 32, 239, panelWidth - 64, 20).method_46431());
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"\u203a"), b -> {
            if ((this.regionFilterPage + 1) * 6 < filters.size()) {
                ++this.regionFilterPage;
                this.rebuild();
            }
        }).method_46434(left + panelWidth - 28, 239, 28, 20).method_46431());
        this.addCopyPasteButtons(left, 266, panelWidth);
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)(this.editingRegionFilters ? "Back to region" : "Back")), b -> {
            this.editingRegionFilters = false;
            this.editingGlobalBlockFilters = false;
            this.rememberedFilterId = "";
            this.rebuild();
        }).method_46434((this.field_22789 - 140) / 2, 293, 140, 20).method_46431());
    }

    private void addCopyPasteButtons(int left, int y, int width) {
        int gap = 6;
        int buttonWidth = (width - gap) / 2;
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"Copy filters"), b -> this.copyCurrentFilters()).method_46434(left, y, buttonWidth, 20).method_46431());
        this.method_37063(class_4185.method_46430((class_2561)class_2561.method_43470((String)"Paste filters"), b -> this.pasteCurrentFilters()).method_46434(left + buttonWidth + gap, y, buttonWidth, 20).method_46431());
    }

    private Set<class_2960> currentFilters() {
        if (this.editingEntityFilters) {
            return this.entityFiltersForRegion ? RegionManager.regionEntityFilters(this.editingRegion) : RegionManager.visibleEntityFilters();
        }
        if (this.editingRegionFilters && this.editingRegion != null) {
            return RegionManager.regionFilters(this.editingRegion);
        }
        return RegionManager.visibleBlockFilters();
    }

    private void copyCurrentFilters() {
        boolean entities = this.editingEntityFilters;
        StringBuilder text = new StringBuilder("# Render Hide ").append(entities ? "entity" : "block").append(" filters\n");
        this.currentFilters().stream().sorted(Comparator.comparing(class_2960::toString)).forEach(id -> text.append(id).append('\n'));
        this.field_22787.field_1774.method_1455(text.toString());
        RegionManager.message("Copied " + this.currentFilters().size() + " " + (entities ? "entity" : "block") + " filters.");
    }

    private void pasteCurrentFilters() {
        boolean entities = this.editingEntityFilters;
        String clipboard = this.field_22787.field_1774.method_1460();
        if (clipboard == null || clipboard.isBlank()) {
            RegionManager.message("Clipboard is empty.");
            return;
        }
        String lower = clipboard.toLowerCase(Locale.ROOT);
        if (lower.contains("# render hide entity filters") && !entities || lower.contains("# render hide block filters") && entities) {
            RegionManager.message("Clipboard contains the wrong filter type.");
            return;
        }
        Set<class_2960> before = this.currentFilters();
        ArrayList<class_2960> valid = new ArrayList<class_2960>();
        int invalid = 0;
        int duplicates = 0;
        LinkedHashSet<class_2960> seen = new LinkedHashSet<class_2960>();
        for (String token : clipboard.split("[\\s,;]+")) {
            String value = token.trim();
            if (value.isEmpty() || value.startsWith("#") || value.equalsIgnoreCase("Render") || value.equalsIgnoreCase("Hide") || value.equalsIgnoreCase("block") || value.equalsIgnoreCase("entity") || value.equalsIgnoreCase("filters")) continue;
            class_2960 id = class_2960.method_12829((String)value);
            boolean exists = id != null && (entities ? class_7923.field_41177.method_10250(id) : class_7923.field_41175.method_10250(id));
            if (!exists) {
                ++invalid;
                continue;
            }
            if (before.contains(id) || !seen.add(id)) {
                ++duplicates;
                continue;
            }
            valid.add(id);
        }
        String region = this.editingEntityFilters ? (this.entityFiltersForRegion ? this.editingRegion : null) : (this.editingRegionFilters ? this.editingRegion : null);
        int added = RegionManager.addVisibleFiltersBatch(valid, entities, region);
        RegionManager.message("Pasted filters: " + added + " added, " + duplicates + " duplicates, " + invalid + " invalid.");
        if (added > 0) {
            this.rebuild();
        }
    }

    private void saveCoordinates() {
        try {
            int[] value = new int[6];
            for (int i = 0; i < 6; ++i) {
                value[i] = Integer.parseInt(this.coordinates[i].method_1882());
            }
            class_2338 first = new class_2338(value[0], value[1], value[2]);
            class_2338 second = new class_2338(value[3], value[4], value[5]);
            if (this.editingSelection) {
                RegionManager.setPos1(first);
                RegionManager.setPos2(second);
            } else if (this.editingRegion != null) {
                RegionManager.updateCoordinates(this.editingRegion, first, second);
            }
            this.rebuild();
        }
        catch (NumberFormatException e) {
            RegionManager.message("Enter all six coordinates as whole numbers.");
        }
    }

    private void addFilter() {
        boolean added;
        String value = this.filterId.method_1882().trim();
        class_2960 id = class_2960.method_12829((String)value);
        boolean valid = id != null && (this.editingEntityFilters ? class_7923.field_41177.method_10250(id) : class_7923.field_41175.method_10250(id));
        if (!valid) {
            RegionManager.message("Unknown " + (this.editingEntityFilters ? "entity" : "block") + ": " + value);
            return;
        }
        if (this.editingEntityFilters) {
            added = this.entityFiltersForRegion ? RegionManager.addRegionVisibleEntityFilter(this.editingRegion, id) : RegionManager.addVisibleEntityFilter(id);
        } else {
            boolean bl2 = added = this.editingRegionFilters && this.editingRegion != null ? RegionManager.addRegionVisibleBlockFilter(this.editingRegion, id) : RegionManager.addVisibleBlockFilter(id);
        }
        if (added) {
            this.rememberedFilterId = "";
            this.rebuild();
        }
    }

    private void rebuild() {
        this.method_37067();
        this.method_25426();
    }

    private static int clampPage(int page, int size) {
        return Math.max(0, Math.min(page, Math.max(0, (size - 1) / 6)));
    }

    private static class_2561 toggleText(String name, boolean enabled) {
        return class_2561.method_43470((String)(name + ": " + (enabled ? "ON" : "OFF")));
    }

    public void method_25394(class_332 context, int mouseX, int mouseY, float deltaTicks) {
        if (this.editingEntityFilters) {
            this.renderEntityFilterEditor(context, mouseX, mouseY, deltaTicks);
            return;
        }
        if (this.editingGlobalBlockFilters || this.editingRegionFilters && this.editingRegion != null) {
            this.renderRegionFilterEditor(context, mouseX, mouseY, deltaTicks);
            return;
        }
        if (this.editingSelection || this.editingRegion != null) {
            this.renderCoordinateEditor(context, mouseX, mouseY, deltaTicks);
            return;
        }
        int panelWidth = Math.min(470, this.field_22789 - 20);
        int left = (this.field_22789 - panelWidth) / 2;
        int gap = 8;
        int columnWidth = (panelWidth - gap) / 2;
        context.method_25294(left - 6, 8, left + panelWidth + 6, 367, -1341124592);
        context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 12, 0xFFFFFF);
        context.method_27535(this.field_22793, RenderHideScreen.positionText("Position 1", RegionManager.getPos1()), left, 80, 0xD0D0D0);
        context.method_27535(this.field_22793, RenderHideScreen.positionText("Position 2", RegionManager.getPos2()), left, 92, 0xD0D0D0);
        context.method_25303(this.field_22793, "Saved regions", left, 132, 0xFFFFFF);
        context.method_25300(this.field_22793, this.regionPage + 1 + "/" + Math.max(1, (RegionManager.regions().size() + 6 - 1) / 6), this.field_22789 / 2, 317, 0xA0A0A0);
        super.method_25394(context, mouseX, mouseY, deltaTicks);
    }

    private void renderCoordinateEditor(class_332 context, int mouseX, int mouseY, float deltaTicks) {
        int panelWidth = Math.min(390, this.field_22789 - 20);
        int left = (this.field_22789 - panelWidth) / 2;
        context.method_25294(left - 8, 18, left + panelWidth + 8, this.editingRegion == null ? 282 : 327, -1341124592);
        String heading = this.editingSelection ? "Current selection" : "Region: " + this.editingRegion;
        context.method_25300(this.field_22793, heading, this.field_22789 / 2, 29, 0xFFFFFF);
        context.method_25300(this.field_22793, this.editingSelection ? "Edit both corners of the red selection" : "Edit the saved region bounds", this.field_22789 / 2, 47, 0xB0B0B0);
        context.method_25303(this.field_22793, "Position 1", left, 67, 0xFFFFFF);
        context.method_25303(this.field_22793, "Position 2", left, 115, 0xFFFFFF);
        String[] labels = new String[]{"X", "Y", "Z", "X", "Y", "Z"};
        for (int i = 0; i < 6; ++i) {
            int x = left + i % 3 * 130;
            int y = i < 3 ? 72 : 120;
            context.method_25303(this.field_22793, labels[i], x + 2, y, 0xA0A0A0);
        }
        super.method_25394(context, mouseX, mouseY, deltaTicks);
    }

    private void renderRegionFilterEditor(class_332 context, int mouseX, int mouseY, float deltaTicks) {
        int panelWidth = Math.min(390, this.field_22789 - 20);
        int left = (this.field_22789 - panelWidth) / 2;
        int count = this.editingRegionFilters ? RegionManager.regionFilters(this.editingRegion).size() : RegionManager.visibleBlockFilters().size();
        context.method_25294(left - 8, 18, left + panelWidth + 8, 324, -1341124592);
        context.method_25300(this.field_22793, (String)(this.editingRegionFilters ? "Region block filters: " + this.editingRegion : "Global block filters"), this.field_22789 / 2, 29, 0xFFFFFF);
        context.method_25300(this.field_22793, this.editingRegionFilters ? "Added to global filters \u2022 Tab to complete" : "Visible exceptions \u2022 Tab to complete", this.field_22789 / 2, 47, 0xB0B0B0);
        context.method_25300(this.field_22793, this.regionFilterPage + 1 + "/" + Math.max(1, (count + 6 - 1) / 6), this.field_22789 / 2, 245, 0xA0A0A0);
        super.method_25394(context, mouseX, mouseY, deltaTicks);
    }

    private void renderEntityFilterEditor(class_332 context, int mouseX, int mouseY, float deltaTicks) {
        int panelWidth = Math.min(390, this.field_22789 - 20);
        int left = (this.field_22789 - panelWidth) / 2;
        int count = this.entityFiltersForRegion ? RegionManager.regionEntityFilters(this.editingRegion).size() : RegionManager.visibleEntityFilters().size();
        context.method_25294(left - 8, 18, left + panelWidth + 8, 324, -1341124592);
        String heading = this.entityFiltersForRegion ? "Region entity filters: " + this.editingRegion : "Global entity filters";
        context.method_25300(this.field_22793, heading, this.field_22789 / 2, 29, 0xFFFFFF);
        context.method_25300(this.field_22793, "Visible exceptions \u2022 Tab to complete", this.field_22789 / 2, 47, 0xB0B0B0);
        context.method_25300(this.field_22793, this.regionFilterPage + 1 + "/" + Math.max(1, (count + 6 - 1) / 6), this.field_22789 / 2, 245, 0xA0A0A0);
        super.method_25394(context, mouseX, mouseY, deltaTicks);
    }

    private class_2561 selectionText() {
        class_2338 a = RegionManager.getPos1();
        class_2338 b = RegionManager.getPos2();
        return class_2561.method_43470((String)("Selection: " + (a == null ? "pos1 unset" : RenderHideScreen.shortPos(a)) + " / " + (b == null ? "pos2 unset" : RenderHideScreen.shortPos(b))));
    }

    private static class_2561 positionText(String label, class_2338 pos) {
        return class_2561.method_43470((String)(label + ": " + (pos == null ? "unset" : RenderHideScreen.shortPos(pos))));
    }

    private static String shortPos(class_2338 pos) {
        return pos.method_10263() + "," + pos.method_10264() + "," + pos.method_10260();
    }

    @Environment(value=EnvType.CLIENT)
    private static final class OpacitySlider
    extends class_357 {
        private OpacitySlider(int x, int y, int width, int height) {
            super(x, y, width, height, (class_2561)class_2561.method_43473(), (double)RegionManager.hiddenBlockOpacity());
            this.method_25346();
        }

        protected void method_25346() {
            this.method_25355((class_2561)class_2561.method_43470((String)("Hidden block opacity: " + Math.round(this.field_22753 * 100.0) + "%")));
        }

        protected void method_25344() {
            RegionManager.setHiddenBlockOpacity(this.field_22753);
        }
    }
}

