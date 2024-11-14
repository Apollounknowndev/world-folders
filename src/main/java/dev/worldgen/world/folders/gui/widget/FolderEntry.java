package dev.worldgen.world.folders.gui.widget;

import dev.worldgen.world.folders.config.FolderData;
import dev.worldgen.world.folders.mixin.WorldListWidgetAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;

public class FolderEntry extends WorldListWidget.Entry {
    private static final Identifier SELECTED_TEXTURE = Identifier.ofVanilla("widget/checkbox_selected");
    private static final Identifier TEXTURE = Identifier.ofVanilla("widget/checkbox");

    private final MinecraftClient client;
    private final WorldListWidget list;
    private final FolderData folderData;
    private final String name;
    private final int rgb;
    private final Text entryText;

    private boolean selected;
    private long time;
    public FolderEntry(FolderData folderData, WorldListWidget list, int entries, boolean selected) {
        this.client = MinecraftClient.getInstance();
        this.list = list;
        this.folderData = folderData;
        this.name = folderData.name();
        this.entryText = entries == 1 ? Text.translatable("world_folders.folder_entry.single") : Text.translatable("world_folders.folder_entry.multiple", entries);
        this.rgb = FolderEntry.convert(folderData.color().rgb());
        this.selected = selected;
    }

    private static int convert(int rgba) {
        float r = (rgba >> 16 & 0xFF) / 255.0f;
        float g = (rgba >> 8 & 0xFF) / 255.0f;
        float b = (rgba & 0xFF) / 255.0f;
        return ColorHelper.Argb.fromFloats(1, r, g, b);
    }

    public FolderData folderData() {
        return this.folderData;
    }

    public boolean selected() {
        return this.selected;
    }

    @Override
    public Text getNarration() {
        return Text.translatable("world_folders.folder_entry"+ this.name);
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        context.fill(x, y, x + 4, y + 32, this.rgb);

        context.drawGuiTexture(this.selected ? SELECTED_TEXTURE : TEXTURE, x + 8, y + 6, 20, 20);

        context.drawText(this.client.textRenderer, this.name, x + 32 + 3, y + 4, Colors.WHITE, false);
        context.drawText(this.client.textRenderer, this.entryText, x + 32 + 3, y + this.client.textRenderer.fontHeight + 6, Colors.LIGHT_GRAY, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX - list.getRowLeft() <= 32.0 || Util.getMeasuringTimeMs() - this.time < 250L) {
            this.selected = !this.selected;
            this.refreshWorldList();
            return true;
        }
        this.time = Util.getMeasuringTimeMs();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void refreshWorldList() {
        WorldListWidgetAccessor accessor = (WorldListWidgetAccessor)this.list;
        accessor.worldFolders$show(accessor.worldFolders$tryGet());
    }
}
