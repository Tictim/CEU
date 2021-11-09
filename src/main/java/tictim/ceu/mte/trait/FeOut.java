package tictim.ceu.mte.trait;

import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import tictim.ceu.mte.Converter;

import javax.annotation.Nullable;

import static tictim.ceu.enums.BatteryFilter.ALL;

public class FeOut extends ConverterEmitterTrait implements IEnergyStorage{
	public <MTE extends MetaTileEntity & Converter> FeOut(MTE mte){
		super(mte);
	}

	@Override protected void operate(TileEntity tileEntity){
		IEnergyStorage storage = tileEntity.getCapability(CapabilityEnergy.ENERGY, metaTileEntity.getFrontFacing().getOpposite());
		if(storage==null||!storage.canReceive()) return;
		int extracted = extractEnergy(Integer.MAX_VALUE, true);
		if(extracted>0){
			extracted = storage.receiveEnergy(extracted, false);
			if(extracted>0){
				extractEnergy(extracted, false);
			}
		}
	}

	@Override public String getName(){
		return "FeOut";
	}
	@Override public int getNetworkID(){
		return 0;
	}
	@Override public int receiveEnergy(int maxReceive, boolean simulate){
		return 0;
	}
	@Override public int extractEnergy(int maxExtract, boolean simulate){
		return converter.toFE().convertToInt(
				converter.getEnergyStorage().extract(
						converter.toGTEU().convertToLong(maxExtract), false, true, simulate));
	}
	@Override public int getEnergyStored(){
		return converter.toFE().convertToInt(converter.getEnergyStorage().stored(ALL));
	}
	@Override public int getMaxEnergyStored(){
		return converter.toFE().convertToInt(converter.getEnergyStorage().capacity(ALL));
	}
	@Override public boolean canExtract(){
		return true;
	}
	@Override public boolean canReceive(){
		return false;
	}

	@Nullable @Override public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side){
		return capability==CapabilityEnergy.ENERGY&&(side==null||isValidSideForCapability(side)) ?
				CapabilityEnergy.ENERGY.cast(this) : null;
	}
}
