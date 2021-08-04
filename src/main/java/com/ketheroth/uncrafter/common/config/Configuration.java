package com.ketheroth.uncrafter.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class Configuration {
	public static ForgeConfigSpec CONFIG;

	public static ForgeConfigSpec.ConfigValue<List<String>> BLACKLIST;

	static {
		ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
		BLACKLIST = BUILDER.comment("Blacklisted crafting recipes that can't be inverted. This is to avoid item duplication").define("blacklist", List.of(
				"minecraft:coal",
				"minecraft:copper_ingot",
				"minecraft:copper_ingot_from_waxed_copper_block",
				"minecraft:diamond",
				"minecraft:emerald",
				"minecraft:gold_ingot_from_gold_block",
				"minecraft:gold_nugget",
				"minecraft:iron_ingot_from_iron_block",
				"minecraft:iron_nugget",
				"minecraft:lapis_lazuli",
				"minecraft:netherite_ingot_from_netherite_block",
				"minecraft:redstone",
				"minecraft:raw_copper",
				"minecraft:raw_gold",
				"minecraft:raw_iron",
				"minecraft:acacia_planks",
				"minecraft:birch_planks",
				"minecraft:crimson_planks",
				"minecraft:dark_oak_planks",
				"minecraft:jungle_planks",
				"minecraft:oak_planks",
				"minecraft:spruce_planks",
				"minecraft:warped_planks",
				"minecraft:slime_ball",
				"minecraft:soul_torch",
				"minecraft:stick",
				"minecraft:torch"
		));

		CONFIG = BUILDER.build();
	}
}
