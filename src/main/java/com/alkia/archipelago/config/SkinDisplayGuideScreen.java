package com.alkia.archipelago.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;
// Skin guide display skin, made this because it was getting super confusing and people wanted a guide
// (TBH I also need to reference this sometimes)
// Will hopefully be irrelevant whenever archi gets serverside support (if it does)
public class SkinDisplayGuideScreen extends Screen {
    private final Screen parent;
    private int page = 0;
    private static final int TEXT_TOP = 41;
    private static final int COLOR_HEADER  = 0xFFFF55;
    private static final int COLOR_LABEL   = 0xFFFFFF;
    private static final int COLOR_VALUE   = 0xAAAAAA;
    // Every line of the guide in order. Easy to add to when peeps request more stuff to add to the screen.
    private static final String[][] LINES = {
            {"[ Block Setup ]"},
            {"Item Frame", "Holds your Skin Token item"},
            {"Main Note Block", "Directly behind the item frame"},
            {"Activator Note Block", "Directly below the main note block"},
            {"Depth Note Block", "Directly below the activator block"},
            {"Height Note Block", "Directly below the depth block"},
            {"Glow Item Frame", "Makes the Pokemon shiny"},
            {"[ Main Note Block ]"},
            {"Note (0-24)", "Selects the pose / animation"},
            {"Instrument controls Mega form:"},
            {"  BIT", "Mega form 1  (Emerald block below)"},
            {"  XYLOPHONE", "Mega form 2  (Bone block below)"},
            {"  COW_BELL", "Mega form 3  (Soul sand below)"},
            {"  (anything else)", "No mega"},
            {"[ Activator Note Block - Attack Animations ]"},
            {"  BASS", "Physical attack  (Wood below)"},
            {"  FLUTE", "Special attack   (Clay below)"},
            {"  SNARE", "Status move      (Sand/Gravel below)"},
            {"  HAT", "Recoil           (Glass below)"},
            {"  BELL", "Cry              (Gold block below)"},
            {"  (no note block)", "No attack animation"},
            {"[ Depth Note Block - Evolution / Form ]"},
            {"Note (0-24)", "Selects the form index"},
            {"Instrument controls evolution depth:"},
            {"  IRON_XYLOPHONE", "Depth 1  (Iron block below)"},
            {"  BELL", "Depth 2  (Gold block below)"},
            {"  CHIME", "Depth 3  (Packed ice below)"},
            {"  BIT", "Depth 4  (Emerald block below)"},
            {"  COW_BELL", "Depth 5  (Soul sand below)"},
            {"  XYLOPHONE", "Depth 6  (Bone block below)"},
            {"  GUITAR", "Depth 7  (Wool below)"},
            {"  FLUTE", "Depth 8  (Clay below)"},
            {"  BASS", "Depth 9  (Wood below)"},
            {"  SNARE", "Depth 10 (Sand/Gravel below)"},
            {"  HAT", "Depth 11 (Glass below)"},
            {"  BASEDRUM", "Depth 12 (Stone below)"},
            {"  (no note block)", "Uses item below main block for depth instead"},
            {"[ Evolution Depth Fallback (no depth note block) ]"},
            {"  Iron Block", "Depth 1"},
            {"  Gold Block", "Depth 2"},
            {"  Diamond Block", "Depth 3"},
            {"[ Height Note Block - Height Offset ]"},
            {"Placed directly below the depth note block."},
            {"Note (0-24)", "Each note = +1 block of height (max +24)"},
            {"Note 0", "Default height (no offset)"},
            {"Note 1", "+1 block"},
            {"Note 4", "+4 blocks"},
            {"Note 12", "+12 blocks"},
            {"Note 24", "+24 blocks"},
            {"(no note block)", "No height offset applied"},
    };
    private final List<Integer> pageStarts = new ArrayList<>();
    public SkinDisplayGuideScreen(Screen parent) {
        super(Component.literal("Skin Display Guide"));
        this.parent = parent;
    }
    private static boolean isHeader(String[] line) {
        return line.length == 1 && line[0].startsWith("[");
    }
    @Override
    protected void init() {
        // Split the lines into however many pages actually fit
        pageStarts.clear();
        int limit = this.height - 40;
        int y = limit;
        for (int i = 0; i < LINES.length; i++) {
            int h = isHeader(LINES[i]) ? 20 : 10;
            int need = isHeader(LINES[i]) ? h + 10 : h;
            if (y + need > limit) {
                pageStarts.add(i);
                y = TEXT_TOP;
                h = isHeader(LINES[i]) ? 12 : 10;
            }
            y += h;
        }
        if (page >= pageStarts.size()) page = pageStarts.size() - 1;
        int centerX = this.width / 2;
        int bottomY = this.height - 30;
        this.addRenderableWidget(Button.builder(Component.literal("< Prev"), b -> {
            if (page > 0) page--;
        }).bounds(centerX - 120, bottomY, 50, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Next >"), b -> {
            if (page < pageStarts.size() - 1) page++;
        }).bounds(centerX + 70, bottomY, 50, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Back"), b -> {
            if (this.minecraft != null) this.minecraft.setScreen(parent);
        }).bounds(centerX - 25, bottomY, 50, 20).build());
    }
    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        super.render(g, mouseX, mouseY, delta);
        int cx = this.width / 2;
        g.drawCenteredString(this.font, "Skin Display Guide", cx, 15, COLOR_HEADER);
        g.drawCenteredString(this.font, "Page " + (page + 1) + " / " + pageStarts.size(), cx, 25, COLOR_VALUE);
        int end = page + 1 < pageStarts.size() ? pageStarts.get(page + 1) : LINES.length;
        int y = TEXT_TOP;
        for (int i = pageStarts.get(page); i < end; i++) {
            String[] line = LINES[i];
            if (isHeader(line)) {
                if (y != TEXT_TOP) y += 8;
                g.drawString(this.font, line[0], 10, y, COLOR_HEADER);
                y += 12;
            } else if (line.length == 1) {
                g.drawString(this.font, line[0], 10, y, COLOR_VALUE);
                y += 10;
            } else {
                g.drawString(this.font, line[0], 10, y, COLOR_LABEL);
                g.drawString(this.font, line[1], 120, y, COLOR_VALUE);
                y += 10;
            }
        }
    }
    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(parent);
    }
}
