package dev.worldgen.world.folders.mixin;

import dev.worldgen.world.folders.config.ConfigHandler;
import dev.worldgen.world.folders.gui.screen.SelectFolderScreen;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.text.Text;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditWorldScreen.class)
public abstract class EditWorldScreenMixin extends Screen {
    protected EditWorldScreenMixin(Text title) {
        super(title);
    }

    @Shadow
    @Final
    private DirectionalLayoutWidget layout;

    @Shadow
    @Final
    private LevelStorage.Session storageSession;


    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/EmptyWidget;<init>(II)V", shift = At.Shift.BEFORE, ordinal = 1))
    public void worldFolders$addFolderButton(MinecraftClient client, LevelStorage.Session session, String worldName, BooleanConsumer callback, CallbackInfo ci) {
        var folderButton = ButtonWidget.builder(Text.translatable("world_folders.select_folder"), button -> client.setScreen(new SelectFolderScreen(this, selectedFolder -> {
            ConfigHandler.getAllFolders().forEach(folder -> folder.removeWorld(storageSession.getDirectoryName()));
            selectedFolder.addWorld(storageSession.getDirectoryName());
            ConfigHandler.write();
        }))).width(200).build();

        layout.add(folderButton);
    }
}
