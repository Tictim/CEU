package tictim.ceu.trait.converter;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.util.GTUtility;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import tictim.ceu.mte.MTEConverter;
import tictim.ceu.trait.energystorage.ConverterEnergyStorage;

import javax.annotation.Nullable;

import static tictim.ceu.enums.BatteryFilter.ALL;
import static tictim.ceu.enums.BatteryFilter.NONE;

public class TraitGTEUIn extends TraitConverterIO implements IEnergyContainer{
	public TraitGTEUIn(MTEConverter converter){
		super(converter);
	}

	@Override public String getName(){
		return "TraitGteuIn";
	}
	@Override public int getNetworkID(){
		return TraitNetworkIds.TRAIT_ID_ENERGY_CONTAINER;
	}
	@Override public long acceptEnergyFromNetwork(EnumFacing side, long voltage, long amperage){
		if(voltage>0&&amperage>0&&(side==null||inputsEnergy(side))){
			if(voltage>getInputVoltage()){
				GTUtility.doOvervoltageExplosion(metaTileEntity, voltage);
				return Math.min(amperage, getInputAmperage());
			}
			ConverterEnergyStorage es = converter.getEnergyStorage();
			long canAccept = es.capacity(NONE)-es.stored(NONE);
			if(canAccept>=voltage){
				long amperesAccepted = Math.min(canAccept/voltage, Math.min(amperage, getInputAmperage()));
				if(amperesAccepted>0){
					es.insert(voltage*amperesAccepted, true, false);
					return amperesAccepted;
				}
			}
		}
		return 0;
	}
	@Override public boolean inputsEnergy(EnumFacing side){
		return converter.getFrontFacing()!=side;
	}
	@Override public long changeEnergy(long differenceAmount){
		if(differenceAmount==0) return 0;
		else if(differenceAmount>0){
			return converter.getEnergyStorage()
					.insert(differenceAmount,
							true,
							ALL,
							false);
		}else{
			return -converter.getEnergyStorage()
					.extract(differenceAmount==Long.MIN_VALUE ? Long.MAX_VALUE : -differenceAmount,
							true,
							true,
							ALL,
							false);
		}
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
