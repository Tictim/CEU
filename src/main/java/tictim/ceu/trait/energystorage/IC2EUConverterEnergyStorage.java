package tictim.ceu.trait.energystorage;

import tictim.ceu.mte.MTEConverter;
import tictim.ceu.energy.ElectricItemIC2EU;
import gregtech.api.capability.IElectricItem;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class IC2EUConverterEnergyStorage extends ConverterEnergyStorage{
	public IC2EUConverterEnergyStorage(MTEConverter converter){
		super(converter);
	}

	@Nullable
	@Override
	protected IElectricItem getWrappedBatteryContainer(ItemStack s){
		return ElectricItemIC2EU.fromItem(converter, s);
	}
}
