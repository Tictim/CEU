package tictim.ceu.config;

import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import tictim.ceu.CeuMod;
import tictim.ceu.enums.CeuType;
import tictim.ceu.enums.CommonEnergy;
import tictim.ceu.util.Ratio;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = CeuMod.MODID)
public final class CeuConfig{
	private static CeuConfig localConfig;
	@Nullable private static CeuConfig syncedConfig;

	public static CeuConfig config(){
		return syncedConfig==null ? localConfig : syncedConfig;
	}
	public static CeuConfig localConfig(){
		return localConfig;
	}

	public static void setSyncedConfig(@Nullable CeuConfig config){
		syncedConfig = config;
	}

	public static void createConfigInstance(Configuration cfg){
		localConfig = new CeuConfig(cfg);
		cfg.save();
	}

	private final ImmutableMap<CeuType, CeuVoltageCategory> ceuSettings;
	private final InfiniteEnergySourceSetting infiniteEnergySource;
	private final boolean exactVoltage;
	private final boolean constrainBattery;

	public CeuConfig(Configuration cfg){
		this.exactVoltage = cfg.get(
				"general",
				"exactVoltage",
				false,
				"True if you want CEU & CEF to accept batteries with same voltage as CEU/CEF's. False if you want CEU & CEF to accept any tier of batteries.")
				.getBoolean();
		this.constrainBattery = cfg.get(
				"general",
				"constrainBattery",
				false,
				"Constrain input/output of batteries to input/output capability of energy converters.")
				.getBoolean();
		ImmutableMap.Builder<CeuType, CeuVoltageCategory> b = new ImmutableMap.Builder<>();
		for(CeuType type : CeuType.values()) b.put(type, new CeuVoltageCategory(cfg, type));
		this.ceuSettings = b.build();
		this.infiniteEnergySource = new InfiniteEnergySourceSetting(cfg);
	}

	public CeuConfig(NBTTagCompound nbt){
		this.exactVoltage = nbt.getBoolean("exactVoltage");
		this.constrainBattery = nbt.getBoolean("constrainBattery");
		ImmutableMap.Builder<CeuType, CeuVoltageCategory> b = new ImmutableMap.Builder<>();
		for(CeuType type : CeuType.values()) b.put(type, new CeuVoltageCategory(nbt, type));
		this.ceuSettings = b.build();
		this.infiniteEnergySource = new InfiniteEnergySourceSetting(nbt.getCompoundTag("infiniteEnergySource"));
	}

	public boolean isDisabled(CeuType type, int tier){
		return ceuSettings.get(type).getSetting(tier).disable;
	}
	public Ratio getRatio(CeuType type, int tier){
		return ceuSettings.get(type).getSetting(tier).conversionRatio;
	}
	public boolean isEnergySourceDisabled(CommonEnergy e, boolean isEmitter){
		return isEmitter ? isEnergyEmitterDisabled(e) : isEnergyReceiverDisabled(e);
	}
	public boolean isEnergyEmitterDisabled(CommonEnergy e){
		return this.infiniteEnergySource.isEnergyEmitterDisabled(e);
	}
	public boolean isEnergyReceiverDisabled(CommonEnergy e){
		return this.infiniteEnergySource.isEnergyReceiverDisabled(e);
	}

	public boolean permitOnlyExactVoltage(){
		return this.exactVoltage;
	}
	public boolean constrainBattery(){
		return constrainBattery;
	}

	public void serialize(NBTTagCompound nbt){
		nbt.setBoolean("exactVoltage", exactVoltage);
		nbt.setBoolean("constrainBattery", constrainBattery);
		for(CeuVoltageCategory category : this.ceuSettings.values())
			category.serialize(nbt);
		NBTTagCompound subnbt = new NBTTagCompound();
		infiniteEnergySource.serialize(subnbt);
		nbt.setTag("infiniteEnergySource", subnbt);
	}
}
