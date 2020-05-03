package common.tileentities;

import org.lwjgl.input.Keyboard;

import common.Blocks;
import gregtech.api.enums.Dyes;
import gregtech.api.enums.Textures.BlockIcons;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.objects.GT_RenderedTexture;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import util.MultiBlockTooltipBuilder;
import util.Vector3i;
import util.Vector3ic;

public class GTMTE_LapotronicSuperCapacitor extends GT_MetaTileEntity_MultiBlockBase {
	
	private final static String glassNameIC2Reinforced = "blockAlloyGlass";
	private static final Block LSC_PART = Blocks.lscLapotronicEnergyUnit;
	private static final int CASING_META = 0;
	private static final int CASING_TEXTURE_ID = 82;
	
	public GTMTE_LapotronicSuperCapacitor(int aID, String aName, String aNameRegional) {
		super(aID, aName, aNameRegional);
		
	}

	public GTMTE_LapotronicSuperCapacitor(String aName) {
		super(aName);
		
	}
	
	@Override
	public IMetaTileEntity newMetaEntity(IGregTechTileEntity var1) {
		return new GTMTE_LapotronicSuperCapacitor(super.mName);
	}
	
	@Override
	public String[] getDescription() {
		final MultiBlockTooltipBuilder b = new MultiBlockTooltipBuilder();
		b.addInfo("LapotronicTM Multi-block power storage")
				.addSeparator()
				.beginStructureBlock(5, 4-18, 5)
				.addController("Front Bottom Center")
				.addDynamoHatch("Instead of any casing")
				.addEnergyHatch("Instead of any casing")
				.addOtherStructurePart("Lapotronic Capacitor Base", "At least 17x, 5x2x5 base")
				.addOtherStructurePart("Lapotronic Capacitor, (Really) Ultimate Capacitor", "9-135x, Center 3x1-15x3 above base")
				.addOtherStructurePart("Glass?", "41-265x, Encase capacitor pillar")
				.addMaintenanceHatch("Instead of any casing")
				.signAndFinalize("Kekzdealer");
		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			return b.getInformation();
		} else {
			return b.getStructureInformation();
		}
	}
	
	@Override
	public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex,
			boolean aActive, boolean aRedstone) {
		ITexture[] sTexture;
		if (aSide == aFacing) {
			sTexture = new ITexture[]{new GT_RenderedTexture(BlockIcons.MACHINE_CASING_FUSION_GLASS,
					Dyes.getModulation(-1, Dyes._NULL.mRGBa)), new GT_RenderedTexture(BlockIcons.OVERLAY_FUSION1)};
		} else if (!aActive) {
			sTexture = new ITexture[]{new GT_RenderedTexture(BlockIcons.MACHINE_CASING_FUSION_GLASS,
					Dyes.getModulation(-1, Dyes._NULL.mRGBa))};
		} else {
			sTexture = new ITexture[]{new GT_RenderedTexture(BlockIcons.MACHINE_CASING_FUSION_GLASS_YELLOW,
					Dyes.getModulation(-1, Dyes._NULL.mRGBa))};
		}

		return sTexture;
	}
	
	public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
		return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, this.getLocalName(),
				"MultiblockDisplay.png");
	}
	
	@Override
	public boolean isCorrectMachinePart(ItemStack stack) {
		return true;
	}
	
	@Override
	public boolean checkRecipe(ItemStack stack) {
		return true;
	}
	
	public Vector3ic rotateOffsetVector(Vector3ic forgeDirection, int x, int y, int z) {
		final Vector3i offset = new Vector3i();
		
		// either direction on z-axis
		if(forgeDirection.x() == 0 && forgeDirection.z() == -1) {
			offset.x = x;
			offset.y = y;
			offset.z = z;
		}
		if(forgeDirection.x() == 0 && forgeDirection.z() == 1) {
			offset.x = -x;
			offset.y = y;
			offset.z = -z;
		}
		// either direction on x-axis
		if(forgeDirection.x() == -1 && forgeDirection.z() == 0) {
			offset.x = z;
			offset.y = y;
			offset.z = -x;
		}
		if(forgeDirection.x() == 1 && forgeDirection.z() == 0) {
			offset.x = -z;
			offset.y = y;
			offset.z = x;
		}
		
		return offset;
	}
	
	@Override
	public boolean checkMachine(IGregTechTileEntity thisController, ItemStack guiSlotItem) {
		// Figure out the vector for the direction the back face of the controller is facing
		final Vector3ic forgeDirection = new Vector3i(
				ForgeDirection.getOrientation(thisController.getBackFacing()).offsetX,
				ForgeDirection.getOrientation(thisController.getBackFacing()).offsetY,
				ForgeDirection.getOrientation(thisController.getBackFacing()).offsetZ
				);
		int minCasingAmount = 17; 
		boolean formationChecklist = true; // if this is still true at the end, machine is good to go :)
		
		for(int X = -2; X <= 2; X++) {
			for(int Y = 0; Y <= 1; Y++) {
				for(int Z = -1; Z <= 4; Z++) {
					if(X == 0 && Y == 0) {
						continue; // is controller
					}
					
					final Vector3ic offset = rotateOffsetVector(forgeDirection, X, Y, Z);
					IGregTechTileEntity currentTE = 
							thisController.getIGregTechTileEntityOffset(offset.x(), offset.y(), offset.z());
					
					// Tries to add TE as either of those kinds of hatches.
					// The number is the texture index number for the texture that needs to be painted over the hatch texture (TAE for GT++)
					if (   !super.addMaintenanceToMachineList(currentTE, CASING_TEXTURE_ID) 
						&& !super.addInputToMachineList(currentTE, CASING_TEXTURE_ID)
						&& !super.addOutputToMachineList(currentTE, CASING_TEXTURE_ID)) {
						
						// If it's not a hatch, is it the right casing for this machine? Check block and block meta.
						if ((thisController.getBlockOffset(offset.x(), offset.y(), offset.z()) == LSC_PART) 
								&& (thisController.getMetaIDOffset(offset.x(), offset.y(), offset.z()) == CASING_META)) {
							// Seems to be valid casing. Decrement counter.
							minCasingAmount--;
						} else {
							formationChecklist = false;
						}
					}
				}
			}
		}
		
		int firstGlassHeight = 3; // Initialize to minimum height
		for(int X = -1; X <= 1; X++) {
			for(int Y = 2; Y <= 17; Y++) {
				for(int Z = 0; Z <= 2; Z++) {
					final Vector3ic offset = rotateOffsetVector(forgeDirection, X, Y, Z);
					
					if(!((thisController.getBlockOffset(offset.x(), offset.y(), offset.z()) == LSC_PART) 
								&& (thisController.getMetaIDOffset(offset.x(), offset.y(), offset.z()) > 0))) {
						
						// If there's glass instead, terminate and remember the height
						if(thisController.getBlockOffset(offset.x(), offset.y(), offset.z()).getUnlocalizedName().equals(glassNameIC2Reinforced)){
							firstGlassHeight = Y;
						} else {
							formationChecklist = false;
						}
					}
				}
			}
		}
		
		for(int X = -2; X <= 2; X++) {
			for(int Y = 2; Y <= firstGlassHeight; Y++) {
				for(int Z = -1; Z <= 4; Z++) {
					final Vector3ic offset = rotateOffsetVector(forgeDirection, X, Y, Z);
					
					if(!thisController.getBlockOffset(offset.x(), offset.y(), offset.z()).getUnlocalizedName().equals(glassNameIC2Reinforced)){
						formationChecklist = false;	
					}
				}
			}
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
