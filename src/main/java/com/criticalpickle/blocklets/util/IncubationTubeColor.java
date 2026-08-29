package com.criticalpickle.blocklets.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public enum IncubationTubeColor implements StringRepresentable {
    NONE,
    WHITE,
    ORANGE,
    MAGENTA,
    LIGHT_BLUE,
    YELLOW,
    LIME,
    PINK,
    GRAY,
    LIGHT_GRAY,
    CYAN,
    PURPLE,
    BLUE,
    BROWN,
    GREEN,
    RED,
    BLACK;

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase();
    }

    /**
     * Gets a color based on a dye item.
     * @param dye the dye item to compare.
     * @return The relating IncubationTubeColor enum or null if none exist.
     */
    public static IncubationTubeColor getColor(Item dye) {
        for (IncubationTubeColor color : IncubationTubeColor.values()) {
            final String keyName = BuiltInRegistries.ITEM.getKey(dye).toShortLanguageKey();
            if (keyName.equals(color.getSerializedName() + "_dye")) {
                return color;
            }
        }
        return null;
    }
}
