package tictim.ceu.mte;

import gregtech.api.GTValues;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.metatileentity.MTETrait;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import tictim.ceu.CeuResources;
import tictim.ceu.enums.BatteryChargeStrategy;
import tictim.ceu.enums.BatteryFilter;
import tictim.ceu.enums.BatteryIOOption;
import tictim.ceu.gui.TexturedCycleButtonWidget;
import tictim.ceu.mte.trait.ConverterTrait;
import tictim.ceu.mte.trait.ConverterEnergyStorage;
import tictim.ceu.util.CeuModes;
import tictim.ceu.util.Ratio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public abstract class ConverterMTE extends TieredMetaTileEntity{
	private final int slots;
	private ConverterEnergyStorage energyStorage;

	private final CeuModes modes = new CeuModes();

	public ConverterMTE(ResourceLocation id, int tier, int slots){
		super(id, tier);
		this.slots = slots;
		reinitializeEnergyContainer();
		initializeInventory();
	}

	public ConverterEnergyStorage getEnergyStorage(){
		return energyStorage;
	}

	public int getSlots(){
		return slots;
	}
	public long energyIOLimit(){
		return GTValues.V[getTier()]*slots*slots;
	}

	public final long amperage(){
		return getSlots()*getSlots();
	}
	public final long voltage(){
		return GTValues.V[getTier()];
	}

	public abstract Ratio ratio();
	public abstract boolean isDisabled();
	public abstract boolean convertsToFE();

	public CeuModes getModes(){
		return modes;
	}

	public Ratio toFE(){
		return convertsToFE() ? ratio() : ratio().reverse();
	}
	public Ratio toGTEU(){
		return convertsToFE() ? ratio().reverse() : ratio();
	}

	@Override protected boolean isEnergyEmitter(){
		return !convertsToFE();
	}

	@Override protected void initializeInventory(){
		super.initializeInventory();
		this.itemInventory = importItems;
	}
	@Override protected void reinitializeEnergyContainer(){
		this.energyStorage = new ConverterEnergyStorage(this);
	}
	@Override public int getActualComparatorValue(){
		long energyStored = energyStorage.stored(BatteryFilter.ALL);
		long energyCapacity = energyStorage.capacity(BatteryFilter.ALL);
		float f = energyCapacity==0 ? 0 : energyStored/(float)energyCapacity;
		return MathHelper.floor(f*14)+(energyStored>0 ? 1 : 0);
	}

	@Override public void addInformation(
			ItemStack stack,
			@Nullable World player,
			List<String> tooltip,
			boolean advanced){
		String gteuName = I18n.format("ceu.energy.gteu");
		String feName = I18n.format("ceu.energy.fe");
		if(convertsToFE()){
			tooltip.add(I18n.format("ceu.description", gteuName, feName));
			tooltip.add(I18n.format("ceu.conversion_rate", gteuName, ratio().in, feName, ratio().out));
		}else{
			tooltip.add(I18n.format("ceu.description", feName, gteuName));
			tooltip.add(I18n.format("ceu.conversion_rate", feName, ratio().in, gteuName, ratio().out));
		}
		tooltip.add(I18n.format("gregtech.universal.tooltip.item_storage_capacity", slots*slots));

		if(isDisabled()) tooltip.add(I18n.format("ceu.disabled"));
		else{
			tooltip.add(I18n.format(convertsToFE() ? "gregtech.universal.tooltip.voltage_in" : "gregtech.universal.tooltip.voltage_out",
					GTValues.V[getTier()], GTValues.VN[getTier()]));
			tooltip.add(I18n.format(convertsToFE() ? "ceu.energy_out" : "ceu.energy_in",
					toFE().convert(energyIOLimit(), Integer.MAX_VALUE)));
		}
		tooltip.add(I18n.format("gregtech.universal.tooltip.energy_storage_capacity", energyStorage.internalCapacity()));
	}

	@Override public boolean isValidFrontFacing(EnumFacing facing){
		return true;
	}

	@Override protected IItemHandlerModifiable createImportItemHandler(){
		return new ItemStackHandler(slots*slots){
			@Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
				if(energyStorage.getBatteryContainer(stack, BatteryFilter.ALL)==null) return stack;
				return super.insertItem(slot, stack, simulate);
			}
			@Override public int getSlotLimit(int slot){
				return 1;
			}
		};
	}

	@Override protected IItemHandlerModifiable createExportItemHandler(){
		return new EmptyHandler();
	}

	@Override protected ModularUI createUI(EntityPlayer entityPlayer){
		final int width = 176;
		final int upperPartHeight = Math.max(slots==1 ? (5+20+2+20) : (5+20+2+20+2+20), 18+18*slots);
		ModularUI.Builder b =
				ModularUI.builder(GuiTextures.BACKGROUND, width, upperPartHeight+94)
						.label(10, 5, getMetaFullName());

		for(int y = 0; y<slots; y++){
			for(int x = 0; x<slots; x++){
				int index = y*slots+x;
				b.widget(
						new SlotWidget(
								importItems,
								index,
								89-slots*9+x*18,
								18+y*18,
								true,
								true
						).setBackgroundTexture(GuiTextures.SLOT, GuiTextures.BATTERY_OVERLAY)
				);
			}
		}

		b.widget(new TexturedCycleButtonWidget(
				width-25,
				5,
				20,
				20,
				Arrays.stream(BatteryIOOption.values())
						.map(o -> "ceu.gteu_io_option."+o.name().toLowerCase())
						.toArray(String[]::new),
				() -> modes.getGteuBatteryOption().ordinal(),
				value -> modes.setGteuBatteryOption(BatteryIOOption.of(value)),
				CeuResources.GTEU_IO_MODE_BUTTON,
				true));

		TextureArea icon = getTargetEnergyBatteryOptionIcon();
		b.widget(new TexturedCycleButtonWidget(
				width-25,
				5+20+2,
				20,
				20,
				Arrays.stream(BatteryIOOption.values())
						.map(o -> "ceu.fe_io_option."+o.name().toLowerCase())
						.toArray(String[]::new),
				() -> modes.getTargetEnergyBatteryOption().ordinal(),
				value -> modes.setTargetEnergyBatteryOption(BatteryIOOption.of(value)),
				icon,
				icon!=null));

		if(slots>1){
			b.widget(new TexturedCycleButtonWidget(
					width-25,
					5+20+2+20+2,
					20,
					20,
					Arrays.stream(BatteryChargeStrategy.values())
							.map(o -> "ceu.charge_strategy."+o.name().toLowerCase())
							.toArray(String[]::new),
					() -> modes.getChargeStrategy().ordinal(),
					value -> modes.setChargeStrategy(BatteryChargeStrategy.of(value)),
					CeuResources.CHARGE_STRATEGY_BUTTON,
					true));
		}

		return b.bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, 8, upperPartHeight+12)
				.build(getHolder(), entityPlayer);
	}

	@Nullable protected abstract TextureArea getTargetEnergyBatteryOptionIcon();

	@Nullable @Override public <T> T getCapability(Capability<T> cap, EnumFacing side){
		for(MTETrait trait : mteTraits){
			if(trait instanceof ConverterTrait){
				T c = ((ConverterTrait)trait).getCapability(cap, side);
				if(c!=null) return c;
			}
		}
		return super.getCapability(cap, side);
	}

	@Override public NBTTagCompound writeToNBT(NBTTagCompound data){
		super.writeToNBT(data);
		data.setTag("EnergyStorage", this.energyStorage.serializeNBT());
		modes.write(data);
		return data;
	}

	@Override public void readFromNBT(NBTTagCompound data){
		super.readFromNBT(data);
		this.energyStorage.deserializeNBT(data.getCompoundTag("EnergyStorage"));
		this.modes.read(data);
	}
}
