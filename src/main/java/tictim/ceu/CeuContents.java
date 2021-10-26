package tictim.ceu;

import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.items.OreDictNames;
import gregtech.api.recipes.ModHandler;
import net.minecraft.util.ResourceLocation;
import tictim.ceu.mte.CefMTE;
import tictim.ceu.mte.CeuMTE;
import tictim.ceu.mte.ConverterMTE;
import tictim.ceu.mte.InfiniteFeEmitterMTE;
import tictim.ceu.mte.InfiniteFeReceiverMTE;
import tictim.ceu.mte.InfiniteGteuEmitterMTE;
import tictim.ceu.mte.InfiniteGteuReceiverMTE;
import tictim.ceu.util.CeuCraftingHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CeuContents{
	private CeuContents(){}

	private static ResourceLocation n(String s){
		return new ResourceLocation(CeuMod.MODID, s);
	}

	public static final List<CeuMTE> CEU;
	public static final List<CefMTE> CEF;

	public static final InfiniteFeEmitterMTE INFINITE_FE_EMITTER = new InfiniteFeEmitterMTE(n("fe_emitter"));
	public static final InfiniteFeReceiverMTE INFINITE_FE_RECEIVER = new InfiniteFeReceiverMTE(n("fe_receiver"));
	public static final InfiniteGteuEmitterMTE INFINITE_GTEU_EMITTER = new InfiniteGteuEmitterMTE(n("gteu_emitter"));
	public static final InfiniteGteuReceiverMTE INFINITE_GTEU_RECEIVER = new InfiniteGteuReceiverMTE(n("gteu_receiver"));

	static{
		List<CeuMTE> ceus = new ArrayList<>();
		for(int tier = 0; tier<GTValues.V.length; tier++)
			for(int slots = 1; slots<=4; slots++)
				ceus.add(new CeuMTE(n("ceu."+GTValues.VN[tier]+"."+slots*slots), tier, slots));
		CEU = Collections.unmodifiableList(ceus);

		List<CefMTE> cefs = new ArrayList<>();
		for(int tier = 0; tier<GTValues.V.length; tier++)
			for(int slots = 1; slots<=4; slots++)
				cefs.add(new CefMTE(n("cef."+GTValues.VN[tier]+"."+slots*slots), tier, slots));
		CEF = Collections.unmodifiableList(cefs);
	}

	public static void register(){
		int id = 10650;
		for(int i = 0; i<CEU.size(); i++){
			GregTechAPI.registerMetaTileEntity(id++, CEU.get(i));
			GregTechAPI.registerMetaTileEntity(id++, CEF.get(i));
		}

		GregTechAPI.registerMetaTileEntity(10649, INFINITE_FE_EMITTER);
		GregTechAPI.registerMetaTileEntity(10648, INFINITE_FE_RECEIVER);
		GregTechAPI.registerMetaTileEntity(10647, INFINITE_GTEU_EMITTER);
		GregTechAPI.registerMetaTileEntity(10646, INFINITE_GTEU_RECEIVER);
	}

	public static void addRecipes(){
		CeuCraftingHelper helper = new CeuCraftingHelper();
		for(ConverterMTE mte : CEU) addCeuRecipe(mte, helper);
		for(ConverterMTE mte : CEF) addCefRecipe(mte, helper);
	}

	private static void addCeuRecipe(ConverterMTE mte, CeuCraftingHelper h){
		int tier = mte.getTier();
		int slots = mte.getSlots();
		ModHandler.addShapedRecipe(mte.metaTileEntityId.toString(), mte.getStackForm(), "WTW",
				"RMR",
				"WSW",

				'M', h.hull(tier),
				'W', h.cable(tier, slots),
				'T', OreDictNames.chestWood,
				'R', h.redCable(slots),
				'S', h.circuit(tier));
	}
	private static void addCefRecipe(ConverterMTE mte, CeuCraftingHelper h){
		int tier = mte.getTier();
		int slots = mte.getSlots();
		ModHandler.addShapedRecipe(mte.metaTileEntityId.toString(), mte.getStackForm(), "WSW",
				"RMR",
				"WTW",

				'M', h.hull(tier),
				'W', h.cable(tier, slots),
				'T', OreDictNames.chestWood,
				'R', h.redCable(slots),
				'S', h.circuit(tier));
	}
}
