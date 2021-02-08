package tictim.ceu.contents;

import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.items.OreDictNames;
import gregtech.api.recipes.ModHandler;
import net.minecraft.util.ResourceLocation;
import tictim.ceu.CeuMod;
import tictim.ceu.mte.MTEConverter;
import tictim.ceu.mte.*;
import tictim.ceu.util.CeuCraftingHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CeuMetaTileEntities{
	private CeuMetaTileEntities(){}

	private static ResourceLocation n(String s){
		return new ResourceLocation(CeuMod.MODID, s);
	}

	public static final List<MTECeu> CEU;
	public static final List<MTECef> CEF;
	public static final List<MTEIceu> ICEU;
	public static final List<MTEIcef> ICEF;

	static{
		List<MTECeu> ceus = new ArrayList<>();
		for(int tier = GTValues.ULV; tier<=GTValues.MAX; tier++){
			for(int slots = 1; slots<=4; slots++){
				ceus.add(new MTECeu(n("ceu."+GTValues.VN[tier]+"."+slots*slots), tier, slots));
			}
		}
		CEU = Collections.unmodifiableList(ceus);

		List<MTECef> cefs = new ArrayList<>();
		for(int tier = GTValues.ULV; tier<=GTValues.MAX; tier++){
			for(int slots = 1; slots<=4; slots++){
				cefs.add(new MTECef(n("cef."+GTValues.VN[tier]+"."+slots*slots), tier, slots));
			}
		}
		CEF = Collections.unmodifiableList(cefs);

		List<MTEIceu> iceus = new ArrayList<>();
		for(int tier = GTValues.LV; tier<=GTValues.IV; tier++){
			for(int slots = 1; slots<=4; slots++){
				iceus.add(new MTEIceu(n("iceu."+GTValues.VN[tier]+"."+slots*slots), tier, slots));
			}
		}
		ICEU = Collections.unmodifiableList(iceus);

		List<MTEIcef> icefs = new ArrayList<>();
		for(int tier = GTValues.LV; tier<=GTValues.IV; tier++){
			for(int slots = 1; slots<=4; slots++){
				icefs.add(new MTEIcef(n("icef."+GTValues.VN[tier]+"."+slots*slots), tier, slots));
			}
		}
		ICEF = Collections.unmodifiableList(icefs);
	}

	public static final MTEInfiniteFEEmitter INFINITE_FE_EMITTER = new MTEInfiniteFEEmitter(n("fe_emitter"));
	public static final MTEInfiniteFEReceiver INFINITE_FE_RECEIVER = new MTEInfiniteFEReceiver(n("fe_receiver"));
	public static final MTEInfiniteGTEUEmitter INFINITE_GTEU_EMITTER = new MTEInfiniteGTEUEmitter(n("gteu_emitter"));
	public static final MTEInfiniteGTEUReceiver INFINITE_GTEU_RECEIVER = new MTEInfiniteGTEUReceiver(n("gteu_receiver"));
	public static final MTEInfiniteIC2EUEmitter INFINITE_IC2EU_EMITTER = new MTEInfiniteIC2EUEmitter(n("ic2eu_emitter"));
	public static final MTEInfiniteIC2EUReceiver INFINITE_IC2EU_RECEIVER = new MTEInfiniteIC2EUReceiver(n("ic2eu_receiver"));

	public static void register(){
		int id = 10650;
		for(int i = 0; i<CEU.size(); i++){
			GregTechAPI.registerMetaTileEntity(id++, CEU.get(i));
			GregTechAPI.registerMetaTileEntity(id++, CEF.get(i));
		}
		for(int i = 0; i<ICEU.size(); i++){
			GregTechAPI.registerMetaTileEntity(id++, ICEU.get(i));
			GregTechAPI.registerMetaTileEntity(id++, ICEF.get(i));
		}

		GregTechAPI.registerMetaTileEntity(10649, INFINITE_FE_EMITTER);
		GregTechAPI.registerMetaTileEntity(10648, INFINITE_FE_RECEIVER);
		GregTechAPI.registerMetaTileEntity(10647, INFINITE_GTEU_EMITTER);
		GregTechAPI.registerMetaTileEntity(10646, INFINITE_GTEU_RECEIVER);
		GregTechAPI.registerMetaTileEntity(10645, INFINITE_IC2EU_EMITTER);
		GregTechAPI.registerMetaTileEntity(10644, INFINITE_IC2EU_RECEIVER);
	}

	public static void addRecipes(){
		CeuCraftingHelper helper = new CeuCraftingHelper();
		for(MTEConverter mte : CEU) addCeuRecipe(mte, helper);
		for(MTEConverter mte : CEF) addCefRecipe(mte, helper);
	}

	private static void addCeuRecipe(MTEConverter mte, CeuCraftingHelper h){
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
	private static void addCefRecipe(MTEConverter mte, CeuCraftingHelper h){
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
