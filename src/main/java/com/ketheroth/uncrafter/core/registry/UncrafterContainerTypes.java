package com.ketheroth.uncrafter.core.registry;

import com.ketheroth.uncrafter.Uncrafter;
import com.ketheroth.uncrafter.common.inventory.container.AdvancedUncrafterContainer;
import com.ketheroth.uncrafter.common.inventory.container.UncrafterContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class UncrafterContainerTypes {

	public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Uncrafter.MODID);

	public static final RegistryObject<ContainerType<UncrafterContainer>> UNCRAFTER_CONTAINER = CONTAINERS.register("uncrafter_container",
			() -> IForgeContainerType.create((windowId, inv, data) -> new UncrafterContainer(windowId, inv, inv.player, data.readBlockPos())));

	public static final RegistryObject<ContainerType<AdvancedUncrafterContainer>> ADVANCED_UNCRAFTER_CONTAINER = CONTAINERS.register("advanced_uncrafter_container",
			() -> IForgeContainerType.create((windowId, inv, data) -> new AdvancedUncrafterContainer(windowId, inv, inv.player, data.readBlockPos())));

}
