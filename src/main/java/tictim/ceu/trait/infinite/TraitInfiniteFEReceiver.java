package tictim.ceu.trait.infinite;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import tictim.ceu.mte.MTEInfiniteEnergyBase;
import tictim.ceu.util.Conversion;

import javax.annotation.Nullable;
import java.math.BigInteger;

public class TraitInfiniteFEReceiver extends TraitInfiniteEnergy implements IEnergyStorage{
	public TraitInfiniteFEReceiver(MTEInfiniteEnergyBase mte){
		super(mte);
	}

	@Override public String getName(){
		return "infinite_fe_receiver";
	}
	@Override public int getNetworkID(){
		return 0;
	}

	@Override public int receiveEnergy(int maxReceive, boolean simulate){
		if(maxReceive<=0||mte.isDisabled()) return 0;
		else{
			if(!simulate) add(BigInteger.valueOf(maxReceive));
			return maxReceive;
		}
	}

	@Override public int extractEnergy(int maxExtract, boolean simulate){
		return 0;
	}
	@Override public int getEnergyStored(){
		return energy.compareTo(Conversion.MAX_INT)>=0 ? Integer.MAX_VALUE : energy.intValueExact();
	}
	@Override public int getMaxEnergyStored(){ //whatever
		return getEnergyStored();
	}
	@Override public boolean canExtract(){
		return false;
	}
	@Override public boolean canReceive(){
		return !mte.isDisabled();
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability){
		return capability==CapabilityEnergy.ENERGY ? (T)this : null;
	}
}
