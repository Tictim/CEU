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

	@Nullable protected abstract Capability<?> getImplementingCapability();

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability){
		return getCapability(capability, null);
	}

	@Nullable @SuppressWarnings("unchecked") public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side){
		Capability<?> implCap = getImplementingCapability();
		if(implCap!=capability) return null;
		return side==null||isValidSideForCapability(side) ? (T)this : null;
	}

	protected boolean isValidSideForCapability(EnumFacing side){
		return side!=converter.getFrontFacing();
	}
}
