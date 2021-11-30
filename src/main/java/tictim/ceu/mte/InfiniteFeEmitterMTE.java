package tictim.ceu.mte;

import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MTETrait;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.render.SimpleOverlayRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import tictim.ceu.CeuResources;
import tictim.ceu.gui.InfiniteEnergyUIData;

import javax.annotation.Nullable;
import java.math.BigInteger;

public class InfiniteFeEmitterMTE extends InfiniteEnergyMTE{
	private final Trait trait = new Trait();

	public InfiniteFeEmitterMTE(ResourceLocation metaTileEntityId){
		super(metaTileEntityId);
	}

	@Override public void update(){
		super.update();
		if(getWorld().isRemote) return;
		for(EnumFacing facing : EnumFacing.VALUES){
			TileEntity te = getWorld().getTileEntity(getPos().offset(facing));
			if(te==null) continue;
			IEnergyStorage s = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
			if(s!=null&&s.canReceive()){
				int received = s.receiveEnergy(trait.getEnergyStored(), false);
				addToRecord(received);
				if(!trait.isInfinite()){
					subtract(BigInteger.valueOf(received));
					if(energy.signum()!=1) return;
				}
			}
		}
	}

	@Override protected SimpleOverlayRenderer getOverlay(){
		return CeuResources.FE_EMITTER_FACE;
	}
	@Override public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder){
		return new InfiniteFeEmitterMTE(this.metaTileEntityId);
	}
	@Override protected ModularUI createUI(EntityPlayer entityPlayer){
		InfiniteEnergyUIData d = new InfiniteEnergyUIData();
		d.setEnergy(getEnergy());
		d.setInfinite(trait.isInfinite());
		return d.guiBuilder()
				.buttonInfinite(trait::setInfinite)
				.energyInput("FE", this::setEnergy)
				.buttonAcceptDecline()
				.createUI(getHolder(), entityPlayer);
	}

	private final class Trait extends MTETrait implements IEnergyStorage{
		private boolean infinite;

		public Trait(){
			super(InfiniteFeEmitterMTE.this);
		}

		public boolean isInfinite(){
			return infinite;
		}
		public void setInfinite(boolean infinite){
			this.infinite = infinite;
		}

		@Override public String getName(){
			return "Trait";
		}
		@Override public int getNetworkID(){
			return 0;
		}

		@Override public int receiveEnergy(int maxReceive, boolean simulate){
			return 0;
		}
		@Override public int extractEnergy(int maxExtract, boolean simulate){
			if(maxExtract<=0) return 0;
			BigInteger i = BigInteger.valueOf(maxExtract);
			if(!isInfinite()&&i.compareTo(energy)>0) i = energy;
			if(!simulate){
				subtract(i);
				addToRecord(i.longValue());
			}
			return i.intValue();
		}

		@Override public int getEnergyStored(){
			return isInfinite()||energy.compareTo(MAX_INT)>=0 ? Integer.MAX_VALUE : energy.intValueExact();
		}
		@Override public int getMaxEnergyStored(){ //whatever
			return getEnergyStored();
		}
		@Override public boolean canExtract(){
			return true;
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

		@Override public NBTTagCompound serializeNBT(){
			NBTTagCompound tag = super.serializeNBT();
			if(infinite) tag.setBoolean("Infinite", true);
			return tag;
		}
		@Override public void deserializeNBT(NBTTagCompound tag){
			super.deserializeNBT(tag);
			this.infinite = tag.getBoolean("Infinite");
		}
	}
}
