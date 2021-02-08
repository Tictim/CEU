package tictim.ceu.trait.energystorage;

import com.google.common.math.LongMath;
import gregtech.api.GTValues;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.metatileentity.MTETrait;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandlerModifiable;
import tictim.ceu.config.CeuConfig;
import tictim.ceu.enums.BatteryFilter;
import tictim.ceu.mte.MTEConverter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class ConverterEnergyStorage extends MTETrait{
	protected final MTEConverter converter;

	private long internalStored;

	public ConverterEnergyStorage(MTEConverter converter){
		super(converter);
		this.converter = converter;
	}

	@Override public String getName(){
		return "ConverterEnergyStorage";
	}
	@Override public int getNetworkID(){
		return -1;
	}
	@Override @Nullable public <T> T getCapability(Capability<T> capability){
		return null;
	}

	@Override public void update(){
		if(!converter.getWorld().isRemote&&!converter.isDisabled()){
			BatteryFilter extract = converter.getModes().getOnlyDischargeFilter();
			if(extract!=BatteryFilter.NONE){
				// Draw the energy out from items and store them in internal buffer
				long spaceLeft = internalCapacity()-internalStored;
				if(spaceLeft>0){
					long extracted = extractFromBatteries(spaceLeft, false, false, extract, false);
					internalStored += extracted;
				}
			}
			BatteryFilter insert = converter.getModes().getOnlyChargeFilter();
			if(insert!=BatteryFilter.NONE){
				// Charge items using internal buffer
				long inserted = insertToBatteries(internalStored, false, insert, false);
				internalStored -= inserted;
			}
		}
	}

	public long internalCapacity(){
		return GTValues.V[converter.getTier()]*32;
	}

	public long stored(BatteryFilter filter){
		if(filter==BatteryFilter.NONE) return internalStored;

		long total = internalStored;
		IItemHandlerModifiable inventory = converter.getImportItems();
		for(int i = 0; i<inventory.getSlots(); i++){
			IElectricItem item = getBatteryContainer(inventory.getStackInSlot(i), filter);
			if(item!=null){
				total = LongMath.saturatedAdd(item.getCharge(), total);
				if(total==Long.MAX_VALUE) return total;
			}
		}
		return total;
	}

	public long capacity(BatteryFilter filter){
		if(filter==BatteryFilter.NONE) return internalCapacity();

		long total = internalCapacity();
		IItemHandlerModifiable inventory = converter.getImportItems();
		for(int i = 0; i<inventory.getSlots(); i++){
			IElectricItem item = getBatteryContainer(inventory.getStackInSlot(i), filter);
			if(item!=null){
				total = LongMath.saturatedAdd(item.getCharge(), total);
				if(total==Long.MAX_VALUE) return total;
			}
		}
		return total;
	}

	public long extract(long amount, boolean ignoreLimit, boolean externally, boolean simulate){
		return extract(amount, ignoreLimit, externally, converter.getModes().getDischargeFilter(), simulate);
	}
	public long extract(long amount, boolean ignoreLimit, boolean externally, BatteryFilter filter, boolean simulate){
		if(amount<=0) return 0;
		if(!ignoreLimit) amount = Math.min(amount, converter.energyIOLimit());

		long extractedFromInternal = Math.min(internalStored, amount);
		if(!simulate) internalStored -= extractedFromInternal;

		if(filter==BatteryFilter.NONE||extractedFromInternal==amount) return extractedFromInternal;

		long extractedFromBatteries = extractFromBatteries(amount-extractedFromInternal, ignoreLimit, externally, filter, simulate);
		return extractedFromBatteries+extractedFromInternal;
	}

	public long extractFromBatteries(long amount, boolean ignoreLimit, boolean externally, BatteryFilter filter, boolean simulate){
		if(amount<=0||filter==BatteryFilter.NONE) return 0;
		if(!ignoreLimit) amount = Math.min(amount, converter.energyIOLimit());

		long total = 0;
		IItemHandlerModifiable inventory = converter.getImportItems();
		for(int i = 0; i<inventory.getSlots(); i++){
			IElectricItem item = getBatteryContainer(inventory.getStackInSlot(i), filter);
			if(item!=null){
				total += discharge(item, amount-total, ignoreLimit, externally, simulate);
				if(total>=amount) return amount;
			}
		}
		return total;
	}

	public long insert(long amount, boolean ignoreLimit, boolean simulate){
		return insert(amount, ignoreLimit, converter.getModes().getChargeFilter(), simulate);
	}
	public long insert(long amount, boolean ignoreLimit, BatteryFilter filter, boolean simulate){
		if(amount<=0) return 0;
		if(!ignoreLimit) amount = Math.min(amount, converter.energyIOLimit());

		long spaceLeft = internalCapacity()-internalStored;
		if(spaceLeft<amount) amount = spaceLeft;
		long insertedToInternal = Math.min(spaceLeft, amount);
		if(!simulate) internalStored += amount;

		if(filter==BatteryFilter.NONE||insertedToInternal==amount) return insertedToInternal;

		long insertedToBatteries = insertToBatteries(amount, ignoreLimit, filter, simulate);
		return insertedToBatteries+insertedToInternal;
	}

	public long insertToBatteries(long amount, boolean ignoreLimit, BatteryFilter filter, boolean simulate){
		if(amount<=0||filter==BatteryFilter.NONE) return 0;
		if(!ignoreLimit) amount = Math.min(amount, converter.energyIOLimit());

		// Amount of consumed energy shouldn't differ between strategies. Also strategy is meaningless with single-slot converters.
		// So in that case, we just use fastest algorithm to save some time.
		if(simulate||converter.getSlots()==1) return insertToBatteriesFirstSlotFirst(amount, ignoreLimit, filter, simulate);

		switch(converter.getModes().getChargeStrategy()){
			case DISTRIBUTE:
				return distributeToBatteries(amount, ignoreLimit, filter);
			case FIRST_SLOT_FIRST:
				return insertToBatteriesFirstSlotFirst(amount, ignoreLimit, filter, simulate);
			case MOST_EMPTY_FIRST:
				return insertToBatteriesMostEmptyFirst(amount, ignoreLimit, filter);
			default:
				throw new IllegalStateException("Unreachable");
		}
	}

	private long insertToBatteriesFirstSlotFirst(long amount, boolean ignoreLimit, BatteryFilter filter, boolean simulate){
		long total = 0;
		IItemHandlerModifiable inventory = converter.getImportItems();
		for(int i = 0; i<inventory.getSlots(); i++){
			IElectricItem item = getBatteryContainer(inventory.getStackInSlot(i), filter);
			if(item!=null){
				total += charge(item, amount-total, ignoreLimit, simulate);
				if(total>=amount) return amount;
			}
		}
		return total;
	}

	private long distributeToBatteries(long amount, boolean ignoreLimit, BatteryFilter filter){
		List<BatteryEnergyDistribution> batteries = null;

		IItemHandlerModifiable inventory = converter.getImportItems();
		for(int i = 0; i<inventory.getSlots(); i++){
			IElectricItem item = getBatteryContainer(inventory.getStackInSlot(i), filter);
			if(item!=null){
				BatteryEnergyDistribution e = new BatteryEnergyDistribution(item, ignoreLimit);
				if(e.maxInput>0){
					if(batteries==null) batteries = new ArrayList<>();
					batteries.add(e); // Basic check for fully charged batteries...
				}
			}
		}

		if(batteries==null) return 0;
		if(batteries.size()==1){
			BatteryEnergyDistribution batteryEnergyDistribution = batteries.get(0);
			return charge(batteryEnergyDistribution.battery, amount, batteryEnergyDistribution.ignoreLimit, false);
		}

		final long optimisticDistribution = amount/batteries.size();
		long distributionRemainder = amount%batteries.size();

		// First we try to equally distribute energies by equally dividing input by number of batteries
		// Any amount of rejected energy is added to remainder, which handled in next step
		if(optimisticDistribution>0){
			for(BatteryEnergyDistribution battery : batteries){
				battery.input = charge(battery.battery, Math.min(optimisticDistribution, battery.maxInput), battery.ignoreLimit, true);
				if(battery.input!=optimisticDistribution){
					distributionRemainder += optimisticDistribution-battery.input;
				}
			}
		}
		// Here we get rid of remainder by just jamming energy into batteries and hope it'll take all of it
		// Unspent energy after this step is not consumed
		for(int i = 0; distributionRemainder>0&&i<batteries.size(); i++){
			BatteryEnergyDistribution battery = batteries.get(i);
			if(battery.input>=battery.maxInput) continue;

			long newInput = charge(battery.battery, Math.min(battery.input+distributionRemainder, battery.maxInput), battery.ignoreLimit, true);
			if(newInput!=battery.input){
				distributionRemainder -= newInput-battery.input;
				battery.input = newInput;
			}
		}

		long total = 0;
		// Feed energy for real.
		for(BatteryEnergyDistribution battery : batteries){
			long applied = charge(battery.battery, battery.input, battery.ignoreLimit, false);
			if(applied!=battery.input){ // TODO might just remove it
				System.out.println("Your mod couldn't even divide numbers correctly Tictim, go figure");
			}
			total += applied;
		}
		return total;
	}

	private long insertToBatteriesMostEmptyFirst(long amount, boolean ignoreLimit, BatteryFilter filter){
		List<IElectricItem> batteries = null;
		long total = 0;

		IItemHandlerModifiable inventory = converter.getImportItems();
		for(int i = 0; i<inventory.getSlots(); i++){
			IElectricItem item = getBatteryContainer(inventory.getStackInSlot(i), filter);
			if(item!=null&&charge(item, amount, ignoreLimit, true)>0){
				long charge = item.getCharge();
				if(charge==0){ // Totally empty batteries are top priority, we don't have to include it in sorting
					total += charge(item, amount-total, ignoreLimit, false);
					if(total>=amount) return amount;
				}else{
					if(batteries==null) batteries = new ArrayList<>();
					addBatterySorted(batteries, charge, item);
				}
			}
		}

		if(batteries!=null){
			for(IElectricItem item : batteries){
				total += charge(item, amount-total, ignoreLimit, false);
				if(total>=amount) return amount;
			}
		}
		return total;
	}

	private void addBatterySorted(List<IElectricItem> batteries, long charge, IElectricItem item){
		for(int i2 = 0; i2<batteries.size(); i2++){
			IElectricItem item2 = batteries.get(i2);
			if(item2.getCharge()>charge){
				batteries.add(i2, item);
				return;
			}
		}
		batteries.add(item);
	}

	@Nullable public IElectricItem getBatteryContainer(ItemStack s, BatteryFilter filter){
		if(s.isEmpty()||filter==BatteryFilter.NONE) return null;
		if(filter.chargeGTEU()){
			IElectricItem i = s.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
			if(i!=null&&i.canProvideChargeExternally()){
				if(!CeuConfig.config().permitOnlyExactVoltage()||i.getTier()==this.converter.getTier())
					return i;
			}
		}
		if(filter.chargeWrapped()){
			return getWrappedBatteryContainer(s);
		}
		return null;
	}

	// TODO Better cache the thing instead of creating wrapper instance every time...
	@Nullable protected abstract IElectricItem getWrappedBatteryContainer(ItemStack s);

	@Override public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setLong("current", internalStored);
		return nbt;
	}

	@Override public void deserializeNBT(NBTTagCompound nbt){
		this.internalStored = nbt.getLong("current");
	}

	protected long charge(IElectricItem item, long amount, boolean ignoreTransferLimit, boolean simulate){
		return item.charge(CeuConfig.config().constrainBattery() ? Math.min(amount, converter.voltage()) : amount,
				item.getTier(),
				ignoreTransferLimit,
				simulate);
	}

	protected long discharge(IElectricItem item, long amount, boolean ignoreTransferLimit, boolean externally, boolean simulate){
		return item.discharge(CeuConfig.config().constrainBattery() ? Math.min(amount, converter.voltage()) : amount,
				item.getTier(),
				ignoreTransferLimit,
				externally,
				simulate);
	}


	private final class BatteryEnergyDistribution{
		private final IElectricItem battery;
		private final boolean ignoreLimit;
		private final long maxInput;
		private long input;

		public BatteryEnergyDistribution(IElectricItem battery, boolean ignoreLimit){
			this.battery = battery;
			this.ignoreLimit = ignoreLimit;
			this.maxInput = charge(this.battery, Long.MAX_VALUE, this.ignoreLimit, true);
		}

		@Override public String toString(){
			return input+"/"+maxInput;
		}
	}
}
