package com.ketheroth.uncrafter.common.inventory.container;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.Tuple;

import javax.annotation.Nullable;
import java.util.List;

public interface IUncrafterContainer {

	InputHandler getInputHandler();

	OutputHandler getOutputHandler();

	@Nullable
	EnchantmentHandler getEnchantmentHandler();

	RecipeManager getRecipeManager();

	Tuple<ItemStack, List<ItemStack>> getCache();

	void setCache(List<ItemStack> b);

	void setCache(ItemStack a, List<ItemStack> b);

	boolean isInputLocked();

	boolean isAdvanced();

}
