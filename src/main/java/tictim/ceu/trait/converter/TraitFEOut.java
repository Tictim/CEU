package tictim.ceu.trait.converter;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import tictim.ceu.mte.MTEConverter;

import static tictim.ceu.enums.BatteryFilter.ALL;

public class TraitFEOut extends TraitConverterEmitter<IEnergyStorage> implements IEnergyStorage{
	public TraitFEOut(MTEConverter converter){
		super(converter);
	}

	@Override
	protected void operate(IEnergyStorage storage){
		if(storage.canReceive()){
			int extracted = extractEnergy(Integer.MAX_VALUE, true);

			if(extracted>0){
				extracted = storage.receiveEnergy(extracted, false);
				if(extracted>0){
					extractEnergy(extracted, false);
				}
			}
		}
	}

	@Override public String getName(){
		return "TraitFeOut";
	}
	@Override public int getNetworkID(){
		return 0;
	}
	@Override protected Capability<IEnergyStorage> getImplementingCapability(){
		return CapabilityEnergy.ENERGY;
	}
	@Override public int receiveEnergy(int maxReceive, boolean simulate){
		return 0;
	}
	@Override public int extractEnergy(int maxExtract, boolean simulate){
		return converter.toTargetEnergy().convertToInt(
				converter.getEnergyStorage().extract(
						converter.toGTEU().convertToLong(maxExtract), false, true, simulate));
	}
	@Override public int getEnergyStored(){
		return converter.toTargetEnergy().convertToInt(converter.getEnergyStorage().stored(ALL));
	}
	@Override public int getMaxEnergyStored(){
		return converter.toTargetEnergy().convertToInt(converter.getEnergyStorage().capacity(ALL));
	}
	@Override public boolean canExtract(){
		return true;
	}
	@Override public boolean canReceive(){
		return false;
	}
}
