package tictim.ceu.mte;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import tictim.ceu.contents.CeuResources;
import tictim.ceu.enums.CommonEnergy;
import tictim.ceu.util.Energy;
import tictim.ceu.trait.energystorage.ConverterEnergyStorage;
import tictim.ceu.config.CeuConfig;
import tictim.ceu.enums.CeuType;
import tictim.ceu.trait.converter.TraitIC2EUIn;
import tictim.ceu.trait.energystorage.IC2EUConverterEnergyStorage;
import tictim.ceu.util.Ratio;

import javax.annotation.Nullable;
import java.util.List;

public class MTEIcef extends MTEToGTEUConverter{
	public MTEIcef(ResourceLocation id, int tier, int slots){
		super(id, tier, slots);
	}

	@Override protected void createEnergyReceiverTrait(){
		new TraitIC2EUIn(this);
	}
	@Override public Energy targetEnergy(){
		return CommonEnergy.IC2EU;
	}
	@Override public Ratio ratio(){
		return CeuConfig.config().getRatio(CeuType.ICEF, getTier());
	}
	@Override public boolean isDisabled(){
		return true; // TODO CeuConfig.config().isDisabled(CeuType.ICEF, getTier());
	}
	@Override protected ConverterEnergyStorage createEnergyStorage(){
		return new IC2EUConverterEnergyStorage(this);
	}
	@Override public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder){
		return new MTEIcef(metaTileEntityId, getTier(), getSlots());
	}

	@Override protected void addConverterInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced){
		// TODO Message preserved for ICEU/ICEF
		tooltip.add(I18n.format("ceu.disabled.ic2"));
	}

	@Override public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline){
		super.renderMetaTileEntity(renderState, translation, pipeline);
		CeuResources.ICEF_FACE.renderSided(getFrontFacing(), renderState, translation, pipeline);
	}

	// TODO
	@Nullable @Override protected TextureArea getTargetEnergyBatteryOptionIcon(){
		return null;
	}
}
