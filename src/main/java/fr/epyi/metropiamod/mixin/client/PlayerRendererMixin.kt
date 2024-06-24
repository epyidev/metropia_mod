package fr.epyi.metropiamod.mixin.client

import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.PlayerRenderer
import net.minecraft.util.ResourceLocation
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Redirect

@Mixin(PlayerRenderer::class)
class PlayerRendererMixin {
    @Redirect(
        method = ["renderHand"],
        at = At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderType;entitySolid(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"
        )
    )
    private fun transparentHandRenderer(resourceLocation: ResourceLocation): RenderType {
        return RenderType.getEntityTranslucent(resourceLocation)
    }
}