package tictim.ceu.trait.energystorage;

import gregtech.api.capability.IElectricItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import tictim.ceu.energy.ElectricItemFE;
import tictim.ceu.mte.MTEConverter;

import javax.annotation.Nullable;

public class FEConverterEnergyStorage extends ConverterEnergyStorage{
	public FEConverterEnergyStorage(MTEConverter mte){
		super(mte);
	}

	@Nullable @Override protected IElectricItem getWrappedBatteryContainer(ItemStack s){
		IEnergyStorage storage = getEnergyStorage(s);
		return storage!=null ? new ElectricItemFE(converter, storage) : null;
	}

	@Nullable protected IEnergyStorage getEnergyStorage(ItemStack s){
		IEnergyStorage storage = s.getCapability(CapabilityEnergy.ENERGY, null);
		return storage!=null&&(converter.convertsToTargetEnergy() ? storage.canReceive() : storage.canExtract()) ? storage : null;
	}
}
