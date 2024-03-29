package tictim.ceu.config;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import tictim.ceu.CeuMod;
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

	private final TierCategory ceuSettings;
	private final TierCategory cefSettings;
	private final boolean exactVoltage;
	private final boolean constrainBattery;
	private final boolean disableGregicalityCompat;

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
		this.disableGregicalityCompat = cfg.get(
						"general",
						"disableGregicalityCompat",
						false,
						"Disables Gregicality compat. Otherwise compat will be active when mod with id of 'gtadditions' is present.")
				.getBoolean();
		this.ceuSettings = new TierCategory(cfg, "ceu", true);
		this.cefSettings = new TierCategory(cfg, "cef", false);
	}

	public CeuConfig(NBTTagCompound nbt){
		this.exactVoltage = nbt.getBoolean("exactVoltage");
		this.constrainBattery = nbt.getBoolean("constrainBattery");
		this.disableGregicalityCompat = nbt.getBoolean("disableGregicalityCompat");
		this.ceuSettings = new TierCategory(nbt.getCompoundTag("ceu"));
		this.cefSettings = new TierCategory(nbt.getCompoundTag("cef"));
	}

	public boolean isCeuDisabled(int tier){
		return ceuSettings.getSetting(tier).disable;
	}
	public Ratio getCeuRatio(int tier){
		return ceuSettings.getSetting(tier).conversionRatio;
	}
	public boolean isCefDisabled(int tier){
		return cefSettings.getSetting(tier).disable;
	}
	public Ratio getCefRatio(int tier){
		return cefSettings.getSetting(tier).conversionRatio;
	}
	public boolean permitOnlyExactVoltage(){
		return this.exactVoltage;
	}
	public boolean constrainBattery(){
		return constrainBattery;
	}
	public boolean disableGregicalityCompat(){
		return disableGregicalityCompat;
	}

	public void serialize(NBTTagCompound nbt){
		nbt.setBoolean("exactVoltage", exactVoltage);
		nbt.setBoolean("constrainBattery", constrainBattery);
		nbt.setTag("ceu", ceuSettings.serialize());
		nbt.setTag("cef", cefSettings.serialize());
		NBTTagCompound subnbt = new NBTTagCompound();
		nbt.setTag("infiniteEnergySource", subnbt);
	}
}
