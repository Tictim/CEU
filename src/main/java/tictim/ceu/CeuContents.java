package tictim.ceu;

import gregicadditions.GAValues;
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
import tictim.ceu.mte.InfiniteEnergyMTE;
import tictim.ceu.mte.InfiniteFeEmitterMTE;
import tictim.ceu.mte.InfiniteFeReceiverMTE;
import tictim.ceu.mte.InfiniteGteuEmitterMTE;
import tictim.ceu.mte.InfiniteGteuReceiverMTE;
import tictim.ceu.util.CeuCraftingHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class CeuContents{
	private CeuContents(){}

	private static ResourceLocation n(String s){
		return new ResourceLocation(CeuMod.MODID, s);
	}

	private static List<MetaTileEntity> ceu;
	private static List<MetaTileEntity> cef;

	public static List<MetaTileEntity> getCeu(){
		return ceu;
	}
	public static List<MetaTileEntity> getCef(){
		return cef;
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

	static void register(){
		if(ceu!=null) throw new IllegalStateException("Registered twice");

		InfiniteEnergyMTE infiniteGteuEmitter;
		if(isGregicalityCompatActive()){
			ceu = CeuGregicalityCompat.converter(true);
			cef = CeuGregicalityCompat.converter(false);
			infiniteGteuEmitter = CeuGregicalityCompat.infiniteGteuEmitter(n("gteu_emitter"));
		}else{
			ceu = converter(true);
			cef = converter(false);
			infiniteGteuEmitter = new InfiniteGteuEmitterMTE(n("gteu_emitter"));
		}

		for(int i = 0; i<ceu.size(); i++){
			GregTechAPI.registerMetaTileEntity(10650+i*2, ceu.get(i));
			GregTechAPI.registerMetaTileEntity(10650+i*2+1, cef.get(i));
		}

		GregTechAPI.registerMetaTileEntity(10649, new InfiniteFeEmitterMTE(n("fe_emitter")));
		GregTechAPI.registerMetaTileEntity(10648, new InfiniteFeReceiverMTE(n("fe_receiver")));
		GregTechAPI.registerMetaTileEntity(10647, infiniteGteuEmitter);
		GregTechAPI.registerMetaTileEntity(10646, new InfiniteGteuReceiverMTE(n("gteu_receiver")));
	}

	@Nullable private static volatile Boolean gregicalityPresent;

	/**
	 * FUCK
	 */
	public static boolean isGregicalityPresent(){
		if(gregicalityPresent==null){
			synchronized(CeuContents.class){
				if(gregicalityPresent==null){
					if(Loader.isModLoaded("gtadditions")){
						try{
							new Object(){{
								//noinspection ResultOfMethodCallIgnored
								GAValues.class.toString();
							}};
							gregicalityPresent = true;
						}catch(NoClassDefFoundError ex){
							gregicalityPresent = false;
						}
					}else gregicalityPresent = false;
				}
			}
		}
		return gregicalityPresent;
	}

	public static boolean isGregicalityCompatActive(){
		return isGregicalityPresent()&&!CeuConfig.config().disableGregicalityCompat();
	}

	public static void addRecipes(){
		CeuCraftingHelper helper = isGregicalityCompatActive() ? CeuGregicalityCompat.getCraftingHelper() : new CeuCraftingHelper();
		for(MetaTileEntity mte : ceu) addConverterRecipe(mte, helper);
		for(MetaTileEntity mte : cef) addConverterRecipe(mte, helper);
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
