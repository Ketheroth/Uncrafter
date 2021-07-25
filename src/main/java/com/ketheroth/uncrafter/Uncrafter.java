package com.ketheroth.uncrafter;

import com.ketheroth.uncrafter.client.gui.screen.inventory.UncrafterScreen;
import com.ketheroth.uncrafter.core.registry.UncrafterBlocks;
import com.ketheroth.uncrafter.core.registry.UncrafterContainerTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Uncrafter.MODID)
public class Uncrafter {

	public static final String MODID = "uncrafter";
	private static final Logger LOGGER = LogManager.getLogger();

	public Uncrafter() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

//		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::clientSetup);
//		modEventBus.addListener(this::enqueueIMC);
//		modEventBus.addListener(this::processIMC);

		UncrafterBlocks.BLOCKS.register(modEventBus);
		UncrafterBlocks.ITEMS.register(modEventBus);
		UncrafterContainerTypes.CONTAINERS.register(modEventBus);

		MinecraftForge.EVENT_BUS.register(this);
	}

//	private void setup(final FMLCommonSetupEvent event) {
//
//	}

	private void clientSetup(final FMLClientSetupEvent event) {
		MenuScreens.register(UncrafterContainerTypes.UNCRAFTER_CONTAINER.get(), UncrafterScreen::new);
	}

//	private void enqueueIMC(final InterModEnqueueEvent event) {
//		// some example code to dispatch IMC to another mod
//		InterModComms.sendTo("examplemod", "helloworld", () -> {
//			LOGGER.info("Hello world from the MDK");
//			return "Hello world";
//		});
//	}
//
//	private void processIMC(final InterModProcessEvent event) {
//		// some example code to receive and process InterModComms from other mods
//		LOGGER.info("Got IMC {}", event.getIMCStream().
//				map(m -> m.messageSupplier().get()).
//				collect(Collectors.toList()));
//	}

}
