package tictim.ceu.trait.infinite;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import tictim.ceu.mte.MTEInfiniteEnergyBase;

import javax.annotation.Nullable;

// TODO Left unfinished until we have solution for removal of TileEntity
public class TraitInfiniteIC2EUEmitter extends TraitInfiniteEmitter{
	public TraitInfiniteIC2EUEmitter(MTEInfiniteEnergyBase mte){
		super(mte);
	}

	@Override public String getName(){
		return "infinite_ic2eu_emitter";
	}
	@Override public int getNetworkID(){
		return 0;
	}
	@Nullable @Override public <T> T getCapability(Capability<T> capability){
		return null;
	}
	@Override protected boolean send(EnumFacing facing){
		return false;
	}
}
