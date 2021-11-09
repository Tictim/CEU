package tictim.ceu.mte.trait;

import gregtech.api.metatileentity.MTETrait;
import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import tictim.ceu.mte.Converter;

import javax.annotation.Nullable;

public abstract class ConverterTrait extends MTETrait{
	protected final Converter converter;

	public <MTE extends MetaTileEntity & Converter> ConverterTrait(MTE mte){
		super(mte);
		this.converter = mte;
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability){
		return getCapability(capability, null);
	}

	@Nullable public abstract <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side);

	protected boolean isValidSideForCapability(EnumFacing side){
		return side!=metaTileEntity.getFrontFacing();
	}
}
