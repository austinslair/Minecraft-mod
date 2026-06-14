package com.example.backrooms.client.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void backroomsLevel0$renderBackroomsLogo(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int centerX = context.getScaledWindowWidth() / 2;
        context.drawCenteredTextWithShadow(net.minecraft.client.MinecraftClient.getInstance().textRenderer, "THE BACKROOMS", centerX, 18, 0xFFE7D875);
    }
}
