package com.ketheroth.uncrafter.common.inventory.container;

import com.ketheroth.uncrafter.core.registry.UncrafterContainerTypes;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AdvancedUncrafterContainer extends AbstractContainerMenu implements IUncrafterContainer {

	private final BlockPos pos;
	private final Player player;
	private final IItemHandler playerInventory;
	private final OutputHandler outputItems;
	private final InputHandler inputItems;
	private Tuple<Item, List<ItemStack>> cache = new Tuple<>(null, null);

	public AdvancedUncrafterContainer(int windowId, Inventory playerInventory, Player player, BlockPos pos) {
		super(UncrafterContainerTypes.UNCRAFTER_CONTAINER.get(), windowId);
		this.pos = pos;
		this.player = player;
		this.playerInventory = new InvWrapper(playerInventory);
		this.outputItems = new OutputHandler(9, this);
		this.inputItems = new InputHandler(1, this);

		//layout player inventory
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlot(new SlotItemHandler(this.playerInventory, 9 + 9 * y + x, 8 + 18 * x, 84 + 18 * y));
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlot(new SlotItemHandler(this.playerInventory, i, 8 + 18 * i, 196));
		}
		//layout output inventory
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				addSlot(new SlotItemHandler(this.outputItems, 3 * y + x, 93 + 18 * x, 17 + 18 * y) {
					@Override
					public boolean mayPlace(@Nonnull ItemStack stack) {
						return false;
					}
				});
			}
		}
		//layout input inventory
		addSlot(new SlotItemHandler(this.inputItems, 0, 35, 35) {
			@Override
			public boolean mayPickup(Player playerIn) {
				return !AdvancedUncrafterContainer.this.outputItems.isExtracting() && super.mayPickup(playerIn);
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
				if (!outputItems.isExtracting()) {
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

	@Override
	public boolean isInputLocked() {
		return outputItems.isExtracting();
	}

	@Override
	public OutputHandler getOutputHandler() {
		return outputItems;
	}

	@Override
	public InputHandler getInputHandler() {
		return inputItems;
	}

	@Override
	public Tuple<Item, List<ItemStack>> getCache() {
		return cache;
	}

	@Override
	public RecipeManager getRecipeManager() {
		return player.level.getRecipeManager();
	}

}
