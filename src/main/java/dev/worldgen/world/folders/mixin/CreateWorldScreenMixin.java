package dev.worldgen.world.folders.mixin;

import dev.worldgen.world.folders.access.NewWorldFolderAccessor;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin implements NewWorldFolderAccessor {
}
