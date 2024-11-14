package dev.worldgen.world.folders;

import dev.worldgen.world.folders.access.SelectWorldButtonsAccessor;
import dev.worldgen.world.folders.config.ConfigHandler;
import dev.worldgen.world.folders.gui.SelectWorldButtons;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WorldFolders implements ModInitializer {
	public static final String MOD_ID = "world_folders";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ConfigHandler.load();

		ScreenEvents.BEFORE_INIT.register(((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof SelectWorldScreen selectScreen) {
				((SelectWorldButtonsAccessor)selectScreen).set(new SelectWorldButtons());
			}
		}));

		ScreenEvents.AFTER_INIT.register(Event.DEFAULT_PHASE, (client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof SelectWorldScreen selectScreen) {
				List<ClickableWidget> widgets = Screens.getButtons(screen);

				widgets.removeIf(widget -> widget instanceof ButtonWidget);

				((SelectWorldButtonsAccessor)selectScreen).get().init(widgets, client, selectScreen, scaledWidth, scaledHeight);
			}
		});
	}
}