package tictim.ceu.trait.infinite;

import gregtech.api.GTValues;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IEnergyContainer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import tictim.ceu.mte.MTEInfiniteEnergyBase;
import tictim.ceu.util.Conversion;

import javax.annotation.Nullable;
import java.math.BigInteger;

public class TraitInfiniteGTEUReceiver extends TraitInfiniteEnergy implements IEnergyContainer{
	public TraitInfiniteGTEUReceiver(MTEInfiniteEnergyBase mte){
		super(mte);
	}

	@Override public String getName(){
		return "infinite_gteu_receiver";
	}
	@Override public int getNetworkID(){
		return TraitNetworkIds.TRAIT_ID_ENERGY_CONTAINER;
	}
	@Override public long acceptEnergyFromNetwork(EnumFacing side, long voltage, long amperage){
		if(mte.isDisabled()) return 0;
		add(BigInteger.valueOf(voltage*amperage));
		return amperage;
	}
	@Override public boolean inputsEnergy(EnumFacing side){
		return !mte.isDisabled();
	}
	@Override public long changeEnergy(long differenceAmount){
		add(BigInteger.valueOf(differenceAmount));
		return differenceAmount;
	}
	@Override public long getInputAmperage(){
		return 64;
	}
	@Override public long getInputVoltage(){
		return GTValues.V[GTValues.MAX];
	}
	@Override public long getEnergyStored(){
		return energy.compareTo(Conversion.MAX_LONG)>=0 ? Long.MAX_VALUE : energy.longValueExact();
	}
	@Override public long getEnergyCapacity(){
		return energy.compareTo(Conversion.MAX_LONG)>=0 ? Long.MAX_VALUE : energy.longValueExact();
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability){
		return capability==GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER ? (T)this : null;
	}
}
