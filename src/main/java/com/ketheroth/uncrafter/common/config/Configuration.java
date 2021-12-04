package com.ketheroth.uncrafter.common.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class Configuration {

	public static ForgeConfigSpec CONFIG;

	public static ForgeConfigSpec.ConfigValue<List<String>> BLACKLIST;
	public static ForgeConfigSpec.ConfigValue<List<String>> WHITELIST;
	public static ForgeConfigSpec.ConfigValue<Integer> EXTRACT_AMOUNT;
	public static final ForgeConfigSpec.ConfigValue<Integer> ADVANCED_EXTRACT_AMOUNT;
	public static final ForgeConfigSpec.ConfigValue<Integer> ENCHANTMENT_EXTRACT_AMOUNT;
	public static final ForgeConfigSpec.ConfigValue<Boolean> MINIMUM_LEVEL_FOR_ENCHANTMENTS;

	public static List<String> IMC_BLACKLIST = new ArrayList<>();

	static {
		ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

		BLACKLIST = BUILDER.comment("Blacklisted items. Items in this list won't be able to be used in the uncrafter. This list is used only if the whitelist is empty.")
				.define("blacklist", Lists.newArrayList(
						"minecraft:coal", "minecraft:diamond", "minecraft:emerald",
						"minecraft:gold_ingot", "minecraft:gold_nugget",
						"minecraft:iron_ingot", "minecraft:iron_nugget", "minecraft:lapis_lazuli",
						"minecraft:netherite_ingot", "minecraft:redstone",
						"minecraft:acacia_planks", "minecraft:birch_planks", "minecraft:crimson_planks",
						"minecraft:dark_oak_planks", "minecraft:jungle_planks", "minecraft:oak_planks",
						"minecraft:spruce_planks", "minecraft:warped_planks", "minecraft:acacia_plank",
						"minecraft:birch_plank", "minecraft:crimson_plank", "minecraft:dark_oak_plank",
						"minecraft:jungle_plank", "minecraft:oak_plank", "minecraft:spruce_plank",
						"minecraft:warped_plank", "minecraft:blaze_powder", "minecraft:slime_ball",
						"minecraft:soul_torch", "minecraft:stick", "minecraft:torch", "minecraft:wheat",
						"minecraft:magenta_dye", "minecraft:pink_dye",
						"minecraft:red_dye", "minecraft:yellow_dye",
						"minecraft:black_stained_glass_pane", "minecraft:blue_stained_glass_pane",
						"minecraft:brown_stained_glass_pane", "minecraft:cyan_stained_glass_pane",
						"minecraft:gray_stained_glass_pane", "minecraft:light_blue_stained_glass_pane",
						"minecraft:light_gray_stained_glass_pane", "minecraft:lime_stained_glass_pane",
						"minecraft:magenta_stained_glass_pane", "minecraft:orange_stained_glass_pane",
						"minecraft:pink_stained_glass_pane", "minecraft:red_stained_glass_pane",
						"minecraft:white_stained_glass_pane", "minecraft:yellow_stained_glass_pane",
						"minecraft:andesite_slab", "minecraft:blackstone_slab", "minecraft:brick_slab",
						"minecraft:cut_red_sandstone_slab", "minecraft:cut_sandstone_slab",
						"minecraft:dark_prismarine_slab", "minecraft:diorite_slab", "minecraft:end_stone_brick_slab",
						"minecraft:granite_slab", "minecraft:mossy_cobblestone_slab", "minecraft:mossy_stone_brick_slab",
						"minecraft:nether_brick_slab", "minecraft:polished_andesite_slab",
						"minecraft:polished_blackstone_brick_slab", "minecraft:polished_blackstone_slab",
						"minecraft:polished_diorite_slab", "minecraft:polished_granite_slab",
						"minecraft:prismarine_brick_slab", "minecraft:purpur_slab", "minecraft:quartz_slab",
						"minecraft:red_nether_brick_slab", "minecraft:red_sandstone_slab", "minecraft:sandstone_slab",
						"minecraft:smooth_quart_slab", "minecraft:smooth_red_sandstone_slab",
						"minecraft:smooth_stone_slab", "minecraft:stone_slab"
				));

		WHITELIST = BUILDER.comment("Whitelisted items. Only these items are allowed to be used in the uncrafter. The blacklist will have no effect if the whitelist isn't empty.")
				.define("whitelist", new ArrayList<>());

		EXTRACT_AMOUNT = BUILDER.comment("Amount of ingredients that can be retrieved in the uncrafter (default=1)")
				.define("extractAmount", 1);

		ADVANCED_EXTRACT_AMOUNT = BUILDER.comment("Amount of ingredients that can be retrieved in the advanced uncrafter (default=3)")
				.define("advancedExtractAmount", 3);

		ENCHANTMENT_EXTRACT_AMOUNT = BUILDER.comment("Amount of enchantment that can be retrieved in the advanced uncrafter (default=3)")
				.define("enchantmentExtractAmount", 3);

		MINIMUM_LEVEL_FOR_ENCHANTMENTS = BUILDER.comment("Determine if the enchantments keep their level or are reduced to their first level (default=false)")
				.define("minimumLevelForEnchantments", false);

		CONFIG = BUILDER.build();
	}

}
