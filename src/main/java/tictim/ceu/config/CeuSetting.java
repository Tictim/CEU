package tictim.ceu.config;

import tictim.ceu.enums.CeuType;
import tictim.ceu.enums.ConverterType;
import tictim.ceu.util.Ratio;
import gregtech.api.GTValues;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public final class CeuSetting{
	private final int tier;
	private final String category;

	public final Ratio conversionRatio;
	public final boolean disable;

	public CeuSetting(Configuration cfg, CeuType type, int tier){
		this.tier = tier;
		this.category = "general."+type.toString().toLowerCase()+"."+GTValues.VN[tier];

		this.conversionRatio = readOrDitchRatio(
				cfg.get(
						category,
						"conversionRatio",
						type.getDefaultConversionRate().toString(),
						"Conversion ratio between two energy units (INPUT:OUTPUT)"),
				type.getDefaultConversionRate());
		disable = cfg.get(category, "disable", type.getConverterType()==ConverterType.ICEU_ICEF, "").getBoolean();
	}

	public CeuSetting(NBTTagCompound nbt, CeuType type, int tier){
		this.tier = tier;
		this.category = "general."+type.toString().toLowerCase()+"."+GTValues.VN[tier];

		NBTTagCompound subnbt = nbt.getCompoundTag(GTValues.VN[tier]);

		conversionRatio = Ratio.deserialize(subnbt);
		disable = subnbt.getBoolean("disable");
	}

	public void serialize(NBTTagCompound nbt){
		NBTTagCompound subnbt = new NBTTagCompound();
		subnbt.setBoolean("disable", disable);
		conversionRatio.serialize(subnbt);
		nbt.setTag(GTValues.VN[tier], subnbt);
	}

	/**
	 * Parse config string to Ratio. Might override it with default value if i ever felt cute,idk
	 */
	public static Ratio readOrDitchRatio(Property config, Ratio defaultValue){
		Ratio ratio = Ratio.tryParse(config.getString());
		if(ratio!=null){
			config.set(ratio.toString());
			return ratio;
		}else{
			config.set(defaultValue.toString());
			return defaultValue;
		}
	}
}
