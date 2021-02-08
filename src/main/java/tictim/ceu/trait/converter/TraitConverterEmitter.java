package tictim.ceu.trait.converter;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import tictim.ceu.mte.MTEConverter;
import tictim.ceu.trait.converter.TraitConverterIO;

import javax.annotation.Nonnull;

public abstract class TraitConverterEmitter<C> extends TraitConverterIO{
	public TraitConverterEmitter(MTEConverter converter){
		super(converter);
	}

	@Override public void update(){
		if(!converter.getWorld().isRemote&&!converter.isDisabled()){
			TileEntity te = converter.getWorld().getTileEntity(converter.getPos().offset(converter.getFrontFacing()));
			if(te!=null){
				C capability = te.getCapability(getImplementingCapability(), converter.getFrontFacing().getOpposite());
				if(capability!=null) operate(capability);
			}
		}
	}

	@Override protected boolean isValidSideForCapability(EnumFacing side){
		return side==converter.getFrontFacing();
	}

	protected abstract void operate(C capability);
	@Nonnull protected abstract Capability<C> getImplementingCapability();
}
