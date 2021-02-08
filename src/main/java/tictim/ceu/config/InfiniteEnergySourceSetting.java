package tictim.ceu.config;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import tictim.ceu.enums.CommonEnergy;

public class InfiniteEnergySourceSetting{
	private final boolean[] emitterDisabled = new boolean[CommonEnergy.values().length];
	private final boolean[] receiverDisabled = new boolean[CommonEnergy.values().length];

	public InfiniteEnergySourceSetting(Configuration cfg){
		for(CommonEnergy e : CommonEnergy.values()){
			emitterDisabled[e.ordinal()] = cfg.get("general.infinite_energy_emitter", getEmitterDisabledOptionName(e), e==CommonEnergy.IC2EU).getBoolean();
			receiverDisabled[e.ordinal()] = cfg.get("general.infinite_energy_receiver", getReceiverDisabledOptionName(e), e==CommonEnergy.IC2EU).getBoolean();
		}
	}

	public InfiniteEnergySourceSetting(NBTTagCompound nbt){
		for(CommonEnergy e : CommonEnergy.values()){
			emitterDisabled[e.ordinal()] = nbt.getBoolean(getEmitterDisabledOptionName(e));
			emitterDisabled[e.ordinal()] = nbt.getBoolean(getReceiverDisabledOptionName(e));
		}
	}

	public void serialize(NBTTagCompound nbt){
		for(CommonEnergy e : CommonEnergy.values()){
			if(emitterDisabled[e.ordinal()]) nbt.setBoolean(getEmitterDisabledOptionName(e), true);
			if(receiverDisabled[e.ordinal()]) nbt.setBoolean(getReceiverDisabledOptionName(e), true);
		}
	}

	private static String getEmitterDisabledOptionName(CommonEnergy energy){
		return "disable"+energy.name()+"Emitter";
	}
	private static String getReceiverDisabledOptionName(CommonEnergy energy){
		return "disable"+energy.name()+"Receiver";
	}
	public boolean isEnergyEmitterDisabled(CommonEnergy e){
		return emitterDisabled[e.ordinal()];
	}
	public boolean isEnergyReceiverDisabled(CommonEnergy e){
		return receiverDisabled[e.ordinal()];
	}
}
