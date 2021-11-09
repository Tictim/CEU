package tictim.ceu.mte.trait;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import tictim.ceu.mte.Converter;

import javax.annotation.Nullable;

import static tictim.ceu.enums.BatteryFilter.ALL;

public class GteuOut extends ConverterEmitterTrait implements IEnergyContainer{
	public <MTE extends MetaTileEntity & Converter> GteuOut(MTE mte){
		super(mte);
	}

	@Override public String getName(){
		return "GteuOut";
	}
	@Override public int getNetworkID(){
		return TraitNetworkIds.TRAIT_ID_ENERGY_CONTAINER;
	}

	@Override protected void operate(TileEntity tileEntity){
		IEnergyContainer energy = tileEntity.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, metaTileEntity.getFrontFacing().getOpposite());
		if(energy==null||!energy.inputsEnergy(metaTileEntity.getFrontFacing().getOpposite())) return;
		long voltage = converter.voltage();
		long canEmit = converter.getEnergyStorage().extract(Long.MAX_VALUE, false, false, true);

		if(canEmit>=voltage){
			long emitAmpere = Math.min(converter.amperage()==9 ? 8 : converter.amperage(), canEmit/voltage);
			if(emitAmpere>0){
				emitAmpere = energy.acceptEnergyFromNetwork(metaTileEntity.getFrontFacing().getOpposite(), voltage, emitAmpere);
				if(emitAmpere>0) converter.getEnergyStorage().extract(voltage*emitAmpere, false, false, false);
			}
		}
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
