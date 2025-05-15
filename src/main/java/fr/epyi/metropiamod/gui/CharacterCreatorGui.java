package fr.epyi.metropiamod.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import fr.epyi.metropiamod.config.SkinConfig;
import fr.epyi.metropiamod.events.ModSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

import static net.minecraft.client.Minecraft.getInstance;
import static net.minecraft.client.gui.screen.inventory.InventoryScreen.drawEntityOnScreen;

public class CharacterCreatorGui extends ContainerScreen<CharacterCreatorContainer> {

    public static double scrollSpeed = 0.0;
    public static double scrollPosition = 0.0;
    public static double maxScrollPosition = 0.0;
    public static double minScrollPosition = -1000.0;
    public static double scrollDelta = 0.0;
    public static boolean clicked = false;

    public CharacterCreatorGui(CharacterCreatorContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

    List<? extends String> bodyTypes = SkinConfig.CREATOR_BODY_TYPES.get();
    int selectedBodyType = 0;

    List<? extends String> bodyColors = SkinConfig.CREATOR_BODY_COLORS.get();
    int selectedBodyColor = 0;

    List<? extends String> hairTypes = SkinConfig.CREATOR_HAIR_TYPES.get();
    int selectedHairType = 0;

    List<? extends String> beardTypes = SkinConfig.CREATOR_BEARD_TYPES.get();
    int selectedBeardType = 0;

    List<? extends String> hairColors = SkinConfig.CREATOR_HAIR_COLORS.get();
    int selectedHairColor = 0;

    List<? extends String> eyeTypes = SkinConfig.CREATOR_EYE_TYPES.get();
    int selectedEyeType = 0;

    List<? extends String> eyeColors = SkinConfig.CREATOR_EYE_COLORS.get();
    int selectedEyeColor = 0;

    List<? extends String> mouthTypes = SkinConfig.CREATOR_MOUTH_TYPES.get();
    int selectedMouthType = 0;

    List<? extends String> noseTypes = SkinConfig.CREATOR_NOSE_TYPES.get();
    int selectedNoseType = 0;

    int strokeSize = 1; // Size of the gray stroke
    int hoveredIndex = -1;


    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // Gérer le défilement
        if (scrollSpeed > 1)  {
            scrollPosition += scrollDelta * scrollSpeed;
            scrollSpeed *= 0.9;
        } else {
            scrollSpeed = 0;
        }

        // Limiter le défilement
        if (scrollPosition > maxScrollPosition) {
            scrollPosition = maxScrollPosition;
        } else if (scrollPosition < minScrollPosition) {
            scrollPosition = minScrollPosition;
        }

        // Appliquer le décalage de défilement
        int scrollOffset = (int) scrollPosition;

        // Dessiner l'arrière-plan
        fill(matrixStack, 0, 0, this.width, this.height, 0x9d000000);
        font.drawString(matrixStack, "PERSONNALISATION DE VOTRE PERSONNAGE", (this.width / 2f - (float) font.getStringWidth("PERSONNALISATION DE VOTRE PERSONNAGE") / 2), 10, 0xFFFFFFFF);

        // Section des morphologies
        font.drawString(matrixStack, "Type de morphologie", 40, 40 + scrollOffset, 0xFFFFFFFF);
        int bodyCaseSize = 15; // Taille des cases pour les morphologies
        int bodySpaceBetween = 3; // Espacement entre les cases
        int bodyCasesPerRow = 10; // Nombre de cases par ligne

        for (int i = 0; i < bodyTypes.size(); i++) {
            int col = i % bodyCasesPerRow;
            int x = 40 + col * (bodyCaseSize + bodySpaceBetween);
            int y = 55 + scrollOffset;

            boolean isHovering = mouseX >= x - strokeSize && mouseX <= x + bodyCaseSize + strokeSize && mouseY >= y - strokeSize && mouseY <= y + bodyCaseSize + strokeSize;

            // Dessiner le contour gris
            int bodyStrokeColor = (isHovering || selectedBodyType == i) ? 0xFFFFFFFF : 0xFF808080;
            fill(matrixStack, x - strokeSize, y - strokeSize, x + bodyCaseSize + strokeSize, y + bodyCaseSize + strokeSize, bodyStrokeColor);

            // Dessiner la case
            fill(matrixStack, x, y, x + bodyCaseSize, y + bodyCaseSize, 0xFF000000);

            // Afficher l'index
            String bodyIndex = String.valueOf(i + 1);
            font.drawString(matrixStack, bodyIndex, x + (bodyCaseSize / 2f) - (font.getStringWidth(bodyIndex) / 2f), y + (bodyCaseSize / 2f) - (font.FONT_HEIGHT / 2f), 0xFFFFFFFF);

            // Selectionner la case
            if (isHovering && clicked) {
                clicked = false;
                getInstance().player.playSound(ModSoundEvents.TICK.get(), 0.7F, 1F);
                selectedBodyType = i;
            }

            // Jouer son quand hover
            if (isHovering && hoveredIndex != i) {
                hoveredIndex = i;
                Minecraft.getInstance().player.playSound(ModSoundEvents.HOVER.get(), 0.2F, 0.8F);
            }
        }

        // Section des couleurs de peau
        int bodySectionHeight = 55 + (int) Math.ceil((double) bodyTypes.size() / bodyCasesPerRow) * (bodyCaseSize + bodySpaceBetween);
        font.drawString(matrixStack, "Teinte de la peau", 40, bodySectionHeight + 15 + scrollOffset, 0xFFFFFFFF);
        int skinColorSize = 15; // Taille des blocs de couleur
        int skinSpaceBetween = 3; // Espacement entre les blocs
        int skinColorsPerRow = 10; // Nombre de couleurs par ligne

        for (int i = 0; i < bodyColors.size(); i++) {
            int row = i / skinColorsPerRow;
            int col = i % skinColorsPerRow;
            int x = 40 + col * (skinColorSize + skinSpaceBetween);
            int y = scrollOffset + bodySectionHeight + 30 + row * (skinColorSize + skinSpaceBetween);

            boolean isHovering = mouseX >= x - strokeSize && mouseX <= x + skinColorSize + strokeSize && mouseY >= y - strokeSize && mouseY <= y + skinColorSize + strokeSize;

            // Dessiner le contour gris
            int skinStrokeColor = (isHovering || selectedBodyColor == i) ? 0xFFFFFFFF : 0xFF808080;
            fill(matrixStack, x - strokeSize, y - strokeSize, x + skinColorSize + strokeSize, y + skinColorSize + strokeSize, skinStrokeColor);

            // Dessiner le bloc de couleur
            fill(matrixStack, x, y, x + skinColorSize, y + skinColorSize, (int) Long.parseLong("FF" + bodyColors.get(i), 16));

            // Selectionner la case
            if (isHovering && clicked) {
                clicked = false;
                getInstance().player.playSound(ModSoundEvents.TICK.get(), 0.7F, 1F);
                selectedBodyColor = i;
            }

            // Jouer son quand hover
            if (isHovering && hoveredIndex != i) {
                hoveredIndex = i;
                Minecraft.getInstance().player.playSound(ModSoundEvents.HOVER.get(), 0.2F, 0.8F);
            }
        }

        // Section des cheveux
        int bodyColorsSectionHeight = bodySectionHeight + 30 + (int) Math.ceil((double) bodyColors.size() / skinColorsPerRow) * (skinColorSize + skinSpaceBetween);
        font.drawString(matrixStack, "Type de cheveux", 40, bodyColorsSectionHeight + 15 + scrollOffset, 0xFFFFFFFF);
        int hairCaseSize = 15;
        int hairSpaceBetween = 3;
        int hairCasesPerRow = 10;

        for (int i = 0; i < hairTypes.size(); i++) {
            int row = i / hairCasesPerRow;
            int col = i % hairCasesPerRow;
            int x = 40 + col * (hairCaseSize + hairSpaceBetween);
            int y = scrollOffset + bodyColorsSectionHeight + 30 + row * (hairCaseSize + hairSpaceBetween);

            boolean isHovering = mouseX >= x - strokeSize && mouseX <= x + hairCaseSize + strokeSize && mouseY >= y - strokeSize && mouseY <= y + hairCaseSize + strokeSize;

            int hairStrokeColor = (isHovering || selectedHairType == i) ? 0xFFFFFFFF : 0xFF808080;
            fill(matrixStack, x - strokeSize, y - strokeSize, x + hairCaseSize + strokeSize, y + hairCaseSize + strokeSize, hairStrokeColor);
            fill(matrixStack, x, y, x + hairCaseSize, y + hairCaseSize, 0xFF000000);

            String hairIndex = String.valueOf(i + 1);
            font.drawString(matrixStack, hairIndex, x + (hairCaseSize / 2f) - (font.getStringWidth(hairIndex) / 2f), y + (hairCaseSize / 2f) - (font.FONT_HEIGHT / 2f), 0xFFFFFFFF);

            // Selectionner la case
            if (isHovering && clicked) {
                clicked = false;
                getInstance().player.playSound(ModSoundEvents.TICK.get(), 0.7F, 1F);
                selectedHairType = i;
            }

            // Jouer son quand hover
            if (isHovering && hoveredIndex != i) {
                hoveredIndex = i;
                Minecraft.getInstance().player.playSound(ModSoundEvents.HOVER.get(), 0.2F, 0.8F);
            }
        }

        // Section de la barbe
        int hairSectionHeight = bodyColorsSectionHeight + 30 + (int) Math.ceil((double) hairTypes.size() / hairCasesPerRow) * (hairCaseSize + hairSpaceBetween);
        font.drawString(matrixStack, "Type de barbe", 40, hairSectionHeight + 15 + scrollOffset, 0xFFFFFFFF);
        int beardCaseSize = 15;
        int beardSpaceBetween = 3;
        int beardCasesPerRow = 10;

        for (int i = 0; i < beardTypes.size(); i++) {
            int row = i / beardCasesPerRow;
            int col = i % beardCasesPerRow;
            int x = 40 + col * (beardCaseSize + beardSpaceBetween);
            int y = scrollOffset + hairSectionHeight + 30 + row * (beardCaseSize + beardSpaceBetween);

            boolean isHovering = mouseX >= x - strokeSize && mouseX <= x + beardCaseSize + strokeSize && mouseY >= y - strokeSize && mouseY <= y + beardCaseSize + strokeSize;

            int beardStrokeColor = (isHovering || selectedBeardType == i) ? 0xFFFFFFFF : 0xFF808080;
            fill(matrixStack, x - strokeSize, y - strokeSize, x + beardCaseSize + strokeSize, y + beardCaseSize + strokeSize, beardStrokeColor);
            fill(matrixStack, x, y, x + beardCaseSize, y + beardCaseSize, 0xFF000000);

            String beardIndex = String.valueOf(i + 1);
            font.drawString(matrixStack, beardIndex, x + (beardCaseSize / 2f) - (font.getStringWidth(beardIndex) / 2f), y + (beardCaseSize / 2f) - (font.FONT_HEIGHT / 2f), 0xFFFFFFFF);

            // Selectionner la case
            if (isHovering && clicked) {
                clicked = false;
                getInstance().player.playSound(ModSoundEvents.TICK.get(), 0.7F, 1F);
                selectedBeardType = i;
            }

            // Jouer son quand hover
            if (isHovering && hoveredIndex != i) {
                hoveredIndex = i;
                Minecraft.getInstance().player.playSound(ModSoundEvents.HOVER.get(), 0.2F, 0.8F);
            }
        }

        // Section des couleurs de cheveux
        int beardSectionHeight = hairSectionHeight + 30 + (int) Math.ceil((double) beardTypes.size() / hairCasesPerRow) * (hairCaseSize + hairSpaceBetween);
        font.drawString(matrixStack, "Couleur des cheveux/barbe", 40, beardSectionHeight + 15 + scrollOffset, 0xFFFFFFFF);
        int colorSize = 15;
        int spaceBetween = 3;
        int colorsPerRow = 10;

        for (int i = 0; i < hairColors.size(); i++) {
            int row = i / colorsPerRow;
            int col = i % colorsPerRow;
            int x = 40 + col * (colorSize + spaceBetween);
            int y = scrollOffset + beardSectionHeight + 30 + row * (colorSize + spaceBetween);

            boolean isHovering = mouseX >= x - strokeSize && mouseX <= x + colorSize + strokeSize && mouseY >= y - strokeSize && mouseY <= y + colorSize + strokeSize;

            int colorStroke = (isHovering || selectedHairColor == i) ? 0xFFFFFFFF : 0xFF808080;
            fill(matrixStack, x - strokeSize, y - strokeSize, x + colorSize + strokeSize, y + colorSize + strokeSize, colorStroke);
            fill(matrixStack, x, y, x + colorSize, y + colorSize, (int) Long.parseLong("FF" + hairColors.get(i), 16));

            // Selectionner la case
            if (isHovering && clicked) {
                clicked = false;
                getInstance().player.playSound(ModSoundEvents.TICK.get(), 0.7F, 1F);
                selectedHairColor = i;
            }

            // Jouer son quand hover
            if (isHovering && hoveredIndex != i) {
                hoveredIndex = i;
                Minecraft.getInstance().player.playSound(ModSoundEvents.HOVER.get(), 0.2F, 0.8F);
            }
        }

        // Section des yeux
        int hairColorsSectionHeight = beardSectionHeight + 30 + (int) Math.ceil((double) hairColors.size() / colorsPerRow) * (colorSize + spaceBetween);
        font.drawString(matrixStack, "Type d'yeux", 40, hairColorsSectionHeight + 15 + scrollOffset, 0xFFFFFFFF);
        int eyeCaseSize = 15;
        int eyeSpaceBetween = 3;
        int eyeCasesPerRow = 10;

        for (int i = 0; i < eyeTypes.size(); i++) {
            int row = i / eyeCasesPerRow;
            int col = i % eyeCasesPerRow;
            int x = 40 + col * (eyeCaseSize + eyeSpaceBetween);
            int y = scrollOffset + hairColorsSectionHeight + 30 + row * (eyeCaseSize + eyeSpaceBetween);

            boolean isHovering = mouseX >= x - strokeSize && mouseX <= x + eyeCaseSize + strokeSize && mouseY >= y - strokeSize && mouseY <= y + eyeCaseSize + strokeSize;

            int eyeStrokeColor = (isHovering || selectedEyeType == i) ? 0xFFFFFFFF : 0xFF808080;
            fill(matrixStack, x - strokeSize, y - strokeSize, x + eyeCaseSize + strokeSize, y + eyeCaseSize + strokeSize, eyeStrokeColor);
            fill(matrixStack, x, y, x + eyeCaseSize, y + eyeCaseSize, 0xFF000000);

            String eyeIndex = String.valueOf(i + 1);
            font.drawString(matrixStack, eyeIndex, x + (eyeCaseSize / 2f) - (font.getStringWidth(eyeIndex) / 2f), y + (eyeCaseSize / 2f) - (font.FONT_HEIGHT / 2f), 0xFFFFFFFF);

            // Selectionner la case
            if (isHovering && clicked) {
                clicked = false;
                getInstance().player.playSound(ModSoundEvents.TICK.get(), 0.7F, 1F);
                selectedEyeType = i;
            }

            // Jouer son quand hover
            if (isHovering && hoveredIndex != i) {
                hoveredIndex = i;
                Minecraft.getInstance().player.playSound(ModSoundEvents.HOVER.get(), 0.2F, 0.8F);
            }
        }

        // Section des couleurs des yeux
        int eyeSectionHeight = hairColorsSectionHeight + 30 + (int) Math.ceil((double) eyeTypes.size() / eyeCasesPerRow) * (eyeCaseSize + eyeSpaceBetween);
        font.drawString(matrixStack, "Couleur des yeux", 40, eyeSectionHeight + 15 + scrollOffset, 0xFFFFFFFF);
        int eyeColorSize = 15;
        int eyeColorSpaceBetween = 3;
        int eyeColorsPerRow = 10;

        for (int i = 0; i < eyeColors.size(); i++) {
            int row = i / eyeColorsPerRow;
            int col = i % eyeColorsPerRow;
            int x = 40 + col * (eyeColorSize + eyeColorSpaceBetween);
            int y = scrollOffset + eyeSectionHeight + 30 + row * (eyeColorSize + eyeColorSpaceBetween);

            boolean isHovering = mouseX >= x - strokeSize && mouseX <= x + eyeColorSize + strokeSize && mouseY >= y - strokeSize && mouseY <= y + eyeColorSize + strokeSize;

            int eyeColorStroke = (isHovering || selectedEyeColor == i) ? 0xFFFFFFFF : 0xFF808080;
            fill(matrixStack, x - strokeSize, y - strokeSize, x + eyeColorSize + strokeSize, y + eyeColorSize + strokeSize, eyeColorStroke);
            fill(matrixStack, x, y, x + eyeColorSize, y + eyeColorSize, (int) Long.parseLong("FF" + eyeColors.get(i), 16));

            // Selectionner la case
            if (isHovering && clicked) {
                clicked = false;
                getInstance().player.playSound(ModSoundEvents.TICK.get(), 0.7F, 1F);
                selectedEyeColor = i;
            }

            // Jouer son quand hover
            if (isHovering && hoveredIndex != i) {
                hoveredIndex = i;
                Minecraft.getInstance().player.playSound(ModSoundEvents.HOVER.get(), 0.2F, 0.8F);
            }
        }

        // Section des bouches
        int eyeColorsSectionHeight = eyeSectionHeight + 30 + (int) Math.ceil((double) eyeColors.size() / eyeColorsPerRow) * (eyeColorSize + eyeColorSpaceBetween);
        font.drawString(matrixStack, "Type de bouche", 40, eyeColorsSectionHeight + 15 + scrollOffset, 0xFFFFFFFF);
        int mouthCaseSize = 15;
        int mouthSpaceBetween = 3;
        int mouthCasesPerRow = 10;

        for (int i = 0; i < mouthTypes.size(); i++) {
            int row = i / mouthCasesPerRow;
            int col = i % mouthCasesPerRow;
            int x = 40 + col * (mouthCaseSize + mouthSpaceBetween);
            int y = scrollOffset + eyeColorsSectionHeight + 30 + row * (mouthCaseSize + mouthSpaceBetween);

            boolean isHovering = mouseX >= x - strokeSize && mouseX <= x + mouthCaseSize + strokeSize && mouseY >= y - strokeSize && mouseY <= y + mouthCaseSize + strokeSize;

            int mouthStrokeColor = (isHovering || selectedMouthType == i) ? 0xFFFFFFFF : 0xFF808080;
            fill(matrixStack, x - strokeSize, y - strokeSize, x + mouthCaseSize + strokeSize, y + mouthCaseSize + strokeSize, mouthStrokeColor);
            fill(matrixStack, x, y, x + mouthCaseSize, y + mouthCaseSize, 0xFF000000);

            String mouthIndex = String.valueOf(i + 1);
            font.drawString(matrixStack, mouthIndex, x + (mouthCaseSize / 2f) - (font.getStringWidth(mouthIndex) / 2f), y + (mouthCaseSize / 2f) - (font.FONT_HEIGHT / 2f), 0xFFFFFFFF);

            // Selectionner la case
            if (isHovering && clicked) {
                clicked = false;
                getInstance().player.playSound(ModSoundEvents.TICK.get(), 0.7F, 1F);
                selectedMouthType = i;
            }

            // Jouer son quand hover
            if (isHovering && hoveredIndex != i) {
                hoveredIndex = i;
                Minecraft.getInstance().player.playSound(ModSoundEvents.HOVER.get(), 0.2F, 0.8F);
            }
        }

        // Section des nez
        int mouthSectionHeight = eyeColorsSectionHeight + 30 + (int) Math.ceil((double) mouthTypes.size() / mouthCasesPerRow) * (mouthCaseSize + mouthSpaceBetween);
        font.drawString(matrixStack, "Type de nez", 40, mouthSectionHeight + 15 + scrollOffset, 0xFFFFFFFF);
        int noseCaseSize = 15;
        int noseSpaceBetween = 3;
        int noseCasesPerRow = 10;

        for (int i = 0; i < noseTypes.size(); i++) {
            int row = i / noseCasesPerRow;
            int col = i % noseCasesPerRow;
            int x = 40 + col * (noseCaseSize + noseSpaceBetween);
            int y = scrollOffset + mouthSectionHeight + 30 + row * (noseCaseSize + noseSpaceBetween);

            boolean isHovering = mouseX >= x - strokeSize && mouseX <= x + noseCaseSize + strokeSize && mouseY >= y - strokeSize && mouseY <= y + noseCaseSize + strokeSize;

            int noseStrokeColor = (isHovering || selectedNoseType == i) ? 0xFFFFFFFF : 0xFF808080;
            fill(matrixStack, x - strokeSize, y - strokeSize, x + noseCaseSize + strokeSize, y + noseCaseSize + strokeSize, noseStrokeColor);
            fill(matrixStack, x, y, x + noseCaseSize, y + noseCaseSize, 0xFF000000);

            String noseIndex = String.valueOf(i + 1);
            font.drawString(matrixStack, noseIndex, x + (noseCaseSize / 2f) - (font.getStringWidth(noseIndex) / 2f), y + (noseCaseSize / 2f) - (font.FONT_HEIGHT / 2f), 0xFFFFFFFF);

            // Selectionner la case
            if (isHovering && clicked) {
                clicked = false;
                getInstance().player.playSound(ModSoundEvents.TICK.get(), 0.7F, 1F);
                selectedNoseType = i;
            }

            // Jouer son quand hover
            if (isHovering && hoveredIndex != i) {
                hoveredIndex = i;
                Minecraft.getInstance().player.playSound(ModSoundEvents.HOVER.get(), 0.2F, 0.8F);
            }
        }

        int noseSectionHeight = mouthSectionHeight + 30 + (int) Math.ceil((double) noseTypes.size() / noseCasesPerRow) * (noseCaseSize + noseSpaceBetween);

        minScrollPosition = -noseSectionHeight + 200;

        assert this.minecraft.player != null;
        drawEntityOnScreen(this.width / 2, (this.height / 2) + 100, 100, -(mouseX - (this.width / 2.0F)), -(mouseY - (this.height / 2.0F)), this.minecraft.player);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
    }
}
