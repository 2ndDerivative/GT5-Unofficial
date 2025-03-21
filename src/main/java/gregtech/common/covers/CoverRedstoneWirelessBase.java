package gregtech.common.covers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.Fluid;

import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.TextWidget;

import gregtech.api.GregTechAPI;
import gregtech.api.covers.CoverContext;
import gregtech.api.gui.modularui.CoverUIBuildContext;
import gregtech.api.interfaces.ITexture;
import gregtech.api.util.GTUtility;
import gregtech.api.util.ISerializableObject;
import gregtech.common.gui.modularui.widget.CoverDataControllerWidget;
import gregtech.common.gui.modularui.widget.CoverDataFollowerNumericWidget;
import gregtech.common.gui.modularui.widget.CoverDataFollowerToggleButtonWidget;

public abstract class CoverRedstoneWirelessBase extends CoverBehavior {

    private static final int MAX_CHANNEL = 65535;
    private static final int PRIVATE_MASK = 0xFFFE0000;
    private static final int PUBLIC_MASK = 0x0000FFFF;
    private static final int CHECKBOX_MASK = 0x00010000;

    public CoverRedstoneWirelessBase(CoverContext context, ITexture coverTexture) {
        super(context, coverTexture);
    }

    @Override
    public void onCoverRemoval() {
        GregTechAPI.sWirelessRedstone.put(coverData.get(), (byte) 0);
    }

    @Override
    public boolean onCoverRightClick(EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if (((aX > 0.375D) && (aX < 0.625D)) || ((coverSide.offsetX != 0) && ((aY > 0.375D) && (aY < 0.625D)))) {
            GregTechAPI.sWirelessRedstone.put(coverData.get(), (byte) 0);
            coverData.set(
                (coverData.get() & (PRIVATE_MASK | CHECKBOX_MASK))
                    | (((Integer) GTUtility.stackToInt(aPlayer.inventory.getCurrentItem())).hashCode() & PUBLIC_MASK));
            GTUtility.sendChatToPlayer(aPlayer, GTUtility.trans("081", "Frequency: ") + coverData);
            return true;
        }
        return false;
    }

    @Override
    public ISerializableObject.LegacyCoverData onCoverScrewdriverClick(EntityPlayer aPlayer, float aX, float aY,
        float aZ) {
        int coverDataValue = coverData.get();
        if (((aX > 0.375D) && (aX < 0.625D)) || ((coverSide.offsetX == 0)
            || (((aY > 0.375D) && (aY < 0.625D)) || ((((aZ <= 0.375D) || (aZ >= 0.625D))))))) {
            GregTechAPI.sWirelessRedstone.put(coverDataValue, (byte) 0);
            final float[] tCoords = GTUtility.getClickedFacingCoords(coverSide, aX, aY, aZ);

            final short tAdjustVal = switch ((byte) ((byte) (int) (tCoords[0] * 2.0F)
                + 2 * (byte) (int) (tCoords[1] * 2.0F))) {
                case 0 -> -32;
                case 1 -> 32;
                case 2 -> -1024;
                case 3 -> 1024;
                default -> 0;
            };

            final int tPublicChannel = (coverDataValue & PUBLIC_MASK) + tAdjustVal;

            if (tPublicChannel < 0) {
                coverDataValue = coverDataValue & ~PUBLIC_MASK;
            } else if (tPublicChannel > MAX_CHANNEL) {
                coverDataValue = (coverDataValue & (PRIVATE_MASK | CHECKBOX_MASK)) | MAX_CHANNEL;
            } else {
                coverDataValue = (coverDataValue & (PRIVATE_MASK | CHECKBOX_MASK)) | tPublicChannel;
            }
        }
        GTUtility.sendChatToPlayer(aPlayer, GTUtility.trans("081", "Frequency: ") + (coverDataValue & PUBLIC_MASK));
        return ISerializableObject.LegacyCoverData.of(coverDataValue);
    }

    @Override
    public boolean letsEnergyIn() {
        return true;
    }

    @Override
    public boolean letsEnergyOut() {
        return true;
    }

    @Override
    public boolean letsFluidIn(Fluid aFluid) {
        return true;
    }

    @Override
    public boolean letsFluidOut(Fluid aFluid) {
        return true;
    }

    @Override
    public boolean letsItemsIn(int aSlot) {
        return true;
    }

    @Override
    public boolean letsItemsOut(int aSlot) {
        return true;
    }

    @Override
    public String getDescription() {
        return GTUtility.trans("081", "Frequency: ") + coverData.get();
    }

    @Override
    public int getMinimumTickRate() {
        return 1;
    }

    // GUI stuff

    @Override
    public boolean hasCoverGUI() {
        return true;
    }

    @Override
    public ModularWindow createWindow(CoverUIBuildContext buildContext) {
        return new RedstoneWirelessBaseUIFactory(buildContext).createWindow();
    }

    private class RedstoneWirelessBaseUIFactory extends UIFactory {

        private static final int startX = 10;
        private static final int startY = 25;
        private static final int spaceX = 18;
        private static final int spaceY = 18;

        public RedstoneWirelessBaseUIFactory(CoverUIBuildContext buildContext) {
            super(buildContext);
        }

        @Override
        protected int getGUIWidth() {
            return 250;
        }

        @SuppressWarnings("PointlessArithmeticExpression")
        @Override
        protected void addUIWidgets(ModularWindow.Builder builder) {
            builder
                .widget(
                    new CoverDataControllerWidget<>(
                        this::getCoverData,
                        this::setCoverData,
                        CoverRedstoneWirelessBase.this::loadFromNbt)

                            .addFollower(
                                new CoverDataFollowerNumericWidget<>(),
                                coverData -> (double) getFlagFrequency(convert(coverData)),
                                (coverData, state) -> new ISerializableObject.LegacyCoverData(
                                    state.intValue() | getFlagCheckbox(convert(coverData))),
                                widget -> widget.setBounds(0, MAX_CHANNEL)
                                    .setScrollValues(1, 1000, 10)
                                    .setFocusOnGuiOpen(true)
                                    .setPos(spaceX * 0, spaceY * 0 + 2)
                                    .setSize(spaceX * 4 - 3, 12))
                            .addFollower(
                                CoverDataFollowerToggleButtonWidget.ofCheck(),
                                coverData -> getFlagCheckbox(convert(coverData)) > 0,
                                (coverData, state) -> new ISerializableObject.LegacyCoverData(
                                    getFlagFrequency(convert(coverData)) | (state ? CHECKBOX_MASK : 0)),
                                widget -> widget.setPos(spaceX * 0, spaceY * 2))
                            .setPos(startX, startY))
                .widget(
                    new TextWidget(GTUtility.trans("246", "Frequency")).setDefaultColor(COLOR_TEXT_GRAY.get())
                        .setPos(startX + spaceX * 4, 4 + startY + spaceY * 0))
                .widget(
                    new TextWidget(GTUtility.trans("602", "Use Private Frequency"))
                        .setDefaultColor(COLOR_TEXT_GRAY.get())
                        .setPos(startX + spaceX * 1, startY + spaceY * 2 + 4));
        }

        private int getFlagFrequency(int coverVariable) {
            return coverVariable & PUBLIC_MASK;
        }

        private int getFlagCheckbox(int coverVariable) {
            return coverVariable & CHECKBOX_MASK;
        }
    }
}
