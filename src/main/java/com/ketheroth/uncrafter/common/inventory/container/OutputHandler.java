package com.ketheroth.uncrafter.common.inventory.container;

import com.ketheroth.uncrafter.common.config.Configuration;
import com.ketheroth.uncrafter.common.tileentity.UncrafterTileEntity;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;

public class OutputHandler extends ItemStackHandler {

	private final UncrafterTileEntity uncrafterTileEntity;
	private int extracted;

	public OutputHandler(int size, UncrafterTileEntity uncrafterTileEntity) {
		super(size);
		extracted = 0;
		this.uncrafterTileEntity = uncrafterTileEntity;
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
		if (extracted >= (uncrafterTileEntity.isAdvanced() ? Configuration.ADVANCED_EXTRACT_AMOUNT.get() : Configuration.EXTRACT_AMOUNT.get()) || this.isEmpty()) {
			uncrafterTileEntity.getInput().extractItem(0, amount, simulate);
			if (!simulate) {
				extracted = 0;
			}
		}
		if (extracted == 1 && !simulate) {
			if (uncrafterTileEntity.isAdvanced() && uncrafterTileEntity.getEnchantmentOutput() != null && uncrafterTileEntity.getInput().getStackInSlot(0).isEnchanted()) {
				//clear enchantment slots
				for (int i = 0; i < 6; i++) {
					uncrafterTileEntity.getEnchantmentOutput().setStackInSlot(i, ItemStack.EMPTY);
				}
				uncrafterTileEntity.getEnchantmentOutput().resetExtracting();
				//fill enchantments slots
				ArrayList<ItemStack> books = new ArrayList<>();
				EnchantmentHelper.getEnchantments(uncrafterTileEntity.getInput().getStackInSlot(0)).forEach((enchantment, level) -> {
					if (!enchantment.isCurse()) {
						books.add(EnchantedBookItem.createForEnchantment(new EnchantmentData(enchantment, Configuration.MINIMUM_LEVEL_FOR_ENCHANTMENTS.get() ? 1 : level)));
					}
				});
				Collections.shuffle(books);
				for (int i = 0; i < 6 && i < books.size(); i++) {
					uncrafterTileEntity.getEnchantmentOutput().setStackInSlot(i, books.get(i));
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

