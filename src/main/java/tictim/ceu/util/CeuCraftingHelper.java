package tictim.ceu.util;

import gregtech.api.GTValues;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.item.ItemStack;

public class CeuCraftingHelper{
	private ItemStack[] hulls;

	public ItemStack hull(int tier){
		if(hulls==null){
			hulls = new ItemStack[MetaTileEntities.HULL.length];
			for(int i = 0; i<hulls.length; i++)
				hulls[i] = MetaTileEntities.HULL[i].getStackForm();
		}
		return hulls[tier];
	}

	private Object[] circuits;

	public Object circuit(int tier){
		if(circuits==null){
			circuits = new Object[GTValues.V.length];
			for(int i = 0; i<GTValues.V.length; i++)
				circuits[i] = createCircuit(i);
		}
		return circuits[tier];
	}

	private Object createCircuit(int tier){
		switch(tier){
			case 0:
				return new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Primitive);
			case 1:
				return new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Basic);
			case 2:
				return new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Good);
			case 3:
				return new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Advanced);
			case 4:
				return new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Extreme);
			case 5:
				return new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Elite);
			case 6:
				return new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Master);
			case 7:
				return new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Ultimate);
			case 8:
				return new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Superconductor);
			default: // case 9:
				return new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Infinite);
		}
	}

	private UnificationEntry[][] wires;

	public UnificationEntry wire(int tier, int stack){
		if(wires==null){
			wires = new UnificationEntry[GTValues.V.length][4];
			for(int t = 0; t<wires.length; t++)
				for(int s = 0; s<4; s++)
					wires[t][s] = new UnificationEntry(prefixWire(s+1), cableMaterialByTier(t));
		}
		return wires[tier][stack-1];
	}

	private UnificationEntry[] redCables;

	public UnificationEntry redCable(int stack){
		if(redCables==null){
			redCables = new UnificationEntry[4];
			for(int slot = 0; slot<4; slot++)
				redCables[slot] = new UnificationEntry(prefixCable(slot+1), Materials.RedAlloy);
		}
		return redCables[stack-1];
	}

	private UnificationEntry[][] cables;

	public UnificationEntry cable(int tier, int stack){
		if(cables==null){
			cables = new UnificationEntry[GTValues.V.length][4];
			for(int t = 0; t<cables.length; t++)
				for(int s = 0; s<4; s++)
					cables[t][s] = new UnificationEntry(prefixCable(s+1), cableMaterialByTier(t));
		}
		return cables[tier][stack-1];
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
			default: // case 9:
				return MarkerMaterials.Tier.Superconductor;
		}
	}

	public OrePrefix prefixCable(int stack){
		switch(stack){
			case 1:
				return OrePrefix.cableGtSingle;
			case 2:
				return OrePrefix.cableGtQuadruple;
			case 3:
				return OrePrefix.cableGtOctal;
			default: // case 3:
				return OrePrefix.cableGtHex;
		}
	}

	public OrePrefix prefixWire(int stack){
		switch(stack){
			case 1:
				return OrePrefix.wireGtSingle;
			case 2:
				return OrePrefix.wireGtQuadruple;
			case 3:
				return OrePrefix.wireGtOctal;
			default: // case 4:
				return OrePrefix.wireGtHex;
		}
	}
}
