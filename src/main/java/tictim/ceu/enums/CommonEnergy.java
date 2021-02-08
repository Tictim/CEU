package tictim.ceu.enums;

import tictim.ceu.util.Energy;
import tictim.ceu.util.Ratio;

public enum CommonEnergy implements Energy{
	FE("fe", Ratio.DEFAULT, Integer.MAX_VALUE),
	GTEU("gteu", Ratio.EQ, Long.MAX_VALUE),
	// TODO: IC2 uses doubles to represent their energy. But it doesn't mean it could handle all possible long values perfectly.
	//       Floating points varies in density of numbers can be expressed in proportion to the number's size.
	//       After some point, doubles will fail expressing some big long numbers. Setting aside all sort of decimal point garbages, of course.
	IC2EU("ic2eu", Ratio.EQ, Long.MAX_VALUE);

	private final String name;
	private final Ratio defaultRatioToGtEu;
	private final long threshold;

	CommonEnergy(String name, Ratio defaultRatioFromGtEu, long threshold){
		this.name = name;
		this.defaultRatioToGtEu = defaultRatioFromGtEu;
		this.threshold = threshold;
	}

	@Override public String getRawName(){
		return name;
	}
	@Override public Ratio defaultRatioToGtEu(){
		return defaultRatioToGtEu;
	}
	@Override public long valueThreshold(){
		return threshold;
	}
}
