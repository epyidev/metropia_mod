package fr.epyi.metropiamod.server;

import fr.epyi.metropiamod.CustomSkinManager;
import fr.epyi.metropiamod.capabilities.SkinLocationProvider;
import fr.epyi.metropiamod.MetropiaMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MetropiaMod.MOD_ID)
public class ServerEventHook {

    public static final ResourceLocation SKIN_LOCATION = new ResourceLocation(MetropiaMod.MOD_ID, "skin");

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(SKIN_LOCATION, new SkinLocationProvider());
        }
    }

    @SubscribeEvent
    public static void playerJoin(EntityJoinWorldEvent event) {
        if(event.getEntity() instanceof PlayerEntity) {
            event.getEntity().getCapability(SkinLocationProvider.SKIN_LOC, null).ifPresent(skin -> {
                if (!skin.getSkin().isEmpty()) {
                    CustomSkinManager.setSkin((PlayerEntity) event.getEntity(),
                            skin.getSkin());
                    if(event.getEntity() instanceof ServerPlayerEntity) {
                        CustomSkinManager.sendAllToPlayer((ServerPlayerEntity) event.getEntity(), true);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void playerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        CustomSkinManager.playerLoggedOut(event.getPlayer().getUniqueID());
    }

}
