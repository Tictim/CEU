package tictim.ceu.enums;

import tictim.ceu.config.CeuConfig;
import tictim.ceu.util.Ratio;

public enum CeuType{
	CEU(ConverterType.CEU_CEF, true),
	CEF(ConverterType.CEU_CEF, false),
	ICEU(ConverterType.ICEU_ICEF, true),
	ICEF(ConverterType.ICEU_ICEF, false);

	private final ConverterType type;
	private final boolean convertsToTargetEnergy;

	CeuType(ConverterType type, boolean convertsToTargetEnergy){
		this.type = type;
		this.convertsToTargetEnergy = convertsToTargetEnergy;
	}

	public ConverterType getConverterType(){
		return type;
	}
	public boolean convertsToTargetEnergy(){
		return convertsToTargetEnergy;
	}
	public Ratio getDefaultConversionRate(){
		if(convertsToTargetEnergy) return type.getTargetEnergy().defaultRatioToGtEu().reverse();
		else return type.getTargetEnergy().defaultRatioToGtEu();
	}
	public Ratio ratio(int tier){
		return CeuConfig.config().getRatio(this, tier);
	}
	public boolean isDisabled(int tier){
		return CeuConfig.config().isDisabled(this, tier);
	}

	@Override public String toString(){
		return name().toLowerCase();
	}
}
