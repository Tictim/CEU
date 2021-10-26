package tictim.ceu.mte;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.metatileentity.MTETrait;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.util.GTUtility;
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
import tictim.ceu.mte.trait.ConverterEnergyStorage;
import tictim.ceu.util.Ratio;

import javax.annotation.Nullable;

import static tictim.ceu.enums.BatteryFilter.ALL;
import static tictim.ceu.enums.BatteryFilter.NONE;

public class CeuMTE extends ConverterMTE{
	public CeuMTE(ResourceLocation id, int tier, int slots){
		super(id, tier, slots);
	}

	@Override protected void reinitializeEnergyContainer(){
		super.reinitializeEnergyContainer();
		new GteuIn();
		new FeOut();
	}

	@Override public Ratio ratio(){
		return CeuConfig.config().getCeuRatio(getTier());
	}
	@Override public boolean isDisabled(){
		return CeuConfig.config().isCeuDisabled(getTier());
	}
	@Override public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder){
		return new CeuMTE(metaTileEntityId, getTier(), getSlots());
	}

	@Override public boolean convertsToFE(){
		return true;
	}

	@Override public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline){
		super.renderMetaTileEntity(renderState, translation, pipeline);
		CeuResources.CEU_FACE.renderSided(getFrontFacing(), renderState, translation, pipeline);
	}

	@Nullable @Override protected TextureArea getTargetEnergyBatteryOptionIcon(){
		return CeuResources.FE_IO_MODE_BUTTON;
	}

	private final class GteuIn extends ConverterTrait implements IEnergyContainer{
		public GteuIn(){
			super(CeuMTE.this);
		}

		@Override public String getName(){
			return "GteuIn";
		}
		@Override public int getNetworkID(){
			return MTETrait.TraitNetworkIds.TRAIT_ID_ENERGY_CONTAINER;
		}
		@Override public long acceptEnergyFromNetwork(EnumFacing side, long voltage, long amperage){
			if(voltage>0&&amperage>0&&(side==null||inputsEnergy(side))){
				if(voltage>getInputVoltage()){
					GTUtility.doOvervoltageExplosion(metaTileEntity, voltage);
					return Math.min(amperage, getInputAmperage());
				}
				ConverterEnergyStorage es = getEnergyStorage();
				long canAccept = es.capacity(NONE)-es.stored(NONE);
				if(canAccept>=voltage){
					long amperesAccepted = Math.min(canAccept/voltage, Math.min(amperage, getInputAmperage()));
					if(amperesAccepted>0){
						es.insert(voltage*amperesAccepted, true, false);
						return amperesAccepted;
					}
				}
			}
			return 0;
		}
		@Override public boolean inputsEnergy(EnumFacing side){
			return getFrontFacing()!=side;
		}
		@Override public long changeEnergy(long differenceAmount){
			if(differenceAmount==0) return 0;
			else if(differenceAmount>0){
				return getEnergyStorage()
						.insert(differenceAmount,
								true,
								ALL,
								false);
			}else{
				return -getEnergyStorage()
						.extract(differenceAmount==Long.MIN_VALUE ? Long.MAX_VALUE : -differenceAmount,
								true,
								true,
								ALL,
								false);
			}
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

	public class FeOut extends ConverterEmitterTrait implements IEnergyStorage{
		public FeOut(){
			super(CeuMTE.this);
		}

		@Override protected void operate(TileEntity tileEntity){
			IEnergyStorage storage = tileEntity.getCapability(CapabilityEnergy.ENERGY, getFrontFacing().getOpposite());
			if(storage==null||!storage.canReceive()) return;
			int extracted = extractEnergy(Integer.MAX_VALUE, true);
			if(extracted>0){
				extracted = storage.receiveEnergy(extracted, false);
				if(extracted>0){
					extractEnergy(extracted, false);
				}
			}
		}

		@Override public String getName(){
			return "FeOut";
		}
		@Override public int getNetworkID(){
			return 0;
		}
		@Override public int receiveEnergy(int maxReceive, boolean simulate){
			return 0;
		}
		@Override public int extractEnergy(int maxExtract, boolean simulate){
			return toFE().convertToInt(
					getEnergyStorage().extract(
							toGTEU().convertToLong(maxExtract), false, true, simulate));
		}
		@Override public int getEnergyStored(){
			return toFE().convertToInt(getEnergyStorage().stored(ALL));
		}
		@Override public int getMaxEnergyStored(){
			return toFE().convertToInt(getEnergyStorage().capacity(ALL));
		}
		@Override public boolean canExtract(){
			return true;
		}
		@Override public boolean canReceive(){
			return false;
		}

		@Nullable @Override public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side){
			return capability==CapabilityEnergy.ENERGY&&(side==null||isValidSideForCapability(side)) ?
					CapabilityEnergy.ENERGY.cast(this) : null;
		}
	}
}
