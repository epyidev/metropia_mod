package fr.epyi.metropiamod.config

import fr.epyi.metropiamod.MetropiaMod
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue
import net.minecraftforge.fml.common.Mod.EventBusSubscriber

@EventBusSubscriber(modid = MetropiaMod.MOD_ID)
object SkinConfig {
    //public static final String CATEGORY_SERVER = "server";
    var SERVER_CONFIG: ForgeConfigSpec

    var ALLOW_TRANSPARENT_SKIN: ForgeConfigSpec.BooleanValue

    var SELF_SKIN_NEEDS_OP: ForgeConfigSpec.BooleanValue

    var OTHERS_SELF_SKIN_NEEDS_OP: ForgeConfigSpec.BooleanValue

    var ENABLE_SKIN_SERVER_WHITELIST: ForgeConfigSpec.BooleanValue

    var SKIN_SERVER_WHITELIST: ConfigValue<List<String>>

    init {
        val SERVER_BUILDER = ForgeConfigSpec.Builder()

        //SERVER_BUILDER.comment("Server side variable allowing transparent skins to be set.").push(CATEGORY_SERVER);
        ALLOW_TRANSPARENT_SKIN = SERVER_BUILDER.comment("Server side variable allowing transparent skins to be set")
            .define("allowTransparentSkin", false)

        SELF_SKIN_NEEDS_OP = SERVER_BUILDER.comment("Does setting their own skins need op?")
            .define("setSelfSkinsNeedsOp", false)

        OTHERS_SELF_SKIN_NEEDS_OP = SERVER_BUILDER.comment("Does setting other peoples skins need op?")
            .define("setOtherSkinsNeedsOp", true)

        ENABLE_SKIN_SERVER_WHITELIST = SERVER_BUILDER.comment("Server skin whitelist")
            .define("enableSkinServerWhitelist", false)

        SKIN_SERVER_WHITELIST = SERVER_BUILDER.comment("Server skin whitelist")
            .defineList(
                "enforceSkinWhitelist", listOf("https://i.imgur.com/")
            ) { value: Any? -> false }

        //SERVER_BUILDER.pop();
        SERVER_CONFIG = SERVER_BUILDER.build()
    } /*@SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        // add things when needed
    }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent) {
        // add things when needed
    }*/
}