package com.ketheroth.uncrafter.common.inventory.container;

import com.ketheroth.uncrafter.common.config.Configuration;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class EnchantmentHandler extends ItemStackHandler {

	private final IUncrafterContainer container;
	private int extracted;

	public EnchantmentHandler(int size, IUncrafterContainer container) {
		super(size);
		this.container = container;
		this.extracted = 0;
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack extractedStack = super.extractItem(slot, amount, simulate);
		if (!simulate) {
			extracted++;
		}
		if (extracted >= Configuration.ENCHANTMENT_EXTRACT_AMOUNT.get() || this.isEmpty()) {
			for (int i = 0; i < this.getSlots(); i++) {
				this.setStackInSlot(i, ItemStack.EMPTY);
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

	public void resetExtracting() {
		this.extracted = 0;
	}

	public boolean isExtracting() {
		return this.extracted > 0;
	}

}
