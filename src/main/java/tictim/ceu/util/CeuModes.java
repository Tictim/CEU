package tictim.ceu.util;

import net.minecraft.nbt.NBTTagCompound;
import tictim.ceu.enums.BatteryChargeStrategy;
import tictim.ceu.enums.BatteryFilter;
import tictim.ceu.enums.BatteryIOOption;

import java.util.Objects;

public class CeuModes{
	private static final String KEY_GTEU_BATTERY_OPTION = "GTEUBatteryOption";
	private static final String KEY_TARGET_ENERGY_BATTERY_OPTION = "TargetEnergyBatteryOption";
	private static final String KEY_BATTERY_CHARGE_STRATEGY = "BatteryChargeStrategy";

	private BatteryIOOption gteuBatteryOption;
	private BatteryIOOption targetEnergyBatteryOption;
	private BatteryChargeStrategy chargeStrategy;

	public CeuModes(){
		this(BatteryIOOption.CHARGE_AND_DISCHARGE, BatteryIOOption.CHARGE_AND_DISCHARGE, BatteryChargeStrategy.MOST_EMPTY_FIRST);
	}
	public CeuModes(BatteryIOOption gteuBatteryOption, BatteryIOOption targetEnergyBatteryOption, BatteryChargeStrategy chargeStrategy){
		this.gteuBatteryOption = Objects.requireNonNull(gteuBatteryOption);
		this.targetEnergyBatteryOption = Objects.requireNonNull(targetEnergyBatteryOption);
		this.chargeStrategy = Objects.requireNonNull(chargeStrategy);
	}

	public BatteryIOOption getGteuBatteryOption(){
		return gteuBatteryOption;
	}
	public void setGteuBatteryOption(BatteryIOOption gteuBatteryOption){
		this.gteuBatteryOption = Objects.requireNonNull(gteuBatteryOption);
	}
	public BatteryIOOption getTargetEnergyBatteryOption(){
		return targetEnergyBatteryOption;
	}
	public void setTargetEnergyBatteryOption(BatteryIOOption targetEnergyBatteryOption){
		this.targetEnergyBatteryOption = Objects.requireNonNull(targetEnergyBatteryOption);
	}
	public BatteryChargeStrategy getChargeStrategy(){
		return chargeStrategy;
	}
	public void setChargeStrategy(BatteryChargeStrategy chargeStrategy){
		this.chargeStrategy = Objects.requireNonNull(chargeStrategy);
	}

	public BatteryFilter getChargeFilter(){
		return BatteryFilter.of(
				gteuBatteryOption.canCharge(),
				targetEnergyBatteryOption.canCharge());
	}
	public BatteryFilter getDischargeFilter(){
		return BatteryFilter.of(
				gteuBatteryOption.canDischarge(),
				targetEnergyBatteryOption.canDischarge());
	}
	public BatteryFilter getOnlyChargeFilter(){
		return BatteryFilter.of(
				gteuBatteryOption.canOnlyCharge(),
				targetEnergyBatteryOption.canOnlyCharge());
	}
	public BatteryFilter getOnlyDischargeFilter(){
		return BatteryFilter.of(
				gteuBatteryOption.canOnlyDischarge(),
				targetEnergyBatteryOption.canOnlyDischarge());
	}

	public void read(NBTTagCompound nbt){
		this.gteuBatteryOption = BatteryIOOption.of(nbt.getInteger(KEY_GTEU_BATTERY_OPTION));
		this.targetEnergyBatteryOption = BatteryIOOption.of(nbt.getInteger(KEY_TARGET_ENERGY_BATTERY_OPTION));
		this.chargeStrategy = BatteryChargeStrategy.of(nbt.getInteger(KEY_BATTERY_CHARGE_STRATEGY));
	}

	public void write(NBTTagCompound nbt){
		nbt.setInteger(KEY_GTEU_BATTERY_OPTION, this.gteuBatteryOption.ordinal());
		nbt.setInteger(KEY_TARGET_ENERGY_BATTERY_OPTION, this.targetEnergyBatteryOption.ordinal());
		nbt.setInteger(KEY_BATTERY_CHARGE_STRATEGY, this.chargeStrategy.ordinal());
	}

	@Override public String toString(){
		return "CeuModes{"+
				"gteuBatteryOption="+gteuBatteryOption+
				", targetEnergyBatteryOption="+targetEnergyBatteryOption+
				", chargeStrategy="+chargeStrategy+
				'}';
	}
}
