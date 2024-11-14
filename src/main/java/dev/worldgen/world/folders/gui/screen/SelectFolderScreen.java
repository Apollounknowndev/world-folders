package dev.worldgen.world.folders.gui.screen;

import dev.worldgen.world.folders.config.ConfigHandler;
import dev.worldgen.world.folders.config.FolderData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class SelectFolderScreen extends Screen {
    private final Screen parent;
    private final Consumer<FolderData> selectedCallback;
    @Nullable
    private FolderListWidget list;
    final ThreePartsLayoutWidget layoutWidget = new ThreePartsLayoutWidget(this);

    public SelectFolderScreen(Screen parent, Consumer<FolderData> selectedCallback) {
        super(Text.translatable("world_folders.select_folder"));
        this.parent = parent;
        this.selectedCallback = selectedCallback;
    }

    @Override
    protected void init() {
        this.layoutWidget.addHeader(this.title, this.textRenderer);
        this.list = this.layoutWidget.addBody(new FolderListWidget(this.client, this.width, this));
        this.layoutWidget.addFooter(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).width(200).build());
        this.layoutWidget.forEachChild(this::addDrawableChild);
        this.initTabNavigation();
    }

    @Override
    protected void initTabNavigation() {
        this.layoutWidget.refreshPositions();
        if (this.list != null) {
            this.list.position(this.width, this.layoutWidget);
        }
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Environment(value= EnvType.CLIENT)
    private class FolderListWidget extends ElementListWidget<FolderListEntry> {
        private FolderListWidget(MinecraftClient client, int width, SelectFolderScreen screen) {
            super(client, width, screen.layoutWidget.getContentHeight(), screen.layoutWidget.getHeaderHeight(), 25);
            ConfigHandler.getAllFolders().forEach(folder -> this.addEntry(new FolderListEntry(folder)));
        }

        @Override
        public int getRowWidth() {
            return 310;
        }

        @Override
        public void position(int width, ThreePartsLayoutWidget layout) {
            super.position(width, layout);
            int i = width / 2 - 155;
            this.children().forEach(child -> child.button.setX(i));
        }
    }

    @Environment(value=EnvType.CLIENT)
    private class FolderListEntry extends ElementListWidget.Entry<FolderListEntry> {
        private final ClickableWidget button;
        private final FolderData folder;

        private FolderListEntry(FolderData folder) {
            this.button = ButtonWidget.builder(folder.asText(), this::selectFolder).width(310).build();
            this.folder = folder;
        }

        private void selectFolder(ButtonWidget button) {
            selectedCallback.accept(this.folder);
            client.setScreen(parent);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.button.setY(y);
            this.button.render(context, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(this.button);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(this.button);
        }
    }
}
