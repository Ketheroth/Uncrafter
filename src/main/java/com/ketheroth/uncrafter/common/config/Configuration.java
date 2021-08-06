package com.ketheroth.uncrafter.common.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class Configuration {

	public static final ForgeConfigSpec CONFIG;

	public static final ForgeConfigSpec.ConfigValue<List<String>> BLACKLIST;
	public static final ForgeConfigSpec.ConfigValue<Integer> EXTRACT_AMOUNT;
	public static final ForgeConfigSpec.ConfigValue<Integer> ADVANCED_EXTRACT_AMOUNT;
	public static final ForgeConfigSpec.ConfigValue<Integer> ENCHANTMENT_EXTRACT_AMOUNT;
	public static final ForgeConfigSpec.ConfigValue<Boolean> MINIMUM_LEVEL_FOR_ENCHANTMENTS;

	public static final List<String> IMC_BLACKLIST = Lists.newArrayList();

	static {
		ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

		BLACKLIST = BUILDER.comment("Blacklisted crafting recipe. Recipes in the list won't be allowed to be reversed in the uncrafter.")
				.define("blacklist", Lists.newArrayList(
						"minecraft:coal", "minecraft:copper_ingot", "minecraft:copper_ingot_from_waxed_copper_block",
						"minecraft:diamond", "minecraft:emerald", "minecraft:gold_ingot_from_gold_block",
						"minecraft:gold_nugget", "minecraft:iron_ingot_from_iron_block", "minecraft:iron_nugget",
						"minecraft:lapis_lazuli", "minecraft:netherite_ingot_from_netherite_block", "minecraft:redstone",
						"minecraft:raw_copper", "minecraft:raw_gold", "minecraft:raw_iron", "minecraft:acacia_planks",
						"minecraft:birch_planks", "minecraft:crimson_planks", "minecraft:dark_oak_planks",
						"minecraft:jungle_planks", "minecraft:oak_planks", "minecraft:spruce_planks",
						"minecraft:warped_planks", "minecraft:acacia_plank", "minecraft:birch_plank",
						"minecraft:crimson_plank", "minecraft:dark_oak_plank", "minecraft:jungle_plank",
						"minecraft:oak_plank", "minecraft:spruce_plank", "minecraft:warped_plank",
						"minecraft:blaze_powder", "minecraft:slime_ball", "minecraft:soul_torch", "minecraft:stick",
						"minecraft:torch", "minecraft:wheat", "minecraft:magenta_dye_from_lilac",
						"minecraft:pink_dye_from_peony", "minecraft:red_dye_from_rose_bush",
						"minecraft:yellow_dye_from_sunflower", "minecraft:black_stained_glass_pane",
						"minecraft:blue_stained_glass_pane", "minecraft:brown_stained_glass_pane",
						"minecraft:cyan_stained_glass_pane", "minecraft:gray_stained_glass_pane",
						"minecraft:light_blue_stained_glass_pane", "minecraft:light_gray_stained_glass_pane",
						"minecraft:lime_stained_glass_pane", "minecraft:magenta_stained_glass_pane",
						"minecraft:orange_stained_glass_pane", "minecraft:pink_stained_glass_pane",
						"minecraft:red_stained_glass_pane", "minecraft:white_stained_glass_pane",
						"minecraft:yellow_stained_glass_pane", "minecraft:andesite_slab", "minecraft:blackstone_slab",
						"minecraft:brick_slab", "minecraft:cobbled_deepslate_slab", "minecraft:cut_copper_slab",
						"minecraft:cut_red_sandstone_slab", "minecraft:cut_sandstone_slab", "minecraft:dark_prismarine_slab",
						"minecraft:deepslate_brick_slab", "minecraft:deepslate_tile_slab", "minecraft:diorite_slab",
						"minecraft:end_stone_brick_slab", "minecraft:exposed_cut_copper_slab", "minecraft:granite_slab",
						"minecraft:mossy_cobblestone_slab", "minecraft:mossy_stone_brick_slab", "minecraft:nether_brick_slab",
						"minecraft:oxidized_cut_copper_slab", "minecraft:polished_andesite_slab",
						"minecraft:polished_blackstone_brick_slab", "minecraft:polished_blackstone_slab",
						"minecraft:polished_deepslate_slab", "minecraft:polished_diorite_slab",
						"minecraft:polished_granite_slab", "minecraft:prismarine_brick_slab", "minecraft:purpur_slab",
						"minecraft:quartz_slab", "minecraft:red_nether_brick_slab", "minecraft:red_sandstone_slab",
						"minecraft:sandstone_slab", "minecraft:smooth_quart_slab", "minecraft:smooth_red_sandstone_slab",
						"minecraft:smooth_stone_slab", "minecraft:stone_slab", "minecraft:waxed_cut_copper_slab",
						"minecraft:waxed_exposed_cut_copper_slab", "minecraft:waxed_oxidized_cut_copper_slab",
						"minecraft:waxed_weathered_cut_copper_slab", "minecraft:weathered_cut_copper_slab"
				));

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
