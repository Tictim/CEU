package tictim.ceu.config;

import net.minecraftforge.common.config.Configuration;
import tictim.ceu.enums.CeuType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

public final class CeuVoltageCategory{
	private final CeuType type;
	private final CeuSetting[] settings;
	
	public CeuVoltageCategory(Configuration cfg, CeuType type){
		this.type = type;
		
		settings = new CeuSetting[type.getConverterType().getMaxTier()-type.getConverterType().getMinTier()+1];
		for(int i = 0; i<settings.length; i++){
			settings[i] = new CeuSetting(cfg, type, i+type.getConverterType().getMinTier());
		}
	}
	
	public CeuVoltageCategory(NBTTagCompound nbt, CeuType type){
		this.type = type;
		NBTTagCompound subnbt = nbt.getCompoundTag(type.name().toLowerCase());
		
		settings = new CeuSetting[type.getConverterType().getMaxTier()-type.getConverterType().getMinTier()+1];
		for(int i = 0; i<settings.length; i++){
			settings[i] = new CeuSetting(subnbt, type, i+type.getConverterType().getMinTier());
		}
	}
	
	public CeuSetting getSetting(int tier){
		return settings[tier-type.getConverterType().getMinTier()];
	}
	
	public void serialize(NBTTagCompound nbt){
		NBTTagCompound subnbt = new NBTTagCompound();
		for(CeuSetting setting: settings)
			setting.serialize(subnbt);
		if(!subnbt.hasNoTags()) nbt.setTag(type.name().toLowerCase(), subnbt);
	}
}
