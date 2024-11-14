package dev.worldgen.world.folders.mixin;

import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldListWidget.WorldEntry.class)
public interface WorldEntryAccessor {
    @Invoker("<init>")
    static WorldListWidget.WorldEntry init(WorldListWidget object, WorldListWidget levelList, LevelSummary level) {
        throw new IllegalStateException();
    }

    @Accessor("level")
    LevelSummary getLevel();
}
