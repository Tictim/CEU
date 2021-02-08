package tictim.ceu.mte;

import gregtech.api.GTValues;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import tictim.ceu.trait.converter.TraitGTEUOut;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Base implementation for Foreign Energy -> GTEU converter
 */
public abstract class MTEToGTEUConverter extends MTEConverter{
	public MTEToGTEUConverter(ResourceLocation id, int tier, int slots){
		super(id, tier, slots);
	}

	@Override public boolean convertsToTargetEnergy(){
		return false;
	}

	@Override protected void reinitializeEnergyContainer(){
		super.reinitializeEnergyContainer();
		new TraitGTEUOut(this);
		createEnergyReceiverTrait();
	}

	protected abstract void createEnergyReceiverTrait();

	@Override protected void addConverterInformation(
			ItemStack stack,
			@Nullable World player,
			List<String> tooltip,
			boolean advanced){
		super.addConverterInformation(stack, player, tooltip, advanced);
		if(!isDisabled()){
			long io = toTargetEnergy().convert(energyIOLimit(),
					targetEnergy().valueThreshold());
			tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_out",
					GTValues.V[getTier()],
					GTValues.VN[getTier()]));
			tooltip.add(I18n.format("ceu.energy_in",
					targetEnergy().getLocalizedName(),
					io));
		}
	}
}
