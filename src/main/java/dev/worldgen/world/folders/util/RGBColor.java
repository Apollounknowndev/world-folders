package dev.worldgen.world.folders.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Locale;

public record RGBColor(int rgb) {
    private static final String HASH = "#";
    public static final Codec<RGBColor> CODEC = Codec.STRING.comapFlatMap(code -> {
        if (!code.startsWith(HASH) || code.length() != 7) {
            return DataResult.error(() -> "Not a color code: " + code);
        }
        try {
            int i = (int)Long.parseLong(code.substring(1), 16);
            return DataResult.success(new RGBColor(i));
        } catch (NumberFormatException e) {
            return DataResult.error(() -> "Exception parsing color code: " + e.getMessage());
        }
    }, RGBColor::asString);

    private String asString() {
        return String.format(Locale.ROOT, "#%06X", this.rgb);
    }

    @Override
    public String toString() {
        return this.asString();
    }
}
