package com.ketheroth.uncrafter.core.registry;

import com.ketheroth.uncrafter.Uncrafter;
import com.ketheroth.uncrafter.common.inventory.container.AdvancedUncrafterContainer;
import com.ketheroth.uncrafter.common.inventory.container.UncrafterContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class UncrafterContainerTypes {

	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Uncrafter.MODID);

	public static final RegistryObject<MenuType<UncrafterContainer>> UNCRAFTER_CONTAINER = CONTAINERS.register("uncrafter_container",
			() -> IForgeMenuType.create((windowId, inv, data) -> new UncrafterContainer(windowId, inv, inv.player, data.readBlockPos())));

	public static final RegistryObject<MenuType<AdvancedUncrafterContainer>> ADVANCED_UNCRAFTER_CONTAINER = CONTAINERS.register("advanced_uncrafter_container",
			() -> IForgeMenuType.create((windowId, inv, data) -> new AdvancedUncrafterContainer(windowId, inv, inv.player, data.readBlockPos())));

}
