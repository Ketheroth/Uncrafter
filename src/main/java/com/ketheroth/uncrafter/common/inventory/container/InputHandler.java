package com.ketheroth.uncrafter.common.inventory.container;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InputHandler extends ItemStackHandler {

	private final IUncrafterContainer container;

	public InputHandler(int size, IUncrafterContainer container) {
		super(size);
		this.container = container;
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		//when an item is inserted, we set the output slots, then we insert the stack in the input
		for (int i = 0; i < 9; i++) {
			container.getOutputHandler().setStackInSlot(i, ItemStack.EMPTY);
		}
		this.fillOutputSlots(stack);
		return super.insertItem(slot, stack, simulate);
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		//when an item is extracted
		//- we clear the output slots
		//- if input !empty we refill the output slots with the recipe
		//then we remove the items from the input stack
		if (!simulate) {
			for (int i = 0; i < 9; i++) {
				container.getOutputHandler().setStackInSlot(i, ItemStack.EMPTY);
			}
			if (this.getStackInSlot(slot).getCount() > amount) {
				this.fillOutputSlots(this.getStackInSlot(0));
			}
		}
		return super.extractItem(slot, amount, simulate);
	}

	public void fillOutputSlots(ItemStack inputStack) {
		// search ingredient for input item
		if (!inputStack.is(container.getCache().getA())) {
			Recipe<?> recipe = UncrafterContainer.searchRecipe(inputStack, container.getRecipeManager());
			if (recipe != null) {
				List<ItemStack> list = recipe.getIngredients().stream().collect(ArrayList::new,
						(accumulator, ingredient) -> accumulator.add(ingredient.isEmpty() ? ItemStack.EMPTY : ingredient.getItems()[new Random().nextInt(ingredient.getItems().length)]),
						ArrayList::addAll);
				container.getCache().setA(inputStack.getItem());
				container.getCache().setB(list);
			} else {
				container.getCache().setB(new ArrayList<>());
			}
		}
		// fill output slots with ingredients
		int index = 0;
		for (ItemStack ingredient : container.getCache().getB()) {
			container.getOutputHandler().setStackInSlot(index, ingredient.copy());
			index++;
		}
	}
}

