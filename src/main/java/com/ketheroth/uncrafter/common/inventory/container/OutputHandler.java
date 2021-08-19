package com.ketheroth.uncrafter.common.inventory.container;

import com.ketheroth.uncrafter.common.config.Configuration;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;

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
		if (extracted >= (container.isAdvanced() ? Configuration.ADVANCED_EXTRACT_AMOUNT.get() : Configuration.EXTRACT_AMOUNT.get()) || this.isEmpty()) {
			container.getInputHandler().extractItem(0, amount, simulate);
			if (!simulate) {
				extracted = 0;
			}
		}
		if (extracted == 1 && !simulate) {
			if (container.isAdvanced() && container.getEnchantmentHandler() != null && container.getInputHandler().getStackInSlot(0).isEnchanted()) {
				//clear enchantment slots
				for (int i = 0; i < 6; i++) {
					container.getEnchantmentHandler().setStackInSlot(i, ItemStack.EMPTY);
				}
				container.getEnchantmentHandler().resetExtracting();
				//fill enchantments slots
				ArrayList<ItemStack> books = new ArrayList<>();
				EnchantmentHelper.getEnchantments(container.getInputHandler().getStackInSlot(0)).forEach((enchantment, level) -> {
					if (!enchantment.isCurse()) {
						books.add(EnchantedBookItem.createForEnchantment(new EnchantmentData(enchantment, Configuration.MINIMUM_LEVEL_FOR_ENCHANTMENTS.get() ? 1 : level)));
					}
				});
				Collections.shuffle(books);
				for (int i = 0; i < 6 && i < books.size(); i++) {
					container.getEnchantmentHandler().setStackInSlot(i, books.get(i));
				}
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

