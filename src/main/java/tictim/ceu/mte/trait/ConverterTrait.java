package tictim.ceu.mte.trait;

import gregtech.api.metatileentity.MTETrait;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import tictim.ceu.mte.ConverterMTE;

import javax.annotation.Nullable;

public abstract class ConverterTrait extends MTETrait{
	public ConverterTrait(ConverterMTE converter){
		super(converter);
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability){
		return getCapability(capability, null);
	}

	@Nullable public abstract  <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side);

	protected boolean isValidSideForCapability(EnumFacing side){
		return side!=metaTileEntity.getFrontFacing();
	}
}
