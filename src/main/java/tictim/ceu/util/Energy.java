package tictim.ceu.util;

import net.minecraft.client.resources.I18n;
import tictim.ceu.util.Ratio;

public interface Energy{
	String getRawName();

	default String getLocalizedName(){
		return I18n.format("ceu.energy."+getRawName());
	}

	Ratio defaultRatioToGtEu();
	long valueThreshold();
}
