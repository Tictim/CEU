package tictim.ceu.util;

import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.loaders.recipe.CraftingComponent;

public class CeuCraftingHelper{
	public Object hull(int tier){
		return CraftingComponent.HULL.getIngredient(tier);
	}

	public Object circuit(int tier){
		return CraftingComponent.CIRCUIT.getIngredient(tier);
	}

	public Material cableMaterialByTier(int tier){
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
				return Materials.Tungsten;
			case 6:
				return Materials.VanadiumGallium;
			case 7:
				return Materials.Naquadah;
			case 8:
				return Materials.NaquadahAlloy;
			case 9:
				return MarkerMaterials.Tier.Superconductor;
			default:
				throw new IllegalArgumentException("tier = "+tier);
		}
	}

	public OrePrefix cablePrefix(int level){
		switch(level){
			case 1:
				return OrePrefix.cableGtSingle;
			case 2:
				return OrePrefix.cableGtQuadruple;
			case 3:
				return OrePrefix.cableGtOctal;
			case 4:
				return OrePrefix.cableGtHex;
			default:
				throw new IllegalArgumentException("level = "+level);
		}
	}
}
