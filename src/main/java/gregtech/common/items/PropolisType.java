package gregtech.common.items;

import gregtech.api.enums.Materials;
import gregtech.api.util.GT_LanguageManager;

public enum PropolisType {

	End("End",true),
    Ectoplasma("Ectoplasma",true),
    Arcaneshard("Arcaneshard",true);

    private static int[] colours = new int[]{
        0xCC00FA,
        0xDCB0E5,
        0x9010AD
    };

    public boolean showInList;
    public Materials material;
    public int chance;
    private String name;
    private PropolisType(String pName, boolean show) {
        this.name = pName;
        this.showInList = show;
    }

    public void setHidden() {
        this.showInList = false;
    }

    public String getName() {
//		return "gt.comb."+this.name;
        return GT_LanguageManager.addStringLocalization("propolis." + this.name, this.name.substring(0, 1).toUpperCase() + this.name.substring(1) + " Propolis");
    }

    public int getColours() {
        return colours[this.ordinal()];
    }
}