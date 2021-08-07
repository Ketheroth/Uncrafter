package com.ketheroth.uncrafter.common.bock;

import com.ketheroth.uncrafter.common.inventory.container.AdvancedUncrafterContainer;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AdvancedUncrafterBlock extends Block {

	public AdvancedUncrafterBlock(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (level.isClientSide) {
			return ActionResultType.SUCCESS;
		}

		INamedContainerProvider provider = new INamedContainerProvider() {
			@Override
			public ITextComponent getDisplayName() {
				return new TranslationTextComponent("screen.uncrafter.advanced_uncrafter_inventory");
			}

			@Override
			public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
				return new AdvancedUncrafterContainer(windowId, inventory, player, pos);
			}
		};

		NetworkHooks.openGui((ServerPlayerEntity) player, provider, buf -> buf.writeBlockPos(pos));
		return ActionResultType.SUCCESS;
	}

}
