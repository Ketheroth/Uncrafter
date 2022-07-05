package com.ketheroth.uncrafter.common.bock;

import com.ketheroth.uncrafter.common.blockentity.UncrafterBlockEntity;
import com.ketheroth.uncrafter.common.inventory.container.UncrafterContainer;
import com.ketheroth.uncrafter.core.registry.UncrafterBlockEntities;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class UncrafterBlock extends Block implements EntityBlock {

	public UncrafterBlock(Properties properties) {
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
				return Component.translatable("screen.uncrafter.uncrafter_inventory");
			}

			@Override
			public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
				return new UncrafterContainer(windowId, inventory, player, pos);
			}
		};

		NetworkHooks.openGui((ServerPlayer) player, provider, buf -> buf.writeBlockPos(pos));
		return InteractionResult.SUCCESS;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new UncrafterBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return blockEntityType == UncrafterBlockEntities.UNCRAFTER.get() ? (level1, pos, state1, be) -> ((UncrafterBlockEntity) be).tick(level1, pos, state1, (UncrafterBlockEntity) be) : null;
	}

}
