package dev.worldgen.world.folders.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.worldgen.world.folders.WorldFolders;
import dev.worldgen.world.folders.util.RGBColor;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getGameDir().resolve("world_folders.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Codec<List<FolderData>> CODEC = FolderData.CODEC.listOf().fieldOf("folders").codec();
    private static final List<FolderData> DEFAULT_DATA = List.of();
    private static List<FolderData> config = DEFAULT_DATA;
    public static void load() {
        if (!Files.isRegularFile(CONFIG_PATH)) {
            write();
        }
        try {
            JsonElement json = JsonParser.parseString(new String(Files.readAllBytes(CONFIG_PATH)));
            var dataResult = CODEC.parse(JsonOps.INSTANCE, json);
            dataResult.ifError(error -> {
                WorldFolders.LOGGER.error("Config file has missing or invalid data: "+error.message());
                write();
            });
            if (dataResult.result().isPresent()) {
                config = dataResult.result().get();
            }
        } catch (IOException e) {
            WorldFolders.LOGGER.error("Malformed json in config file found, default config will be used");
            write();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void write() {
        try(BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH)) {
            JsonElement json = CODEC.encodeStart(JsonOps.INSTANCE, config).getOrThrow();
            writer.write(GSON.toJson(json));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<FolderData> getConfig() {
        return config;
    }

    public static List<FolderData> getAllFolders() {
        List<FolderData> folders = new ArrayList<>(ConfigHandler.getConfig());
        folders.addFirst(FolderData.UNSORTED);
        return folders;
    }

    public static void addFolder(String name, int color) {
        FolderData folder = new FolderData(name, new RGBColor(color), List.of());

        List<FolderData> folders = new ArrayList<>(config);
        folders.add(folder);
        config = folders;

        write();
    }

    public static void setFolder(FolderData folderData, String name, int color) {
        FolderData folder = new FolderData(name, new RGBColor(color), folderData.worlds());

        List<FolderData> folders = new ArrayList<>(config);
        folders.remove(folderData);
        folders.add(folder);
        config = folders;

        write();
    }

    public static FolderData getFolder(String worldName) {
        for (FolderData entry : config) {
            if (entry.worlds().contains(worldName)) return entry;
        }
        return FolderData.UNSORTED;
    }

    public static void deleteFolder(FolderData folderData) {
        List<FolderData> folders = new ArrayList<>(config);
        folders.remove(folderData);
        config = folders;

        write();
    }
}