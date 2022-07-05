package com.ketheroth.uncrafter.common.blockentity;

import com.ketheroth.uncrafter.common.config.Configuration;
import com.ketheroth.uncrafter.common.inventory.container.EnchantmentHandler;
import com.ketheroth.uncrafter.common.inventory.container.InputHandler;
import com.ketheroth.uncrafter.common.inventory.container.OutputHandler;
import com.ketheroth.uncrafter.core.registry.UncrafterBlockEntities;
import com.ketheroth.uncrafter.core.registry.UncrafterBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class UncrafterBlockEntity extends BlockEntity {

	private final InputHandler input;
	private final OutputHandler output;
	private final EnchantmentHandler enchantmentOutput;
	private final Tuple<Item, List<ItemStack>> cache = new Tuple<>(null, null);
	private final boolean isAdvanced;
	private final int maxExtract;
	private int cooldown;
	public final boolean[] selected;


	public UncrafterBlockEntity(BlockPos pos, BlockState state) {
		super(UncrafterBlockEntities.UNCRAFTER.get(), pos, state);
		this.isAdvanced = state.getBlock() == UncrafterBlocks.ADVANCED_UNCRAFTER_BLOCK.get();
		this.input = new InputHandler(1, this);
		this.output = new OutputHandler(9, this);
		this.enchantmentOutput = isAdvanced ? new EnchantmentHandler(6) : null;
		this.cooldown = 0;
		this.selected = new boolean[9];
		this.maxExtract = isAdvanced ? Configuration.ADVANCED_EXTRACT_AMOUNT.get() : Configuration.EXTRACT_AMOUNT.get();
	}

	public void tick(Level level, BlockPos pos, BlockState state, UncrafterBlockEntity be) {
		this.cooldown--;
		if (this.cooldown > 0) {
			return;
		}
//		cooldown = 8;
		// import input items from container above
		importItem(level, this.worldPosition.above(), Direction.DOWN);

		// export output items to container below
		if (!this.input.getStackInSlot(0).isEmpty()) {
			Container container = HopperBlockEntity.getContainerAt(level, this.worldPosition.below());
			if (container == null || isFullContainer(container, Direction.UP)) {
				return;
			}
			int extracted = 0;
			for (int i = 0; i < this.selected.length; i++) {
				// extract item if it is selected
				if (this.selected[i]) {
					if (extractItem(container, this.output, i)) {
						extracted++;
					}
					if (extracted == this.maxExtract) {
						break;
					}
				}
			}
			if (extracted > 0) {
				// if all slots haven't been selected, we remove the items in order until the max amount is reached
				for (int i = 0; i < 9 && extracted < this.maxExtract; i++) {
					if (extractItem(container, this.output, i)) {
						extracted++;
					}
				}
			}
			// export enchantments
			if (this.isAdvanced) {
				for (int i = 0; i < 6 && i < Configuration.ENCHANTMENT_EXTRACT_AMOUNT.get(); i++) {
					extractItem(container, this.enchantmentOutput, i);
				}
			}
		}
	}

	private void importItem(Level level, BlockPos position, Direction direction) {
		Container container = HopperBlockEntity.getContainerAt(level, position);
		if (container == null || isEmptyContainer(container, direction)) {
			return;
		}
		int[] slots = getSlots(container, direction).toArray();
		for (int slot : slots) {
			ItemStack extracting = container.getItem(slot);
			if (!extracting.isEmpty() && canTakeItemFromContainer(container, extracting, slot, direction)) {
				ItemStack removed = container.removeItem(slot, 1);
				if (Configuration.isValidItem(removed.getItem())) {
					boolean imported = false;
					if (this.input.getStackInSlot(0).isEmpty()) {
						this.input.insertItem(0, removed, false);
						imported = true;
					} else if (canMergeItems(this.input.getStackInSlot(0), removed)) {
						ItemStack inputExtracted = this.input.extractItem(0, 64, false);
						int canAdd = removed.getMaxStackSize() - inputExtracted.getCount();
						int willAdd = Math.min(removed.getCount(), canAdd);
						removed.shrink(willAdd);
						inputExtracted.grow(willAdd);
						this.input.insertItem(0, inputExtracted, false);
						imported = true;
					}
					if (imported) {
						this.setChanged();
						container.setChanged();
						this.cooldown = 8;
						return;
					}
				}
				container.setItem(slot, extracting);
			}
		}
	}

	private boolean extractItem(Container container, ItemStackHandler stackHandler, int index) {
		ItemStack copy = stackHandler.getStackInSlot(index).copy();
		if (!copy.isEmpty()) {
			int[] slots = getSlots(container, Direction.UP).toArray();
			for (int slot : slots) {
				// we try to place our item in one of the slots of the container
				ItemStack stack = container.getItem(slot).copy();
				ItemStack remaining = HopperBlockEntity.addItem(null, container, copy, Direction.UP);
				if (remaining.isEmpty()) {
					// we managed to insert our item, we really remove it from the output slot
					container.setChanged();
					this.setChanged();
					stackHandler.extractItem(index, 1, false);
					this.cooldown = 8;
					return true;
				}
				// we couldn't add our item in the stack, we revert our change
				container.setItem(slot, stack);
			}
		}
		return false;
	}

	public InputHandler getInput() {
		return this.input;
	}

	public OutputHandler getOutput() {
		return this.output;
	}

	public EnchantmentHandler getEnchantmentOutput() {
		return this.enchantmentOutput;
	}

	public Tuple<Item, List<ItemStack>> getCache() {
		return this.cache;
	}

	public RecipeManager getRecipeManager() {
		return this.level.getRecipeManager();
	}

	public boolean isAdvanced() {
		return this.isAdvanced;
	}

	public boolean isInputLocked() {
		return this.output.isExtracting();
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithoutMetadata();
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("input", this.input.serializeNBT());
		tag.put("output", this.output.serializeNBT());
		if (this.isAdvanced) {
			tag.put("enchantmentOutput", this.enchantmentOutput.serializeNBT());
		}
		ArrayList<Integer> array = new ArrayList<>();
		for (int i = 0; i < this.selected.length; i++) {
			if (this.selected[i]) {
				array.add(i);
			}
		}
		tag.putIntArray("selected", array);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.input.deserializeNBT(tag);
		this.output.deserializeNBT(tag);
		if (this.isAdvanced) {
			this.enchantmentOutput.deserializeNBT(tag);
		}
		int[] selected = tag.getIntArray("selected");
		for (int value : selected) {
			this.selected[value] = true;
		}
	}

	private static boolean isFullContainer(Container container, Direction direction) {
		return getSlots(container, direction).allMatch((slot) -> {
			ItemStack itemstack = container.getItem(slot);
			return itemstack.getCount() >= itemstack.getMaxStackSize();
		});
	}

	private static boolean isEmptyContainer(Container container, Direction direction) {
		return getSlots(container, direction).allMatch((slot) -> container.getItem(slot).isEmpty());
	}

	private static IntStream getSlots(Container container, Direction direction) {
		return container instanceof WorldlyContainer ? IntStream.of(((WorldlyContainer) container).getSlotsForFace(direction)) : IntStream.range(0, container.getContainerSize());
	}

	private static boolean canTakeItemFromContainer(Container container, ItemStack stack, int slot, Direction direction) {
		return !(container instanceof WorldlyContainer) || ((WorldlyContainer) container).canTakeItemThroughFace(slot, stack, direction);
	}

	private static boolean canMergeItems(ItemStack stack1, ItemStack stack2) {
		if (!stack1.is(stack2.getItem())) {
			return false;
		} else if (stack1.getDamageValue() != stack2.getDamageValue()) {
			return false;
		} else if (stack1.getCount() > stack1.getMaxStackSize()) {
			return false;
		} else {
			return ItemStack.tagMatches(stack1, stack2);
		}
	}

}
