package dev.worldgen.world.folders.mixin;

import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.world.level.storage.LevelSummary;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(WorldListWidget.class)
public interface WorldListWidgetAccessor {
    @Invoker("tryGet")
    List<LevelSummary> worldFolders$tryGet();

    @Invoker("show")
    void worldFolders$show(@Nullable List<LevelSummary> levels);
}
