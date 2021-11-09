package tictim.ceu.mte.trait;

import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import tictim.ceu.mte.Converter;

import javax.annotation.Nullable;

import static tictim.ceu.enums.BatteryFilter.ALL;
import static tictim.ceu.enums.BatteryFilter.NONE;

public class FeIn extends ConverterTrait implements IEnergyStorage{
	public <MTE extends MetaTileEntity & Converter> FeIn(MTE mte){
		super(mte);
	}

	@Override public String getName(){
		return "FeIn";
	}
	@Override public int getNetworkID(){
		return 0;
	}

	@Override public int receiveEnergy(int maxReceive, boolean simulate){
		return converter.toFE().convertToInt(converter.getEnergyStorage().insert(
				converter.toGTEU().convertToLong(maxReceive),
				false,
				NONE,
				simulate));
	}

	@Override public int extractEnergy(int maxExtract, boolean simulate){
		return 0;
	}
	@Override public int getEnergyStored(){
		return converter.toFE().convertToInt(converter.getEnergyStorage().stored(ALL));
	}
	@Override public int getMaxEnergyStored(){
		return converter.toFE().convertToInt(converter.getEnergyStorage().capacity(ALL));
	}
	@Override public boolean canExtract(){
		return false;
	}
	@Override public boolean canReceive(){
		return true;
	}

	@Nullable @Override public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side){
		return capability==CapabilityEnergy.ENERGY&&(side==null||isValidSideForCapability(side)) ?
				CapabilityEnergy.ENERGY.cast(this) : null;
	}
}
