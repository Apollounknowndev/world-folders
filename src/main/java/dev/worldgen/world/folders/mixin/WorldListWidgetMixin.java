package dev.worldgen.world.folders.mixin;

import dev.worldgen.world.folders.config.ConfigHandler;
import dev.worldgen.world.folders.config.FolderData;
import dev.worldgen.world.folders.gui.widget.FolderEntry;
import dev.worldgen.world.folders.gui.widget.WrappedWorldEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(WorldListWidget.class)
public abstract class WorldListWidgetMixin extends AlwaysSelectedEntryListWidget<WorldListWidget.Entry> {
	@Unique
	private final Map<FolderData, Boolean> selectedFolders = new HashMap<>();

	public WorldListWidgetMixin(MinecraftClient minecraftClient, int i, int j, int k, int l) {
		super(minecraftClient, i, j, k, l);
	}

	@Inject(method = "getSelectedAsOptional", at = @At("HEAD"), cancellable = true)
	private void worldFolders$fixWorldButtons(CallbackInfoReturnable<Optional<WorldListWidget.WorldEntry>> cir) {
		WorldListWidget.Entry entry = this.getSelectedOrNull();
		if (entry instanceof WrappedWorldEntry wrapped) {
			cir.setReturnValue(Optional.of(wrapped.worldEntry()));
		}
	}

	@Inject(method = "showSummaries", at = @At("HEAD"))
	private void worldFolders$captureSelectedFolders(String search, List<LevelSummary> summaries, CallbackInfo ci) {
		this.selectedFolders.clear();
		this.children().stream()
			.filter(entry -> entry instanceof FolderEntry)
			.map(entry -> (FolderEntry)entry)
			.forEach(folder -> this.selectedFolders.put(folder.folderData(), folder.selected()));
	}

	@Inject(method = "showSummaries", at = @At("TAIL"))
	private void worldFolders$regenerateSummaries(String search, List<LevelSummary> summaries, CallbackInfo ci) {
		if (!search.isBlank()) return;

		WorldListWidget $this = ((WorldListWidget)(Object)this);
		this.clearEntries();

		LinkedHashMap<FolderData, List<LevelSummary>> folders = new LinkedHashMap<>();
		List<LevelSummary> unsortedWorlds = new ArrayList<>();

		for (LevelSummary summary : summaries) {
			FolderData folder = ConfigHandler.getFolder(summary.getName());

			if (folder == FolderData.UNSORTED) {
				unsortedWorlds.add(summary);
				continue;
			}

			if (folders.containsKey(folder)) {
				List<LevelSummary> entries = new ArrayList<>(folders.get(folder));
				entries.add(summary);
				folders.replace(folder, entries);
			} else {
				folders.putLast(folder, List.of(summary));
			}

		}

		for (Map.Entry<FolderData, List<LevelSummary>> folder : folders.entrySet()) {
			addFolderAndEntries($this, folder.getKey(), folder.getValue());
		}

		addEmptyFolders($this, folders);
		addFolderAndEntries($this, FolderData.UNSORTED, unsortedWorlds);
	}

	@Unique
	private void addEmptyFolders(WorldListWidget list, Map<FolderData, List<LevelSummary>> folders) {
		for (FolderData folder : ConfigHandler.getConfig()) {
			if (!folders.containsKey(folder)) {
				boolean selected = this.selectedFolders.getOrDefault(folder, false);
				this.addEntry(new FolderEntry(folder, list, 0, selected));
			}
		}
	}

	@Unique
	private void addFolderAndEntries(WorldListWidget list, FolderData folderData, List<LevelSummary> summaries) {
		boolean selected = this.selectedFolders.getOrDefault(folderData, false);
		this.addEntry(new FolderEntry(folderData, list, summaries.size(), selected));

		if (selected) {
			for (LevelSummary summary : summaries) {
				this.addEntry(new WrappedWorldEntry(list, summary));
			}
		}
	}
}