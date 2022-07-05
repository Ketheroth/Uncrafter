package com.ketheroth.uncrafter.client.gui.screen.inventory;

import com.ketheroth.uncrafter.Uncrafter;
import com.ketheroth.uncrafter.common.inventory.container.AdvancedUncrafterContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AdvancedUncrafterScreen extends AbstractContainerScreen<AdvancedUncrafterContainer> {

	private final ResourceLocation GUI = new ResourceLocation(Uncrafter.MODID, "textures/gui/advanced_uncrafter_gui.png");

	public AdvancedUncrafterScreen(AdvancedUncrafterContainer container, Inventory inventory, Component name) {
		super(container, inventory, name);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(poseStack, mouseX, mouseY);
		if (this.menu.isInputLocked()) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, GUI);
			this.blit(poseStack, this.leftPos + 28, this.topPos + 51, 176, 0, 3, 3);
		}
	}

	@Override
	protected void renderLabels(PoseStack poseStack, int x, int y) {
		super.renderLabels(poseStack, x, y);
		this.font.draw(poseStack, this.title, this.titleLabelX, this.titleLabelY, 4210752);
		this.font.draw(poseStack, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int x, int y) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, GUI);
		int relX = (this.width - this.imageWidth) / 2;
		int relY = (this.height - this.imageHeight) / 2;
		this.blit(poseStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
		boolean[] selected = this.menu.selectedIndexes();
		for (int i = 0; i < selected.length; i++) {
			if (selected[i]) {
				this.blit(poseStack, this.leftPos + 70 + i%3 *18, this.topPos + 17 + i/3*18, 176, 3, 16, 16);
			}
		}
	}

}
