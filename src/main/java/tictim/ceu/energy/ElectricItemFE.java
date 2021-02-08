package tictim.ceu.energy;

import gregtech.api.GTValues;
import gregtech.api.capability.IElectricItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import tictim.ceu.mte.MTEConverter;

import java.util.function.BiConsumer;

public class ElectricItemFE implements IElectricItem{
	private final MTEConverter converter;
	private final IEnergyStorage storage;

	public ElectricItemFE(MTEConverter converter, IEnergyStorage storage){
		this.converter = converter;
		this.storage = storage;
	}

	@Override public boolean canProvideChargeExternally(){
		return true;
	}
	@Override public void addChargeListener(BiConsumer<ItemStack, Long> chargeListener){}
	@Override public long charge(long amount, int tier, boolean ignoreTransferLimit, boolean simulate){
		if(!ignoreTransferLimit) amount = Math.min(amount, getTransferLimit());
		int received = storage.receiveEnergy(converter.toTargetEnergy().convertToInt(amount), simulate);
		return converter.toGTEU().convertToLong(received);
	}
	@Override public long discharge(long amount, int tier, boolean ignoreTransferLimit, boolean externally, boolean simulate){
		if(!ignoreTransferLimit) amount = Math.min(amount, getTransferLimit());
		int received = storage.extractEnergy(converter.toTargetEnergy().convertToInt(amount), simulate);
		return converter.toGTEU().convertToLong(received);
	}
	@Override public long getCharge(){
		return converter.toGTEU().convertToLong(storage.getEnergyStored());
	}
	@Override public long getMaxCharge(){
		return converter.toGTEU().convertToLong(storage.getMaxEnergyStored());
	}
	@Override public boolean canUse(long amount){
		return storage.getEnergyStored()>=converter.toTargetEnergy().convertToInt(amount);
	}
	@Override public int getTier(){
		return converter.getTier();
	}
	@Override public long getTransferLimit(){
		return GTValues.V[getTier()];
	}
}
