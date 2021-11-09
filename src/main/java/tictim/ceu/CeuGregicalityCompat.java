package tictim.ceu;

import gregicadditions.GAValues;
import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.util.ResourceLocation;
import tictim.ceu.mte.GAConverterMTE;
import tictim.ceu.util.CeuCraftingHelper;
import tictim.ceu.util.CeuGregicalityCompatCraftingHelper;

import java.util.ArrayList;
import java.util.List;

public final class CeuGregicalityCompat{
	private CeuGregicalityCompat(){}

	public static List<MetaTileEntity> converter(boolean convertsToFe){
		// Putting MAX in between UV and UHV so MAX converters stay in same numerical IDs
		List<MetaTileEntity> converters = new ArrayList<>();
		for(int tier = GAValues.ULV; tier<=GAValues.UV; tier++)
			addConverter(converters, tier, convertsToFe);
		addConverter(converters, GAValues.MAX, convertsToFe);
		for(int tier = GAValues.UHV; tier<=GAValues.UXV; tier++)
			addConverter(converters, tier, convertsToFe);
		return converters;
	}

	private static void addConverter(List<MetaTileEntity> converters, int tier, boolean convertsToFe){
		for(int slots = 1; slots<=4; slots++)
			converters.add(new GAConverterMTE(new ResourceLocation(CeuMod.MODID, (convertsToFe ? "ceu." : "cef.")+GAValues.VN[tier]+"."+slots*slots),
					tier,
					slots,
					convertsToFe));
	}

	public static CeuCraftingHelper getCraftingHelper(){
		return new CeuGregicalityCompatCraftingHelper();
	}
}
