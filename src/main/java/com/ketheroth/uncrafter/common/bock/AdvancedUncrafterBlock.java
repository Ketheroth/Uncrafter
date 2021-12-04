package com.ketheroth.uncrafter.common.bock;

import com.ketheroth.uncrafter.common.inventory.container.AdvancedUncrafterContainer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AdvancedUncrafterBlock extends Block {

	public AdvancedUncrafterBlock(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}

		MenuProvider provider = new MenuProvider() {
			@Override
			public Component getDisplayName() {
				return new TranslatableComponent("screen.uncrafter.advanced_uncrafter_inventory");
			}

			@Override
			public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
				return new AdvancedUncrafterContainer(windowId, inventory, player, pos);
			}
		};

		NetworkHooks.openGui((ServerPlayer) player, provider, buf -> buf.writeBlockPos(pos));
		return InteractionResult.SUCCESS;
	}

}
