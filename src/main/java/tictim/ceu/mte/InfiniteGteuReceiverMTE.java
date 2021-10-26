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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import tictim.ceu.CeuResources;
import tictim.ceu.gui.InfiniteEnergyUIData;

import javax.annotation.Nullable;
import java.math.BigInteger;

public class InfiniteGteuReceiverMTE extends InfiniteEnergyMTE{
	public InfiniteGteuReceiverMTE(ResourceLocation metaTileEntityId){
		super(metaTileEntityId);
		new Trait();
	}

	@Override protected SimpleOverlayRenderer getOverlay(){
		return CeuResources.GTEU_RECEIVER_FACE;
	}
	@Override public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder){
		return new InfiniteGteuReceiverMTE(this.metaTileEntityId);
	}
	@Override protected ModularUI createUI(EntityPlayer entityPlayer){
		InfiniteEnergyUIData d = new InfiniteEnergyUIData();
		d.setEnergy(getEnergy());
		return d.guiBuilder()
				.energyInput("EU", this::setEnergy)
				.buttonAcceptDecline()
				.createUI(getHolder(), entityPlayer);
	}

	private final class Trait extends MTETrait implements IEnergyContainer{
		public Trait(){
			super(InfiniteGteuReceiverMTE.this);
		}

		@Override public String getName(){
			return "Trait";
		}
		@Override public int getNetworkID(){
			return MTETrait.TraitNetworkIds.TRAIT_ID_ENERGY_CONTAINER;
		}

		@Override public long acceptEnergyFromNetwork(EnumFacing side, long voltage, long amperage){
			add(BigInteger.valueOf(voltage*amperage));
			addToRecord(voltage*amperage);
			return amperage;
		}
		@Override public boolean inputsEnergy(EnumFacing side){
			return true;
		}
		@Override public long changeEnergy(long differenceAmount){
			add(BigInteger.valueOf(differenceAmount));
			addToRecord(differenceAmount);
			return differenceAmount;
		}
		@Override public long getInputAmperage(){
			return 64;
		}
		@Override public long getInputVoltage(){
			return GTValues.V[GTValues.MAX];
		}
		@Override public long getEnergyStored(){
			return energy.compareTo(tictim.ceu.mte.InfiniteEnergyMTE.MAX_LONG)>=0 ? Long.MAX_VALUE : energy.longValueExact();
		}
		@Override public long getEnergyCapacity(){
			return energy.compareTo(tictim.ceu.mte.InfiniteEnergyMTE.MAX_LONG)>=0 ? Long.MAX_VALUE : energy.longValueExact();
		}

		@Nullable
		@Override
		@SuppressWarnings("unchecked")
		public <T> T getCapability(Capability<T> capability){
			return capability==GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER ? (T)this : null;
		}
	}
}
