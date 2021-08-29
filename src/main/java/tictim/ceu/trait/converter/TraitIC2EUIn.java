package tictim.ceu.trait.converter;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import tictim.ceu.mte.MTEConverter;
import tictim.ceu.trait.energystorage.ConverterEnergyStorage;
import tictim.ceu.enums.BatteryFilter;

import javax.annotation.Nullable;

// TODO Left unfinished until we have solution for removal of TileEntity
public class TraitIC2EUIn extends TraitConverterIO{
	public TraitIC2EUIn(MTEConverter converter){
		super(converter);
	}

	@Override public String getName(){
		return "TraitIc2EuIn";
	}
	@Override public int getNetworkID(){
		return 0;
	}

	private SinkWrapper sinkWrapper;

	@Override public void update(){
		if(!this.converter.getWorld().isRemote&&sinkWrapper==null){
			sinkWrapper = new SinkWrapper();
			if(Loader.isModLoaded("ic2")) sinkWrapper.addToENet();
		}
	}

	@Nullable @Override public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side){
		return null;
	}

	@Optional.InterfaceList({
			@Optional.Interface(modid = "ic2", iface = "ic2.api.energy.tile.IEnergySink"),
			@Optional.Interface(modid = "ic2", iface = "ic2.api.energy.tile.IEnergyEmitter"),
			@Optional.Interface(modid = "ic2", iface = "ic2.api.energy.tile.IEnergyTile")
	})
	private class SinkWrapper extends TileEntity implements IEnergySink{
		private SinkWrapper(){
			this.setWorld(converter.getWorld());
			this.setPos(converter.getPos());
		}

		@Optional.Method(modid = "ic2")
		public void addToENet(){
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
		}

		@Optional.Method(modid = "ic2")
		public void removeFromENet(){
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
		}

		@Override
		@Optional.Method(modid = "ic2")
		public double getDemandedEnergy(){
			ConverterEnergyStorage e = converter.getEnergyStorage();
			long a = Math.min(converter.energyIOLimit(), e.capacity(BatteryFilter.ALL)-e.stored(BatteryFilter.ALL));
			return a<=0 ? 0 : converter.toTargetEnergy().convert(a, Long.MAX_VALUE);
		}

		@Override
		@Optional.Method(modid = "ic2")
		public int getSinkTier(){
			return converter.getTier();
		}

		@Override
		@Optional.Method(modid = "ic2")
		public double injectEnergy(EnumFacing directionFrom, double amount, double voltage){
			return amount*voltage;
		}

		@Override
		@Optional.Method(modid = "ic2")
		public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side){
			return converter.getFrontFacing()!=side;
		}
	}
}
