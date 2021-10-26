package tictim.ceu.mte;

import gregtech.api.GTValues;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IEnergyContainer;
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
import tictim.ceu.CeuResources;
import tictim.ceu.gui.InfiniteEnergyUIData;

import javax.annotation.Nullable;
import java.math.BigInteger;

public class InfiniteGteuEmitterMTE extends InfiniteEnergyMTE{
	private final Trait trait = new Trait();

	public InfiniteGteuEmitterMTE(ResourceLocation metaTileEntityId){
		super(metaTileEntityId);
	}

	@Override public void update(){
		super.update();
		if(getWorld().isRemote) return;
		for(EnumFacing facing : EnumFacing.VALUES){
			TileEntity te = getWorld().getTileEntity(getPos().offset(facing));
			if(te==null) continue;
			IEnergyContainer s = te.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, facing.getOpposite());
			if(s==null||!s.inputsEnergy(facing.getOpposite())) continue;

			long stored = trait.getEnergyStored(), voltage = Math.min(GTValues.V[trait.tier], s.getInputVoltage());
			if(stored/voltage>0){
				long accepted = s.acceptEnergyFromNetwork(facing.getOpposite(), voltage, Math.min(s.getInputAmperage(), stored/voltage))*voltage;
				if(accepted>0){
					addToRecord(accepted);
					if(!trait.isInfinite()){
						subtract(BigInteger.valueOf(accepted));
						if(energy.signum()!=1) return;
					}
				}
			}
		}
	}

	@Override protected SimpleOverlayRenderer getOverlay(){
		return CeuResources.GTEU_EMITTER_FACE;
	}
	@Override public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder){
		return new InfiniteGteuEmitterMTE(this.metaTileEntityId);
	}
	@Override protected ModularUI createUI(EntityPlayer entityPlayer){
		InfiniteEnergyUIData d = new InfiniteEnergyUIData();
		d.setEnergy(getEnergy());
		d.setInfinite(trait.isInfinite());
		d.setTier(trait.tier);
		return d.guiBuilder()
				.buttonInfinite(trait::setInfinite)
				.energyInput("EU", this::setEnergy)
				.buttonTier(trait::setTier)
				.buttonAcceptDecline()
				.createUI(getHolder(), entityPlayer);
	}

	private final class Trait extends MTETrait implements IEnergyContainer{
		private int tier = GTValues.MAX;
		private boolean infinite;

		public Trait(){
			super(InfiniteGteuEmitterMTE.this);
		}

		public int getTier(){
			return tier;
		}
		public void setTier(int tier){
			this.tier = tier;
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
			add(BigInteger.valueOf(differenceAmount));
			addToRecord(-differenceAmount);
			return differenceAmount;
		}
		@Override public long getInputAmperage(){
			return 64;
		}
		@Override public long getInputVoltage(){
			return GTValues.V[tier];
		}
		@Override public long getEnergyStored(){
			return isInfinite()||energy.compareTo(tictim.ceu.mte.InfiniteEnergyMTE.MAX_LONG)>=0 ? Long.MAX_VALUE : energy.longValueExact();
		}
		@Override public long getEnergyCapacity(){
			return getEnergyStored();
		}

		@Override public NBTTagCompound serializeNBT(){
			NBTTagCompound tag = super.serializeNBT();
			if(infinite) tag.setBoolean("Infinite", true);
			tag.setByte("Tier", (byte)tier);
			return tag;
		}
		@Override public void deserializeNBT(NBTTagCompound tag){
			super.deserializeNBT(tag);
			this.infinite = tag.getBoolean("Infinite");
			this.tier = tag.getByte("Tier");
		}

		@Nullable
		@Override
		@SuppressWarnings("unchecked")
		public <T> T getCapability(Capability<T> capability){
			return capability==GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER ? (T)this : null;
		}
	}
}
