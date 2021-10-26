package tictim.ceu.mte;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import tictim.ceu.config.CeuConfig;
import tictim.ceu.CeuResources;
import tictim.ceu.mte.trait.ConverterEmitterTrait;
import tictim.ceu.mte.trait.ConverterTrait;
import tictim.ceu.util.Ratio;

import javax.annotation.Nullable;

import static tictim.ceu.enums.BatteryFilter.ALL;
import static tictim.ceu.enums.BatteryFilter.NONE;

public class CefMTE extends ConverterMTE{
	public CefMTE(ResourceLocation id, int tier, int slots){
		super(id, tier, slots);
	}

	@Override protected void reinitializeEnergyContainer(){
		super.reinitializeEnergyContainer();
		new FeIn();
		new GteuOut();
	}

	@Override public Ratio ratio(){
		return CeuConfig.config().getCefRatio(getTier());
	}
	@Override public boolean isDisabled(){
		return CeuConfig.config().isCefDisabled(getTier());
	}
	@Override public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder){
		return new CefMTE(metaTileEntityId, getTier(), getSlots());
	}

	@Override public boolean convertsToFE(){
		return false;
	}

	@Override public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline){
		super.renderMetaTileEntity(renderState, translation, pipeline);
		CeuResources.CEF_FACE.renderSided(getFrontFacing(), renderState, translation, pipeline);
	}

	@Nullable @Override protected TextureArea getTargetEnergyBatteryOptionIcon(){
		return CeuResources.FE_IO_MODE_BUTTON;
	}

	private final class FeIn extends ConverterTrait implements IEnergyStorage{
		public FeIn(){
			super(CefMTE.this);
		}

		@Override public String getName(){
			return "FeIn";
		}
		@Override public int getNetworkID(){
			return 0;
		}

		@Override public int receiveEnergy(int maxReceive, boolean simulate){
			return toFE().convertToInt(getEnergyStorage().insert(
					toGTEU().convertToLong(maxReceive),
					false,
					NONE,
					simulate));
		}

		@Override public int extractEnergy(int maxExtract, boolean simulate){
			return 0;
		}
		@Override public int getEnergyStored(){
			return toFE().convertToInt(getEnergyStorage().stored(ALL));
		}
		@Override public int getMaxEnergyStored(){
			return toFE().convertToInt(getEnergyStorage().capacity(ALL));
		}
		@Override public boolean canExtract(){
			return false;
		}
		@Override public boolean canReceive(){
			return true;
		}

		@Nullable @Override public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side){
			return capability==CapabilityEnergy.ENERGY&&(side==null||isValidSideForCapability(side)) ?
					CapabilityEnergy.ENERGY.cast(this) : null;
		}
	}

	private final class GteuOut extends ConverterEmitterTrait implements IEnergyContainer{
		public GteuOut(){
			super(CefMTE.this);
		}

		@Override public String getName(){
			return "GteuOut";
		}
		@Override public int getNetworkID(){
			return TraitNetworkIds.TRAIT_ID_ENERGY_CONTAINER;
		}

		@Override protected void operate(TileEntity tileEntity){
			IEnergyContainer energy = tileEntity.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, getFrontFacing().getOpposite());
			if(energy==null||!energy.inputsEnergy(getFrontFacing().getOpposite())) return;
			long voltage = voltage();
			long canEmit = getEnergyStorage().extract(Long.MAX_VALUE, false, false, true);

			if(canEmit>=voltage){
				long emitAmpere = Math.min(amperage()==9 ? 8 : amperage(), canEmit/voltage);
				if(emitAmpere>0){
					emitAmpere = energy.acceptEnergyFromNetwork(getFrontFacing().getOpposite(), voltage, emitAmpere);
					if(emitAmpere>0) getEnergyStorage().extract(voltage*emitAmpere, false, false, false);
				}
			}
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
			return 0;
		}
		@Override public long getEnergyStored(){
			return getEnergyStorage().stored(ALL);
		}
		@Override public long getEnergyCapacity(){
			return getEnergyStorage().capacity(ALL);
		}
		@Override public long getInputAmperage(){
			return amperage();
		}
		@Override public long getInputVoltage(){
			return voltage();
		}

		@Nullable @Override public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side){
			return capability==GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER&&(side==null||isValidSideForCapability(side)) ?
					GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER.cast(this) : null;
		}
	}
}