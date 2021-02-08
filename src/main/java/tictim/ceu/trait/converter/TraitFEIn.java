package tictim.ceu.trait.converter;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import tictim.ceu.mte.MTEConverter;

import static tictim.ceu.enums.BatteryFilter.ALL;
import static tictim.ceu.enums.BatteryFilter.NONE;

public class TraitFEIn extends TraitConverterIO implements IEnergyStorage{
	public TraitFEIn(MTEConverter converter){
		super(converter);
	}

	@Override protected Capability<?> getImplementingCapability(){
		return CapabilityEnergy.ENERGY;
	}
	@Override public String getName(){
		return "TraitFeIn";
	}
	@Override public int getNetworkID(){
		return 0;
	}
	@Override public int receiveEnergy(int maxReceive, boolean simulate){
		long changed = converter.getEnergyStorage().insert(
				converter.toGTEU().convert(maxReceive, Long.MAX_VALUE),
				false,
				NONE,
				simulate);
		return converter.toTargetEnergy().convertToInt(changed, Integer.MAX_VALUE);
	}
	@Override public int extractEnergy(int maxExtract, boolean simulate){
		return 0;
	}
	@Override public int getEnergyStored(){
		return converter.toTargetEnergy().convertToInt(converter.getEnergyStorage().stored(ALL));
	}
	@Override public int getMaxEnergyStored(){
		return converter.toTargetEnergy().convertToInt(converter.getEnergyStorage().capacity(ALL));
	}
	@Override public boolean canExtract(){
		return false;
	}
	@Override public boolean canReceive(){
		return true;
	}
}
