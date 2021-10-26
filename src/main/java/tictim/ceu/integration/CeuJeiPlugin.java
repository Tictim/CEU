package tictim.ceu.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import tictim.ceu.CeuContents;
import tictim.ceu.mte.ConverterMTE;

@JEIPlugin
public class CeuJeiPlugin implements IModPlugin{
	@Override
	public void register(IModRegistry registry){
		IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();

		for(ConverterMTE mte : CeuContents.CEU) disableItem(blacklist, mte);
		for(ConverterMTE mte : CeuContents.CEF) disableItem(blacklist, mte);
	}

	private void disableItem(IIngredientBlacklist blacklist, ConverterMTE mte){
		if(mte.isDisabled()) blacklist.addIngredientToBlacklist(mte.getStackForm());
	}
}
