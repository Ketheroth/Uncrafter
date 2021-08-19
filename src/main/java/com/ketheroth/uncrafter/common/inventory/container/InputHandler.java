package com.ketheroth.uncrafter.common.inventory.container;

import com.ketheroth.uncrafter.common.config.Configuration;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
			Recipe<?> recipe = searchRecipe(inputStack, container.getRecipeManager());
			if (recipe != null) {
				List<ItemStack> list = convertTo3x3(recipe).stream().collect(ArrayList::new,
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

	/**
	 * Convert a random sized recipe to a 3x3 recipe.
	 *
	 * @param recipe the recipe to convert.
	 * @return the ordered list of ingredients.
	 */
	public static List<Ingredient> convertTo3x3(Recipe<?> recipe) {
		List<Ingredient> ingredients = new ArrayList<>(recipe.getIngredients());
		if (recipe instanceof IShapedRecipe) {
			int width = ((IShapedRecipe<?>) recipe).getRecipeWidth();
			if (width == 2) {
				ingredients.add(Ingredient.EMPTY);
				ingredients.add(Ingredient.EMPTY);
				ingredients.add(Ingredient.EMPTY);
				ingredients.add(4, Ingredient.EMPTY);
				ingredients.add(2, Ingredient.EMPTY);
			} else if (width == 1) {
				ingredients.add(Ingredient.EMPTY);
				ingredients.add(Ingredient.EMPTY);
				ingredients.add(2, Ingredient.EMPTY);
				ingredients.add(2, Ingredient.EMPTY);
				ingredients.add(1, Ingredient.EMPTY);
				ingredients.add(1, Ingredient.EMPTY);
				ingredients.add(0, Ingredient.EMPTY);

			}
		}
		while (ingredients.size() > 9) {
			ingredients.remove(ingredients.size() - 1);
		}
		return ingredients;
	}

	/**
	 * Search a recipe having its result item being {@code input}.
	 *
	 * @param input         the result item of the recipe.
	 * @param recipeManager the recipe manager in which the recipe should be searched.
	 * @return the found recipe, or null if the recipe was not found.
	 */
	@Nullable
	public static Recipe<?> searchRecipe(ItemStack input, RecipeManager recipeManager) {
		Item inputItem = input.getItem();
		Optional<Recipe<?>> optionalRecipe = recipeManager.getRecipes().stream()
				.filter(recipe -> recipe.getType().equals(RecipeType.CRAFTING))
				.filter(recipe -> !Configuration.BLACKLIST.get().contains(recipe.getId().toString()))
				.filter(recipe -> !Configuration.IMC_BLACKLIST.contains(recipe.getId().toString()))
				.filter(recipe -> recipe.canCraftInDimensions(3, 3)
						&& recipe.getResultItem().getItem() == inputItem
						&& !recipe.getIngredients().isEmpty())
				.findAny();
		return optionalRecipe.orElse(null);
	}

}

