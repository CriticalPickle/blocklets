package com.criticalpickle.blocklets;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue BLOCKLET_CAN_INCUBATE = BUILDER
            .comment(" Whether to allow Blocklets to be spawned with incubation chamber.\n" +
                     " Default is \"blockletCanIncubate = true\"")
            .define("blockletCanIncubate", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}
