package com.ketheroth.uncrafter.common.inventory.container;

import com.ketheroth.uncrafter.common.config.Configuration;
import com.ketheroth.uncrafter.core.registry.UncrafterContainerTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class UncrafterContainer extends AbstractContainerMenu {

	private BlockPos pos;
	private Player player;
	private IItemHandler playerInventory;
	private Tuple<Item, List<ItemStack>> cache = new Tuple<>(null, null);
	private OutputHandler outputItems = new OutputHandler(9);
	private InputHandler inputItems = new InputHandler(1);

	public UncrafterContainer(int windowId, Inventory playerInventory, Player player, BlockPos pos) {
		super(UncrafterContainerTypes.UNCRAFTER_CONTAINER.get(), windowId);
		this.pos = pos;
		this.player = player;
		this.playerInventory = new InvWrapper(playerInventory);
		layoutPlayerInventorySlot(8, 84);
		int index = 0;
		int y = 17;
		int x = 93;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				addSlot(new SlotItemHandler(outputItems, index, x + 18 * j, y + 18 * i) {
					@Override
					public boolean mayPlace(@Nonnull ItemStack stack) {
						return false;
					}
				});
				index++;
			}
		}
		addSlot(new SlotItemHandler(inputItems, 0, 35, 35) {
			@Override
			public boolean mayPickup(Player playerIn) {
				return outputItems.extracted == 0 && super.mayPickup(playerIn);
			}
		});
	}

	@Override
	public boolean stillValid(Player player) {
		return player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= 16.0D;
	}

	@Override
	public void removed(Player player) {
		if (player instanceof ServerPlayer) {
			ItemStack itemstack = this.inputItems.extractItem(0, 64, false);
			if (!itemstack.isEmpty()) {
				if (outputItems.extracted == 0) {
					if (player.isAlive() && !((ServerPlayer) player).hasDisconnected()) {
						player.getInventory().placeItemBackInInventory(itemstack);
					} else {
						player.drop(itemstack, false);
					}
				}
			}
		}
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		return ItemStack.EMPTY;
//		ItemStack itemstack = ItemStack.EMPTY;
//		Slot slot = this.slots.get(index);
//		if (slot != null && slot.hasItem()) {
//			ItemStack itemstack1 = slot.getItem();
//			itemstack = itemstack1.copy();
//			if (index < playerInventory.getSlots()) {
//				if (!this.moveItemStackTo(itemstack1, playerInventory.getSlots(), this.slots.size(), true)) {
//					return ItemStack.EMPTY;
//				}
//			} else if (!this.moveItemStackTo(itemstack1, 0, playerInventory.getSlots(), false)) {
//				return ItemStack.EMPTY;
//			}
//			if (itemstack1.isEmpty()) {
//				slot.set(ItemStack.EMPTY);
//			} else {
//				slot.setChanged();
//			}
//		}
//		return itemstack;
	}

	private int addSlotRange(IItemHandler handler, int index, int x, int y, int xAmount, int dx) {
		for (int i = 0; i < xAmount; i++) {
			addSlot(new SlotItemHandler(handler, index, x, y));
			x += dx;
			index++;
		}
		return index;
	}

	private int addSlotBox(IItemHandler handler, int index, int x, int y, int xAmount, int dx, int yAmount, int dy) {
		for (int i = 0; i < yAmount; i++) {
			index = addSlotRange(handler, index, x, y, xAmount, dx);
			y += dy;
		}
		return index;
	}

	private void layoutPlayerInventorySlot(int x, int y) {
		addSlotBox(playerInventory, 9, x, y, 9, 18, 3, 18);
		y += 58;
		addSlotRange(playerInventory, 0, x, y, 9, 18);
	}

	private Recipe<?> searchRecipe(ItemStack input) {
		Item inputItem = input.getItem();
		RecipeManager recipeManager = this.player.level.getRecipeManager();
		Optional<Recipe<?>> optionalRecipe = recipeManager.getRecipes().stream()
				.filter(recipe -> recipe.getType().equals(RecipeType.CRAFTING))
				.filter(recipe -> !Configuration.BLACKLIST.get().contains(recipe.getId().toString()))
				.filter(recipe -> !Configuration.IMC_BLACKLIST.contains(recipe.getId().toString()))
				.filter(recipe -> recipe.canCraftInDimensions(3, 3)
						&& recipe.getResultItem().getItem() == inputItem
						&& !recipe.getIngredients().isEmpty())
				.findAny();
		if (optionalRecipe.isEmpty()) {
			return null;
		}
		return optionalRecipe.get();
	}

	public boolean isInputLocked() {
		return outputItems.extracted != 0;
	}

	private class OutputHandler extends ItemStackHandler {

		private int extracted;

		public OutputHandler(int size) {
			super(size);
			extracted = 0;
		}

		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			//first we extract the chosen stack
			ItemStack extractedStack = super.extractItem(slot, amount, simulate);
			if (!simulate) {
				extracted++;
			}
			//then we remove one item from the input stack
			if (extracted >= Configuration.EXTRACT_AMOUNT.get() || this.isEmpty()) {
				inputItems.extractItem(0, amount, simulate);
				if (!simulate) {
					extracted = 0;
				}
			}
			return extractedStack;
		}

		private boolean isEmpty() {
			for (int i = 0; i < this.getSlots(); i++) {
				if (!this.getStackInSlot(i).isEmpty()) {
					return false;
				}
			}
			return true;
		}

	}

	private class InputHandler extends ItemStackHandler {

		public InputHandler(int size) {
			super(size);
		}

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			//when an item is inserted, we set the output slots, then we insert the stack in the input
			for (int i = 0; i < 9; i++) {
				outputItems.setStackInSlot(i, ItemStack.EMPTY);
			}
			if (!stack.is(cache.getA())) {
				Recipe<?> recipe = searchRecipe(stack);
				if (recipe != null) {
					List<ItemStack> list = recipe.getIngredients().stream().collect(ArrayList::new,
							(accumulator, ingredient) -> accumulator.add(ingredient.isEmpty() ? ItemStack.EMPTY : ingredient.getItems()[0]),
							ArrayList::addAll);
					cache.setA(stack.getItem());
					cache.setB(list);
				} else {
					cache.setB(new ArrayList<>());
				}
			}
			//add ingredient items
			int index = 0;
			for (ItemStack ingredient : cache.getB()) {
				outputItems.setStackInSlot(index, ingredient.copy());
				index++;
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
					outputItems.setStackInSlot(i, ItemStack.EMPTY);
				}
				if (this.getStackInSlot(slot).getCount() > amount) {
					ItemStack stack = this.getStackInSlot(0);
					if (!stack.is(cache.getA())) {
						Recipe<?> recipe = searchRecipe(stack);
						if (recipe != null) {
							List<ItemStack> list = recipe.getIngredients().stream().collect(ArrayList::new,
									(accumulator, ingredient) -> accumulator.add(ingredient.isEmpty() ? ItemStack.EMPTY : ingredient.getItems()[new Random().nextInt(ingredient.getItems().length)]),
									ArrayList::addAll);
							cache.setA(stack.getItem());
							cache.setB(list);
						} else {
							cache.setB(new ArrayList<>());
						}
					}
					int index = 0;
					for (ItemStack ingredient : cache.getB()) {
						outputItems.setStackInSlot(index, ingredient.copy());
						index++;
					}
				}
			}
			return super.extractItem(slot, amount, simulate);
		}

	}

}
