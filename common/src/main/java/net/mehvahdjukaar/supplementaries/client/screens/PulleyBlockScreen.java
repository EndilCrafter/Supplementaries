package net.mehvahdjukaar.supplementaries.client.screens;

import net.mehvahdjukaar.supplementaries.common.inventories.PulleyBlockContainerMenu;
import net.mehvahdjukaar.supplementaries.integration.CompatHandler;
import net.mehvahdjukaar.supplementaries.integration.ImmediatelyFastCompat;
import net.mehvahdjukaar.supplementaries.reg.ModTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;


public class PulleyBlockScreen extends AbstractContainerScreen<PulleyBlockContainerMenu> {

    private final CyclingSlotBackground slotBG = new CyclingSlotBackground(0);

    public PulleyBlockScreen(PulleyBlockContainerMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int x, int y) {
        if (CompatHandler.IMMEDIATELY_FAST) ImmediatelyFastCompat.startBatching();
        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;
        graphics.blit(ModTextures.PULLEY_BLOCK_GUI_TEXTURE, k, l, 0, 0, this.imageWidth, this.imageHeight);
        this.slotBG.render(this.menu, graphics, partialTicks, this.leftPos, this.topPos);
        if (CompatHandler.IMMEDIATELY_FAST) ImmediatelyFastCompat.endBatching();
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }
        return super.keyPressed(key, b, c);
    }

    @Override
    public void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.slotBG.tick(ModTextures.PULLEY_SLOT_ICONS);
    }
}