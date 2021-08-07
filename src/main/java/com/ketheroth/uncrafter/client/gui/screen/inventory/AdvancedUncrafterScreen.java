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
			this.blit(poseStack, (this.width - this.imageWidth) / 2 + 28, (this.height - this.imageHeight) / 2 + 51, 176, 0, 3, 3);
		}
	}

	@Override
	protected void renderLabels(MatrixStack poseStack, int x, int y) {
		super.renderLabels(poseStack, x, y);
		this.font.draw(poseStack, this.title, this.titleLabelX, this.titleLabelY, 4210752);
		this.font.draw(poseStack, inventory.getDisplayName(), this.inventoryLabelX, this.inventoryLabelY, 4210752);
	}

	@Override
	protected void renderBg(MatrixStack poseStack, float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(GUI);
		int relX = (this.width - this.imageWidth) / 2;
		int relY = (this.height - this.imageHeight) / 2;
		this.blit(poseStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
	}

}
