package tictim.ceu.mte;

import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MTETrait;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.render.SimpleOverlayRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import tictim.ceu.CeuResources;
import tictim.ceu.gui.InfiniteEnergyUIData;

import java.math.BigInteger;

public class InfiniteFeReceiverMTE extends InfiniteEnergyMTE{
	public InfiniteFeReceiverMTE(ResourceLocation metaTileEntityId){
		super(metaTileEntityId);
		new Trait();
	}

	@Override protected SimpleOverlayRenderer getOverlay(){
		return CeuResources.FE_RECEIVER_FACE;
	}
	@Override public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder){
		return new InfiniteFeReceiverMTE(this.metaTileEntityId);
	}
	@Override protected ModularUI createUI(EntityPlayer entityPlayer){
		InfiniteEnergyUIData d = new InfiniteEnergyUIData();
		d.setEnergy(getEnergy());
		return d.guiBuilder()
				.energyInput("FE", this::setEnergy)
				.buttonAcceptDecline()
				.createUI(getHolder(), entityPlayer);
	}

	private final class Trait extends MTETrait implements IEnergyStorage{
		public Trait(){
			super(InfiniteFeReceiverMTE.this);
		}

		@Override public String getName(){
			return "Trait";
		}
		@Override public int getNetworkID(){
			return 0;
		}

		@Override public int receiveEnergy(int maxReceive, boolean simulate){
			if(maxReceive<=0) return 0;
			else{
				if(!simulate){
					add(BigInteger.valueOf(maxReceive));
					addToRecord(maxReceive);
				}
				return maxReceive;
			}
		}

		@Override public int extractEnergy(int maxExtract, boolean simulate){
			return 0;
		}
		@Override public int getEnergyStored(){
			return energy.compareTo(MAX_INT)>=0 ? Integer.MAX_VALUE : energy.intValueExact();
		}
		@Override public int getMaxEnergyStored(){ //whatever
			return getEnergyStored();
		}
		@Override public boolean canExtract(){
			return false;
		}
		@Override public boolean canReceive(){
			return true;
		}

		@Override public <T> T getCapability(Capability<T> capability){
			return capability==CapabilityEnergy.ENERGY ? CapabilityEnergy.ENERGY.cast(this) : null;
		}
	}
}
