package dev.worldgen.world.folders.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.world.folders.gui.screen.SelectFolderScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.GameTab.class)
public class GameTabMixin {
    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    private void addSetFolderButton(CreateWorldScreen screen, CallbackInfo ci, @Local(ordinal = 0) GridWidget.Adder adder) {
        //adder.add(ButtonWidget.builder(Text.translatable("world_folders.select_folder"), button -> client.setScreen(new SelectFolderScreen(this, storageSession))).width(210).build());
    }
}
