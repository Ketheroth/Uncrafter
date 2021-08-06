package com.ketheroth.uncrafter;

import com.ketheroth.uncrafter.client.gui.screen.inventory.AdvancedUncrafterScreen;
import com.ketheroth.uncrafter.client.gui.screen.inventory.UncrafterScreen;
import com.ketheroth.uncrafter.common.config.Configuration;
import com.ketheroth.uncrafter.core.registry.UncrafterBlocks;
import com.ketheroth.uncrafter.core.registry.UncrafterContainerTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod(Uncrafter.MODID)
public class Uncrafter {

	public static final String MODID = "uncrafter";
	private static final Logger LOGGER = LogManager.getLogger();

	public Uncrafter() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(this::clientSetup);
		modEventBus.addListener(this::processIMC);

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Configuration.CONFIG);

		UncrafterBlocks.BLOCKS.register(modEventBus);
		UncrafterBlocks.ITEMS.register(modEventBus);
		UncrafterContainerTypes.CONTAINERS.register(modEventBus);

		MinecraftForge.EVENT_BUS.register(this);
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		MenuScreens.register(UncrafterContainerTypes.UNCRAFTER_CONTAINER.get(), UncrafterScreen::new);
		MenuScreens.register(UncrafterContainerTypes.ADVANCED_UNCRAFTER_CONTAINER.get(), AdvancedUncrafterScreen::new);
	}

	private void processIMC(final InterModProcessEvent event) {
		event.getIMCStream().filter(message -> message.method().equals("blacklistedRecipes")).forEach(message -> {
			try {
				List<?> recipes = (List<?>) message.messageSupplier().get();
				recipes.stream().map(String.class::cast).forEach(recipe -> Configuration.IMC_BLACKLIST.add(recipe));
			} catch (ClassCastException e) {
				LOGGER.error("Error receiving IMC from : " + message.modId());
			}
		});
	}

}
