package tictim.ceu.energy;

import gregtech.api.GTValues;
import gregtech.api.capability.IElectricItem;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import tictim.ceu.mte.MTEConverter;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

// TODO this might not work...No it doesn't work. I travelled through time to meet future me and he confirmed it.
public class ElectricItemIC2EU implements IElectricItem{
	private static Boolean isIc2Available;

	@Nullable public static ElectricItemIC2EU fromItem(MTEConverter converter, ItemStack stack){
		if(isIc2Available==null) isIc2Available = Loader.isModLoaded("ic2");
		return isIc2Available ? new ElectricItemIC2EU(converter, stack) : null;
	}

	private final MTEConverter converter;
	private final IElectricItemManager manager;
	private final ItemStack stack;

	private ElectricItemIC2EU(MTEConverter converter, ItemStack stack){
		this(converter, ElectricItem.manager, stack);
	}
	private ElectricItemIC2EU(MTEConverter converter, IElectricItemManager manager, ItemStack stack){
		this.converter = converter;
		this.manager = manager;
		this.stack = stack;
	}

	@Override public boolean canProvideChargeExternally(){
		return true;
	}
	@Override public void addChargeListener(BiConsumer<ItemStack, Long> chargeListener){}

	@Optional.Method(modid = "ic2")
	@Override
	public long charge(long amount, int tier, boolean ignoreTransferLimit, boolean simulate){
		double charged = manager.charge(
				stack,
				converter.toTargetEnergy().convert(amount, Long.MAX_VALUE),
				tier,
				ignoreTransferLimit,
				simulate);
		return converter.toGTEU().convertToLong((long)charged);
	}

	@Optional.Method(modid = "ic2")
	@Override
	public long discharge(long amount, int tier, boolean ignoreTransferLimit, boolean externally, boolean simulate){
		double discharged = manager.discharge(
				stack,
				converter.toTargetEnergy().convert(amount, Long.MAX_VALUE),
				tier,
				ignoreTransferLimit,
				externally,
				simulate);
		return converter.toGTEU().convertToLong((long)discharged);
	}

	@Optional.Method(modid = "ic2")
	@Override
	public long getCharge(){
		double charge = manager.getCharge(stack);
		return converter.toGTEU().convertToLong((long)charge);
	}

	@Optional.Method(modid = "ic2")
	@Override
	public long getMaxCharge(){
		return converter.toGTEU().convertToLong((long)manager.getMaxCharge(stack));
	}

	@Optional.Method(modid = "ic2")
	@Override
	public boolean canUse(long amount){
		return manager.canUse(stack, converter.toTargetEnergy().convert(amount, Long.MAX_VALUE));
	}

	@Optional.Method(modid = "ic2")
	@Override
	public int getTier(){
		return manager.getTier(stack);
	}
	@Optional.Method(modid = "ic2")
	@Override
	public long getTransferLimit(){
		return GTValues.V[getTier()];
	}
}
