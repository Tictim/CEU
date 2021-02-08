package tictim.ceu.trait.infinite;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import tictim.ceu.mte.MTEInfiniteEnergyBase;

public abstract class TraitInfiniteEmitter extends TraitInfiniteEnergy{
	private boolean isInfinite;

	public TraitInfiniteEmitter(MTEInfiniteEnergyBase mte){
		super(mte);
	}

	public boolean isInfinite(){
		return isInfinite;
	}
	public void setInfinite(boolean infinite){
		this.isInfinite = infinite;
	}

	@Override public void update(){
		if(!metaTileEntity.getWorld().isRemote&&!mte.isDisabled()){
			if(isInfinite) for(EnumFacing facing : EnumFacing.VALUES) send(facing);
			else if(energy.signum()==1) for(EnumFacing facing : EnumFacing.VALUES)
				if(send(facing)&&energy.signum()!=1) break;
		}
	}

	protected abstract boolean send(EnumFacing facing);

	@Override public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = super.serializeNBT();
		if(isInfinite) nbt.setBoolean("infinite", true);
		return nbt;
	}
	@Override public void deserializeNBT(NBTTagCompound nbt){
		super.deserializeNBT(nbt);
		isInfinite = nbt.getBoolean("infinite");
	}
}
