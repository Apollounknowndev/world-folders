package dev.worldgen.world.folders.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.world.folders.util.RGBColor;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record FolderData(String name, RGBColor color, List<String> worlds) {
    public static final FolderData UNSORTED = new FolderData("Unsorted", new RGBColor(0xBBBBBB), List.of());

    public static final Codec<FolderData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("name").forGetter(FolderData::name),
        RGBColor.CODEC.fieldOf("color").forGetter(FolderData::color),
        Codec.STRING.listOf().fieldOf("worlds").forGetter(FolderData::worlds)
    ).apply(instance, FolderData::new));

    public FolderData(String name, RGBColor color, List<String> worlds) {
        this.name = name;
        this.color = color;
        this.worlds = new ArrayList<>(worlds);
    }

    public Text asText() {
        return Text.literal(this.name).setStyle(Style.EMPTY.withColor(this.color.rgb()));
    }

    public void removeWorld(String world) {
        worlds.remove(world);
    }

    public void addWorld(String world) {
        if (this != UNSORTED) {
            worlds.add(world);
        }
    }
}
