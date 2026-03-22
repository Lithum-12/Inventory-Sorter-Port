package cpw.mods.inventorysorter.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public class SortButton extends Button {
    
    private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation("inventorysorter", "textures/gui/sort_button.png");
    
    public SortButton(int x, int y, OnPress onPress) {
        super(x, y, 18, 18, Component.translatable("inventorysorter.gui.sort"), onPress, Button.DEFAULT_NARRATION);
    }
    
    @Override
    public void renderWidget(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getAssetManager().hasResource(BUTTON_TEXTURE)) {
            // Render with custom texture if available
            RenderSystem.setShaderTexture(0, BUTTON_TEXTURE);
            this.isHovered = pMouseX >= this.getX() && pMouseY >= this.getY() && pMouseX < this.getX() + this.width && pMouseY < this.getY() + this.height;
            int v = this.isHovered ? 18 : 0;
            if (this.isMouseDown(pMouseX, pMouseY)) {
                v = 36;
            }
            blit(pPoseStack, this.getX(), this.getY(), 0, v, this.width, this.height, 18, 54);
        } else {
            // Fallback to default button rendering
            super.renderWidget(pPoseStack, pMouseX, pMouseY, pPartialTicks);
        }
    }
    
    @Override
    public void onPress() {
        super.onPress();
        // Play click sound
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
