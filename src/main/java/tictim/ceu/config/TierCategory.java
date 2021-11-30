package tictim.ceu.config;

import gregicadditions.GAValues;
import gregtech.api.GTValues;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import tictim.ceu.CeuContents;

public final class TierCategory{
	private final ConverterSetting[] settings;

	public TierCategory(Configuration cfg, String converterName, boolean defaultRatioReversed){
		String[] tierNames = getTierNames();
		this.settings = new ConverterSetting[tierNames.length];
		for(int i = 0; i<tierNames.length; i++)
			this.settings[i] = new ConverterSetting(cfg, converterName, tierNames[i], defaultRatioReversed);
	}

	public TierCategory(NBTTagCompound nbt){
		String[] tierNames = getTierNames();
		this.settings = new ConverterSetting[tierNames.length];
		for(int i = 0; i<tierNames.length; i++)
			this.settings[i] = new ConverterSetting(nbt, tierNames[i]);
	}

	public ConverterSetting getSetting(int tier){
		return settings[tier];
	}

	public NBTTagCompound serialize(){
		NBTTagCompound nbt = new NBTTagCompound();
		for(ConverterSetting setting : settings)
			setting.serialize(nbt);
		return nbt;
	}

	private static String[] getTierNames(){
		return CeuContents.isGregicalityPresent() ?
				GregicalityCompat.getTierNames() :
				GTValues.VN;
	}

	private static final class GregicalityCompat{
		private static String[] getTierNames(){
			return GAValues.VN;
		}
	}
}
