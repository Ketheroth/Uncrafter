package com.ketheroth.uncrafter.common.inventory.container;

import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

public interface IUncrafterContainer {

	InputHandler getInputHandler();

	OutputHandler getOutputHandler();

	RecipeManager getRecipeManager();

	Tuple<Item, List<ItemStack>> getCache();

	boolean isInputLocked();

}
