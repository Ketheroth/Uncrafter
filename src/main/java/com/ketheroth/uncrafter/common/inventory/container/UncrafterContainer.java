package com.ketheroth.uncrafter.common.inventory.container;

import com.ketheroth.uncrafter.common.config.Configuration;
import com.ketheroth.uncrafter.core.registry.UncrafterContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class UncrafterContainer extends Container {

	private BlockPos pos;
	private PlayerEntity player;
	private IItemHandler playerInventory;
	private Tuple<ItemStack, List<ItemStack>> cache = new Tuple<>(ItemStack.EMPTY, new ArrayList<>());
	private ItemStackHandler outputItems = createOutputHandler();
	private ItemStackHandler inputItems = createInputHandler();

	public UncrafterContainer(int windowId, PlayerInventory playerInventory, PlayerEntity player, BlockPos pos) {
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
		addSlot(new SlotItemHandler(inputItems, 0, 35, 35));
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= 16.0D;
	}

	@Override
	public void removed(PlayerEntity player) {
		if (player instanceof ServerPlayerEntity) {
			ItemStack itemstack = this.inputItems.extractItem(0, 64, false);
			if (!itemstack.isEmpty()) {
				if (player.isAlive() && !((ServerPlayerEntity) player).hasDisconnected()) {
					player.inventory.placeItemBackInInventory(this.player.level, itemstack);
				} else {
					player.drop(itemstack, false);
				}
			}
		}
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
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

	private ItemStackHandler createInputHandler() {
		return new ItemStackHandler(1) {
			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				//when an item is inserted, we set the output slots, then we insert the stack in the input
				for (int i = 0; i < 9; i++) {
					outputItems.setStackInSlot(i, ItemStack.EMPTY);
				}
				if (!stack.sameItem(cache.getA())) {
					IRecipe<?> recipe = searchRecipe(stack);
					if (recipe != null) {
						List<ItemStack> list = recipe.getIngredients().stream().collect(ArrayList::new,
								(accumulator, ingredient) -> accumulator.add(ingredient.isEmpty() ? ItemStack.EMPTY : ingredient.getItems()[0]),
								ArrayList::addAll);
						cache = new Tuple<>(stack, list);
					} else {
						cache = new Tuple<>(cache.getA(), new ArrayList<>());
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
						if (!stack.sameItem(cache.getA())) {
							IRecipe<?> recipe = searchRecipe(stack);
							if (recipe != null) {
								List<ItemStack> list = recipe.getIngredients().stream().collect(ArrayList::new,
										(accumulator, ingredient) -> accumulator.add(ingredient.isEmpty() ? ItemStack.EMPTY : ingredient.getItems()[new Random().nextInt(ingredient.getItems().length)]),
										ArrayList::addAll);
								cache = new Tuple<>(stack, list);
							} else {
								cache = new Tuple<>(cache.getA(), new ArrayList<>());
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
		};
	}

	private ItemStackHandler createOutputHandler() {
		return new ItemStackHandler(9) {
			private int extracted = 0;

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
		};
	}

	private IRecipe<?> searchRecipe(ItemStack input) {
		Item inputItem = input.getItem();
		RecipeManager recipeManager = this.player.level.getRecipeManager();
		Optional<IRecipe<?>> optionalRecipe = recipeManager.getRecipes().stream()
				.filter(recipe -> recipe.getType().equals(IRecipeType.CRAFTING))
				.filter(recipe -> !Configuration.BLACKLIST.get().contains(recipe.getId().toString()))
				.filter(recipe -> !Configuration.IMC_BLACKLIST.contains(recipe.getId().toString()))
				.filter(recipe -> recipe.canCraftInDimensions(3, 3)
						&& recipe.getResultItem().getItem() == inputItem
						&& !recipe.getIngredients().isEmpty())
				.findAny();
		return optionalRecipe.orElse(null);
	}

}
