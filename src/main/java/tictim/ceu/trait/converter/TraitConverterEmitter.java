package tictim.ceu.trait.converter;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import tictim.ceu.mte.MTEConverter;

public abstract class TraitConverterEmitter extends TraitConverterIO{
	public TraitConverterEmitter(MTEConverter converter){
		super(converter);
	}

	@Override public void update(){
		if(!converter.getWorld().isRemote&&!converter.isDisabled()){
			TileEntity te = converter.getWorld().getTileEntity(converter.getPos().offset(converter.getFrontFacing()));
			if(te!=null) operate(te);
		}
	}

	@Override protected boolean isValidSideForCapability(EnumFacing side){
		return side==converter.getFrontFacing();
	}

	protected abstract void operate(TileEntity tileEntity);
}
