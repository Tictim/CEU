package tictim.ceu.trait.converter;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IEnergyContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import tictim.ceu.mte.MTEConverter;

import javax.annotation.Nullable;

import static tictim.ceu.enums.BatteryFilter.ALL;

public class TraitGTEUOut extends TraitConverterEmitter implements IEnergyContainer{
	public TraitGTEUOut(MTEConverter converter){
		super(converter);
	}

	@Override protected void operate(TileEntity tileEntity){
		IEnergyContainer energy = tileEntity.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, converter.getFrontFacing().getOpposite());
		if(energy==null||!energy.inputsEnergy(converter.getFrontFacing().getOpposite())) return;
		long voltage = converter.voltage();
		long canEmit = converter.toTargetEnergy().convertToInt(converter.getEnergyStorage().extract(Long.MAX_VALUE, false, false, true));

		if(canEmit>=voltage){
			long emitAmpere = Math.min(converter.amperage()==9 ? 8 : converter.amperage(), canEmit/voltage);
			if(emitAmpere>0){
				emitAmpere = energy.acceptEnergyFromNetwork(converter.getFrontFacing().getOpposite(), voltage, emitAmpere);
				if(emitAmpere>0) converter.getEnergyStorage().extract(voltage*emitAmpere, false, false, false);
			}
		}
	}
	@Override public String getName(){
		return "TraitGteuOut";
	}
	@Override public int getNetworkID(){
		return TraitNetworkIds.TRAIT_ID_ENERGY_CONTAINER;
	}
	@Override public long acceptEnergyFromNetwork(EnumFacing side, long voltage, long amperage){
		return 0;
	}
	@Override public boolean inputsEnergy(EnumFacing side){
		return false;
	}
	@Override public boolean outputsEnergy(EnumFacing side){
		return true;
	}
	@Override public long changeEnergy(long differenceAmount){
		return 0;
	}
	@Override public long getEnergyStored(){
		return converter.getEnergyStorage().stored(ALL);
	}
	@Override public long getEnergyCapacity(){
		return converter.getEnergyStorage().capacity(ALL);
	}
	@Override public long getInputAmperage(){
		return converter.amperage();
	}
	@Override public long getInputVoltage(){
		return converter.voltage();
	}

	@Nullable @Override public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side){
		return capability==GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER&&(side==null||isValidSideForCapability(side)) ?
				GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER.cast(this) : null;
	}
}
