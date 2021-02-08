package tictim.ceu.enums;

public enum BatteryFilter{
	NONE,
	ONLY_GTEU,
	ONLY_WRAPPED,
	ALL;

	public boolean chargeGTEU(){
		return this==ONLY_GTEU||this==ALL;
	}
	public boolean chargeWrapped(){
		return this==ONLY_WRAPPED||this==ALL;
	}

	public static BatteryFilter of(boolean includeGteu, boolean includeWrapped){
		if(includeGteu){
			if(includeWrapped) return ALL;
			else return ONLY_GTEU;
		}else{
			if(includeWrapped) return ONLY_WRAPPED;
			else return NONE;
		}
	}
}
