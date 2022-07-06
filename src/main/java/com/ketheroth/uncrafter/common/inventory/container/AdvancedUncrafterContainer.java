package com.ketheroth.uncrafter.common.inventory.container;

import com.ketheroth.uncrafter.common.config.Configuration;
import com.ketheroth.uncrafter.common.tileentity.UncrafterTileEntity;
import com.ketheroth.uncrafter.core.registry.UncrafterContainerTypes;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
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
public class AdvancedUncrafterContainer extends Container {

	private final BlockPos pos;
	private final PlayerEntity player;
	private final UncrafterTileEntity uncrafter;
	private final IItemHandler playerInventory;
	private final OutputHandler outputItems;
	private final InputHandler inputItems;
	private final EnchantmentHandler enchantmentHandler;
	private Tuple<ItemStack, List<ItemStack>> cache = new Tuple<>(ItemStack.EMPTY, new ArrayList<>());

	public AdvancedUncrafterContainer(int windowId, PlayerInventory playerInventory, PlayerEntity player, BlockPos pos) {
		super(UncrafterContainerTypes.ADVANCED_UNCRAFTER_CONTAINER.get(), windowId);
		this.pos = pos;
		this.player = player;
		this.uncrafter = ((UncrafterTileEntity) player.level.getBlockEntity(this.pos));
		this.playerInventory = new InvWrapper(playerInventory);
		this.outputItems = this.uncrafter.getOutput();
		this.inputItems = this.uncrafter.getInput();
		this.enchantmentHandler = this.uncrafter.getEnchantmentOutput();

		//layout input inventory
		addSlot(new SlotItemHandler(this.inputItems, 0, 12, 35) {
			@Override
			public boolean mayPlace(@Nonnull ItemStack stack) {
				return Configuration.isValidItem(stack.getItem());
			}

			@Override
			public boolean mayPickup(PlayerEntity playerIn) {
				return !AdvancedUncrafterContainer.this.uncrafter.isInputLocked() && super.mayPickup(playerIn);
			}
		});
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
		//layout enchantments inventory
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
		//layout player inventory
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlot(new SlotItemHandler(this.playerInventory, 9 + 9 * y + x, 8 + 18 * x, 84 + 18 * y));
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlot(new SlotItemHandler(this.playerInventory, i, 8 + 18 * i, 142));
		}
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return player.distanceToSqr(this.pos.getX(), this.pos.getY(), this.pos.getZ()) <= 16.0D;
	}

	@Override
	public ItemStack clicked(int index, int quickCraft, ClickType clickType, PlayerEntity player) {
		if (1 <= index && index < 10) {
			int maxExtract = this.uncrafter.isAdvanced() ? Configuration.ADVANCED_EXTRACT_AMOUNT.get() : Configuration.EXTRACT_AMOUNT.get();
			if (this.outputItems.getStackInSlot(index - 1).isEmpty()) {
				int n = 0;
				for (int i = 0; i < 9; i++) {
					if (this.uncrafter.selected[i]) {
						n++;
					}
				}
				if (this.uncrafter.selected[index - 1]) {
					this.uncrafter.selected[index - 1] = false;
				} else {
					this.uncrafter.selected[index - 1] = n < maxExtract;
				}
				this.uncrafter.setChanged();
			}
		}
		return super.clicked(index, quickCraft, clickType, player);
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			itemstack = slotStack.copy();
			if (index == 0) {// shift-click in input slot
				if (!this.moveItemStackTo(slotStack, 16, 52, true)) {
					return ItemStack.EMPTY;
				}
				if (slotStack.isEmpty()) {
					for (int i = 0; i < 9; i++) {
						this.outputItems.setStackInSlot(i, ItemStack.EMPTY);
					}
				}
			} else if (1 <= index && index < 10) {// shift-click in output slots
				if (!this.moveItemStackTo(slotStack, 10, 46, true)) {
					return ItemStack.EMPTY;
				}
				outputItems.extractItem(index - 1, 1, false);
				return ItemStack.EMPTY;
			} else if (10 <= index && index < 16) {// shift-click in enchantment slots
				if (!this.moveItemStackTo(slotStack, 10, 46, true)) {
					return ItemStack.EMPTY;
				}
			} else if (index < 52) {// shift-click in inventory slots
				if (!slots.get(0).hasItem() || slotStack.sameItem(slots.get(0).getItem())) {//shift click from inventory
					if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
					inputItems.fillOutputSlots(itemstack);
				}
			}

			if (slotStack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			if (slotStack.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(playerIn, slotStack);
		}
		return itemstack;
	}

	public boolean isInputLocked() {
		return this.uncrafter.isInputLocked();
	}

	public boolean[] selectedIndexes() {
		return this.uncrafter.selected;
	}

}
