package dev.worldgen.world.folders.gui.widget;

import dev.worldgen.world.folders.mixin.WorldEntryAccessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.text.Text;
import net.minecraft.world.level.storage.LevelSummary;

public class WrappedWorldEntry extends WorldListWidget.Entry {
    private final WorldListWidget.WorldEntry worldEntry;
    private final LevelSummary summary;
    public WrappedWorldEntry(WorldListWidget widget, LevelSummary summary) {
        this.worldEntry = WorldEntryAccessor.init(widget, widget, summary);
        this.summary = summary;
    }

    @Override
    public Text getNarration() {
        return this.worldEntry.getNarration();
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        this.worldEntry.render(context, index, y, x + 16, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.worldEntry.mouseClicked(mouseX - 32, mouseY, button);
    }

    @Override
    public void close() {
        this.worldEntry.close();
    }

    public LevelSummary summary() {
        return this.summary;
    }

    public WorldListWidget.WorldEntry worldEntry() {
        return this.worldEntry;
    }
}
