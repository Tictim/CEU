package tictim.ceu.enums;

import gregtech.api.GTValues;
import tictim.ceu.util.Energy;

import java.util.Objects;

public enum ConverterType{
	CEU_CEF(GTValues.ULV, GTValues.MAX, CommonEnergy.FE),
	ICEU_ICEF(GTValues.LV, GTValues.IV, CommonEnergy.IC2EU);

	private final int minTier, maxTier;
	private final Energy targetEnergy;

	ConverterType(int minTier, int maxTier, Energy targetEnergy){
		if(Objects.requireNonNull(targetEnergy)==CommonEnergy.GTEU)
			throw new IllegalArgumentException("An energy converter that converts GTEU to GTEU? What names would they have? GEU and GEF?");
		this.minTier = minTier;
		this.maxTier = maxTier;
		this.targetEnergy = targetEnergy;
	}

	public int getMinTier(){
		return minTier;
	}
	public int getMaxTier(){
		return maxTier;
	}
	public Energy getTargetEnergy(){
		return targetEnergy;
	}
}
