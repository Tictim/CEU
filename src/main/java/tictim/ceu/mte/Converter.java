package tictim.ceu.mte;

import tictim.ceu.config.CeuConfig;
import tictim.ceu.mte.trait.ConverterEnergyStorage;
import tictim.ceu.util.CeuModes;
import tictim.ceu.util.Ratio;

public interface Converter{
	ConverterEnergyStorage getEnergyStorage();
	int getTier();
	/**
	 * @return Width of the slot grid, not actual item slot size :p
	 */
	int getSlots();

	long voltage();
	default long amperage(){
		return (long)getSlots()*getSlots();
	}

	boolean convertsToFE();
	CeuModes getModes();

	default Ratio ratio(){
		return convertsToFE() ? CeuConfig.config().getCeuRatio(getTier()) : CeuConfig.config().getCefRatio(getTier());
	}
	default boolean isDisabled(){
		return convertsToFE() ? CeuConfig.config().isCeuDisabled(getTier()) : CeuConfig.config().isCefDisabled(getTier());
	}

	default Ratio toFE(){
		return convertsToFE() ? ratio() : ratio().reverse();
	}
	default Ratio toGTEU(){
		return convertsToFE() ? ratio().reverse() : ratio();
	}

	default long energyIOLimit(){
		return voltage()*amperage();
	}
}
