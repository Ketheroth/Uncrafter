package com.ketheroth.uncrafter.core.registry;

import com.ketheroth.uncrafter.Uncrafter;
import com.ketheroth.uncrafter.common.blockentity.UncrafterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class UncrafterBlockEntities {

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Uncrafter.MODID);

	public static final RegistryObject<BlockEntityType<UncrafterBlockEntity>> UNCRAFTER = BLOCK_ENTITIES.register("uncrafter",
			() -> BlockEntityType.Builder.of(UncrafterBlockEntity::new, UncrafterBlocks.UNCRAFTER_BLOCK.get(), UncrafterBlocks.ADVANCED_UNCRAFTER_BLOCK.get()).build(null));

}
