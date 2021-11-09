package tictim.ceu.config;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import tictim.ceu.util.Ratio;

public final class ConverterSetting{
	private static Ratio getDefaultConversionRate(boolean reversed){
		return reversed ? Ratio.FOUR_TO_ONE.reverse() : Ratio.FOUR_TO_ONE;
	}

	private final String tier;

	public final Ratio conversionRatio;
	public final boolean disable;

	public ConverterSetting(Configuration cfg, String converterName, String tier, boolean defaultRatioReversed){
		this.tier = tier;
		String category = "general."+converterName+"."+tier;

		Ratio defaultConversionRate = getDefaultConversionRate(defaultRatioReversed);
		Property config = cfg.get(
				category,
				"conversionRatio",
				defaultConversionRate.toString(),
				"Conversion ratio between two energy units (INPUT:OUTPUT)");
		Ratio ratio = Ratio.tryParse(config.getString());
		if(ratio!=null){
			config.set(ratio.toString());
			this.conversionRatio = ratio;
		}else{
			config.set(defaultConversionRate.toString());
			this.conversionRatio = defaultConversionRate;
		}
		this.disable = cfg.get(category, "disable", false, "").getBoolean();
	}

	public ConverterSetting(NBTTagCompound nbt, String tier){
		this.tier = tier;

		NBTTagCompound subnbt = nbt.getCompoundTag(tier);

		this.conversionRatio = Ratio.deserialize(subnbt);
		this.disable = subnbt.getBoolean("disable");
	}

	public void serialize(NBTTagCompound nbt){
		NBTTagCompound subnbt = new NBTTagCompound();
		subnbt.setBoolean("disable", disable);
		conversionRatio.serialize(subnbt);
		nbt.setTag(tier, subnbt);
	}
}
