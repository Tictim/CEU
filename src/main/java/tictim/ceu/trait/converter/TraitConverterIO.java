package tictim.ceu.trait.converter;

import gregtech.api.metatileentity.MTETrait;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import tictim.ceu.mte.MTEConverter;

import javax.annotation.Nullable;

public abstract class TraitConverterIO extends MTETrait{
	protected final MTEConverter converter;

	public TraitConverterIO(MTEConverter converter){
		super(converter);
		this.converter = converter;
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability){
		return getCapability(capability, null);
	}

	@Nullable public abstract  <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side);

	protected boolean isValidSideForCapability(EnumFacing side){
		return side!=converter.getFrontFacing();
	}
}
