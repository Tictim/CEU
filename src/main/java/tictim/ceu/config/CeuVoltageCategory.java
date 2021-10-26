package tictim.ceu.config;

import gregtech.api.GTValues;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

public final class CeuVoltageCategory{
	private final CeuSetting[] settings;

	public CeuVoltageCategory(Configuration cfg, String name, boolean defaultRatioReversed){
		this.settings = new CeuSetting[GTValues.V.length];
		for(int i = 0; i<GTValues.V.length; i++)
			this.settings[i] = new CeuSetting(cfg, name, i, defaultRatioReversed);
	}

	public CeuVoltageCategory(NBTTagCompound nbt, String name){
		this.settings = new CeuSetting[GTValues.V.length];
		for(int i = 0; i<GTValues.V.length; i++)
			this.settings[i] = new CeuSetting(nbt, name, i);
	}

	public CeuSetting getSetting(int tier){
		return settings[tier];
	}

	public NBTTagCompound serialize(){
		NBTTagCompound nbt = new NBTTagCompound();
		for(CeuSetting setting : settings)
			setting.serialize(nbt);
		return nbt;
	}
}
