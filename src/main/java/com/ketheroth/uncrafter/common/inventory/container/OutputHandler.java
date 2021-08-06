package com.ketheroth.uncrafter.common.inventory.container;

import com.ketheroth.uncrafter.common.config.Configuration;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class OutputHandler extends ItemStackHandler {

	private final IUncrafterContainer container;
	private int extracted;

	public OutputHandler(int size, IUncrafterContainer container) {
		super(size);
		extracted = 0;
		this.container = container;
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
			container.getInputHandler().extractItem(0, amount, simulate);
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

	public boolean isExtracting() {
		return extracted != 0;
	}

}

