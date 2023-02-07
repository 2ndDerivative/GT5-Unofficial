package common;

import kekztech.KekzCore;

import net.minecraft.block.Block;

import common.blocks.*;

public class Blocks {

    public static Block yszUnit;
    public static Block gdcUnit;

    public static Block tfftStorageField;

    public static Block reactorChamberOFF;
    public static Block reactorChamberON;
    public static Block reactorControlRod;

    public static Block itemServerDrive;
    public static Block itemServerRackCasing;
    public static Block itemServerIOPort;

    public static Block itemProxyCable;
    public static Block itemProxySource;
    public static Block itemProxyEndpoint;

    public static Block jarThaumiumReinforced;
    public static Block jarIchor;

    public static Block lscLapotronicEnergyUnit;
    public static Block beamTransmitter;

    public static Block spaceElevatorStructure;
    public static Block spaceElevatorCapacitor;
    public static Block spaceElevatorTether;

    public static Block largeHexPlate;

    public static void preInit() {
        KekzCore.LOGGER.info("Registering blocks...");

        registerBlocks_SOFC();
        registerBlocks_TFFT();
        // registerBlocks_Nuclear();
        // registerBlocks_ItemProxy();
        registerBlocks_Jars();
        registerBlocks_LSC();
        // registerBlocks_SpaceElevator();
        registerBlocks_Cosmetics();

        KekzCore.LOGGER.info("Finished registering blocks");
    }

    private static void registerBlocks_SOFC() {
        yszUnit = Block_YSZUnit.registerBlock();
        gdcUnit = Block_GDCUnit.registerBlock();
    }

    private static void registerBlocks_TFFT() {
        tfftStorageField = Block_TFFTStorageField.registerBlock();
    }

    /*
     * private static void registerBlocks_Nuclear() { reactorChamberOFF = Block_ReactorChamber_OFF.registerBlock();
     * reactorChamberON = Block_ReactorChamber_ON.registerBlock(); reactorControlRod = Block_ControlRod.registerBlock();
     * } private static void registerBlocks_ItemProxy() { itemProxyCable = Block_ItemProxyCable.registerBlock();
     * itemProxySource = Block_ItemProxySource.registerBlock(); itemProxyEndpoint =
     * Block_ItemProxyEndpoint.registerBlock(); }
     */

    private static void registerBlocks_Jars() {
        jarThaumiumReinforced = Block_ThaumiumReinforcedJar.registerBlock();
        jarIchor = Block_IchorJar.registerBlock();
    }

    private static void registerBlocks_LSC() {
        lscLapotronicEnergyUnit = Block_LapotronicEnergyUnit.registerBlock();
        // beamTransmitter = Block_BeamTransmitter.registerBlock();
    }

    /*
     * private static void registerBlocks_SpaceElevator() { spaceElevatorStructure =
     * Block_SpaceElevator.registerBlock(); spaceElevatorCapacitor = Block_SpaceElevatorCapacitor.registerBlock();
     * spaceElevatorTether = Block_SpaceElevatorTether.registerBlock(); }
     */

    private static void registerBlocks_Cosmetics() {
        largeHexPlate = Block_LargeHexPlate.registerBlock();
    }
}
