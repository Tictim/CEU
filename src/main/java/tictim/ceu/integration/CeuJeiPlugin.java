package tictim.ceu.integration;

import gregtech.api.metatileentity.MetaTileEntity;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import tictim.ceu.CeuContents;
import tictim.ceu.mte.Converter;

@JEIPlugin
public class CeuJeiPlugin implements IModPlugin{
	@Override
	public void register(IModRegistry registry){
		IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();

		for(MetaTileEntity mte : CeuContents.getCeu()) disableItem(blacklist, mte);
		for(MetaTileEntity mte : CeuContents.getCef()) disableItem(blacklist, mte);
	}

	private void disableItem(IIngredientBlacklist blacklist, MetaTileEntity mte){
		if(!(mte instanceof Converter)) throw new IllegalArgumentException("Expected converter");
		if(((Converter)mte).isDisabled()) blacklist.addIngredientToBlacklist(mte.getStackForm());
	}
}
