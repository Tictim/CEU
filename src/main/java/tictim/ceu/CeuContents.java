package tictim.ceu;

import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.items.OreDictNames;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.stack.UnificationEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import tictim.ceu.config.CeuConfig;
import tictim.ceu.mte.Converter;
import tictim.ceu.mte.ConverterMTE;
import tictim.ceu.mte.InfiniteFeEmitterMTE;
import tictim.ceu.mte.InfiniteFeReceiverMTE;
import tictim.ceu.mte.InfiniteGteuEmitterMTE;
import tictim.ceu.mte.InfiniteGteuReceiverMTE;
import tictim.ceu.util.CeuCraftingHelper;

import java.util.ArrayList;
import java.util.List;

public final class CeuContents{
	private CeuContents(){}

	private static ResourceLocation n(String s){
		return new ResourceLocation(CeuMod.MODID, s);
	}

	public static final List<MetaTileEntity> CEU;
	public static final List<MetaTileEntity> CEF;

	public static final InfiniteFeEmitterMTE INFINITE_FE_EMITTER = new InfiniteFeEmitterMTE(n("fe_emitter"));
	public static final InfiniteFeReceiverMTE INFINITE_FE_RECEIVER = new InfiniteFeReceiverMTE(n("fe_receiver"));
	public static final InfiniteGteuEmitterMTE INFINITE_GTEU_EMITTER = new InfiniteGteuEmitterMTE(n("gteu_emitter"));
	public static final InfiniteGteuReceiverMTE INFINITE_GTEU_RECEIVER = new InfiniteGteuReceiverMTE(n("gteu_receiver"));

	static{
		if(isGregicalityCompatActive()){
			CEU = CeuGregicalityCompat.converter(true);
			CEF = CeuGregicalityCompat.converter(false);
		}else{
			CEU = converter(true);
			CEF = converter(false);
		}
	}

	public static List<MetaTileEntity> converter(boolean convertsToFe){
		List<MetaTileEntity> converters = new ArrayList<>();
		for(int tier = GTValues.ULV; tier<=GTValues.MAX; tier++)
			for(int slots = 1; slots<=4; slots++)
				converters.add(new ConverterMTE(new ResourceLocation(CeuMod.MODID, (convertsToFe ? "ceu." : "cef.")+GTValues.VN[tier]+"."+slots*slots),
						tier,
						slots,
						convertsToFe));
		return converters;
	}

	public static void register(){
		for(int i = 0; i<CEU.size(); i++){
			GregTechAPI.registerMetaTileEntity(10650+i*2, CEU.get(i));
			GregTechAPI.registerMetaTileEntity(10650+i*2+1, CEF.get(i));
		}

		GregTechAPI.registerMetaTileEntity(10649, INFINITE_FE_EMITTER);
		GregTechAPI.registerMetaTileEntity(10648, INFINITE_FE_RECEIVER);
		GregTechAPI.registerMetaTileEntity(10647, INFINITE_GTEU_EMITTER);
		GregTechAPI.registerMetaTileEntity(10646, INFINITE_GTEU_RECEIVER);
	}

	public static boolean isGregicalityCompatActive(){
		return Loader.isModLoaded("gtadditions")&&!CeuConfig.config().disableGregicalityCompat();
	}

	public static void addRecipes(){
		CeuCraftingHelper helper = isGregicalityCompatActive() ? CeuGregicalityCompat.getCraftingHelper() : new CeuCraftingHelper();
		for(MetaTileEntity mte : CEU) addConverterRecipe(mte, helper);
		for(MetaTileEntity mte : CEF) addConverterRecipe(mte, helper);
	}

	private static void addConverterRecipe(MetaTileEntity mte, CeuCraftingHelper h){
		if(!(mte instanceof Converter)) throw new IllegalArgumentException("Expected converter");
		Converter converter = (Converter)mte;
		int tier = converter.getTier();
		int slots = converter.getSlots();
		ModHandler.addShapedRecipe(mte.metaTileEntityId.toString(), mte.getStackForm(), converter.convertsToFE() ? "WTW" : "WSW",
				"RMR",
				converter.convertsToFE() ? "WSW" : "WTW",

				'M', h.hull(tier),
				'W', new UnificationEntry(h.cablePrefix(slots), h.cableMaterialByTier(tier)),
				'T', OreDictNames.chestWood,
				'R', new UnificationEntry(h.cablePrefix(slots), Materials.RedAlloy),
				'S', h.circuit(tier));
	}
}
