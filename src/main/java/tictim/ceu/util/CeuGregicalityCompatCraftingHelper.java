package tictim.ceu.util;

import gregicadditions.GAMaterials;
import gregicadditions.recipes.helper.GACraftingComponents;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.Material;

public class CeuGregicalityCompatCraftingHelper extends CeuCraftingHelper{
	@Override public Object hull(int tier){
		return GACraftingComponents.HULL.getIngredient(tier);
	}

	@Override public Object circuit(int tier){
		return GACraftingComponents.CIRCUIT.getIngredient(tier);
	}

	@Override public Material cableMaterialByTier(int tier){
		switch(tier){
			case 0:
				return Materials.Lead;
			case 1:
				return Materials.Tin;
			case 2:
				return Materials.Copper;
			case 3:
				return Materials.Gold;
			case 4:
				return Materials.Aluminium;
			case 5:
				return Materials.Platinum;
			case 6:
				return Materials.NiobiumTitanium;
			case 7:
				return Materials.Naquadah;
			case 8:
				return Materials.NaquadahAlloy;
			case 9:
				return GAMaterials.AbyssalAlloy;
			case 10:
				return GAMaterials.TitanSteel;
			case 11:
				return GAMaterials.BlackTitanium;
			case 12:
			case 13:
				return GAMaterials.Neutronium;
			case 14:
			default:
				return MarkerMaterials.Tier.Superconductor;
		}
	}
}
