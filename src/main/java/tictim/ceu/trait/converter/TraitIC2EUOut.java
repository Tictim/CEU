package tictim.ceu.trait.converter;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import tictim.ceu.mte.MTEFromGTEUConverter;

import javax.annotation.Nullable;

public class TraitIC2EUOut extends TraitConverterIO{
	private final MTEFromGTEUConverter ceu;

	public TraitIC2EUOut(MTEFromGTEUConverter ceu){
		super(ceu);
		this.ceu = ceu;
	}

	@Override public String getName(){
		return "TraitIc2EuOut";
	}
	@Override public int getNetworkID(){
		return 0;
	}

	private SourceWrapper sinkWrapper;

	@Override public void update(){
		if(!ceu.getWorld().isRemote&&sinkWrapper==null){
			sinkWrapper = new SourceWrapper();
			if(Loader.isModLoaded("ic2")) sinkWrapper.addToENet();
		}
	}

	@Nullable @Override public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side){
		return null;
	}

	@Optional.InterfaceList({
			@Optional.Interface(modid = "ic2", iface = "ic2.api.energy.tile.IEnergySource"),
			@Optional.Interface(modid = "ic2", iface = "ic2.api.energy.tile.IEnergyEmitter"),
			@Optional.Interface(modid = "ic2", iface = "ic2.api.energy.tile.IEnergyTile")
	})
	private class SourceWrapper extends TileEntity implements IEnergySource{
		private SourceWrapper(){
			this.setWorld(ceu.getWorld());
			this.setPos(ceu.getPos());
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
		public double getOfferedEnergy(){
			return (double)ceu.toTargetEnergy().convert(ceu.getEnergyStorage().extract(Long.MAX_VALUE, false, true, true), Long.MAX_VALUE);
		}
		@Override
		@Optional.Method(modid = "ic2")
		public void drawEnergy(double amount){
			ceu.getEnergyStorage().extract(ceu.toGTEU().convert(amount>=Long.MAX_VALUE ? Long.MAX_VALUE : (long)amount, Long.MAX_VALUE), true, true, false);
		}
		@Override
		@Optional.Method(modid = "ic2")
		public int getSourceTier(){
			return ceu.getTier();
		}
		@Override
		@Optional.Method(modid = "ic2")
		public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side){
			return ceu.getFrontFacing()==side;
		}
	}
}
