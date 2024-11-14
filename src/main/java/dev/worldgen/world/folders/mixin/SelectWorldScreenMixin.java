package dev.worldgen.world.folders.mixin;

import dev.worldgen.world.folders.access.SelectWorldButtonsAccessor;
import dev.worldgen.world.folders.gui.SelectWorldButtons;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SelectWorldScreen.class)
public class SelectWorldScreenMixin implements SelectWorldButtonsAccessor {
    @Unique
    SelectWorldButtons buttons;

    @Override
    public SelectWorldButtons get() {
        return buttons;
    }

    @Override
    public void set(SelectWorldButtons buttons) {
        this.buttons = buttons;
    }

    @Inject(method = "worldSelected", at = @At("TAIL"))
    private void worldFolders$fixButtons(LevelSummary levelSummary, CallbackInfo ci) {
        buttons.update();
    }
}
