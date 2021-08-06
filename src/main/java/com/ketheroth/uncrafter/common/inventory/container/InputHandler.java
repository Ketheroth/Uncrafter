package com.ketheroth.uncrafter.common.inventory.container;

import com.ketheroth.uncrafter.common.config.Configuration;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
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
		if (container.isAdvanced() && container.getEnchantmentHandler() != null) {
			for (int i = 0; i < 6; i++) {
				container.getEnchantmentHandler().setStackInSlot(i, ItemStack.EMPTY);
			}
			container.getEnchantmentHandler().resetExtracting();
		}
		if (!stack.is(container.getCache().getA())) {
			Recipe<?> recipe = UncrafterContainer.searchRecipe(stack, container.getRecipeManager());
			if (recipe != null) {
				List<ItemStack> list = recipe.getIngredients().stream().collect(ArrayList::new,
						(accumulator, ingredient) -> accumulator.add(ingredient.isEmpty() ? ItemStack.EMPTY : ingredient.getItems()[0]),
						ArrayList::addAll);
				container.getCache().setA(stack.getItem());
				container.getCache().setB(list);
			} else {
				container.getCache().setB(new ArrayList<>());
			}
		}
		//add ingredient items
		int index = 0;
		for (ItemStack ingredient : container.getCache().getB()) {
			container.getOutputHandler().setStackInSlot(index, ingredient.copy());
			index++;
		}
		//add enchantments
		if (container.isAdvanced() && container.getEnchantmentHandler() != null && stack.isEnchanted()) {
			ArrayList<ItemStack> books = new ArrayList<>();
			EnchantmentHelper.getEnchantments(stack).forEach((enchantment, level) -> {
				if (!enchantment.isCurse()) {
					books.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, Configuration.MINIMUM_LEVEL_FOR_ENCHANTMENTS.get() ? 1 : level)));
				}
			});
			Collections.shuffle(books);
			for (int i = 0; i < 6 && i < books.size(); i++) {
				container.getEnchantmentHandler().setStackInSlot(i, books.get(i));
			}
		}
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
			if (container.isAdvanced() && container.getEnchantmentHandler() != null) {
				for (int i = 0; i < 6; i++) {
					container.getEnchantmentHandler().setStackInSlot(i, ItemStack.EMPTY);
				}
				container.getEnchantmentHandler().resetExtracting();
			}
			if (this.getStackInSlot(slot).getCount() > amount) {
				ItemStack stack = this.getStackInSlot(0);
				if (!stack.is(container.getCache().getA())) {
					Recipe<?> recipe = UncrafterContainer.searchRecipe(stack, container.getRecipeManager());
					if (recipe != null) {
						List<ItemStack> list = recipe.getIngredients().stream().collect(ArrayList::new,
								(accumulator, ingredient) -> accumulator.add(ingredient.isEmpty() ? ItemStack.EMPTY : ingredient.getItems()[new Random().nextInt(ingredient.getItems().length)]),
								ArrayList::addAll);
						container.getCache().setA(stack.getItem());
						container.getCache().setB(list);
					} else {
						container.getCache().setB(new ArrayList<>());
					}
				}
				int index = 0;
				for (ItemStack ingredient : container.getCache().getB()) {
					container.getOutputHandler().setStackInSlot(index, ingredient.copy());
					index++;
				}
				if (container.isAdvanced() && container.getEnchantmentHandler() != null && stack.isEnchanted()) {
					ArrayList<ItemStack> books = new ArrayList<>();
					EnchantmentHelper.getEnchantments(stack).forEach((enchantment, level) -> books.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, Configuration.MINIMUM_LEVEL_FOR_ENCHANTMENTS.get() ? 1 : level))));
					Collections.shuffle(books);
					for (int i = 0; i < 6 && i < books.size(); i++) {
						container.getEnchantmentHandler().setStackInSlot(i, books.get(i));
					}
				}
			}
		}
		return super.extractItem(slot, amount, simulate);
	}

}

