package tictim.ceu.enums;

import codechicken.lib.math.MathHelper;

public enum BatteryIOOption{
	CHARGE_AND_DISCHARGE,
	CHARGE,
	DISCHARGE;

	public boolean canCharge(){
		return this!=DISCHARGE;
	}
	public boolean canDischarge(){
		return this!=CHARGE;
	}
	public boolean canOnlyCharge(){
		return canCharge()&&!canDischarge();
	}
	public boolean canOnlyDischarge(){
		return canDischarge()&&!canCharge();
	}

	public static BatteryIOOption of(int meta){
		BatteryIOOption[] values = BatteryIOOption.values();
		return values[MathHelper.clip(meta, 0, values.length-1)];
	}
}
