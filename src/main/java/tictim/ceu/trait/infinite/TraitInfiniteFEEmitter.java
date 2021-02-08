package tictim.ceu.trait.infinite;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import tictim.ceu.mte.MTEInfiniteEnergyBase;
import tictim.ceu.util.Conversion;

import javax.annotation.Nullable;
import java.math.BigInteger;

public class TraitInfiniteFEEmitter extends TraitInfiniteEmitter implements IEnergyStorage{
	public TraitInfiniteFEEmitter(MTEInfiniteEnergyBase mte){
		super(mte);
	}

	@Override public String getName(){
		return "infinite_fe_emitter";
	}
	@Override public int getNetworkID(){
		return 0;
	}

	@Override protected boolean send(EnumFacing facing){
		TileEntity te = metaTileEntity.getWorld().getTileEntity(this.metaTileEntity.getPos().offset(facing));
		if(te!=null){
			IEnergyStorage s = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
			if(s!=null&&s.canReceive()){
				if(!isInfinite()) subtract(BigInteger.valueOf(s.receiveEnergy(getEnergyStored(), false)));
				else s.receiveEnergy(getEnergyStored(), false);
				return true;
			}
		}
		return false;
	}

	@Override public int receiveEnergy(int maxReceive, boolean simulate){
		return 0;
	}
	@Override public int extractEnergy(int maxExtract, boolean simulate){
		if(maxExtract<=0||mte.isDisabled()) return 0;
		else{
			BigInteger i = BigInteger.valueOf(maxExtract);
			if(i.compareTo(energy)>0) i = energy;
			if(!simulate) subtract(i);
			return i.intValue();
		}
	}

	@Override public int getEnergyStored(){
		return isInfinite()||energy.compareTo(Conversion.MAX_INT)>=0 ? Integer.MAX_VALUE : energy.intValueExact();
	}
	@Override public int getMaxEnergyStored(){ //whatever
		return getEnergyStored();
	}
	@Override public boolean canExtract(){
		return !mte.isDisabled();
	}
	@Override public boolean canReceive(){
		return false;
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability){
		return capability==CapabilityEnergy.ENERGY ? (T)this : null;
	}
}
