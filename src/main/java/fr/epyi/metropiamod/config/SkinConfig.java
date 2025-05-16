package fr.epyi.metropiamod.config;

import fr.epyi.metropiamod.MetropiaMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Mod.EventBusSubscriber(modid = MetropiaMod.MOD_ID)
public class SkinConfig {

    //public static final String CATEGORY_SERVER = "server";

    public static ForgeConfigSpec SERVER_CONFIG;

    public static ForgeConfigSpec.BooleanValue ALLOW_TRANSPARENT_SKIN;

    public static ForgeConfigSpec.BooleanValue SELF_SKIN_NEEDS_OP;

    public static ForgeConfigSpec.BooleanValue OTHERS_SELF_SKIN_NEEDS_OP;

    public static ForgeConfigSpec.BooleanValue ENABLE_SKIN_SERVER_WHITELIST;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> SKIN_SERVER_WHITELIST;

    public static ForgeConfigSpec.ConfigValue<String> SKIN_VOID_SKIN_URL;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> CREATOR_BODY_TYPES;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> CREATOR_BODY_COLORS;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> CREATOR_HAIR_TYPES;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> CREATOR_BEARD_TYPES;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> CREATOR_HAIR_COLORS;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> CREATOR_EYE_TYPES;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> CREATOR_EYE_COLORS;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> CREATOR_MOUTH_TYPES;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> CREATOR_NOSE_TYPES;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        //SERVER_BUILDER.comment("Server side variable allowing transparent skins to be set.").push(CATEGORY_SERVER);

        ALLOW_TRANSPARENT_SKIN = SERVER_BUILDER.comment("Server side variable allowing transparent skins to be set")
                .define("allowTransparentSkin", false);

        SELF_SKIN_NEEDS_OP = SERVER_BUILDER.comment("Does setting their own skins need op?")
                .define("setSelfSkinsNeedsOp", false);

        OTHERS_SELF_SKIN_NEEDS_OP = SERVER_BUILDER.comment("Does setting other peoples skins need op?")
                .define("setOtherSkinsNeedsOp", true);

        ENABLE_SKIN_SERVER_WHITELIST = SERVER_BUILDER.comment("Server skin whitelist")
                .define("enableSkinServerWhitelist", false);

        SKIN_SERVER_WHITELIST = SERVER_BUILDER.comment("Server skin whitelist")
                .defineList("enforceSkinWhitelist", Collections.singletonList("https://i.imgur.com/"), (value) -> false);


        SKIN_VOID_SKIN_URL = SERVER_BUILDER.comment("A direct url pointing to a full transparent image")
                .define("voidSkinUrl", "https://metropia.lets-pop.fr/metropiamod/default/void.png");

        //SERVER_BUILDER.pop();

        CREATOR_BODY_TYPES = SERVER_BUILDER.comment("Body types for the character creator")
                .defineList("creatorBodyTypes", Collections.unmodifiableList(Arrays.asList(
                        "https://metropia.lets-pop.fr/metropiamod/default/void.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/bodyType_1.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/bodyType_2.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/bodyType_3.png"
                )), (value) -> false);

        CREATOR_BODY_COLORS = SERVER_BUILDER.comment("Body colors for the character creator")
                .defineList("creatorBodyColors", Collections.unmodifiableList(Arrays.asList(
                        "E9C8BC",
                        "DFA98F",
                        "CE8E71",
                        "D69D70",
                        "B37344",
                        "88583B",
                        "8F5B48",
                        "724837",
                        "543526"
                )), (value) -> false);

        CREATOR_HAIR_TYPES = SERVER_BUILDER.comment("Hair types for the character creator")
                .defineList("creatorHairTypes", Collections.unmodifiableList(Arrays.asList(
                        "https://metropia.lets-pop.fr/metropiamod/default/void.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/hairType_1.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/hairType_2.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/hairType_3.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/hairType_4.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/hairType_5.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/hairType_6.png"
                )), (value) -> false);

        CREATOR_BEARD_TYPES = SERVER_BUILDER.comment("Beard types for the character creator")
                .defineList("creatorBeardTypes", Collections.unmodifiableList(Arrays.asList(
                        "https://metropia.lets-pop.fr/metropiamod/default/void.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/beardType_1.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/beardType_2.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/beardType_3.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/beardType_4.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/beardType_5.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/beardType_6.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/beardType_7.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/beardType_8.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/beardType_9.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/beardType_10.png"
                )), (value) -> false);

        CREATOR_HAIR_COLORS = SERVER_BUILDER.comment("Hair colors for the character creator")
                .defineList("creatorHairColors", Collections.unmodifiableList(Arrays.asList(
                        "090806",
                        "2C222B",
                        "3B3024",
                        "4E433F",
                        "504444",
                        "6A4E42",
                        "554838",
                        "A7856A",
                        "B89778",
                        "DCD0BA",
                        "DEBC99",
                        "977961",
                        "E6CEA8",
                        "E5C8A8",
                        "A56B46",
                        "91553D",
                        "533D32",
                        "71635A",
                        "B7A69E",
                        "D6C4C2",
                        "FFF5E1",
                        "CABFB1",
                        "8D4A43",
                        "B55239"
                )), (value) -> false);

        CREATOR_EYE_TYPES = SERVER_BUILDER.comment("Eye types for the character creator")
                .defineList("creatorEyeTypes", Collections.unmodifiableList(Arrays.asList(
                        "https://metropia.lets-pop.fr/metropiamod/default/void.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/eyeType_1.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/eyeType_2.png"
                )), (value) -> false);

        CREATOR_EYE_COLORS = SERVER_BUILDER.comment("Eye colors for the character creator")
                .defineList("creatorEyeColors", Collections.unmodifiableList(Arrays.asList(
                        // Marron
                        "4A2E00",
                        "5A3D1F",
                        "3B2F1B",
                        "5C4033",
                        "4E342E",
                        // Noisette
                        "826644",
                        "8E734B",
                        "7C6C4F",
                        "9C7E56",
                        "A0785A",
                        // Vert
                        "6A8E54",
                        "738D5A",
                        "7F9E6D",
                        "86A96E",
                        "91B57D",
                        // Bleu
                        "5A87A0",
                        "507EA6",
                        "4C86C6",
                        "3E7BBB",
                        "6699CC",
                        // Gris
                        "7B8A8B",
                        "8A9B9C",
                        "929E9F",
                        "A0A9AB",
                        "B0B6B7"
                )), (value) -> false);

        CREATOR_MOUTH_TYPES = SERVER_BUILDER.comment("Mouth types for the character creator")
                .defineList("creatorMouthTypes", Collections.unmodifiableList(Arrays.asList(
                        "https://metropia.lets-pop.fr/metropiamod/default/void.png"
                )), (value) -> false);

        CREATOR_NOSE_TYPES = SERVER_BUILDER.comment("Nose types for the character creator")
                .defineList("creatorNoseTypes", Collections.unmodifiableList(Arrays.asList(
                        "https://metropia.lets-pop.fr/metropiamod/default/void.png",
                        "https://metropia.lets-pop.fr/metropiamod/default/noseType_1.png"
                )), (value) -> false);

        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    /*@SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        // add things when needed
    }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent) {
        // add things when needed
    }*/
}
