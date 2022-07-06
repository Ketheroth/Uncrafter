package com.ketheroth.uncrafter.client.gui.screen.inventory;

import com.ketheroth.uncrafter.Uncrafter;
import com.ketheroth.uncrafter.common.inventory.container.AdvancedUncrafterContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AdvancedUncrafterScreen extends ContainerScreen<AdvancedUncrafterContainer> {

	private final ResourceLocation GUI = new ResourceLocation(Uncrafter.MODID, "textures/gui/advanced_uncrafter_gui.png");

	public AdvancedUncrafterScreen(AdvancedUncrafterContainer container, PlayerInventory inventory, ITextComponent name) {
		super(container, inventory, name);
	}

	@Override
	public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(poseStack, mouseX, mouseY);
		if (this.menu.isInputLocked()) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.minecraft.getTextureManager().bind(GUI);
			this.blit(poseStack, this.leftPos + 28, this.topPos + 51, 176, 0, 3, 3);
		}
	}

	@Override
	protected void renderLabels(MatrixStack poseStack, int x, int y) {
		super.renderLabels(poseStack, x, y);
		this.font.draw(poseStack, this.title, this.titleLabelX, this.titleLabelY, 4210752);
		this.font.draw(poseStack, this.inventory.getDisplayName(), this.inventoryLabelX, this.inventoryLabelY, 4210752);
	}

	@Override
	protected void renderBg(MatrixStack poseStack, float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(GUI);
		this.blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
		boolean[] selected = this.menu.selectedIndexes();
		for (int i = 0; i < selected.length; i++) {
			if (selected[i]) {
				this.blit(poseStack, this.leftPos + 70 + i%3 *18, this.topPos + 17 + i/3*18, 176, 3, 16, 16);
			}
		}
	}

}
