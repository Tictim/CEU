package tictim.ceu.mte;

import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.render.SimpleOverlayRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import tictim.ceu.contents.CeuResources;
import tictim.ceu.enums.CommonEnergy;
import tictim.ceu.config.CeuConfig;
import tictim.ceu.gui.InfiniteEnergyUIData;
import tictim.ceu.trait.infinite.TraitInfiniteGTEUEmitter;

public class MTEInfiniteGTEUEmitter extends MTEInfiniteEnergyBase<TraitInfiniteGTEUEmitter>{
	public MTEInfiniteGTEUEmitter(ResourceLocation metaTileEntityId){
		super(metaTileEntityId);
	}

	@Override protected TraitInfiniteGTEUEmitter createTrait(){
		return new TraitInfiniteGTEUEmitter(this);
	}
	@Override public boolean isDisabled(){
		return CeuConfig.config().isEnergyEmitterDisabled(CommonEnergy.GTEU);
	}
	@Override public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder){
		return new MTEInfiniteGTEUEmitter(metaTileEntityId);
	}
	@Override protected ModularUI createUI(EntityPlayer entityPlayer){
		InfiniteEnergyUIData d = new InfiniteEnergyUIData();
		d.setEnergy(trait.getEnergy());
		d.setInfinite(trait.isInfinite());
		d.setTier(trait.getTier());
		return d.guiBuilder()
				.buttonInfinite(trait::setInfinite)
				.energyInput("EU", trait::setEnergy)
				.buttonTier(trait::setTier)
				.buttonAcceptDecline()
				.createUI(getHolder(), entityPlayer);
	}

	@Override protected SimpleOverlayRenderer getOverlay(){
		return CeuResources.GTEU_EMITTER_FACE;
	}
}
