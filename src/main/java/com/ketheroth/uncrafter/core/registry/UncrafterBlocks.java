package com.ketheroth.uncrafter.core.registry;

import com.ketheroth.uncrafter.Uncrafter;
import com.ketheroth.uncrafter.common.bock.UncrafterBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class UncrafterBlocks {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Uncrafter.MODID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Uncrafter.MODID);

	public static final RegistryObject<Block> UNCRAFTER_BLOCK = BLOCKS.register("uncrafter", () -> new UncrafterBlock(AbstractBlock.Properties.of(Material.STONE, MaterialColor.SAND).requiresCorrectToolForDrops().strength(3.0F, 9.0F)));
	public static final RegistryObject<Item> UNCRAFTER_ITEM = ITEMS.register("uncrafter", () -> new BlockItem(UNCRAFTER_BLOCK.get(), new Item.Properties().tab(ItemGroup.TAB_DECORATIONS)));

}
