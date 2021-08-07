package com.ketheroth.uncrafter.common.inventory.container;

import com.ketheroth.uncrafter.core.registry.UncrafterContainerTypes;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AdvancedUncrafterContainer extends Container implements IUncrafterContainer {

	private final BlockPos pos;
	private final PlayerEntity player;
	private final IItemHandler playerInventory;
	private final OutputHandler outputItems;
	private final InputHandler inputItems;
	private final EnchantmentHandler enchantmentHandler;
	private Tuple<ItemStack, List<ItemStack>> cache = new Tuple<>(ItemStack.EMPTY, new ArrayList<>());

	public AdvancedUncrafterContainer(int windowId, PlayerInventory playerInventory, PlayerEntity player, BlockPos pos) {
		super(UncrafterContainerTypes.ADVANCED_UNCRAFTER_CONTAINER.get(), windowId);
		this.pos = pos;
		this.player = player;
		this.playerInventory = new InvWrapper(playerInventory);
		this.outputItems = new OutputHandler(9, this);
		this.inputItems = new InputHandler(1, this);
		this.enchantmentHandler = new EnchantmentHandler(6);
		//layout player inventory
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlot(new SlotItemHandler(this.playerInventory, 9 + 9 * y + x, 8 + 18 * x, 84 + 18 * y));
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlot(new SlotItemHandler(this.playerInventory, i, 8 + 18 * i, 142));
		}
		//layout output inventory
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				addSlot(new SlotItemHandler(this.outputItems, 3 * y + x, 70 + 18 * x, 17 + 18 * y) {
					@Override
					public boolean mayPlace(@Nonnull ItemStack stack) {
						return false;
					}
				});
			}
		}
		//layout input inventory
		addSlot(new SlotItemHandler(this.inputItems, 0, 12, 35) {
			@Override
			public boolean mayPickup(PlayerEntity playerIn) {
				return !AdvancedUncrafterContainer.this.isInputLocked() && super.mayPickup(playerIn);
			}
		});

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 2; x++) {
				addSlot(new SlotItemHandler(this.enchantmentHandler, 2 * y + x, 134 + 18 * x, 17 + 18 * y) {
					@Override
					public boolean mayPlace(@Nonnull ItemStack stack) {
						return false;
					}
				});
			}
		}
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return player.distanceToSqr(this.pos.getX(), this.pos.getY(), this.pos.getZ()) <= 16.0D;
	}

	@Override
	public void removed(PlayerEntity player) {
		if (player instanceof ServerPlayerEntity) {
			ItemStack itemstack = this.inputItems.extractItem(0, 64, false);
			if (!itemstack.isEmpty()) {
				if (!this.outputItems.isExtracting()) {
					if (player.isAlive() && !((ServerPlayerEntity) player).hasDisconnected()) {
						player.inventory.placeItemBackInInventory(this.player.level, itemstack);
					} else {
						player.drop(itemstack, false);
					}
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

	@Override
	public boolean isInputLocked() {
		return this.outputItems.isExtracting() || this.enchantmentHandler.isExtracting();
	}

	@Override
	public OutputHandler getOutputHandler() {
		return this.outputItems;
	}

	@Override
	public InputHandler getInputHandler() {
		return this.inputItems;
	}

	@Override
	public EnchantmentHandler getEnchantmentHandler() {
		return this.enchantmentHandler;
	}

	@Override
	public Tuple<ItemStack, List<ItemStack>> getCache() {
		return this.cache;
	}

	@Override
	public void setCache(ItemStack a, List<ItemStack> b) {
		this.cache = new Tuple<>(a, b);
	}

	@Override
	public void setCache(List<ItemStack> b) {
		this.cache = new Tuple<>(cache.getA(), b);
	}

	@Override
	public RecipeManager getRecipeManager() {
		return this.player.level.getRecipeManager();
	}

	@Override
	public boolean isAdvanced() {
		return true;
	}

}
