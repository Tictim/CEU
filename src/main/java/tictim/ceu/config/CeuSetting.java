package tictim.ceu.config;

import gregtech.api.GTValues;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import tictim.ceu.util.Ratio;

public final class CeuSetting{
	private static Ratio getDefaultConversionRate(boolean reversed){
		return reversed ? Ratio.FOUR_TO_ONE.reverse() : Ratio.FOUR_TO_ONE;
	}

	private final int tier;
	private final String category;

	public final Ratio conversionRatio;
	public final boolean disable;

	public CeuSetting(Configuration cfg, String name, int tier, boolean defaultRatioReversed){
		this.tier = tier;
		this.category = "general."+name+"."+GTValues.VN[tier];

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

	public CeuSetting(NBTTagCompound nbt, String name, int tier){
		this.tier = tier;
		this.category = "general."+name+"."+GTValues.VN[tier];

		NBTTagCompound subnbt = nbt.getCompoundTag(GTValues.VN[tier]);

		this.conversionRatio = Ratio.deserialize(subnbt);
		this.disable = subnbt.getBoolean("disable");
	}

	public void serialize(NBTTagCompound nbt){
		NBTTagCompound subnbt = new NBTTagCompound();
		subnbt.setBoolean("disable", disable);
		conversionRatio.serialize(subnbt);
		nbt.setTag(GTValues.VN[tier], subnbt);
	}
}
