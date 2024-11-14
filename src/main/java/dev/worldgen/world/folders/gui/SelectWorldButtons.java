package dev.worldgen.world.folders.gui;

import dev.worldgen.world.folders.config.ConfigHandler;
import dev.worldgen.world.folders.config.FolderData;
import dev.worldgen.world.folders.gui.screen.EditFolderScreen;
import dev.worldgen.world.folders.gui.widget.FolderEntry;
import dev.worldgen.world.folders.gui.widget.WrappedWorldEntry;
import dev.worldgen.world.folders.util.RGBColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.level.storage.LevelSummary;

import java.util.List;
import java.util.Random;

public class SelectWorldButtons {
    private WorldListWidget list;
    private ButtonWidget deleteButton;
    private ButtonWidget editButton;
    private ButtonWidget recreateButton;
    public void init(List<ClickableWidget> widgets, MinecraftClient client, SelectWorldScreen selectScreen, int width, int height) {
        this.list = (WorldListWidget) widgets.getLast();

        widgets.add(ButtonWidget.builder(Text.translatable("selectWorld.create"), button -> CreateWorldScreen.show(client, selectScreen)).dimensions(width / 2 + 4, height - 52, 150, 20).build());
        widgets.add(ButtonWidget.builder(Text.translatable("world_folders.create_new_folder"), button -> EditFolderScreen.create(client, selectScreen, "New Folder", createRandomColor())).dimensions(width / 2 - 154, height - 52, 150, 20).build());

        this.editButton = add(widgets, ButtonWidget.builder(Text.translatable("selectWorld.edit"), button -> {
            list.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::edit);

            WorldListWidget.Entry entry = list.getSelectedOrNull();
            if (entry instanceof FolderEntry folderEntry) {
                EditFolderScreen.create(client, selectScreen, folderEntry.folderData());
            }
        }).dimensions(width / 2 - 154, height - 28, 72, 20).build());
        this.deleteButton = add(widgets, ButtonWidget.builder(Text.translatable("selectWorld.delete"), button -> {
            list.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::deleteIfConfirmed);

            WorldListWidget.Entry entry = list.getSelectedOrNull();
            if (entry instanceof FolderEntry folderEntry) {
                confirmFolderDeletion(client, selectScreen, folderEntry.folderData());
            }
        }).dimensions(width / 2 - 76, height - 28, 72, 20).build());
        this.recreateButton = add(widgets, ButtonWidget.builder(Text.translatable("selectWorld.recreate"), button -> list.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::recreate)).dimensions(width / 2 + 4, height - 28, 72, 20).build());
        widgets.add(ButtonWidget.builder(ScreenTexts.BACK, button -> client.setScreen(null)).dimensions(width / 2 + 82, height - 28, 72, 20).build());
    }

    private static String createRandomColor() {
        Random random = new Random();
        return new RGBColor(random.nextInt(16777216)).toString();
    }

    private void confirmFolderDeletion(MinecraftClient client, SelectWorldScreen screen, FolderData folderData) {
        client.setScreen(new ConfirmScreen(
            confirmed -> {
                if (confirmed) {
                    client.setScreen(new ProgressScreen(true));
                    ConfigHandler.deleteFolder(folderData);
                }
                client.setScreen(screen);
            },
            Text.translatable("world_folders.delete_folder.title"),
            Text.translatableWithFallback("world_folders.delete_folder.message", "'%s' will be removed and all worlds within it will be moved to the unsorted list.", folderData.name()),
            Text.translatable("selectWorld.deleteButton"),
            ScreenTexts.CANCEL
        ));

    }

    private ButtonWidget add(List<ClickableWidget> widgets, ButtonWidget button) {
        widgets.add(button);
        return button;
    }

    public void update() {
        if (list == null) return;
        WorldListWidget.Entry entry = list.getSelectedOrNull();
        if (entry == null) return;

        if (entry instanceof FolderEntry folderEntry) {
            boolean enabled = folderEntry.folderData() != FolderData.UNSORTED;
            this.editButton.active = enabled;
            this.recreateButton.active = false;
            this.deleteButton.active = enabled;
        }

        if (entry instanceof WrappedWorldEntry wrappedEntry) {
            LevelSummary summary = wrappedEntry.summary();
            this.editButton.active = summary.isEditable();
            this.recreateButton.active = summary.isRecreatable();
            this.deleteButton.active = summary.isDeletable();
        }
    }
}