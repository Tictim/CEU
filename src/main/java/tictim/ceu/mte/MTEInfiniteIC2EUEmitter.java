package tictim.ceu.mte;

import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.render.SimpleOverlayRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import tictim.ceu.contents.CeuResources;
import tictim.ceu.gui.InfiniteEnergyUIData;
import tictim.ceu.trait.infinite.TraitInfiniteIC2EUEmitter;

public class MTEInfiniteIC2EUEmitter extends MTEInfiniteEnergyBase<TraitInfiniteIC2EUEmitter>{
	public MTEInfiniteIC2EUEmitter(ResourceLocation metaTileEntityId){
		super(metaTileEntityId);
	}

	@Override protected TraitInfiniteIC2EUEmitter createTrait(){
		return new TraitInfiniteIC2EUEmitter(this);
	}
	@Override public boolean isDisabled(){
		return true; //CeuConfig.config().isEnergyEmitterDisabled(CommonEnergy.IC2EU);
	}
	@Override public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder){
		return new MTEInfiniteIC2EUEmitter(metaTileEntityId);
	}
	@Override protected ModularUI createUI(EntityPlayer entityPlayer){
		return InfiniteEnergyUIData.createWIPScreen(getHolder(), entityPlayer);
	}

	@Override protected SimpleOverlayRenderer getOverlay(){
		return CeuResources.IC2EU_EMITTER_FACE;
	}
}
