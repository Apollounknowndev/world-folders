package dev.worldgen.world.folders.gui.screen;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import dev.worldgen.world.folders.config.ConfigHandler;
import dev.worldgen.world.folders.config.FolderData;
import dev.worldgen.world.folders.gui.widget.ColorPickerWidget;
import dev.worldgen.world.folders.util.RGBColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class EditFolderScreen extends Screen {
    private static final Text FOLDER_NAME_TEXT = Text.translatable("world_folders.edit_folder.folder_name").formatted(Formatting.GRAY);
    private static final Text COLOR_TEXT = Text.translatable("world_folders.edit_folder.color").formatted(Formatting.GRAY);

    private final MinecraftClient client;
    private final Screen parent;
    private final DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical().spacing(5);
    @Nullable private final FolderData folderData;
    private final TextFieldWidget nameTextBox;
    private final ColorPickerWidget colorPicker;
    protected EditFolderScreen(MinecraftClient client, Screen parent, @Nullable FolderData folderData, String name, String color) {
        super(Text.translatable("world_folders.edit_folder"));
        this.client = client;
        this.parent = parent;
        this.folderData = folderData;
        this.nameTextBox = new TextFieldWidget(this.client.textRenderer, 200, 20, Text.of(name));
        this.colorPicker = new ColorPickerWidget(this.client.textRenderer, 200, 20, Text.literal(color));
        this.setColor(color);
        this.buildLayout(name, color);
    }

    public static void create(MinecraftClient client, SelectWorldScreen selectScreen, String name, String color) {
        EditFolderScreen screen = new EditFolderScreen(client, selectScreen, null, name, color);
        client.setScreen(screen);
    }

    public static void create(MinecraftClient client, SelectWorldScreen selectScreen, FolderData folderData) {
        EditFolderScreen screen = new EditFolderScreen(client, selectScreen, folderData, folderData.name(), folderData.color().toString());
        client.setScreen(screen);
    }

    private void buildLayout(String name, String color) {
        TextRenderer textRenderer = this.client.textRenderer;

        this.layout.add(new EmptyWidget(200, 20));
        this.layout.add(new TextWidget(FOLDER_NAME_TEXT, textRenderer));
        this.layout.add(this.nameTextBox);
        this.nameTextBox.setText(name);

        this.layout.add(new TextWidget(COLOR_TEXT, textRenderer));
        this.layout.add(this.colorPicker);
        this.colorPicker.setText(color);
        this.colorPicker.setChangedListener(this::setColor);

        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(4);
        directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.createFolder()).width(98).build());
        directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close()).width(98).build());

        this.layout.add(new EmptyWidget(200, 20));
        this.layout.add(directionalLayoutWidget);
        this.layout.forEachChild(this::addDrawableChild);
    }

    private void setColor(String color) {
        JsonElement json = color.isEmpty() ? new JsonPrimitive("") : JsonParser.parseString(String.format("\"%s\"", color));
        var result = RGBColor.CODEC.parse(JsonOps.INSTANCE, json).result();
        this.colorPicker.setColor(result.map(RGBColor::rgb).orElse(0));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 16777215);
    }

    @Override
    protected void init() {
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        SimplePositioningWidget.setPos(this.layout, this.getNavigationFocus());
    }

    public void createFolder() {
        if (folderData == null) {
            ConfigHandler.addFolder(this.nameTextBox.getText(), this.colorPicker.getColor());
        } else {
            ConfigHandler.setFolder(this.folderData, this.nameTextBox.getText(), this.colorPicker.getColor());

        }
        this.close();
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
