package tictim.ceu.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import net.minecraft.item.ItemStack;
import tictim.ceu.contents.CeuBlocks;
import tictim.ceu.contents.CeuMetaTileEntities;
import tictim.ceu.mte.MTEConverter;
import tictim.ceu.mte.MTEInfiniteEnergyBase;

@JEIPlugin
public class CeuJeiPlugin implements IModPlugin{
	@Override
	public void register(IModRegistry registry){
		IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
		blacklist.addIngredientToBlacklist(new ItemStack(CeuBlocks.TEST_ITEM));

		for(MTEConverter mte : CeuMetaTileEntities.CEU) disableItem(blacklist, mte);
		for(MTEConverter mte : CeuMetaTileEntities.CEF) disableItem(blacklist, mte);
		for(MTEConverter mte : CeuMetaTileEntities.ICEU) disableItem(blacklist, mte);
		for(MTEConverter mte : CeuMetaTileEntities.ICEF) disableItem(blacklist, mte);
		disableItem(blacklist, CeuMetaTileEntities.INFINITE_FE_EMITTER);
		disableItem(blacklist, CeuMetaTileEntities.INFINITE_FE_RECEIVER);
		disableItem(blacklist, CeuMetaTileEntities.INFINITE_GTEU_EMITTER);
		disableItem(blacklist, CeuMetaTileEntities.INFINITE_GTEU_RECEIVER);
		disableItem(blacklist, CeuMetaTileEntities.INFINITE_IC2EU_EMITTER);
		disableItem(blacklist, CeuMetaTileEntities.INFINITE_IC2EU_RECEIVER);
	}

	private void disableItem(IIngredientBlacklist blacklist, MTEConverter mte){
		if(mte.isDisabled()) blacklist.addIngredientToBlacklist(mte.getStackForm());
	}
	private void disableItem(IIngredientBlacklist blacklist, MTEInfiniteEnergyBase<?> mte){
		if(mte.isDisabled()) blacklist.addIngredientToBlacklist(mte.getStackForm());
	}
}
