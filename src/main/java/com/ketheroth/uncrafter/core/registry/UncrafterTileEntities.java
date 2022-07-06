package com.ketheroth.uncrafter.core.registry;

import com.ketheroth.uncrafter.Uncrafter;
import com.ketheroth.uncrafter.common.tileentity.UncrafterTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class UncrafterTileEntities {

	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Uncrafter.MODID);

	public static final RegistryObject<TileEntityType<UncrafterTileEntity>> UNCRAFTER = TILE_ENTITIES.register("uncrafter",
			() -> TileEntityType.Builder.of(UncrafterTileEntity::new, UncrafterBlocks.UNCRAFTER_BLOCK.get(), UncrafterBlocks.ADVANCED_UNCRAFTER_BLOCK.get()).build(null));

}
