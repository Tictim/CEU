package tictim.ceu.mte;

import gregtech.api.GTValues;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import tictim.ceu.trait.converter.TraitGTEUIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Base implementation for GTEU -> Foreign Energy converter
 */
public abstract class MTEFromGTEUConverter extends MTEConverter{
	public MTEFromGTEUConverter(ResourceLocation id, int tier, int slots){
		super(id, tier, slots);
	}

	@Override public boolean convertsToTargetEnergy(){
		return true;
	}

	@Override protected void reinitializeEnergyContainer(){
		super.reinitializeEnergyContainer();
		new TraitGTEUIn(this);
		createEnergyEmitterTrait();
	}

	protected abstract void createEnergyEmitterTrait();


	@Override protected void addConverterInformation(
			ItemStack stack,
			@Nullable World player,
			List<String> tooltip,
			boolean advanced){
		super.addConverterInformation(stack, player, tooltip, advanced);
		if(!isDisabled()){
			long io = toTargetEnergy().convert(energyIOLimit(),
					targetEnergy().valueThreshold());
			tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_in",
					GTValues.V[getTier()],
					GTValues.VN[getTier()]));
			tooltip.add(I18n.format("ceu.energy_out",
					targetEnergy().getLocalizedName(),
					io));
		}
	}
}
