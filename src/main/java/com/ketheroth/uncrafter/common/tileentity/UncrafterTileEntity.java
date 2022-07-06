package com.ketheroth.uncrafter.common.tileentity;

import com.ketheroth.uncrafter.common.config.Configuration;
import com.ketheroth.uncrafter.common.inventory.container.EnchantmentHandler;
import com.ketheroth.uncrafter.common.inventory.container.InputHandler;
import com.ketheroth.uncrafter.common.inventory.container.OutputHandler;
import com.ketheroth.uncrafter.core.registry.UncrafterBlocks;
import com.ketheroth.uncrafter.core.registry.UncrafterTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class UncrafterTileEntity extends TileEntity implements ITickableTileEntity {

	private final InputHandler input;
	private final OutputHandler output;
	private final EnchantmentHandler enchantmentOutput;
	private Tuple<ItemStack, List<ItemStack>> cache = new Tuple<>(ItemStack.EMPTY, new ArrayList<>());
	private boolean isAdvanced;
	private int maxExtract;
	private int cooldown;
	public final boolean[] selected;


	public UncrafterTileEntity() {
		super(UncrafterTileEntities.UNCRAFTER.get());
		this.selected = new boolean[9];
		this.cooldown = 0;
		this.input = new InputHandler(1, this);
		this.output = new OutputHandler(9, this);
		this.enchantmentOutput = new EnchantmentHandler(6);
	}

	public void tick() {
		this.isAdvanced = this.level.getBlockState(this.worldPosition).getBlock() == UncrafterBlocks.ADVANCED_UNCRAFTER_BLOCK.get();
		this.maxExtract = isAdvanced ? Configuration.ADVANCED_EXTRACT_AMOUNT.get() : Configuration.EXTRACT_AMOUNT.get();
		this.cooldown--;
		if (this.cooldown > 0) {
			return;
		}
		// import input items from container above
		importItem(this.level, this.worldPosition.above(), Direction.DOWN);

		// export output items to container below
		if (!this.input.getStackInSlot(0).isEmpty()) {
			IInventory container = HopperTileEntity.getContainerAt(this.level, this.worldPosition.below());
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

	private void importItem(World level, BlockPos position, Direction direction) {
		IInventory container = HopperTileEntity.getContainerAt(level, position);
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

	private boolean extractItem(IInventory container, ItemStackHandler stackHandler, int index) {
		ItemStack copy = stackHandler.getStackInSlot(index).copy();
		if (!copy.isEmpty()) {
			int[] slots = getSlots(container, Direction.UP).toArray();
			for (int slot : slots) {
				// we try to place our item in one of the slots of the container
				ItemStack stack = container.getItem(slot).copy();
				ItemStack remaining = HopperTileEntity.addItem(null, container, copy, Direction.UP);
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

	public Tuple<ItemStack, List<ItemStack>> getCache() {
		return this.cache;
	}

	public void setCache(List<ItemStack> b) {
		this.cache = new Tuple<>(this.cache.getA(), b);
	}

	public void setCache(ItemStack a, List<ItemStack> b) {
		this.cache = new Tuple<>(a, b);
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
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.worldPosition, -1, this.getUpdateTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.save(new CompoundNBT());
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);
		tag.put("input", this.input.serializeNBT());
		tag.put("output", this.output.serializeNBT());
		tag.put("enchantmentOutput", this.enchantmentOutput.serializeNBT());
		ArrayList<Integer> array = new ArrayList<>();
		for (int i = 0; i < this.selected.length; i++) {
			if (this.selected[i]) {
				array.add(i);
			}
		}
		tag.putIntArray("selected", array);
		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);
		this.input.deserializeNBT(tag.getCompound("input"));
		this.output.deserializeNBT(tag.getCompound("output"));
		this.enchantmentOutput.deserializeNBT(tag.getCompound("enchantmentOutput"));
		int[] selected = tag.getIntArray("selected");
		for (int value : selected) {
			this.selected[value] = true;
		}
	}

	private static boolean isFullContainer(IInventory container, Direction direction) {
		return getSlots(container, direction).allMatch((slot) -> {
			ItemStack itemstack = container.getItem(slot);
			return itemstack.getCount() >= itemstack.getMaxStackSize();
		});
	}

	private static boolean isEmptyContainer(IInventory container, Direction direction) {
		return getSlots(container, direction).allMatch((slot) -> container.getItem(slot).isEmpty());
	}

	private static IntStream getSlots(IInventory container, Direction direction) {
		return container instanceof ISidedInventory ? IntStream.of(((ISidedInventory) container).getSlotsForFace(direction)) : IntStream.range(0, container.getContainerSize());
	}

	private static boolean canTakeItemFromContainer(IInventory container, ItemStack stack, int slot, Direction direction) {
		return !(container instanceof ISidedInventory) || ((ISidedInventory) container).canTakeItemThroughFace(slot, stack, direction);
	}

	private static boolean canMergeItems(ItemStack stack1, ItemStack stack2) {
		if (!stack1.sameItem(stack2)) {
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
