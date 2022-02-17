package com.ketheroth.uncrafter.common.inventory.container;

import com.ketheroth.uncrafter.common.config.Configuration;
import com.ketheroth.uncrafter.core.registry.UncrafterContainerTypes;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

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
	private final EnchantmentHandler enchantmentHandler;
	private Tuple<Item, List<ItemStack>> cache = new Tuple<>(null, null);

	public AdvancedUncrafterContainer(int windowId, Inventory playerInventory, Player player, BlockPos pos) {
		super(UncrafterContainerTypes.ADVANCED_UNCRAFTER_CONTAINER.get(), windowId);
		this.pos = pos;
		this.player = player;
		this.playerInventory = new InvWrapper(playerInventory);
		this.outputItems = new OutputHandler(9, this);
		this.inputItems = new InputHandler(1, this);
		this.enchantmentHandler = new EnchantmentHandler(6);

		//layout input inventory
		addSlot(new SlotItemHandler(this.inputItems, 0, 12, 35) {
			@Override
			public boolean mayPlace(@NotNull ItemStack stack) {
				if (stack.getItem().getRegistryName() == null) {
					return false;
				}
				String name = stack.getItem().getRegistryName().toString();
				if (Configuration.WHITELIST.get().isEmpty()) {
					return !Configuration.BLACKLIST.get().contains(name) && !Configuration.IMC_BLACKLIST.contains(name);
				}
				return Configuration.WHITELIST.get().contains(name);
			}

			@Override
			public boolean mayPickup(Player playerIn) {
				return !AdvancedUncrafterContainer.this.isInputLocked() && super.mayPickup(playerIn);
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
	public boolean stillValid(Player player) {
		return player.distanceToSqr(this.pos.getX(), this.pos.getY(), this.pos.getZ()) <= 16.0D;
	}

	@Override
	public void removed(Player player) {
		if (player instanceof ServerPlayer) {
			ItemStack itemstack = this.inputItems.extractItem(0, 64, false);
			if (!itemstack.isEmpty()) {
				if (!this.outputItems.isExtracting() && !this.enchantmentHandler.isExtracting()) {
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
				// nothing happens
			} else if (10 <= index && index < 16 ) {// shift-click in enchantment slots
				// nothing happens
			} else if (index < 52) {// shift-click in inventory slots
				if (!slots.get(0).hasItem() || slotStack.is(slots.get(0).getItem().getItem())) {//shift click from inventory
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

	@Override
	public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
		return false;
	}

	@Override
	public boolean isInputLocked() {
		return this.outputItems.isExtracting();
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
	public Tuple<Item, List<ItemStack>> getCache() {
		return this.cache;
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
