package com.ketheroth.uncrafter.common.inventory.container;

import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nullable;
import java.util.List;

public interface IUncrafterContainer {

	InputHandler getInputHandler();

	OutputHandler getOutputHandler();

	@Nullable
	EnchantmentHandler getEnchantmentHandler();

	RecipeManager getRecipeManager();

	Tuple<Item, List<ItemStack>> getCache();

	boolean isInputLocked();

	boolean isAdvanced();

}
