package tictim.ceu.trait.infinite;

import net.minecraftforge.common.capabilities.Capability;
import tictim.ceu.mte.MTEInfiniteEnergyBase;

import javax.annotation.Nullable;

public class TraitInfiniteIC2EUReceiver extends TraitInfiniteEnergy{
	public TraitInfiniteIC2EUReceiver(MTEInfiniteEnergyBase mte){
		super(mte);
	}

	@Override public String getName(){
		return "infinite_ic2eu_receiver";
	}
	@Override public int getNetworkID(){
		return 0;
	}
	@Nullable @Override public <T> T getCapability(Capability<T> capability){
		return null;
	}
}
