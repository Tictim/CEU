package tictim.ceu.mte;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import net.minecraft.util.ResourceLocation;
import tictim.ceu.contents.CeuResources;
import tictim.ceu.enums.CommonEnergy;
import tictim.ceu.util.Energy;
import tictim.ceu.trait.energystorage.ConverterEnergyStorage;
import tictim.ceu.config.CeuConfig;
import tictim.ceu.enums.CeuType;
import tictim.ceu.trait.converter.TraitFEOut;
import tictim.ceu.trait.energystorage.FEConverterEnergyStorage;
import tictim.ceu.util.Ratio;

import javax.annotation.Nullable;

public class MTECeu extends MTEFromGTEUConverter{
	public MTECeu(ResourceLocation id, int tier, int slots){
		super(id, tier, slots);
	}

	@Override protected void createEnergyEmitterTrait(){
		new TraitFEOut(this);
	}
	@Override public Energy targetEnergy(){
		return CommonEnergy.FE;
	}
	@Override public Ratio ratio(){
		return CeuConfig.config().getRatio(CeuType.CEU, getTier());
	}
	@Override public boolean isDisabled(){
		return CeuConfig.config().isDisabled(CeuType.CEU, getTier());
	}
	@Override protected ConverterEnergyStorage createEnergyStorage(){
		return new FEConverterEnergyStorage(this);
	}
	@Override public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder){
		return new MTECeu(metaTileEntityId, getTier(), getSlots());
	}

	@Override public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline){
		super.renderMetaTileEntity(renderState, translation, pipeline);
		CeuResources.CEU_FACE.renderSided(getFrontFacing(), renderState, translation, pipeline);
	}

	@Nullable @Override protected TextureArea getTargetEnergyBatteryOptionIcon(){
		return CeuResources.FE_IO_MODE_BUTTON;
	}
}
