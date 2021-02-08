package tictim.ceu.enums;

import codechicken.lib.math.MathHelper;

public enum BatteryChargeStrategy{
	MOST_EMPTY_FIRST,
	FIRST_SLOT_FIRST,
	DISTRIBUTE;

	public static BatteryChargeStrategy of(int meta){
		BatteryChargeStrategy[] values = BatteryChargeStrategy.values();
		return values[MathHelper.clip(meta, 0, values.length-1)];
	}
}
