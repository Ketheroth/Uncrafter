package com.ketheroth.uncrafter.core.registry;

import com.ketheroth.uncrafter.Uncrafter;
import com.ketheroth.uncrafter.common.inventory.container.UncrafterContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class UncrafterContainerTypes {

	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Uncrafter.MODID);

	public static final RegistryObject<MenuType<UncrafterContainer>> UNCRAFTER_CONTAINER = CONTAINERS.register("uncrafter_container",
			() -> IForgeContainerType.create((windowId, inv, data) -> new UncrafterContainer(windowId, inv, inv.player, data.readBlockPos())));

}
