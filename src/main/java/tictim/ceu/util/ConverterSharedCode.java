package tictim.ceu.util;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tictim.ceu.CeuResources;
import tictim.ceu.enums.BatteryChargeStrategy;
import tictim.ceu.enums.BatteryFilter;
import tictim.ceu.enums.BatteryIOOption;
import tictim.ceu.gui.TexturedCycleButtonWidget;
import tictim.ceu.mte.Converter;
import tictim.ceu.mte.trait.ConverterEnergyStorage;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public final class ConverterSharedCode{
	private ConverterSharedCode(){}

	/**
	 * @see gregtech.api.metatileentity.TieredMetaTileEntity#getActualComparatorValue()
	 */
	public static int comparatorValue(ConverterEnergyStorage storage){
		long energyStored = storage.stored(BatteryFilter.ALL);
		long energyCapacity = storage.capacity(BatteryFilter.ALL);
		float f = energyCapacity==0 ? 0 : energyStored/(float)energyCapacity;
		return MathHelper.floor(f*14)+(energyStored>0 ? 1 : 0);
	}

	public static IItemHandlerModifiable createImportItemHandler(Converter converter){
		return new ItemStackHandler(converter.getSlots()*converter.getSlots()){
			@Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
				if(converter.getEnergyStorage().getBatteryContainer(stack, BatteryFilter.ALL)==null) return stack;
				return super.insertItem(slot, stack, simulate);
			}
			@Override public int getSlotLimit(int slot){
				return 1;
			}
		};
	}

	public static <MTE extends MetaTileEntity & Converter> ModularUI createUI(MTE converter, EntityPlayer entityPlayer){
		final int WIDTH = 176;
		final int upperPartHeight = Math.max(converter.getSlots()==1 ? (5+20+2+20) : (5+20+2+20+2+20), 18+18*converter.getSlots());
		ModularUI.Builder b = ModularUI.builder(GuiTextures.BACKGROUND, WIDTH, upperPartHeight+94)
				.label(10, 5, converter.getMetaFullName());

		for(int y = 0; y<converter.getSlots(); y++){
			for(int x = 0; x<converter.getSlots(); x++){
				b.widget(
						new SlotWidget(
								converter.getImportItems(),
								y*converter.getSlots()+x,
								89-converter.getSlots()*9+x*18,
								18+y*18,
								true,
								true
						).setBackgroundTexture(GuiTextures.SLOT, GuiTextures.BATTERY_OVERLAY)
				);
			}
		}

		b.widget(new TexturedCycleButtonWidget(
				WIDTH-25,
				5,
				20,
				20,
				Arrays.stream(BatteryIOOption.values())
						.map(o -> "ceu.gteu_io_option."+o.name().toLowerCase())
						.toArray(String[]::new),
				() -> converter.getModes().getGteuBatteryOption().ordinal(),
				value -> converter.getModes().setGteuBatteryOption(BatteryIOOption.of(value)),
				CeuResources.GTEU_IO_MODE_BUTTON,
				true));

		b.widget(new TexturedCycleButtonWidget(
				WIDTH-25,
				5+20+2,
				20,
				20,
				Arrays.stream(BatteryIOOption.values())
						.map(o -> "ceu.fe_io_option."+o.name().toLowerCase())
						.toArray(String[]::new),
				() -> converter.getModes().getTargetEnergyBatteryOption().ordinal(),
				value -> converter.getModes().setTargetEnergyBatteryOption(BatteryIOOption.of(value)),
				CeuResources.FE_IO_MODE_BUTTON,
				true));

		if(converter.getSlots()>1){
			b.widget(new TexturedCycleButtonWidget(
					WIDTH-25,
					5+20+2+20+2,
					20,
					20,
					Arrays.stream(BatteryChargeStrategy.values())
							.map(o -> "ceu.charge_strategy."+o.name().toLowerCase())
							.toArray(String[]::new),
					() -> converter.getModes().getChargeStrategy().ordinal(),
					value -> converter.getModes().setChargeStrategy(BatteryChargeStrategy.of(value)),
					CeuResources.CHARGE_STRATEGY_BUTTON,
					true));
		}

		return b.bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, 8, upperPartHeight+12)
				.build(converter.getHolder(), entityPlayer);
	}

	public static final class Client{
		private Client(){}

		public static void addInformation(
				List<String> tooltip,
				Converter converter,
				String tierName,
				long internalCapacity){
			String gteuName = I18n.format("ceu.energy.gteu");
			String feName = I18n.format("ceu.energy.fe");
			if(converter.convertsToFE()){
				tooltip.add(I18n.format("ceu.description.ceu"));
				tooltip.add(I18n.format("ceu.conversion_rate", gteuName, converter.ratio().in, feName, converter.ratio().out));
			}else{
				tooltip.add(I18n.format("ceu.description.cef"));
				tooltip.add(I18n.format("ceu.conversion_rate", feName, converter.ratio().in, gteuName, converter.ratio().out));
			}
			tooltip.add(I18n.format("gregtech.universal.tooltip.item_storage_capacity", converter.getSlots()*converter.getSlots()));

			if(converter.isDisabled()) tooltip.add(I18n.format("ceu.disabled"));
			else{
				tooltip.add(I18n.format(converter.convertsToFE() ?
								"gregtech.universal.tooltip.voltage_in" :
								"gregtech.universal.tooltip.voltage_out",
						converter.voltage(), tierName));
				long limit = converter.energyIOLimit();
				int fe = converter.toFE().convertToInt(limit);
				if(converter.toGTEU().convertToLong(fe)!=limit){
					tooltip.add(I18n.format(converter.convertsToFE() ? "ceu.energy_out" : "ceu.energy_in", converter.toFE().convertToLong(limit)));
					tooltip.add("  "+I18n.format("ceu.energy_io_capped_out", fe));
				}else tooltip.add(I18n.format(converter.convertsToFE() ? "ceu.energy_out" : "ceu.energy_in", fe));
			}
			tooltip.add(I18n.format("gregtech.universal.tooltip.energy_storage_capacity", internalCapacity));
		}
	}
}
