package tictim.ceu.mte.trait;

import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import tictim.ceu.mte.Converter;

public abstract class ConverterEmitterTrait extends ConverterTrait{
	public <MTE extends MetaTileEntity & Converter> ConverterEmitterTrait(MTE mte){
		super(mte);
	}

	@Override public void update(){
		if(!metaTileEntity.getWorld().isRemote){
			TileEntity te = metaTileEntity.getWorld().getTileEntity(metaTileEntity.getPos().offset(metaTileEntity.getFrontFacing()));
			if(te!=null) operate(te);
		}
	}

	@Override protected boolean isValidSideForCapability(EnumFacing side){
		return side==metaTileEntity.getFrontFacing();
	}

	protected abstract void operate(TileEntity tileEntity);
}
