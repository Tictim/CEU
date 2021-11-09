package tictim.ceu.mte;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregicadditions.GAValues;
import gregicadditions.machines.overrides.GATieredMetaTileEntity;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MTETrait;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandlerModifiable;
import tictim.ceu.CeuResources;
import tictim.ceu.mte.trait.ConverterEnergyStorage;
import tictim.ceu.mte.trait.ConverterTrait;
import tictim.ceu.mte.trait.FeIn;
import tictim.ceu.mte.trait.FeOut;
import tictim.ceu.mte.trait.GteuIn;
import tictim.ceu.mte.trait.GteuOut;
import tictim.ceu.util.CeuModes;
import tictim.ceu.util.ConverterSharedCode;

import javax.annotation.Nullable;
import java.util.List;

public class GAConverterMTE extends GATieredMetaTileEntity implements Converter{
	private final int slots;
	private final boolean convertsToFe;
	private final ConverterEnergyStorage energyStorage;

	private final CeuModes modes = new CeuModes();

	public GAConverterMTE(ResourceLocation id, int tier, int slots, boolean convertsToFe){
		super(id, tier);
		this.slots = slots;
		this.convertsToFe = convertsToFe;
		this.energyStorage = new ConverterEnergyStorage(this);
		initializeInventory();
		if(convertsToFE()){
			new GteuIn(this);
			new FeOut(this);
		}else{
			new FeIn(this);
			new GteuOut(this);
		}
	}

	@Override public ConverterEnergyStorage getEnergyStorage(){
		return energyStorage;
	}
	@Override public int getSlots(){
		return slots;
	}
	@Override public final long voltage(){
		return GAValues.V[getTier()];
	}

	@Override public boolean convertsToFE(){
		return convertsToFe;
	}
	@Override public CeuModes getModes(){
		return modes;
	}

	@Override protected boolean isEnergyEmitter(){
		return !convertsToFE();
	}

	@Override protected void initializeInventory(){
		super.initializeInventory();
		this.itemInventory = importItems;
	}
	@Override public GAConverterMTE createMetaTileEntity(MetaTileEntityHolder metaTileEntityHolder){
		return new GAConverterMTE(metaTileEntityId, getTier(), slots, convertsToFe);
	}
	@Override protected void reinitializeEnergyContainer(){}
	@Override public int getActualComparatorValue(){
		return ConverterSharedCode.comparatorValue(energyStorage);
	}

	@Override public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced){
		ConverterSharedCode.Client.addInformation(tooltip, this, GAValues.VN[getTier()], energyStorage.internalCapacity());
	}

	@Override public boolean isValidFrontFacing(EnumFacing facing){
		return true;
	}

	@Override protected IItemHandlerModifiable createImportItemHandler(){
		return ConverterSharedCode.createImportItemHandler(this);
	}

	@Override protected ModularUI createUI(EntityPlayer entityPlayer){
		return ConverterSharedCode.createUI(this, entityPlayer);
	}

	@Override public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline){
		super.renderMetaTileEntity(renderState, translation, pipeline);
		(convertsToFE() ? CeuResources.CEU_FACE : CeuResources.CEF_FACE).renderSided(getFrontFacing(), renderState, translation, pipeline);
	}

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
